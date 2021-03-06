package peterfajdiga.guituner.app;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import peterfajdiga.guituner.R;
import peterfajdiga.guituner.app.parts.TuningPreferenceDialog;
import peterfajdiga.guituner.app.parts.PitchShortcuts;
import peterfajdiga.guituner.general.Pitch;
import peterfajdiga.guituner.gui.AlphaVisibility;
import peterfajdiga.guituner.gui.RippleVisibility;
import peterfajdiga.guituner.gui.views.PitchView;
import peterfajdiga.guituner.gui.views.PitchShortcutsBar;
import peterfajdiga.guituner.pitchdetection.PitchDetector;
import peterfajdiga.guituner.pitchdetection.PitchDetectorHarmony;
import peterfajdiga.guituner.pitchdetection.PitchDetectorThread;
import peterfajdiga.guituner.recording.Recorder;

public class TunerActivity extends AppCompatActivity {
    private Preferences preferences;
    private FrequencySetterRunnable frequencySetterRunnable;
    private final SoundOnClickListener soundOnClickListener = new SoundOnClickListener();
    private static final int MIC_PERMISSION_REQUEST = 1001;

    private boolean initialized = false;
    private int sampleRate;
    private PitchDetectorThread pitchDetectorThread;
    private PitchDetector pitchDetector;
    private Recorder recorder;

    private void initialize() {
        sampleRate = Recorder.getLowestSupportedSampleRate();
        pitchDetector = new PitchDetectorHarmony(sampleRate);

        preferences = new Preferences(getPreferences(Context.MODE_PRIVATE));
        frequencySetterRunnable = new FrequencySetterRunnable(findViewById(android.R.id.content));

        setContentView(R.layout.activity_tuner);

        final PitchView pitchView = findViewById(R.id.pitchview);
        pitchView.setPitches(Pitch.getPitchesUpTo(sampleRate / 2.0));

        setupSelectionButtons();
        initPitchShortcuts();

        initialized = true;
    }

    private void initPitchShortcuts() {
        final PitchShortcuts pitchShortcuts = new PitchShortcuts(
            preferences,
            getLayoutInflater(),
            (PitchView)findViewById(R.id.pitchview),
            (PitchShortcutsBar)findViewById(R.id.shortcutcontainer)
        );
        pitchShortcuts.initialize();

        final TuningPreferenceDialog tuningPreferenceDialog = new TuningPreferenceDialog(this, preferences,
            pitchShortcuts
        );
        final View tuningSetupButton = findViewById(R.id.tuning_setup_button);
        tuningSetupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                tuningPreferenceDialog.show();
            }
        });
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
                    case PackageManager.PERMISSION_GRANTED:
                        initialize();
                        break;
                    case PackageManager.PERMISSION_DENIED:
                        finishWithoutMicPermission();
                        break;
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
    public void onPause() {
        super.onPause();
        if (!initialized) {
            return;
        }
        pitchDetectorThread.signalStop();
        recorder.signalStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!initialized) {
            return;
        }

        pitchDetectorThread = new PitchDetectorThread(new PitchDetectorThread.Receiver() {
            @Override
            public void updatePitch(final double frequency) {
                final View contentView = findViewById(android.R.id.content);
                contentView.post(frequencySetterRunnable.setFrequency(frequency));
            }
        }, pitchDetector);
        pitchDetectorThread.start();
        recorder = new Recorder(pitchDetectorThread, sampleRate);
        recorder.start();

        final PitchView pitchView = findViewById(R.id.pitchview);
        pitchView.setOnFocusChangedListener(new PitchView.OnFocusChangedListener() {
            @Override
            public void onFocusChanged(final double focusedFrequency) {
                if (!initialized) {
                    return;
                }
                AlphaVisibility.showView(findViewById(R.id.selectionbg));
                soundOnClickListener.setFrequency(focusedFrequency);
                pitchDetector.setFocusedFrequency(focusedFrequency);
            }

            @Override
            public void onFocusChanged(final double focusedFrequency, final float clickX, final float clickY) {
                if (!initialized) {
                    return;
                }
                final View selectionBg = findViewById(R.id.selectionbg);
                final int centerX = Math.round(clickX - selectionBg.getX());
                final int centerY = Math.round(clickY - selectionBg.getY());
                RippleVisibility.showViewWithRipple(selectionBg, centerX, centerY);
                soundOnClickListener.setFrequency(focusedFrequency);
                pitchDetector.setFocusedFrequency(focusedFrequency);
            }

            @Override
            public void onFocusRemoved() {
                if (!initialized) {
                    return;
                }
                AlphaVisibility.hideView(findViewById(R.id.selectionbg));
                pitchDetector.removeFocusedFrequency();

                final PitchShortcutsBar pitchShortcutsBar = findViewById(R.id.shortcutcontainer);
                pitchShortcutsBar.removeHighlight();
            }
        });
    }

    private boolean checkMicPermission() {
        switch (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
            case PackageManager.PERMISSION_GRANTED:
                return true;
            case PackageManager.PERMISSION_DENIED:
                return false;
            default:
                throw new RuntimeException("Microphone permission check failed");
        }
    }
}
