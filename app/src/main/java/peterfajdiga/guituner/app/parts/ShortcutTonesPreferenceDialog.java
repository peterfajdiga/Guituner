package peterfajdiga.guituner.app.parts;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import peterfajdiga.guituner.R;
import peterfajdiga.guituner.app.Preferences;
import peterfajdiga.guituner.app.tuning.CustomTuning;
import peterfajdiga.guituner.app.tuning.TonesString;
import peterfajdiga.guituner.app.tuning.Tuning;
import peterfajdiga.guituner.gui.views.ItemedRadioGroup;

public class ShortcutTonesPreferenceDialog {
    private final Context context;
    private final Preferences preferences;
    private final ToneShortcuts toneShortcuts;
    private final CustomTuningDialog customTuningDialog;

    private Dialog dialog = null;

    public ShortcutTonesPreferenceDialog(
            @NonNull final Context context,
            @NonNull final Preferences preferences,
            @NonNull final ToneShortcuts toneShortcuts) {
        this.context = context;
        this.preferences = preferences;
        this.toneShortcuts = toneShortcuts;
        this.customTuningDialog = new CustomTuningDialog(context, this);
    }

    public void show() {
        if (dialog == null) {
            dialog = create();
        }
        dialog.show();
    }

    void setShortcutTones(@NonNull final String shortcutTonesString) {
        preferences.saveShortcutTones(shortcutTonesString);
        toneShortcuts.updateToneShortcuts();
    }

    void setCustomTuning(@NonNull final String customTuning) {
        if (!TonesString.validateTonesString(customTuning)) {
            return;
        }
        if (isCustomTuningSelected()) {
            setShortcutTones(customTuning);
        }
        preferences.saveShortcutTonesCustom(customTuning);
        updateDialog();
    }

    private boolean isCustomTuningSelected() {
        final String selectedTonesString = preferences.getShortcutTonesString();
        final String customTuningString = preferences.getShortcutTonesCustomString();
        return selectedTonesString.equals(customTuningString);
    }

    private void updateDialog() {
        assert dialog != null;
        dialog.dismiss();
        dialog = null;  // recreate the dialog next time it's shown
        // TODO: only hide the dialog if `isCustomTuningSelected()`
    }

    private View createContentView() {
        final String selectedTonesString = preferences.getShortcutTonesString();
        final String customTuningString = preferences.getShortcutTonesCustomString();

        final ItemedRadioGroup.Receiver<Tuning> radioGroupReceiver = new ItemedRadioGroup.Receiver<Tuning>() {
            @Override
            public void onCheckedChanged(final Tuning item) {
                setShortcutTones(item.tonesString);
            }

            @Override
            public void onClick(final Tuning item) {
                assert item instanceof CustomTuning;
                customTuningDialog.showCustomTuningDialog(customTuningString);
            }
        };



        final ItemedRadioGroup<Tuning> container = getRadioGroup();
        for (final Tuning tuning : Tuning.getBuiltInTunings(context.getResources())) {
            container.addItem(tuning, R.layout.radio_button_tuning, tuning.tonesString.equals(selectedTonesString), false);
        }
        final String customTuningLocalizedString = context.getResources().getString(R.string.tuning_custom);
        container.addItem(new CustomTuning(customTuningLocalizedString, customTuningString), R.layout.radio_button_tuning, customTuningString.equals(selectedTonesString), true);

        container.setReceiver(radioGroupReceiver);
        return container;
    }

    private Dialog create() {
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setContentView(createContentView());
        return dialog;
    }

    private ItemedRadioGroup<Tuning> getRadioGroup() {
        final LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return (ItemedRadioGroup<Tuning>) layoutInflater.inflate(R.layout.radio_group_tuning, null, false);
    }
}
