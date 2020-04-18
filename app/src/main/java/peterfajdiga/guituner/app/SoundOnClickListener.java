package peterfajdiga.guituner.app;

import android.view.View;

import peterfajdiga.guituner.beep.BeepThread;

class SoundOnClickListener implements View.OnClickListener {
    private double frequency = 440.0;

    @Override
    public void onClick(View view) {
        new BeepThread(frequency, 2.0).start();
    }

    public void setFrequency(final double frequency) {
        this.frequency = frequency;
    }
}
