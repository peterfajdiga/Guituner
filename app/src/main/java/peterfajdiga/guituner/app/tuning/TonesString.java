package peterfajdiga.guituner.app.tuning;

import androidx.annotation.NonNull;

import peterfajdiga.guituner.general.Tone;

public class TonesString {
    private TonesString() {}  // make class static

    @NonNull
    public static Tone[] parseTonesString(@NonNull final String tonesString) throws NumberFormatException {
        final String[] toneStrings = tonesString.split(" ");
        final int n = toneStrings.length;
        final Tone[] tones = new Tone[n];
        for (int i = 0; i < n; i++) {
            tones[i] = Tone.fromString(toneStrings[i]);
        }
        return tones;
    }

    public static boolean validateTonesString(@NonNull final String tonesString) {
        try {
            final Tone[] tones = parseTonesString(tonesString);
            return tones.length > 0;
        } catch (final NumberFormatException e) {
            return false;
        }
    }
}
