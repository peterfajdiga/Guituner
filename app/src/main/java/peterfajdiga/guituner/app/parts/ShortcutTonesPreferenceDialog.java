package peterfajdiga.guituner.app.parts;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import peterfajdiga.guituner.app.Preferences;
import peterfajdiga.guituner.app.tuning.CustomTuning;
import peterfajdiga.guituner.app.tuning.Tuning;
import peterfajdiga.guituner.gui.views.ItemedRadioGroup;

class ShortcutTonesPreferenceDialog {
    private final Context context;
    private final Preferences preferences;
    private final ItemedRadioGroup.Receiver<Tuning> radioGroupReceiver;

    private Dialog dialog = null;

    ShortcutTonesPreferenceDialog(
            @NonNull final Context context,
            @NonNull final Preferences preferences,
            @NonNull final ItemedRadioGroup.Receiver<Tuning> radioGroupReceiver) {
        this.context = context;
        this.preferences = preferences;
        this.radioGroupReceiver = radioGroupReceiver;
    }

    void show() {
        if (dialog == null) {
            dialog = create(context, preferences, radioGroupReceiver);
        }
        dialog.show();
    }

    static Dialog create(@NonNull final Context context, @NonNull final Preferences preferences, @NonNull final ItemedRadioGroup.Receiver<Tuning> radioGroupReceiver) {
        final String selectedTonesString = preferences.getShortcutTonesString();

        final ItemedRadioGroup<Tuning> container = new ItemedRadioGroup<>(context);
        for (final Tuning tuning : Tuning.tunings) {
            container.addItem(tuning, tuning.tonesString.equals(selectedTonesString), false);
        }
        container.addItem(new CustomTuning(), false, true);

        container.setReceiver(radioGroupReceiver);

        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setContentView(container);
        return dialog;
    }
}
