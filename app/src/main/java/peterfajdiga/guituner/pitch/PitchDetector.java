package peterfajdiga.guituner.pitch;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import peterfajdiga.guituner.fourier.Complex;
import peterfajdiga.guituner.fourier.Fourier;
import peterfajdiga.guituner.general.General;
import peterfajdiga.guituner.gui.PitchView;

public class PitchDetector extends StoppableThread implements ShortBufferReceiver, PitchView.OnFocusChangedListener {

    private static final double NOISE_THRESHOLD = 20.0;
    private static final double NOISE_THRESHOLD_FOCUSED = 2.0;
    private static final int MAX_HARMONICS = 24;
    private static final int HARMONICS_DROP_RADIUS = 16;
    private static final int GCD_MIN_COUNT = 2;
    private static final int GCD_MIN_DELTA = 20;
    private static final double GCD_FAILSAFE_PREFERENCE = 4.0;
    private static final int FOCUSED_BIN_RADIUS = 10;

    private final Receiver receiver;
    private volatile boolean working = false;
    private short[] buffer;
    private int sampleRate;
    private double focusedFrequency = 0.0;

    public PitchDetector(final Receiver receiver) {
        this.receiver = receiver;
    }

    // called by another thread
    @Override
    public synchronized void putBuffer(final short[] buffer) {
        if (working) {
            // still busy with previous buffer, skip this one
            return;
        }
        this.buffer = buffer;
        notify();
    }

    // called by another thread
    @Override
    public void setSampleRate(final int sampleRate) {
        this.sampleRate = sampleRate;
    }

    @Override
    public synchronized void run() {
        try {
            while (threadEnabled) {
                wait();
                working = true;
                findPitch();
                working = false;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("PitchDetector thread stopped");
        }
    }

    // called by this thread
    private double getFrequency(final Complex[] X, final int index) {
        final double binWidth = (double)sampleRate / X.length;
        final double midFrequency = index * binWidth;
        if (index <= 0 || index >= X.length-1) {
            return midFrequency;
        }
        // Quinn's Second Estimator Method for bin interpolation
        final double ap = (X[index+1].re * X[index].re + X[index+1].im * X[index].im)  /  (X[index].re * X[index].re + X[index].im * X[index].im);
        final double dp = -ap / (1 - ap);
        final double am = (X[index-1].re * X[index].re + X[index-1].im * X[index].im)  /  (X[index].re * X[index].re + X[index].im * X[index].im);
        final double dm = am / (1 - am);
        final double d = (dp + dm) / 2 + tau(dp * dp) - tau(dm * dm);
        // linear interpolation
        final double nextFrequency = (index+1) * binWidth;
        return midFrequency + d * (nextFrequency - midFrequency);
    }
    private static final double TAU_CONST_A = Math.sqrt(6.0)/24.0;
    private static final double TAU_CONST_B = Math.sqrt(2.0/3.0);
    private double tau(final double x) {
        return 0.25 * Math.log(3.0*x*x + 6.0*x + 1) - TAU_CONST_A * Math.log((x + 1 - TAU_CONST_B) / (x + 1.0 + TAU_CONST_B));
    }

    private static double homebrewGcd(final List<Double> values) {
        final double failsafe = values.get(0);

        final List<GcdCandidate> candidates = new ArrayList<GcdCandidate>();
        for (double value0 : values) {
            for (double value1 : values) {
                final double delta = Math.abs(value1 - value0);
                if (delta < GCD_MIN_DELTA) {
                    continue;
                }
                boolean addedToExistingCandidate = false;
                for (GcdCandidate candidate : candidates) {
                    if (candidate.add(delta)) {
                        addedToExistingCandidate = true;
                        break;
                    }
                }
                if (!addedToExistingCandidate) {
                    candidates.add(new GcdCandidate(delta));
                }
            }
        }

        // return candidate with highest count
        int maxCount = Integer.MIN_VALUE;
        double gcd = Double.NaN;
        for (GcdCandidate candidate : candidates) {
            if (candidate.count > maxCount) {
                maxCount = candidate.count;
                gcd = candidate.avg();
            }
        }
        if (maxCount < GCD_MIN_COUNT) {
            return failsafe;
        }
        for (Double value : values) {
            if (value < gcd - GCD_FAILSAFE_PREFERENCE) {
                return value;
            }
        }
        return gcd;
    }

    // called by this thread
    private void findPitch() {
        final Complex[] freqSpace = Fourier.fft(buffer);
        final int halfN = buffer.length / 2;

        double sum = 0.0;
        final double[] values = new double[halfN];
        for (int i = 0; i < halfN; i++) {
            values[i] = freqSpace[i].abs();
            sum += values[i];
        }
        final double floor = sum / freqSpace.length;

        final int startIndex, endIndex;
        double noiseThreshold;
        if (focusedFrequency == 0.0) {
            startIndex = 0;
            endIndex = values.length;
            noiseThreshold = NOISE_THRESHOLD;
        } else {
            final double binWidth = (double) sampleRate / freqSpace.length;
            final int focusedIndex = (int) Math.round(focusedFrequency / binWidth);
            startIndex = General.getStart(focusedIndex, FOCUSED_BIN_RADIUS);
            endIndex = General.getEnd(focusedIndex, FOCUSED_BIN_RADIUS, values.length);
            noiseThreshold = NOISE_THRESHOLD_FOCUSED;
        }
        int maxBin = General.max(values, startIndex, endIndex);
        final List<Double> harmonics = new ArrayList<Double>();
        while (maxBin >= 0 && values[maxBin] / floor > noiseThreshold && harmonics.size() < MAX_HARMONICS) {
            harmonics.add(getFrequency(freqSpace, maxBin));
            General.dropAround(values, maxBin, HARMONICS_DROP_RADIUS);
            maxBin = General.max(values, startIndex, endIndex);
            noiseThreshold = NOISE_THRESHOLD;
        }

        if (harmonics.isEmpty()) {
            return;
        }
        receiver.updatePitch(homebrewGcd(harmonics));
    }

    @Override
    public void onFocusChanged(final double focusedFrequency) {
        this.focusedFrequency = focusedFrequency;
    }


    private static class GcdCandidate {

        private static final double MAX_ERROR = 2.0;
        private static final int MULTS = 5;

        double sum;
        int count;

        GcdCandidate(final double value) {
            sum = value;
            count = 1;
        }

        double avg() {
            return sum / count;
        }

        // return true if added
        boolean add(final double value) {
            for (int mult = 1; mult <= MULTS; mult++) {
                final double adjustedValue = value / mult;
                if (Math.abs(avg() - adjustedValue) <= MAX_ERROR) {
                    sum += adjustedValue;
                    count++;
                    return true;
                }
            }
            return false;
        }
    }

    private static class Bin implements Comparable<Bin> {
        double value;
        int index;

        Bin(final double value, final int index) {
            this.value = value;
            this.index = index;
        }

        @Override
        public int compareTo(@NonNull Bin bin) {
            return Double.compare(this.value, bin.value);
        }
    }

    public interface Receiver {
        void updatePitch(double frequency);
    }
}
