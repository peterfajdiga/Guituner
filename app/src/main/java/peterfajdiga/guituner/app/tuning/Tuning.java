package peterfajdiga.guituner.app.tuning;

import android.content.Context;
import android.content.res.Resources;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

import androidx.annotation.NonNull;

import peterfajdiga.guituner.R;
import peterfajdiga.guituner.gui.views.ItemedRadioGroup;

public class Tuning implements ItemedRadioGroup.Item {
    public final String name;
    public final String tonesString;

    private final Style style;

    public Tuning(@NonNull final String name, @NonNull final String tonesString, @NonNull final Style style) {
        this.name = name;
        this.tonesString = tonesString;
        this.style = style;
    }

    @Override
    public CharSequence getButtonText() {
        final SpannableString str = new SpannableString(name + "\n" + tonesString);
        str.setSpan(
                new ForegroundColorSpan(style.secondaryColor),
                name.length() + 1,
                str.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        );
        str.setSpan(
                new AbsoluteSizeSpan(style.secondarySize),
                name.length() + 1,
                str.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        );
        return str;
    }

    @NonNull
    public static Tuning[] getBuiltInTunings(
            @NonNull final Resources resources,
            @NonNull final Style style
    ) {
        return new Tuning[] {
                new Tuning(resources.getString(R.string.tuning_standard), "E2 A2 D3 G3 B3 E4", style),
                new Tuning(resources.getString(R.string.tuning_drop_d), "D2 A2 D3 G3 B3 E4", style),
        };
    }

    public static class Style {
        public final int secondaryColor;
        public final int secondarySize;

        public Style(final int secondaryColor, final int secondarySize) {
            this.secondaryColor = secondaryColor;
            this.secondarySize = secondarySize;
        }
    }
}
