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
}
