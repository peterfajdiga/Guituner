package peterfajdiga.guituner.app.parts;

import android.content.Context;

import androidx.annotation.NonNull;

import peterfajdiga.guituner.app.tuning.TonesString;
import peterfajdiga.guituner.app.tuning.TuningValidator;
import peterfajdiga.guituner.gui.InputDialog;

class CustomTuningDialog {
    private final Context context;
    private final ShortcutTonesPreferenceDialog shortcutTonesPreferenceDialog;

    CustomTuningDialog(@NonNull final Context context, @NonNull ShortcutTonesPreferenceDialog shortcutTonesPreferenceDialog) {
        this.context = context;
        this.shortcutTonesPreferenceDialog = shortcutTonesPreferenceDialog;
    }

    void showCustomTuningDialog(@NonNull final String customTuning) {
        // TODO: localize "Custom tuning" title
        InputDialog.show(
                context,
                "Custom tuning",
                "G1 D2 A2 E3 B3 F4#",
                customTuning,
                new InputDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm(final CharSequence input) {
                        final String inputTuning = input.toString().toUpperCase();
                        assert TonesString.validateTonesString(inputTuning);
                        shortcutTonesPreferenceDialog.setCustomTuning(inputTuning);
                    }
                }, new TuningValidator()
        );
    }
}
