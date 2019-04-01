package peterfajdiga.guituner.pitchdetection;

import java.util.ArrayList;
import java.util.List;

import peterfajdiga.guituner.fourier.Complex;
import peterfajdiga.guituner.fourier.Fourier;
import peterfajdiga.guituner.general.General;
import peterfajdiga.guituner.gui.PitchView;

public class PitchDetector extends StoppableThread implements ShortBufferReceiver, PitchView.OnFocusChangedListener {

    private static final double NOISE_THRESHOLD = 20.0;
    private static final double NOISE_THRESHOLD_FOCUSED = 2.0;
    private static final double MIN_FREQUENCY = 20.0;
    private static final int MAX_HARMONICS = 24;
    private static final int HARMONICS_DROP_RADIUS = 16;
    private static final double FUNDAMENTAL_WEIGHT_GCD = 0.5;
    private static final double FUNDAMENTAL_WEIGHT_MINFREQ = 0.5;
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
        final int n_positiveFrequencies = X.length / 2;
        final double binWidth = (double)sampleRate / X.length;
        final double midFrequency = index * binWidth;
        if (index <= 0 || index >= n_positiveFrequencies-1) {
            return midFrequency;
        }
        // Quinn's Second Estimator Method for bin interpolation
        final double iAbsSq = X[index].re * X[index].re + X[index].im * X[index].im;
        final double ap = (X[index+1].re * X[index].re + X[index+1].im * X[index].im) / iAbsSq;
        final double am = (X[index-1].re * X[index].re + X[index-1].im * X[index].im) / iAbsSq;
        final double dp = -ap / (1 - ap);
        final double dm = am / (1 - am);
        final double d = (dp + dm) / 2 + tau(dp * dp) - tau(dm * dm);
        return (index + d) * binWidth;
    }
    private static final double TAU_CONST_A = Math.sqrt(6.0)/24.0;
    private static final double TAU_CONST_B = Math.sqrt(2.0/3.0);
    private double tau(final double x) {
        return 0.25 * Math.log(3.0*x*x + 6.0*x + 1.0) - TAU_CONST_A * Math.log((x + 1.0 - TAU_CONST_B) / (x + 1.0 + TAU_CONST_B));
    }

    private double findFundamental(final List<Double> values) {
        final List<FundamentalCandidate> candidates = new ArrayList<>();
        for (double value0 : values) {
            for (double value1 : values) {
                final double delta = Math.abs(value1 - value0);
                if (delta < MIN_FREQUENCY) {
                    continue;
                }
                boolean addedToExistingCandidate = false;
                for (FundamentalCandidate candidate : candidates) {
                    if (candidate.add(delta)) {
                        addedToExistingCandidate = true;
                        break;
                    }
                }
                if (!addedToExistingCandidate) {
                    candidates.add(new FundamentalCandidate(delta));
                }
            }
        }

        // return candidate with highest count
        int maxCount = Integer.MIN_VALUE;
        double gcd = Double.MAX_VALUE;
        for (FundamentalCandidate candidate : candidates) {
            if (candidate.count > maxCount) {
                maxCount = candidate.count;
                gcd = candidate.avg();
            }
        }

        double minFreq = Double.MAX_VALUE;
        for (Double value : values) {
            if (value > MIN_FREQUENCY && value < minFreq) {
                minFreq = value;
            }
        }

        if (Math.abs(minFreq - gcd) < FundamentalCandidate.MAX_ERROR) {
            return FUNDAMENTAL_WEIGHT_MINFREQ * minFreq + FUNDAMENTAL_WEIGHT_GCD * gcd;
        }

        if (focusedFrequency > 0.0) {
            final double focusedFrequencyRadius = FOCUSED_BIN_RADIUS * ((double)sampleRate / buffer.length);
            final boolean gcdLegal     = General.nearlyEqual(gcd    , focusedFrequency, focusedFrequencyRadius);
            final boolean minFreqLegal = General.nearlyEqual(minFreq, focusedFrequency, focusedFrequencyRadius);
            if (gcdLegal && !minFreqLegal) {
                return gcd;
            }
            if (minFreqLegal && !gcdLegal) {
                return minFreq;
            }
        }
        if (minFreq < gcd) {
            return minFreq;
        }
        return gcd;
    }

    // called by this thread
    private void findPitch() {
        if (buffer.length == 0) {
            // invalid buffer
            return;
        }

        final Complex[] freqSpace = Fourier.fft(buffer);
        final int halfN = buffer.length / 2;

        double sum = 0.0;
        final double[] values = new double[halfN];
        for (int i = 0; i < halfN; i++) {
            values[i] = freqSpace[i].abs();
            sum += values[i];
        }
        final double floor = sum / freqSpace.length;

        final int startIndex;
        final List<Double> harmonics = new ArrayList<>();
        if (focusedFrequency > 0.0) {
            final double binWidth = (double)sampleRate / freqSpace.length;
            final int focusedIndex = (int)Math.round(focusedFrequency / binWidth);
            final int maxBin = General.max(values, focusedIndex, FOCUSED_BIN_RADIUS);
            if (maxBin >= 0 && values[maxBin] / floor > NOISE_THRESHOLD_FOCUSED) {
                harmonics.add(getFrequency(freqSpace, maxBin));
                General.dropAround(values, maxBin, HARMONICS_DROP_RADIUS);
            }
            startIndex = General.getStart(focusedIndex, FOCUSED_BIN_RADIUS);
        } else {
            startIndex = 0;
        }

        final int endIndex = values.length;
        while (harmonics.size() < MAX_HARMONICS) {
            final int maxBin = General.max(values, startIndex, endIndex);
            if (maxBin < 0 || values[maxBin] / floor < NOISE_THRESHOLD) {
                break;  // no more harmonics
            }
            harmonics.add(getFrequency(freqSpace, maxBin));
            General.dropAround(values, maxBin, HARMONICS_DROP_RADIUS);
        }

        if (harmonics.isEmpty()) {
            return;
        }
        receiver.updatePitch(findFundamental(harmonics));
    }

    @Override
    public void onFocusChanged(final double focusedFrequency) {
        this.focusedFrequency = focusedFrequency;
    }

    @Override
    public void onFocusRemoved() {
        this.focusedFrequency = 0.0;
    }


    private static class FundamentalCandidate {

        private static final double MAX_ERROR = 2.0;
        private static final int MULTS = 5;

        double sum;
        int count;

        FundamentalCandidate(final double value) {
            sum = value;
            count = 1;
        }

        double avg() {
            return sum / count;
        }

        // return true if added
        boolean add(final double value) {
            final double avg = avg();
            for (int mult = 1; mult <= MULTS; mult++) {
                final double adjustedValue = value / mult;
                if (Math.abs(avg - adjustedValue) <= MAX_ERROR) {
                    sum += adjustedValue;
                    count++;
                    return true;
                }
            }
            for (int mult = 1; mult <= MULTS; mult++) {
                final double adjustedValue = value * mult;
                if (Math.abs(avg - adjustedValue) <= MAX_ERROR) {
                    sum += adjustedValue;
                    count++;
                    sum /= mult;
                    return true;
                }
            }
            return false;
        }
    }

    public interface Receiver {
        void updatePitch(double frequency);
    }
}
