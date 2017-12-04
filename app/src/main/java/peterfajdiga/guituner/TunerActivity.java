package peterfajdiga.guituner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import peterfajdiga.guituner.fourier.PitchDetector;
import peterfajdiga.guituner.fourier.Recorder;

public class TunerActivity extends AppCompatActivity implements PitchDetector.Owner {

    private FrequencySetterRunnable frequencySetterRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuner);

        final PitchDetector pitchDetector = new PitchDetector(this);
        pitchDetector.startThread();
        final Recorder recorder = new Recorder(pitchDetector);
        recorder.startThread();

        frequencySetterRunnable = new FrequencySetterRunnable(findViewById(android.R.id.content));
    }

    @Override
    public void putDouble(final double value) {
        final View contentView = findViewById(android.R.id.content);
        contentView.post(frequencySetterRunnable.setFrequency(value));
    }
}
