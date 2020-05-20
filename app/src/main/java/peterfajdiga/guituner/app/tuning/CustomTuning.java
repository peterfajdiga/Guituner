package peterfajdiga.guituner.app.tuning;

import android.content.Context;

import androidx.annotation.NonNull;

public class CustomTuning extends Tuning {
    public CustomTuning(@NonNull final String name, @NonNull final Context context) {
        this(name, "", context);
    }

    public CustomTuning(@NonNull final String name, @NonNull final String tonesString, @NonNull final Context context) {
        super(name, tonesString, context);
    }
}
