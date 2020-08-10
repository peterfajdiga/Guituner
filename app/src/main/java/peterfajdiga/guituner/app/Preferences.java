package peterfajdiga.guituner.app;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import peterfajdiga.guituner.app.tuning.PitchesString;
import peterfajdiga.guituner.general.Pitch;

public class Preferences {
    private static final String shortcutPitchesKey = "shortcutPitches";
    private static final String shortcutPitchesDefault = "E2 A2 D3 G3 B3 E4";

    private static final String shortcutPitchesCustomKey = "shortcutPitchesCustom";
    private static final String shortcutPitchesCustomDefault = "G1 D2 A2 E3 B3 F4#";

    private final SharedPreferences prefs;

    public Preferences(@NonNull final SharedPreferences prefs) {
        this.prefs = prefs;
    }

    @NonNull
    public String getShortcutPitchesString() {
        return getStringNonNull(shortcutPitchesKey, shortcutPitchesDefault);
    }

    @NonNull
    public Pitch[] getShortcutPitches() {
        return PitchesString.parsePitchesString(getShortcutPitchesString());
    }

    public void saveShortcutPitches(@NonNull final String shortcutPitchesString) throws Pitch.PitchFormatException {
        savePitchesString(shortcutPitchesKey, shortcutPitchesString);
    }

    @NonNull
    public String getShortcutPitchesCustomString() {
        return getStringNonNull(shortcutPitchesCustomKey, shortcutPitchesCustomDefault);
    }

    @NonNull
    public Pitch[] getShortcutPitchesCustom() {
        return PitchesString.parsePitchesString(getShortcutPitchesCustomString());
    }

    public void saveShortcutPitchesCustom(@NonNull final String shortcutPitchesCustomString) throws Pitch.PitchFormatException {
        savePitchesString(shortcutPitchesCustomKey, shortcutPitchesCustomString);
    }

    @NonNull
    private String getStringNonNull(@NonNull final String key, @NonNull final String defaultValue) {
        final String value = prefs.getString(key, defaultValue);
        assert value != null;
        return value;
    }

    private void savePitchesString(@NonNull final String key, @NonNull final String pitchesString) throws Pitch.PitchFormatException {
        if (!PitchesString.validatePitchesString(pitchesString)) {
            throw new Pitch.PitchFormatException(pitchesString);
        }
        final SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(key, pitchesString);
        prefsEditor.apply();
    }
}
