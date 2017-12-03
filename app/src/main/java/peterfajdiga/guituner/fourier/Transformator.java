package peterfajdiga.guituner.fourier;

public class Transformator extends Thread implements ShortBufferReceiver {

    private boolean threadEnabled = false;
    private volatile boolean working = false;
    private short[] buffer;

    public void startTransforming() {
        threadEnabled = true;
        start();
    }

    public void stopTransforming() {
        threadEnabled = false;
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
            throw new RuntimeException("Transformator thread stopped");
        }
    }

    // called by this thread
    private void transform() {
        System.err.println("first short: " + buffer[0]);
    }
}
