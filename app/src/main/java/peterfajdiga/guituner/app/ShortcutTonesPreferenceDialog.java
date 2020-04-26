package peterfajdiga.guituner.app;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import peterfajdiga.guituner.app.tuning.CustomTuning;
import peterfajdiga.guituner.app.tuning.Tuning;
import peterfajdiga.guituner.gui.views.ItemedRadioGroup;

class ShortcutTonesPreferenceDialog {
    private ShortcutTonesPreferenceDialog() {}  // make class static

    // TODO: build only once
    static void show(@NonNull final Context context, @NonNull final Preferences preferences, @NonNull final ItemedRadioGroup.Receiver<Tuning> radioGroupReceiver) {
        final String selectedTonesString = preferences.getShortcutTonesString();

        final ItemedRadioGroup<Tuning> container = new ItemedRadioGroup<>(context);
        for (final Tuning tuning : Tuning.tunings) {
            container.addItem(tuning, tuning.tonesString.equals(selectedTonesString), false);
        }
        container.addItem(new CustomTuning(), false, true);

        container.setReceiver(radioGroupReceiver);

        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setContentView(container);
        dialog.show();
    }
}
