package peterfajdiga.guituner.recording;

import androidx.annotation.CallSuper;

public abstract class StoppableThread extends Thread {
    protected boolean threadEnabled = false;

    @CallSuper
    public void startThread() {
        if (threadEnabled || isAlive()) {
            throw new RuntimeException("Thread already running");
        }
        threadEnabled = true;
        start();
    }

    @CallSuper
    public void stopThread() {
        if (!threadEnabled || !isAlive()) {
            throw new RuntimeException("Thread not running");
        }
        threadEnabled = false;
    }
}
