package peterfajdiga.guituner.app.tuning;

import android.content.Context;
import android.content.res.Resources;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import peterfajdiga.guituner.R;
import peterfajdiga.guituner.gui.views.ItemedRadioGroup;

public class Tuning implements ItemedRadioGroup.Item {
    public final String name;
    public final String tonesString;

    private final int secondaryColor;
    private final int secondarySize;

    public Tuning(@NonNull final String name, @NonNull final String tonesString, @NonNull final Context context) {
        this.name = name;
        this.tonesString = tonesString;

        this.secondaryColor = ContextCompat.getColor(context, R.color.secondary_text);
        this.secondarySize = context.getResources().getDimensionPixelSize(R.dimen.secondary_text);
    }

    @Override
    public CharSequence getButtonText() {
        final SpannableString str = new SpannableString(name + "\n" + tonesString);
        str.setSpan(
                new ForegroundColorSpan(secondaryColor),
                name.length() + 1,
                str.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        );
        str.setSpan(
                new AbsoluteSizeSpan(this.secondarySize),
                name.length() + 1,
                str.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        );
        return str;
    }

    @NonNull
    public static Tuning[] getBuiltInTunings(@NonNull final Context context) {
        final Resources resources = context.getResources();
        return new Tuning[] {
                new Tuning(resources.getString(R.string.tuning_standard), "E2 A2 D3 G3 B3 E4", context),
                new Tuning(resources.getString(R.string.tuning_drop_d), "D2 A2 D3 G3 B3 E4", context),
        };
    }
}
