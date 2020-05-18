package peterfajdiga.guituner.app.parts;

import android.content.Context;

import androidx.annotation.NonNull;

import peterfajdiga.guituner.R;
import peterfajdiga.guituner.app.tuning.TonesString;
import peterfajdiga.guituner.app.tuning.TuningValidator;
import peterfajdiga.guituner.gui.InputDialog;

class CustomTuningDialog {
    private final Context context;
    private final ShortcutTonesPreferenceDialog shortcutTonesPreferenceDialog;
    private final String customTuningLocalizedString;

    CustomTuningDialog(@NonNull final Context context, @NonNull ShortcutTonesPreferenceDialog shortcutTonesPreferenceDialog) {
        this.context = context;
        this.shortcutTonesPreferenceDialog = shortcutTonesPreferenceDialog;
        customTuningLocalizedString = context.getResources().getString(R.string.tuning_custom);
    }

    void showCustomTuningDialog(@NonNull final String customTuning) {
        InputDialog.show(
                context,
                customTuningLocalizedString,
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
