package peterfajdiga.guituner.app.parts;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import peterfajdiga.guituner.app.Preferences;
import peterfajdiga.guituner.app.tuning.CustomTuning;
import peterfajdiga.guituner.app.tuning.Tuning;
import peterfajdiga.guituner.app.tuning.TuningValidator;
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
    private final ToneShortcuts toneShortcuts;
    private final ShortcutTonesPreferenceDialog shortcutTonesPreferenceDialog;
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
        this.shortcutTonesPreferenceDialog = new ShortcutTonesPreferenceDialog(context, preferences, new ItemedRadioGroup.Receiver<Tuning>() {
            @Override
            public void onCheckedChanged(final Tuning item) {
                if (item instanceof CustomTuning) {
                    // TODO
                    return;
                }
                preferences.saveShortcutTones(item.tonesString);
                toneShortcuts.updateToneShortcuts();
            }

            @Override
            public void onClick(final Tuning item) {
                assert item instanceof CustomTuning;
                showCustomTuningDialog();
            }
        });
        this.toneShortcuts = new ToneShortcuts(preferences, layoutInflater, pitchView, toneShortcutsBar, this.shortcutTonesPreferenceDialog);
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
}
