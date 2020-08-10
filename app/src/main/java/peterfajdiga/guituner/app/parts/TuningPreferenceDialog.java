package peterfajdiga.guituner.app.parts;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import peterfajdiga.guituner.R;
import peterfajdiga.guituner.app.Preferences;
import peterfajdiga.guituner.app.tuning.CustomTuning;
import peterfajdiga.guituner.app.tuning.PitchesString;
import peterfajdiga.guituner.app.tuning.Tuning;
import peterfajdiga.guituner.gui.views.ItemedRadioGroup;

public class TuningPreferenceDialog {
    private final Context context;
    private final Preferences preferences;
    private final PitchShortcuts pitchShortcuts;
    private final CustomTuningDialog customTuningDialog;

    private Dialog dialog = null;

    public TuningPreferenceDialog(
        @NonNull final Context context,
        @NonNull final Preferences preferences,
        @NonNull final PitchShortcuts pitchShortcuts
    ) {
        this.context = context;
        this.preferences = preferences;
        this.pitchShortcuts = pitchShortcuts;
        this.customTuningDialog = new CustomTuningDialog(context, this);
    }

    public void show() {
        if (dialog == null) {
            dialog = create();
        }
        dialog.show();
    }

    void setTuning(@NonNull final String tuningString) {
        preferences.saveTuning(tuningString);
        pitchShortcuts.updatePitchShortcuts();
    }

    void setCustomTuning(@NonNull final String customTuning) {
        if (!PitchesString.validatePitchesString(customTuning)) {
            return;
        }
        if (isCustomTuningSelected()) {
            setTuning(customTuning);
        }
        preferences.saveCustomTuning(customTuning);
        updateDialog();
    }

    private boolean isCustomTuningSelected() {
        final String selectedPitchesString = preferences.getTuningString();
        final String customTuningString = preferences.getCustomTuningString();
        return selectedPitchesString.equals(customTuningString);
    }

    private void updateDialog() {
        assert dialog != null;
        dialog.dismiss();
        dialog = null;  // recreate the dialog next time it's shown
        // TODO: only hide the dialog if `isCustomTuningSelected()`
    }

    private View createContentView() {
        final NestedScrollView container = new NestedScrollView(context);
        container.addView(createRadioGroup());
        return container;
    }

    private View createRadioGroup() {
        final String selectedPitchesString = preferences.getTuningString();
        final String customTuningString = preferences.getCustomTuningString();

        final ItemedRadioGroup.Receiver<Tuning> radioGroupReceiver = new ItemedRadioGroup.Receiver<Tuning>() {
            @Override
            public void onCheckedChanged(final Tuning item) {
                setTuning(item.pitchesString);
            }

            @Override
            public void onClick(final Tuning item) {
                assert item instanceof CustomTuning;
                customTuningDialog.showCustomTuningDialog(customTuningString);
            }
        };

        final Tuning.Style radioButtonStyle = new Tuning.Style(
            ContextCompat.getColor(context, R.color.secondary_text),
            context.getResources().getDimensionPixelSize(R.dimen.secondary_text)
        );

        final ItemedRadioGroup<Tuning> container = getRadioGroup();
        for (final Tuning tuning : Tuning.getBuiltInTunings(context.getResources(), radioButtonStyle)) {
            container.addItem(
                tuning,
                R.layout.radio_button_tuning,
                tuning.pitchesString.equals(selectedPitchesString),
                false
            );
        }

        final String customTuningLocalizedString = context.getResources().getString(R.string.tuning_custom);
        container.addItem(
            new CustomTuning(customTuningLocalizedString, customTuningString, radioButtonStyle),
            R.layout.radio_button_tuning,
            customTuningString.equals(selectedPitchesString),
            true
        );

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
        return (ItemedRadioGroup<Tuning>)layoutInflater.inflate(R.layout.radio_group_tuning, null, false);
    }
}
