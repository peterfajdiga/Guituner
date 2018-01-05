package peterfajdiga.guituner.pitch;

import android.support.annotation.NonNull;

import java.util.LinkedList;
import java.util.PriorityQueue;

import peterfajdiga.guituner.fourier.Complex;
import peterfajdiga.guituner.fourier.Fourier;

public class PitchDetector extends StoppableThread implements ShortBufferReceiver {

    private static final int N_MAX_BINS = 5;

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
    private static double interpolateBin(final Complex[] freqSpace,
                                         final int index,
                                         final double midFrequency) {
        if (index <= 0 || index >= freqSpace.length-1) {
            return midFrequency;
        }
        // Quadratic Method
        final double y1 = freqSpace[index-1].abs();
        final double y2 = freqSpace[index  ].abs();
        final double y3 = freqSpace[index+1].abs();
        final double d = (y3 - y1) / (2 * (2 * y2 - y1 - y3));
        return midFrequency + d;
    }

    // called by this thread
    private void transform() {
        final Complex[] freqSpace = Fourier.fft(buffer);
        final int halfN = buffer.length / 2;

        final PriorityQueue<Bin> maxBinsQueue = new PriorityQueue<Bin>(N_MAX_BINS);
        for (int i = 0; i < N_MAX_BINS; i++) {
            maxBinsQueue.add(new Bin(Double.MIN_VALUE, -1));
        }
        for (int i = 0; i < halfN; i++) {
            final double val = freqSpace[i].abs();
            if (val > maxBinsQueue.peek().value) {
                maxBinsQueue.poll();
                maxBinsQueue.add(new Bin(val, i));
            }
        }

        final int[] maxBins = new int[N_MAX_BINS];
        for (int i = 0; i < N_MAX_BINS; i++) {
            maxBins[i] = maxBinsQueue.poll().index;
        }

        final double binWidth = (double)sampleRate / freqSpace.length;
        final int maxBin = maxBins[N_MAX_BINS-1];
        final double midFreq = maxBin * binWidth;
        receiver.updatePitch(interpolateBin(freqSpace, maxBin, midFreq));
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
