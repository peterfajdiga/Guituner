package peterfajdiga.guituner.app;

import androidx.annotation.NonNull;

import peterfajdiga.guituner.general.Tone;

class TonesString {
    private TonesString() {}  // make class static

    @NonNull
    static Tone[] parseTonesString(@NonNull final String tonesString) throws NumberFormatException {
        final String[] toneStrings = tonesString.split(" ");
        final int n = toneStrings.length;
        final Tone[] tones = new Tone[n];
        for (int i = 0; i < n; i++) {
            tones[i] = Tone.fromString(toneStrings[i]);
        }
        return tones;
    }

    static boolean validateTonesString(@NonNull final String tonesString) {
        try {
            parseTonesString(tonesString);
        } catch (final NumberFormatException e) {
            return false;
        }
        return true;
    }
}
