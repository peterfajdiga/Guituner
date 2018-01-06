package peterfajdiga.guituner.gui;

import android.view.View;
import android.widget.TextView;

import peterfajdiga.guituner.R;

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

        // debug
        final TextView hello = contentView.findViewById(R.id.hello);
        hello.setText(String.format("%.2f Hz", frequency));
    }
}
