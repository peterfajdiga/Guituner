package peterfajdiga.guituner.fourier;

public class PitchDetector extends StoppableThread implements ShortBufferReceiver {

    private final Owner receiver;
    private volatile boolean working = false;
    private short[] buffer;

    public PitchDetector(final Owner receiver) {
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
        receiver.putDouble(buffer[0]);
    }



    public interface Owner {
        void putDouble(double value);
    }
}
