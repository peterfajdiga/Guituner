package peterfajdiga.guituner.app.tuning;

import androidx.annotation.NonNull;

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

    public static final Tuning[] tunings = new Tuning[] {
            // TODO: localize names
            new Tuning("Standard", "E2 A2 D3 G3 B3 E4"),
            new Tuning("Drop D", "D2 A2 D3 G3 B3 E4"),
    };
}
