package peterfajdiga.guituner.pitchdetection;

import peterfajdiga.guituner.gui.PitchView;
import peterfajdiga.guituner.recording.ShortBufferReceiver;
import peterfajdiga.guituner.recording.StoppableThread;

public class PitchDetectorThread extends StoppableThread implements ShortBufferReceiver, PitchView.OnFocusChangedListener {

    private final Receiver receiver;
    private volatile boolean working = false;
    private short[] buffer;
    private PitchDetector pitchDetector = new PitchDetector();

    public PitchDetectorThread(final Receiver receiver) {
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
        this.pitchDetector.sampleRate = sampleRate;
    }

    @Override
    public synchronized void run() {
        try {
            while (threadEnabled) {
                wait();
                working = true;
                try {
                    final double pitch = pitchDetector.findPitch(buffer);
                    receiver.updatePitch(pitch);
                } catch (final NoPitchFoundException e) {
                    // no problem, we'll find a pitch next time
                }
                working = false;
            }
        } catch (final InterruptedException e) {
            throw new RuntimeException("PitchDetectorThread stopped");
        }
    }

    @Override
    public void onFocusChanged(final double focusedFrequency) {
        this.pitchDetector.focusedMode = true;
        this.pitchDetector.focusedFrequency = focusedFrequency;
    }

    @Override
    public void onFocusRemoved() {
        this.pitchDetector.focusedMode = false;
    }


    public static class NoPitchFoundException extends Exception {}

    public interface Receiver {
        void updatePitch(double frequency);
    }
}
