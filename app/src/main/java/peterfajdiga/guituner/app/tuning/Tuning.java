package peterfajdiga.guituner.app.tuning;

import android.content.res.Resources;

import androidx.annotation.NonNull;

import peterfajdiga.guituner.R;
import peterfajdiga.guituner.gui.views.ItemedRadioGroup;

public class Tuning implements ItemedRadioGroup.Item {
    public final String name;
    public final String tonesString;

    public Tuning(@NonNull final String name, @NonNull final String tonesString) {
        this.name = name;
        this.tonesString = tonesString;
    }

    @Override
    public CharSequence getButtonText() {
        return name + "\n" + tonesString;
    }

    @NonNull
    public static Tuning[] getBuiltInTunings(@NonNull final Resources resources) {
        return new Tuning[] {
                new Tuning(resources.getString(R.string.tuning_standard), "E2 A2 D3 G3 B3 E4"),
                new Tuning(resources.getString(R.string.tuning_drop_d), "D2 A2 D3 G3 B3 E4"),
        };
    }
}
