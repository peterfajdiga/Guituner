package peterfajdiga.guituner.app;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import peterfajdiga.guituner.general.Tone;

class Preferences {
    private static final String shortcutTonesKey = "shortcutTones";
    private static final String shortcutTonesDefault = "E2,A2,D3,G3,B3,E4";

    private final SharedPreferences prefs;

    Preferences(@NonNull final SharedPreferences prefs) {
        this.prefs = prefs;
    }

    @NonNull Tone[] getShortcutTones() {
        final String tonesString = prefs.getString(shortcutTonesKey, shortcutTonesDefault);
        assert tonesString != null;
        return parseTonesString(tonesString);
    }

    private static @NonNull Tone[] parseTonesString(@NonNull final String tonesString) {
        final String[] toneStrings = tonesString.split(",");
        final int n = toneStrings.length;
        final Tone[] tones = new Tone[n];
        for (int i = 0; i < n; i++) {
            tones[i] = Tone.fromString(toneStrings[i]);
        }
        return tones;
    }
}
