package peterfajdiga.guituner.app;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import peterfajdiga.guituner.R;
import peterfajdiga.guituner.general.Tone;
import peterfajdiga.guituner.gui.AlphaVisibility;
import peterfajdiga.guituner.gui.views.PitchView;
import peterfajdiga.guituner.gui.RippleVisibility;
import peterfajdiga.guituner.gui.views.ItemedRadioGroup;
import peterfajdiga.guituner.gui.views.ToneShortcutsBar;
import peterfajdiga.guituner.pitchdetection.PitchDetector;
import peterfajdiga.guituner.pitchdetection.PitchDetectorHarmony;
import peterfajdiga.guituner.pitchdetection.PitchDetectorThread;
import peterfajdiga.guituner.recording.Recorder;

public class TunerActivity extends AppCompatActivity {
    private Preferences preferences;
    private FrequencySetterRunnable frequencySetterRunnable;
    private final SoundOnClickListener soundOnClickListener = new SoundOnClickListener();
    private static final int MIC_PERMISSION_REQUEST = 1001;
    private static final int SAMPLE_RATE = 4000;

    private boolean initialized = false;
    PitchDetectorThread pitchDetectorThread;
    final PitchDetector pitchDetector = new PitchDetectorHarmony(SAMPLE_RATE);
    Recorder recorder;

    private String customTuning;

    private void initialize() {
        setContentView(R.layout.activity_tuner);

        preferences = new Preferences(getPreferences(Context.MODE_PRIVATE));
        frequencySetterRunnable = new FrequencySetterRunnable(findViewById(android.R.id.content));

        final PitchView pitchView = findViewById(R.id.pitchview);
        pitchView.setHighestFrequency(SAMPLE_RATE / 2.0);

        setupSelectionButtons();
        setupToneShortcuts(pitchView);

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

    private void setupToneShortcuts(@NonNull final PitchView targetPitchView) {
        final ToneShortcutsBar toneShortcutsBar = findViewById(R.id.shortcutcontainer);
        updateToneShortcuts(toneShortcutsBar);
        toneShortcutsBar.setReceiver(new ToneShortcutsBar.Receiver() {
            @Override
            public void OnToneClick(final Tone tone) {
                targetPitchView.focusOn(tone);
            }

            @Override
            public boolean OnToneLongClick(final Tone tone) {
                showShortcutTonesPreferenceDialog();
                return true;
            }
        });
    }

    private void updateToneShortcuts(@NonNull final ToneShortcutsBar toneShortcutsBar) {
        toneShortcutsBar.setupTones(preferences.getShortcutTones(), getLayoutInflater(), R.layout.button_tone_shortcut);
    }

    private void showShortcutTonesPreferenceDialog() {
        final String selectedTonesString = preferences.getShortcutTonesString();

        final ItemedRadioGroup<Tuning> container = new ItemedRadioGroup<>(this);
        for (final Tuning tuning : Tuning.tunings) {
            container.addItem(tuning, tuning.tonesString.equals(selectedTonesString));
        }
        container.addItem(new CustomTuning(), false);

        final ToneShortcutsBar toneShortcutsBar = findViewById(R.id.shortcutcontainer);
        container.setReceiver(new ItemedRadioGroup.Receiver<Tuning>() {
            @Override
            public void onCheckedChanged(final Tuning item) {
                if (item instanceof CustomTuning) {
                    showCustomTuningDialog();
                    return;
                }
                preferences.saveShortcutTones(item.tonesString);
                updateToneShortcuts(toneShortcutsBar);
            }
        });

        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(container);
        dialog.show();
    }

    private void showCustomTuningDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Custom tuning"); // TODO: localize

        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        dialogBuilder.setView(editText);

        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                customTuning = editText.getText().toString();
                // TODO: refresh bottom sheet
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                dialog.cancel();
            }
        });

        dialogBuilder.show();
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

        pitchDetectorThread = new PitchDetectorThread(new PitchDetectorThread.Receiver() {
            @Override
            public void updatePitch(final double frequency) {
                final View contentView = findViewById(android.R.id.content);
                contentView.post(frequencySetterRunnable.setFrequency(frequency));
            }
        }, pitchDetector);
        pitchDetectorThread.startThread();
        recorder = new Recorder(pitchDetectorThread, SAMPLE_RATE);
        recorder.startThread();

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

                final ToneShortcutsBar toneShortcutsBar = findViewById(R.id.shortcutcontainer);
                toneShortcutsBar.removeHighlight();
            }
        });
    }

    private boolean checkMicPermission() {
        switch (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
            case PackageManager.PERMISSION_GRANTED: return true;
            case PackageManager.PERMISSION_DENIED: return false;
            default: throw new RuntimeException("Microphone permission check failed");
        }
    }
}
