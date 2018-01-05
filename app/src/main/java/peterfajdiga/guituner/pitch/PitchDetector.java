package peterfajdiga.guituner.pitch;

import peterfajdiga.guituner.fourier.Complex;
import peterfajdiga.guituner.fourier.Fourier;

public class PitchDetector extends StoppableThread implements ShortBufferReceiver {

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
    private void transform() {
        final Complex[] freqSpace = Fourier.fft(buffer);
        final int halfN = buffer.length / 2;

        int max_i = -1;
        double max_val = Double.MIN_VALUE;
        for (int i = 0; i < halfN; i++) {
            final double val = freqSpace[i].abs();
            if (val > max_val) {
                max_val = val;
                max_i = i;
            }
        }

        final double binWidth = (double)sampleRate / freqSpace.length;
        final double halfBinWidth = binWidth / 2;
        final double midFreq = max_i * binWidth;
        receiver.updatePitch(midFreq - halfBinWidth, midFreq + halfBinWidth);
    }



    public interface Receiver {
        void updatePitch(double frequency_min, double frequency_max);
    }
}
