package peterfajdiga.guituner.general;

import androidx.annotation.NonNull;

import java.util.Arrays;

public enum Tone {
    // C0 (ToneFrequencies.C0 ),
    // C0s(ToneFrequencies.C0s),
    // D0 (ToneFrequencies.D0 ),
    // D0s(ToneFrequencies.D0s),
    // E0 (ToneFrequencies.E0 ),
    // F0 (ToneFrequencies.F0 ),
    // F0s(ToneFrequencies.F0s),
    // G0 (ToneFrequencies.G0 ),
    // G0s(ToneFrequencies.G0s),
    A0 (ToneFrequencies.A0 ),
    A0s(ToneFrequencies.A0s),
    B0 (ToneFrequencies.B0 ),
    C1 (ToneFrequencies.C1 ),
    C1s(ToneFrequencies.C1s),
    D1 (ToneFrequencies.D1 ),
    D1s(ToneFrequencies.D1s),
    E1 (ToneFrequencies.E1 ),
    F1 (ToneFrequencies.F1 ),
    F1s(ToneFrequencies.F1s),
    G1 (ToneFrequencies.G1 ),
    G1s(ToneFrequencies.G1s),
    A1 (ToneFrequencies.A1 ),
    A1s(ToneFrequencies.A1s),
    B1 (ToneFrequencies.B1 ),
    C2 (ToneFrequencies.C2 ),
    C2s(ToneFrequencies.C2s),
    D2 (ToneFrequencies.D2 ),
    D2s(ToneFrequencies.D2s),
    E2 (ToneFrequencies.E2 ),
    F2 (ToneFrequencies.F2 ),
    F2s(ToneFrequencies.F2s),
    G2 (ToneFrequencies.G2 ),
    G2s(ToneFrequencies.G2s),
    A2 (ToneFrequencies.A2 ),
    A2s(ToneFrequencies.A2s),
    B2 (ToneFrequencies.B2 ),
    C3 (ToneFrequencies.C3 ),
    C3s(ToneFrequencies.C3s),
    D3 (ToneFrequencies.D3 ),
    D3s(ToneFrequencies.D3s),
    E3 (ToneFrequencies.E3 ),
    F3 (ToneFrequencies.F3 ),
    F3s(ToneFrequencies.F3s),
    G3 (ToneFrequencies.G3 ),
    G3s(ToneFrequencies.G3s),
    A3 (ToneFrequencies.A3 ),
    A3s(ToneFrequencies.A3s),
    B3 (ToneFrequencies.B3 ),
    C4 (ToneFrequencies.C4 ),
    C4s(ToneFrequencies.C4s),
    D4 (ToneFrequencies.D4 ),
    D4s(ToneFrequencies.D4s),
    E4 (ToneFrequencies.E4 ),
    F4 (ToneFrequencies.F4 ),
    F4s(ToneFrequencies.F4s),
    G4 (ToneFrequencies.G4 ),
    G4s(ToneFrequencies.G4s),
    A4 (ToneFrequencies.A4 ),
    A4s(ToneFrequencies.A4s),
    B4 (ToneFrequencies.B4 ),
    C5 (ToneFrequencies.C5 ),
    C5s(ToneFrequencies.C5s),
    D5 (ToneFrequencies.D5 ),
    D5s(ToneFrequencies.D5s),
    E5 (ToneFrequencies.E5 ),
    F5 (ToneFrequencies.F5 ),
    F5s(ToneFrequencies.F5s),
    G5 (ToneFrequencies.G5 ),
    G5s(ToneFrequencies.G5s),
    A5 (ToneFrequencies.A5 ),
    A5s(ToneFrequencies.A5s),
    B5 (ToneFrequencies.B5 ),
    C6 (ToneFrequencies.C6 ),
    C6s(ToneFrequencies.C6s),
    D6 (ToneFrequencies.D6 ),
    D6s(ToneFrequencies.D6s),
    E6 (ToneFrequencies.E6 ),
    F6 (ToneFrequencies.F6 ),
    F6s(ToneFrequencies.F6s),
    G6 (ToneFrequencies.G6 ),
    G6s(ToneFrequencies.G6s),
    A6 (ToneFrequencies.A6 ),
    A6s(ToneFrequencies.A6s),
    B6 (ToneFrequencies.B6 ),
    C7 (ToneFrequencies.C7 ),
    C7s(ToneFrequencies.C7s),
    D7 (ToneFrequencies.D7 ),
    D7s(ToneFrequencies.D7s),
    E7 (ToneFrequencies.E7 ),
    F7 (ToneFrequencies.F7 ),
    F7s(ToneFrequencies.F7s),
    G7 (ToneFrequencies.G7 ),
    G7s(ToneFrequencies.G7s),
    A7 (ToneFrequencies.A7 ),
    A7s(ToneFrequencies.A7s),
    B7 (ToneFrequencies.B7 ),
    C8 (ToneFrequencies.C8 ),
    C8s(ToneFrequencies.C8s),
    D8 (ToneFrequencies.D8 ),
    D8s(ToneFrequencies.D8s),
    E8 (ToneFrequencies.E8 ),
    F8 (ToneFrequencies.F8 ),
    F8s(ToneFrequencies.F8s),
    G8 (ToneFrequencies.G8 ),
    G8s(ToneFrequencies.G8s),
    A8 (ToneFrequencies.A8 ),
    A8s(ToneFrequencies.A8s),
    B8 (ToneFrequencies.B8 );

    public final double frequency;

    Tone(final double frequency) {
        this.frequency = frequency;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString().replace('s', '#');
    }

    @NonNull
    public static Tone fromString(@NonNull final String toneString) throws ToneFormatException {
        final String enumName = toneString.replace('#', 's');
        try {
            return Tone.valueOf(enumName);
        } catch (final IllegalArgumentException e) {
            throw new ToneFormatException(toneString);
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

    public static class ToneFormatException extends IllegalArgumentException {
        public ToneFormatException(@NonNull final String inputToneString) {
            super(inputToneString);
        }
    }
}
