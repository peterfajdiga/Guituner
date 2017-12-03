package peterfajdiga.guituner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import peterfajdiga.guituner.fourier.DoubleReceiver;
import peterfajdiga.guituner.fourier.Recorder;
import peterfajdiga.guituner.fourier.Transformator;

public class TunerActivity extends AppCompatActivity implements DoubleReceiver {

    private FrequencySetterRunnable frequencySetterRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuner);

        final Transformator fourier = new Transformator(this);
        fourier.startTransforming();
        final Recorder recorder = new Recorder(fourier);
        recorder.startRecording();

        frequencySetterRunnable = new FrequencySetterRunnable(findViewById(android.R.id.content));
    }

    @Override
    public void putDouble(final double value) {
        final View contentView = findViewById(android.R.id.content);
        contentView.post(frequencySetterRunnable.setFrequency(value));
    }
}
