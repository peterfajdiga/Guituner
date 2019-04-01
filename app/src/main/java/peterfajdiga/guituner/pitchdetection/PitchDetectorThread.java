package peterfajdiga.guituner.pitchdetection;

import peterfajdiga.guituner.recording.ShortBufferReceiver;
import peterfajdiga.guituner.recording.StoppableThread;

public class PitchDetectorThread extends StoppableThread implements ShortBufferReceiver {

    private final Receiver receiver;
    private volatile boolean working = false;
    private short[] buffer;
    private PitchDetector pitchDetector;

    public PitchDetectorThread(final Receiver receiver, final PitchDetector pitchDetector) {
        this.receiver = receiver;
        this.pitchDetector = pitchDetector;
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

    @Override
    public synchronized void run() {
        try {
            while (threadEnabled) {
                wait();
                working = true;
                try {
                    final double pitch = pitchDetector.findPitch(buffer);
                    receiver.updatePitch(pitch);
                } catch (final PitchDetector.NoPitchFoundException e) {
                    // no problem, we'll find a pitch next time
                }
                working = false;
            }
        } catch (final InterruptedException e) {
            throw new RuntimeException("PitchDetectorThread stopped");
        }
    }

    public interface Receiver {
        void updatePitch(double frequency);
    }
}
