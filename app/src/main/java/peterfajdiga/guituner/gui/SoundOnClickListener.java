package peterfajdiga.guituner.gui;

import android.view.View;

import peterfajdiga.guituner.pitch.BeepThread;

public class SoundOnClickListener implements View.OnClickListener {

    private BeepThread beepThread;
    private double frequency = 440.0;

    @Override
    public void onClick(View view) {
        if (beepThread == null || !beepThread.isAlive()) {
            beepThread = new BeepThread(frequency, 2.0);
            beepThread.start();
        }
    }

    public void setFrequency(final double frequency) {
        this.frequency = frequency;
    }
}
