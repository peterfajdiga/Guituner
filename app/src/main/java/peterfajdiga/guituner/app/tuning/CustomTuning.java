package peterfajdiga.guituner.app.tuning;

import android.content.Context;

import androidx.annotation.NonNull;

public class CustomTuning extends Tuning {
    public CustomTuning(@NonNull final String name, @NonNull final Style style) {
        this(name, "", style);
    }

    public CustomTuning(@NonNull final String name, @NonNull final String tonesString, @NonNull final Style style) {
        super(name, tonesString, style);
    }
}
