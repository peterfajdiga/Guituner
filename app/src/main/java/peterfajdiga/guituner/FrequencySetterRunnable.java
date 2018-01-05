package peterfajdiga.guituner;

import android.view.View;
import android.widget.TextView;

public class FrequencySetterRunnable implements Runnable {

    private double frequency_min;
    private double frequency_max;
    private final View contentView;

    public FrequencySetterRunnable(final View contentView) {
        this.contentView = contentView;
    }

    public FrequencySetterRunnable setFrequency_min(final double frequency_min, final double frequency_max) {
        this.frequency_min = frequency_min;
        this.frequency_max = frequency_max;
        return this;
    }

    @Override
    public void run() {
        final TextView hello = contentView.findViewById(R.id.hello);
        hello.setText(String.format("%.0f - %.0f Hz", frequency_min, frequency_max));
    }
}
