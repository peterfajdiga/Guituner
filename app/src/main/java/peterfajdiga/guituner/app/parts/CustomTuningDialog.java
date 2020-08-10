package peterfajdiga.guituner.app.parts;

import android.content.Context;

import androidx.annotation.NonNull;

import peterfajdiga.guituner.R;
import peterfajdiga.guituner.app.tuning.PitchesString;
import peterfajdiga.guituner.app.tuning.TuningValidator;
import peterfajdiga.guituner.gui.InputDialog;

class CustomTuningDialog {
    private final Context context;
    private final TuningPreferenceDialog tuningPreferenceDialog;
    private final String customTuningLocalizedString;

    CustomTuningDialog(@NonNull final Context context, @NonNull TuningPreferenceDialog tuningPreferenceDialog) {
        this.context = context;
        this.tuningPreferenceDialog = tuningPreferenceDialog;
        customTuningLocalizedString = context.getResources().getString(R.string.tuning_custom);
    }

    void showCustomTuningDialog(@NonNull final String customTuning) {
        InputDialog.show(
            context, customTuningLocalizedString, "G1 D2 A2 E3 B3 F#4", customTuning, new InputDialog.OnConfirmListener() {
                @Override
                public void onConfirm(final CharSequence input) {
                    final String inputTuning = input.toString().toUpperCase();
                    assert PitchesString.validatePitchesString(inputTuning);
                    tuningPreferenceDialog.setCustomTuning(inputTuning);
                }
            }, new TuningValidator()
        );
    }
}
