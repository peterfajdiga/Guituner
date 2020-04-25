package peterfajdiga.guituner.app;

import androidx.annotation.NonNull;

class CustomTuning extends Tuning {
    CustomTuning() {
        this("");
    }

    CustomTuning(@NonNull final String tonesString) {
        // TODO: localize "Custom"
        super("Custom", tonesString);
    }
}
