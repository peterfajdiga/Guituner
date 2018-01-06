package peterfajdiga.guituner.pitch;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import peterfajdiga.guituner.fourier.Complex;
import peterfajdiga.guituner.fourier.Fourier;
import peterfajdiga.guituner.general.General;

public class PitchDetector extends StoppableThread implements ShortBufferReceiver {

    private static final int MAX_HARMONICS = 24;
    private static final int HARMONICS_DROP_RADIUS = 16;
    private static final double HARMONICS_THRESHOLD = 0.05;
    private static final int GCD_MIN_COUNT = 2;
    private static final int GCD_MIN_DELTA = 20;

    private final Receiver receiver;
    private volatile boolean working = false;
    private short[] buffer;
    private int sampleRate;

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
                transform();
                working = false;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("PitchDetector thread stopped");
        }
    }

    // called by this thread
    private double getFrequency(final double[] freqSpace, final int index) {
        final double binWidth = (double)sampleRate / (freqSpace.length * 2);
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
        return gcd;
    }

    // called by this thread
    private void transform() {
        final long startTime = System.currentTimeMillis();
        final Complex[] freqSpace = Fourier.fft(buffer);
        final int halfN = buffer.length / 2;

        double sum = 0.0;
        final double[] values = new double[halfN];
        for (int i = 0; i < halfN; i++) {
            values[i] = freqSpace[i].abs();
            sum += values[i];
        }
        final double floor = sum / halfN;

        int maxBin = General.max(values);
        final double threshold = (values[maxBin] - floor) * HARMONICS_THRESHOLD;

        final List<Double> harmonics = new ArrayList<Double>();
        while (values[maxBin] - floor > threshold && harmonics.size() < MAX_HARMONICS) {
            harmonics.add(getFrequency(values, maxBin));
            General.drop(values, maxBin, HARMONICS_DROP_RADIUS);
            maxBin = General.max(values);
        }

        receiver.updatePitch(homebrewGcd(harmonics));
        System.err.println("Time elapsed: " + (System.currentTimeMillis() - startTime) + " for harmonics:");
        for (double harmonic : harmonics) {
            System.err.printf("%f Hz\n", harmonic);
        }
        System.err.println();
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
