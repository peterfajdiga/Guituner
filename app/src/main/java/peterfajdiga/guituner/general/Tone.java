package peterfajdiga.guituner.general;

import androidx.annotation.NonNull;

import java.util.Arrays;

public enum Tone {
    // C0 (ToneFrequencies.C0 , "C0"),
    // C0s(ToneFrequencies.C0s, "C0#"),
    // D0 (ToneFrequencies.D0 , "D0"),
    // D0s(ToneFrequencies.D0s, "D0#"),
    // E0 (ToneFrequencies.E0 , "E0"),
    // F0 (ToneFrequencies.F0 , "F0"),
    // F0s(ToneFrequencies.F0s, "F0#"),
    // G0 (ToneFrequencies.G0 , "G0"),
    // G0s(ToneFrequencies.G0s, "G0#"),
    A0 (ToneFrequencies.A0 , "A0"),
    A0s(ToneFrequencies.A0s, "A0#"),
    B0 (ToneFrequencies.B0 , "B0"),
    C1 (ToneFrequencies.C1 , "C1"),
    C1s(ToneFrequencies.C1s, "C1#"),
    D1 (ToneFrequencies.D1 , "D1"),
    D1s(ToneFrequencies.D1s, "D1#"),
    E1 (ToneFrequencies.E1 , "E1"),
    F1 (ToneFrequencies.F1 , "F1"),
    F1s(ToneFrequencies.F1s, "F1#"),
    G1 (ToneFrequencies.G1 , "G1"),
    G1s(ToneFrequencies.G1s, "G1#"),
    A1 (ToneFrequencies.A1 , "A1"),
    A1s(ToneFrequencies.A1s, "A1#"),
    B1 (ToneFrequencies.B1 , "B1"),
    C2 (ToneFrequencies.C2 , "C2"),
    C2s(ToneFrequencies.C2s, "C2#"),
    D2 (ToneFrequencies.D2 , "D2"),
    D2s(ToneFrequencies.D2s, "D2#"),
    E2 (ToneFrequencies.E2 , "E2"),
    F2 (ToneFrequencies.F2 , "F2"),
    F2s(ToneFrequencies.F2s, "F2#"),
    G2 (ToneFrequencies.G2 , "G2"),
    G2s(ToneFrequencies.G2s, "G2#"),
    A2 (ToneFrequencies.A2 , "A2"),
    A2s(ToneFrequencies.A2s, "A2#"),
    B2 (ToneFrequencies.B2 , "B2"),
    C3 (ToneFrequencies.C3 , "C3"),
    C3s(ToneFrequencies.C3s, "C3#"),
    D3 (ToneFrequencies.D3 , "D3"),
    D3s(ToneFrequencies.D3s, "D3#"),
    E3 (ToneFrequencies.E3 , "E3"),
    F3 (ToneFrequencies.F3 , "F3"),
    F3s(ToneFrequencies.F3s, "F3#"),
    G3 (ToneFrequencies.G3 , "G3"),
    G3s(ToneFrequencies.G3s, "G3#"),
    A3 (ToneFrequencies.A3 , "A3"),
    A3s(ToneFrequencies.A3s, "A3#"),
    B3 (ToneFrequencies.B3 , "B3"),
    C4 (ToneFrequencies.C4 , "C4"),
    C4s(ToneFrequencies.C4s, "C4#"),
    D4 (ToneFrequencies.D4 , "D4"),
    D4s(ToneFrequencies.D4s, "D4#"),
    E4 (ToneFrequencies.E4 , "E4"),
    F4 (ToneFrequencies.F4 , "F4"),
    F4s(ToneFrequencies.F4s, "F4#"),
    G4 (ToneFrequencies.G4 , "G4"),
    G4s(ToneFrequencies.G4s, "G4#"),
    A4 (ToneFrequencies.A4 , "A4"),
    A4s(ToneFrequencies.A4s, "A4#"),
    B4 (ToneFrequencies.B4 , "B4"),
    C5 (ToneFrequencies.C5 , "C5"),
    C5s(ToneFrequencies.C5s, "C5#"),
    D5 (ToneFrequencies.D5 , "D5"),
    D5s(ToneFrequencies.D5s, "D5#"),
    E5 (ToneFrequencies.E5 , "E5"),
    F5 (ToneFrequencies.F5 , "F5"),
    F5s(ToneFrequencies.F5s, "F5#"),
    G5 (ToneFrequencies.G5 , "G5"),
    G5s(ToneFrequencies.G5s, "G5#"),
    A5 (ToneFrequencies.A5 , "A5"),
    A5s(ToneFrequencies.A5s, "A5#"),
    B5 (ToneFrequencies.B5 , "B5"),
    C6 (ToneFrequencies.C6 , "C6"),
    C6s(ToneFrequencies.C6s, "C6#"),
    D6 (ToneFrequencies.D6 , "D6"),
    D6s(ToneFrequencies.D6s, "D6#"),
    E6 (ToneFrequencies.E6 , "E6"),
    F6 (ToneFrequencies.F6 , "F6"),
    F6s(ToneFrequencies.F6s, "F6#"),
    G6 (ToneFrequencies.G6 , "G6"),
    G6s(ToneFrequencies.G6s, "G6#"),
    A6 (ToneFrequencies.A6 , "A6"),
    A6s(ToneFrequencies.A6s, "A6#"),
    B6 (ToneFrequencies.B6 , "B6"),
    C7 (ToneFrequencies.C7 , "C7"),
    C7s(ToneFrequencies.C7s, "C7#"),
    D7 (ToneFrequencies.D7 , "D7"),
    D7s(ToneFrequencies.D7s, "D7#"),
    E7 (ToneFrequencies.E7 , "E7"),
    F7 (ToneFrequencies.F7 , "F7"),
    F7s(ToneFrequencies.F7s, "F7#"),
    G7 (ToneFrequencies.G7 , "G7"),
    G7s(ToneFrequencies.G7s, "G7#"),
    A7 (ToneFrequencies.A7 , "A7"),
    A7s(ToneFrequencies.A7s, "A7#"),
    B7 (ToneFrequencies.B7 , "B7"),
    C8 (ToneFrequencies.C8 , "C8"),
    C8s(ToneFrequencies.C8s, "C8#"),
    D8 (ToneFrequencies.D8 , "D8"),
    D8s(ToneFrequencies.D8s, "D8#"),
    E8 (ToneFrequencies.E8 , "E8"),
    F8 (ToneFrequencies.F8 , "F8"),
    F8s(ToneFrequencies.F8s, "F8#"),
    G8 (ToneFrequencies.G8 , "G8"),
    G8s(ToneFrequencies.G8s, "G8#"),
    A8 (ToneFrequencies.A8 , "A8"),
    A8s(ToneFrequencies.A8s, "A8#"),
    B8 (ToneFrequencies.B8 , "B8");

    public final double frequency;
    public final String name;

    Tone(final double frequency, final String name) {
        this.frequency = frequency;
        this.name = name;
    }

    @NonNull
    public static Tone fromString(@NonNull final String toneString) throws ToneFormatException {
        final String enumName = toneString.replace('#', 's');
        try {
            return Tone.valueOf(enumName);
        } catch (final IllegalArgumentException e) {
            throw new ToneFormatException();
        }
    }

    @NonNull
    public static Tone[] getTonesUpTo(final double highestFrequency) {
        int detectableToneCount = 0;
        for (final Tone tone : Tone.values()) {
            if (tone.frequency > highestFrequency) {
                break;
            }
            detectableToneCount++;
        }
        return Arrays.copyOf(Tone.values(), detectableToneCount);
    }

    public static class ToneFormatException extends IllegalArgumentException {}
}
