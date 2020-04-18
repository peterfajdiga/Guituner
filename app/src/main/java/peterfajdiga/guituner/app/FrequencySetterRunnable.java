package peterfajdiga.guituner.app;

import android.view.View;

import peterfajdiga.guituner.R;
import peterfajdiga.guituner.gui.PitchView;

public class FrequencySetterRunnable implements Runnable {
    private double frequency;
    private final View contentView;

    public FrequencySetterRunnable(final View contentView) {
        this.contentView = contentView;
    }

    public FrequencySetterRunnable setFrequency(final double frequency) {
        this.frequency = frequency;
        return this;
    }

    @Override
    public void run() {
        final PitchView pitchView = contentView.findViewById(R.id.pitchview);
        pitchView.setFrequency(frequency);
    }
}
