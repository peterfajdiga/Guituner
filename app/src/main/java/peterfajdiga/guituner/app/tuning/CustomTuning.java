package peterfajdiga.guituner.app.tuning;

import androidx.annotation.NonNull;

public class CustomTuning extends Tuning {
    public CustomTuning(@NonNull final String name, @NonNull final Style style) {
        this(name, "", style);
    }

    public CustomTuning(@NonNull final String name, @NonNull final String pitchesString, @NonNull final Style style) {
        super(name, pitchesString, style);
    }
}
