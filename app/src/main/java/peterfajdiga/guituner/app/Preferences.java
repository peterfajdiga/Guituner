package peterfajdiga.guituner.app;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import peterfajdiga.guituner.app.tuning.TonesString;
import peterfajdiga.guituner.general.Tone;

public class Preferences {
    private static final String shortcutTonesKey = "shortcutTones";
    private static final String shortcutTonesDefault = "E2 A2 D3 G3 B3 E4";

    private static final String shortcutTonesCustomKey = "shortcutTonesCustom";
    private static final String shortcutTonesCustomDefault = "G1 D2 A2 E3 B3 F4#";

    private final SharedPreferences prefs;

    public Preferences(@NonNull final SharedPreferences prefs) {
        this.prefs = prefs;
    }

    @NonNull
    public String getShortcutTonesString() {
        return getStringNonNull(shortcutTonesKey, shortcutTonesDefault);
    }

    @NonNull
    public Tone[] getShortcutTones() {
        return TonesString.parseTonesString(getShortcutTonesString());
    }

    public void saveShortcutTones(@NonNull final String shortcutTonesString) throws Tone.ToneFormatException {
        saveTonesString(shortcutTonesKey, shortcutTonesString);
    }

    @NonNull
    public String getShortcutTonesCustomString() {
        return getStringNonNull(shortcutTonesCustomKey, shortcutTonesCustomDefault);
    }

    @NonNull
    public Tone[] getShortcutTonesCustom() {
        return TonesString.parseTonesString(getShortcutTonesCustomString());
    }

    public void saveShortcutTonesCustom(@NonNull final String shortcutTonesCustomString) throws Tone.ToneFormatException {
        saveTonesString(shortcutTonesCustomKey, shortcutTonesCustomString);
    }

    @NonNull
    private String getStringNonNull(@NonNull final String key, @NonNull final String defaultValue) {
        final String value = prefs.getString(key, defaultValue);
        assert value != null;
        return value;
    }

    private void saveTonesString(@NonNull final String key, @NonNull final String tonesString) throws Tone.ToneFormatException {
        if (!TonesString.validateTonesString(tonesString)) {
            throw new Tone.ToneFormatException(tonesString);
        }
        final SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(key, tonesString);
        prefsEditor.apply();
    }
}
