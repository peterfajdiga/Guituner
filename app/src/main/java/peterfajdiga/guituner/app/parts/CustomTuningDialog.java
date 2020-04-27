package peterfajdiga.guituner.app.parts;

import android.content.Context;

import androidx.annotation.NonNull;

import peterfajdiga.guituner.app.tuning.TuningValidator;
import peterfajdiga.guituner.gui.InputDialog;

class CustomTuningDialog {
    private final Context context;
    private String customTuning;

    CustomTuningDialog(@NonNull final Context context) {
        this.context = context;
    }

    void showCustomTuningDialog() {
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
