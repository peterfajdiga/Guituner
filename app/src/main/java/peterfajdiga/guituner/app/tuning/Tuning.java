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
                new Tuning(resources.getString(R.string.tuning_standard) + " - 1", "D2# G2# C3# F3# A3# D4#", style),
                new Tuning(resources.getString(R.string.tuning_standard) + " - 2", "D2 G2 C3 F3 A3 D4", style),
                new Tuning(resources.getString(R.string.tuning_standard) + " - 3", "C2# F2# B2 E3 G3# C4#", style),
                new Tuning(resources.getString(R.string.tuning_standard) + " - 4", "C2 F2 A2# D3# G3 C4", style),
                new Tuning(resources.getString(R.string.tuning_standard) + " - 5", "B1 E2 A2 D3 F3# B3", style),
                new Tuning(resources.getString(R.string.tuning_standard) + " + 1", "F2 A2# D3# G3# C4 F4", style),
                new Tuning(resources.getString(R.string.tuning_standard) + " + 2", "F2# B2 E3 A3 C4# F4#", style),
                new Tuning(resources.getString(R.string.tuning_standard) + " + 3", "G2 C3 F3 A3# D4 G4", style),
                new Tuning(resources.getString(R.string.tuning_standard) + " + 4", "G2# C3# F3# B3 D4# G4#", style),

                new Tuning(resources.getString(R.string.tuning_drop_d), "D2 A2 D3 G3 B3 E4", style),
                new Tuning(resources.getString(R.string.tuning_drop_d) + " - 1", "C2# G2# C3# F3# A3# D4#", style),
                new Tuning(resources.getString(R.string.tuning_drop_d) + " - 2", "C2 G2 C3 F3 A3 D4", style),
                new Tuning(resources.getString(R.string.tuning_drop_d) + " - 3", "B1 F2# B2 E3 G3# C4#", style),
                new Tuning(resources.getString(R.string.tuning_drop_d) + " - 4", "A1# F2 A2# D3# G3 C4", style),
                new Tuning(resources.getString(R.string.tuning_drop_d) + " - 5", "A1 E2 A2 D3 F3# B3", style),

                new Tuning(resources.getString(R.string.tuning_open_d), "D2 A2 D3 F3# A3 D4", style),
                new Tuning(resources.getString(R.string.tuning_open_c), "C2 G2 C3 G3 C4 E4", style),
                new Tuning(resources.getString(R.string.tuning_open_g), "D2 G2 D3 G3 B3 D4", style),
                new Tuning(resources.getString(R.string.tuning_open_a), "E2 A2 E3 A3 C4# E4", style),

                new Tuning(resources.getString(R.string.tuning_all_fourths), "E2 A2 D3 G3 C4 F4", style),
                new Tuning(resources.getString(R.string.tuning_major_thirds), "G2# C3 E3 G3# C4 E4", style),
                new Tuning(resources.getString(R.string.tuning_all_fifths), "C2 G2 D3 A3 E4 B4", style),

                new Tuning(resources.getString(R.string.tuning_violin_standard), "G3 D4 A4 E4", style),
                new Tuning(resources.getString(R.string.tuning_viola_standard), "C3 G3 D4 A4", style),
                new Tuning(resources.getString(R.string.tuning_ukulele_soprano), "G4 C4 E4 A4", style),
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
