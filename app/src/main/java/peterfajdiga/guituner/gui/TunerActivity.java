package peterfajdiga.guituner.gui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import peterfajdiga.guituner.R;
import peterfajdiga.guituner.pitch.PitchDetector;
import peterfajdiga.guituner.pitch.Recorder;

public class TunerActivity extends AppCompatActivity implements PitchDetector.Receiver, PitchView.OnFocusChangedListener {

    private FrequencySetterRunnable frequencySetterRunnable;
    private SoundOnClickListener soundOnClickListener = new SoundOnClickListener();

    PitchDetector pitchDetector;
    Recorder recorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuner);

        frequencySetterRunnable = new FrequencySetterRunnable(findViewById(android.R.id.content));

        final View soundButton = findViewById(R.id.soundbtn);
        soundButton.setSoundEffectsEnabled(false);
        soundButton.setOnClickListener(soundOnClickListener);
    }

    @Override
    public void updatePitch(final double frequency) {
        final View contentView = findViewById(android.R.id.content);
        contentView.post(frequencySetterRunnable.setFrequency(frequency));
    }

    @Override
    public void onPause() {
        super.onPause();
        pitchDetector.stopThread();
        recorder.stopThread();
    }

    @Override
    public void onResume() {
        super.onResume();

        pitchDetector = new PitchDetector(this);
        pitchDetector.startThread();
        recorder = new Recorder(pitchDetector);
        recorder.startThread();

        final PitchView pitchView = findViewById(R.id.pitchview);
        pitchView.setOnFocusChangedListener(this);
    }

    @Override
    public void onFocusChanged(double focusedFrequency) {
        soundOnClickListener.setFrequency(focusedFrequency);
        pitchDetector.onFocusChanged(focusedFrequency);
    }
}
