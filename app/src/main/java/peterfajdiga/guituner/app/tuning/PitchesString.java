package peterfajdiga.guituner.app.tuning;

import androidx.annotation.NonNull;

import peterfajdiga.guituner.general.Pitch;

public class PitchesString {
    private PitchesString() {}  // make class static

    @NonNull
    public static Pitch[] parsePitchesString(@NonNull final String pitchesString) throws Pitch.PitchFormatException {
        final String[] pitchStrings = pitchesString.split(" ");
        final int n = pitchStrings.length;
        final Pitch[] pitches = new Pitch[n];
        for (int i = 0; i < n; i++) {
            pitches[i] = Pitch.fromString(pitchStrings[i]);
        }
        return pitches;
    }

    public static boolean validatePitchesString(@NonNull final String pitchesString) {
        try {
            final Pitch[] pitches = parsePitchesString(pitchesString);
            return pitches.length > 0;
        } catch (final Pitch.PitchFormatException e) {
            return false;
        }
    }
}
