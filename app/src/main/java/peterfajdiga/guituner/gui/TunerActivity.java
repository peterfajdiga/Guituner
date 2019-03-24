package peterfajdiga.guituner.gui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
    private static final int MIC_PERMISSION_REQUEST = 1001;

    private boolean initialized = false;
    PitchDetector pitchDetector;
    Recorder recorder;

    private void initialize() {
        setContentView(R.layout.activity_tuner);

        frequencySetterRunnable = new FrequencySetterRunnable(findViewById(android.R.id.content));

        final PitchView pitchView = findViewById(R.id.pitchview);
        pitchView.setHighestFrequency(Recorder.SAMPLE_RATE / 2.0);

        setupSelectionButtons();

        initialized = true;
    }

    private void setupSelectionButtons() {
        final PitchView pitchView = findViewById(R.id.pitchview);

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

    private void finishWithoutMicPermission() {
        Toast.makeText(this, R.string.no_mic_permission, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        if (requestCode != MIC_PERMISSION_REQUEST) {
            // unknown request code
            return;
        }

        for (int i = 0; i < permissions.length; i++) {
            if (permissions[i].equals(Manifest.permission.RECORD_AUDIO)) {
                final int micGrantResult = grantResults[i];
                switch (micGrantResult) {
                    case PackageManager.PERMISSION_GRANTED: initialize(); break;
                    case PackageManager.PERMISSION_DENIED: finishWithoutMicPermission(); break;
                }
                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.err.println("on create");
        super.onCreate(savedInstanceState);
        if (checkMicPermission()) {
            initialize();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MIC_PERMISSION_REQUEST);
        }
    }

    @Override
    public void updatePitch(final double frequency) {
        final View contentView = findViewById(android.R.id.content);
        contentView.post(frequencySetterRunnable.setFrequency(frequency));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!initialized) {
            return;
        }
        pitchDetector.stopThread();
        recorder.stopThread();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!initialized) {
            return;
        }

        pitchDetector = new PitchDetector(this);
        pitchDetector.startThread();
        recorder = new Recorder(pitchDetector);
        recorder.startThread();

        final PitchView pitchView = findViewById(R.id.pitchview);
        pitchView.setOnFocusChangedListener(this);
    }

    @Override
    public void onFocusChanged(double focusedFrequency) {
        if (!initialized) {  // probably unnecessary
            return;
        }
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
