package peterfajdiga.guituner.app;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import peterfajdiga.guituner.app.tuning.PitchesString;
import peterfajdiga.guituner.general.Pitch;

public class Preferences {
    private static final String tuningKey = "tuning";
    private static final String tuningDefault = "E2 A2 D3 G3 B3 E4";

    private static final String customTuningKey = "customTuning";
    private static final String customTuningDefault = "G1 D2 A2 E3 B3 F4#";

    private final SharedPreferences prefs;

    public Preferences(@NonNull final SharedPreferences prefs) {
        this.prefs = prefs;
    }

    @NonNull
    public String getTuningString() {
        return getStringNonNull(tuningKey, tuningDefault);
    }

    @NonNull
    public Pitch[] getTuningPitches() {
        return PitchesString.parsePitchesString(getTuningString());
    }

    public void saveTuning(@NonNull final String tuningString) throws Pitch.PitchFormatException {
        savePitchesString(tuningKey, tuningString);
    }

    @NonNull
    public String getCustomTuningString() {
        return getStringNonNull(customTuningKey, customTuningDefault);
    }

    @NonNull
    public Pitch[] getCustomTuningPitches() {
        return PitchesString.parsePitchesString(getCustomTuningString());
    }

    public void saveCustomTuning(@NonNull final String customTuningString) throws Pitch.PitchFormatException {
        savePitchesString(customTuningKey, customTuningString);
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
