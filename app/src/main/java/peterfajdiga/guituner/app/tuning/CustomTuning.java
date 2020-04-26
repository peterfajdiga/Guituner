package peterfajdiga.guituner.app.tuning;

import androidx.annotation.NonNull;

public class CustomTuning extends Tuning {
    public CustomTuning() {
        this("");
    }

    public CustomTuning(@NonNull final String tonesString) {
        // TODO: localize "Custom"
        super("Custom", tonesString);
    }
}
