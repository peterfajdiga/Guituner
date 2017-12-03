package peterfajdiga.guituner;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

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
        final TextView hello = contentView.findViewById(R.id.hello);
        hello.setText(Double.toString(frequency));
    }
}
