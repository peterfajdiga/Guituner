package peterfajdiga.guituner.app;

import android.view.View;

import peterfajdiga.guituner.beep.Beeper;

class SoundOnClickListener implements View.OnClickListener {
    private double frequency = 440.0;
    private final Beeper player = new Beeper();

    @Override
    public void onClick(View view) {
        player.play(frequency);
    }

    public void setFrequency(final double frequency) {
        this.frequency = frequency;
    }
}
