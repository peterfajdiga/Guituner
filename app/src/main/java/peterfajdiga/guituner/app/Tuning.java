package peterfajdiga.guituner.app;

import androidx.annotation.NonNull;

import peterfajdiga.guituner.gui.ItemedRadioGroup;

class Tuning implements ItemedRadioGroup.Item {
    final String tonesString;

    Tuning(@NonNull final String tonesString) {
        this.tonesString = tonesString;
    }

    @Override
    public CharSequence getButtonText() {
        return tonesString;
    }

    static final Tuning[] tunings = new Tuning[] {
            new Tuning("E2,A2,D3,G3,B3,E4"),
            new Tuning("D2,A2,D3,G3,B3,E4"),
    };
}
