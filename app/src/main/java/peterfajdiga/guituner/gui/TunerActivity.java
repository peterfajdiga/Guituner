package peterfajdiga.guituner.gui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import peterfajdiga.guituner.R;
import peterfajdiga.guituner.pitchdetection.PitchDetector;
import peterfajdiga.guituner.pitchdetection.Recorder;

public class TunerActivity extends AppCompatActivity implements PitchDetector.Receiver, PitchView.OnFocusChangedListener {

    private FrequencySetterRunnable frequencySetterRunnable;
    private SoundOnClickListener soundOnClickListener = new SoundOnClickListener();

    PitchDetector pitchDetector;
    Recorder recorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!checkMicPermission()) {
            Toast.makeText(this, R.string.no_mic_permission, Toast.LENGTH_SHORT).show();
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuner);

        frequencySetterRunnable = new FrequencySetterRunnable(findViewById(android.R.id.content));

        final PitchView pitchView = findViewById(R.id.pitchview);
        pitchView.setHighestFrequency(Recorder.SAMPLE_RATE / 2.0);

        final View soundButton = findViewById(R.id.soundbtn);
        soundButton.setSoundEffectsEnabled(false);
        soundButton.setOnClickListener(soundOnClickListener);

        final View unpinButton = findViewById(R.id.unpinbtn);
        unpinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pitchView.removeFocus();
            }
        });
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
        final View selectionBackground = findViewById(R.id.selectionbg);
        selectionBackground.setVisibility(focusedFrequency == 0.0 ? View.INVISIBLE : View.VISIBLE);

        soundOnClickListener.setFrequency(focusedFrequency);
        pitchDetector.onFocusChanged(focusedFrequency);
    }

    private boolean checkMicPermission() {
        switch (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
            case PackageManager.PERMISSION_GRANTED: return true;
            case PackageManager.PERMISSION_DENIED: return false;
            default: throw new RuntimeException("Microphone permission check failed");
        }
    }
}
