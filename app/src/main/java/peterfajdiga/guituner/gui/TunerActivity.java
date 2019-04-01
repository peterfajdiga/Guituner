package peterfajdiga.guituner.gui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import peterfajdiga.guituner.R;
import peterfajdiga.guituner.general.Tone;
import peterfajdiga.guituner.pitchdetection.PitchDetector;
import peterfajdiga.guituner.pitchdetection.PitchDetectorHarmony;
import peterfajdiga.guituner.pitchdetection.PitchDetectorThread;
import peterfajdiga.guituner.recording.Recorder;

public class TunerActivity extends AppCompatActivity implements PitchDetectorThread.Receiver, PitchView.OnFocusChangedListener {

    private FrequencySetterRunnable frequencySetterRunnable;
    private SoundOnClickListener soundOnClickListener = new SoundOnClickListener();
    private static final int MIC_PERMISSION_REQUEST = 1001;
    private static final int SAMPLE_RATE = 4000;

    private boolean initialized = false;
    PitchDetectorThread pitchDetectorThread;
    final PitchDetector pitchDetector = new PitchDetectorHarmony(SAMPLE_RATE);
    Recorder recorder;

    private void initialize() {
        setContentView(R.layout.activity_tuner);

        frequencySetterRunnable = new FrequencySetterRunnable(findViewById(android.R.id.content));

        final PitchView pitchView = findViewById(R.id.pitchview);
        pitchView.setHighestFrequency(SAMPLE_RATE / 2.0);

        setupSelectionButtons();
        setupToneShortcutButtons(pitchView);

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
            public void onClick(final View view) {
                pitchView.removeFocus();
            }
        });
    }

    private static final Tone[] shortcutTones = new Tone[]{
            Tone.E2,
            Tone.A2,
            Tone.D3,
            Tone.G3,
            Tone.B3,
            Tone.E4
    };

    private void setupToneShortcutButton(final Button button, final Tone tone, final PitchView targetPitchView) {
        button.setText(tone.name);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                targetPitchView.setFocus(tone);
            }
        });
    }

    private void setupToneShortcutButtons(final PitchView targetPitchView) {
        final ViewGroup shortcutContainer = findViewById(R.id.shortcutcontainer);
        shortcutContainer.removeAllViews();
        for (final Tone shortcutTone : shortcutTones) {
            final Button button = (Button)(getLayoutInflater().inflate(R.layout.button_pitchview, null));
            button.setLayoutParams(new LinearLayout.LayoutParams(
                    (int)getResources().getDimension(R.dimen.button_pitchview_width),
                    (int)getResources().getDimension(R.dimen.selection_bar_height)
            ));
            setupToneShortcutButton(button, shortcutTone, targetPitchView);
            shortcutContainer.addView(button);
        }
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
        pitchDetectorThread.stopThread();
        recorder.stopThread();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!initialized) {
            return;
        }

        pitchDetectorThread = new PitchDetectorThread(this, pitchDetector);
        pitchDetectorThread.startThread();
        recorder = new Recorder(pitchDetectorThread, SAMPLE_RATE);
        recorder.startThread();

        final PitchView pitchView = findViewById(R.id.pitchview);
        pitchView.setOnFocusChangedListener(this);
    }

    @Override
    public void onFocusChanged(final double focusedFrequency) {
        if (!initialized) {
            return;
        }
        findViewById(R.id.selectionbg).setVisibility(View.VISIBLE);
        soundOnClickListener.setFrequency(focusedFrequency);
        pitchDetector.setFocusedFrequency(focusedFrequency);
    }

    @Override
    public void onFocusRemoved() {
        if (!initialized) {
            return;
        }
        findViewById(R.id.selectionbg).setVisibility(View.INVISIBLE);
        pitchDetector.removeFocusedFrequency();
    }

    private boolean checkMicPermission() {
        switch (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
            case PackageManager.PERMISSION_GRANTED: return true;
            case PackageManager.PERMISSION_DENIED: return false;
            default: throw new RuntimeException("Microphone permission check failed");
        }
    }
}
