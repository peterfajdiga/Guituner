package peterfajdiga.guituner.app.tuning;

import androidx.annotation.NonNull;

public class CustomTuning extends Tuning {
    public CustomTuning(@NonNull final String name) {
        this(name, "");
    }

    public CustomTuning(@NonNull final String name, @NonNull final String tonesString) {
        super(name, tonesString);
    }
}
