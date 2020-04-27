package peterfajdiga.guituner.app.tuning;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import peterfajdiga.guituner.R;
import peterfajdiga.guituner.app.Preferences;
import peterfajdiga.guituner.general.Tone;
import peterfajdiga.guituner.gui.InputDialog;
import peterfajdiga.guituner.gui.views.ItemedRadioGroup;
import peterfajdiga.guituner.gui.views.PitchView;
import peterfajdiga.guituner.gui.views.ToneShortcutsBar;

public class TuningGui {
    private final Context context;
    private final Preferences preferences;
    private final LayoutInflater layoutInflater;
    private final PitchView pitchView;
    private final ToneShortcutsBar toneShortcutsBar;

    private Dialog shortcutTonesPreferenceDialog;
    private String customTuning;

    public TuningGui(@NonNull final Context context,
              @NonNull final Preferences preferences,
              @NonNull final LayoutInflater layoutInflater,
              @NonNull final PitchView pitchView,
              @NonNull ToneShortcutsBar toneShortcutsBar) {
        this.context = context;
        this.preferences = preferences;
        this.layoutInflater = layoutInflater;
        this.pitchView = pitchView;
        this.toneShortcutsBar = toneShortcutsBar;
        initialize();
    }

    private void initialize() {
        setupToneShortcuts();
    }

    private void showShortcutTonesPreferenceDialog() {
        if (shortcutTonesPreferenceDialog == null) {
            shortcutTonesPreferenceDialog = ShortcutTonesPreferenceDialog.create(context, preferences, new ItemedRadioGroup.Receiver<Tuning>() {
                @Override
                public void onCheckedChanged(final Tuning item) {
                    if (item instanceof CustomTuning) {
                        // TODO
                        return;
                    }
                    preferences.saveShortcutTones(item.tonesString);
                    updateToneShortcuts();
                }

                @Override
                public void onClick(final Tuning item) {
                    assert item instanceof CustomTuning;
                    showCustomTuningDialog();
                }
            });
        }
        shortcutTonesPreferenceDialog.show();
    }

    private void showCustomTuningDialog() {
        // TODO: localize "Custom tuning"
        InputDialog.show(context, "Custom tuning", "G1 D2 A2 E3 B3 F4#", new InputDialog.OnConfirmListener() {
            @Override
            public void onConfirm(final CharSequence input) {
                customTuning = input.toString().toUpperCase();
                // TODO: refresh bottom sheet
            }
        }, new TuningValidator());
    }

    private void setupToneShortcuts() {
        updateToneShortcuts();
        toneShortcutsBar.setReceiver(new ToneShortcutsBar.Receiver() {
            @Override
            public void OnToneClick(final Tone tone) {
                pitchView.focusOn(tone);
            }

            @Override
            public boolean OnToneLongClick(final Tone tone) {
                showShortcutTonesPreferenceDialog();
                return true;
            }
        });
    }

    private void updateToneShortcuts() {
        toneShortcutsBar.setupTones(preferences.getShortcutTones(), layoutInflater, R.layout.button_tone_shortcut);
    }
}
