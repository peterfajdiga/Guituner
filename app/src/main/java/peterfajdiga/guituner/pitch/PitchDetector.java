package peterfajdiga.guituner.pitch;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import peterfajdiga.guituner.fourier.Complex;
import peterfajdiga.guituner.fourier.Fourier;
import peterfajdiga.guituner.general.General;
import peterfajdiga.guituner.gui.PitchView;

public class PitchDetector extends StoppableThread implements ShortBufferReceiver, PitchView.OnFocusChangedListener {

    private static final int MAX_HARMONICS = 24;
    private static final int HARMONICS_DROP_RADIUS = 16;
    private static final double HARMONICS_THRESHOLD = 0.05;
    private static final int GCD_MIN_COUNT = 2;
    private static final int GCD_MIN_DELTA = 20;
    private static final int FOCUSED_FREQUENCY_RADIUS = 20;

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
                if (focusedFrequency == 0.0) {
                    findPitch();
                } else {
                    findPitch(focusedFrequency);
                }
                working = false;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("PitchDetector thread stopped");
        }
    }

    // called by this thread
    private double getFrequency(final double[] freqSpace, final int index) {
        final double binWidth = sampleRate / (freqSpace.length * 2.0);
        final double midFrequency = index * binWidth;
        if (index <= 0 || index >= freqSpace.length-1) {
            return midFrequency;
        }
        // Quadratic Method for bin interpolation
        final double y1 = freqSpace[index-1];
        final double y2 = freqSpace[index  ];
        final double y3 = freqSpace[index+1];
        final double d = (y3 - y1) / (2.0 * (2.0 * y2 - y1 - y3));
        return midFrequency + d;
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
            if (value < gcd) {
                return value;
            }
        }
        return gcd;
    }

    // called by this thread
    // in-place FFT
    private double[] transform() {
        final Complex[] freqSpace = Fourier.fft(buffer);
        final int halfN = buffer.length / 2;

        final double[] values = new double[halfN];
        for (int i = 0; i < halfN; i++) {
            values[i] = freqSpace[i].abs();
        }
        return values;
    }

    // called by this thread
    // unfocused findPitch, search whole frequency space
    private void findPitch() {
        final double[] freqSpace = transform();
        double sum = 0.0;
        for (double value : freqSpace) {
            sum += value;
        }
        final double floor = sum / freqSpace.length;

        int maxBin = General.max(freqSpace);
        if (maxBin < 0) {
            // invalid buffer
            return;
        }
        final double threshold = (freqSpace[maxBin] - floor) * HARMONICS_THRESHOLD;

        final List<Double> harmonics = new ArrayList<Double>();
        while (maxBin >= 0 && freqSpace[maxBin] - floor > threshold && harmonics.size() < MAX_HARMONICS) {
            harmonics.add(getFrequency(freqSpace, maxBin));
            General.dropAround(freqSpace, maxBin, HARMONICS_DROP_RADIUS);
            maxBin = General.max(freqSpace);
        }

        receiver.updatePitch(homebrewGcd(harmonics));
    }

    // focused findPitch, search around given frequency
    private void findPitch(final double focusedFrequency) {
        final double[] freqSpace = transform();

        final double binWidth = sampleRate / (freqSpace.length * 2.0);
        final int focusedIndex = (int)Math.round(focusedFrequency / binWidth);

        final int maxBin = General.maxAround(freqSpace, focusedIndex, FOCUSED_FREQUENCY_RADIUS);
        if (maxBin < 0) {
            return;
        }
        receiver.updatePitch(getFrequency(freqSpace, maxBin));
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
