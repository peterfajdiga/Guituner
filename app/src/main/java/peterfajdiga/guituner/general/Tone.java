package peterfajdiga.guituner.general;

import androidx.annotation.NonNull;

import java.util.Arrays;

public class Tone {
    public final double frequency;
    public final String name;

    // don't allow creation of a new Tone
    private Tone(final double frequency, final String name) {
        this.frequency = frequency;
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public static final Tone C0   = new Tone(ToneFrequencies.C0 , "C0");
    public static final Tone C0s  = new Tone(ToneFrequencies.C0s, "C0#");
    public static final Tone D0   = new Tone(ToneFrequencies.D0 , "D0");
    public static final Tone D0s  = new Tone(ToneFrequencies.D0s, "D0#");
    public static final Tone E0   = new Tone(ToneFrequencies.E0 , "E0");
    public static final Tone F0   = new Tone(ToneFrequencies.F0 , "F0");
    public static final Tone F0s  = new Tone(ToneFrequencies.F0s, "F0#");
    public static final Tone G0   = new Tone(ToneFrequencies.G0 , "G0");
    public static final Tone G0s  = new Tone(ToneFrequencies.G0s, "G0#");
    public static final Tone A0   = new Tone(ToneFrequencies.A0 , "A0");
    public static final Tone A0s  = new Tone(ToneFrequencies.A0s, "A0#");
    public static final Tone B0   = new Tone(ToneFrequencies.B0 , "B0");
    public static final Tone C1   = new Tone(ToneFrequencies.C1 , "C1");
    public static final Tone C1s  = new Tone(ToneFrequencies.C1s, "C1#");
    public static final Tone D1   = new Tone(ToneFrequencies.D1 , "D1");
    public static final Tone D1s  = new Tone(ToneFrequencies.D1s, "D1#");
    public static final Tone E1   = new Tone(ToneFrequencies.E1 , "E1");
    public static final Tone F1   = new Tone(ToneFrequencies.F1 , "F1");
    public static final Tone F1s  = new Tone(ToneFrequencies.F1s, "F1#");
    public static final Tone G1   = new Tone(ToneFrequencies.G1 , "G1");
    public static final Tone G1s  = new Tone(ToneFrequencies.G1s, "G1#");
    public static final Tone A1   = new Tone(ToneFrequencies.A1 , "A1");
    public static final Tone A1s  = new Tone(ToneFrequencies.A1s, "A1#");
    public static final Tone B1   = new Tone(ToneFrequencies.B1 , "B1");
    public static final Tone C2   = new Tone(ToneFrequencies.C2 , "C2");
    public static final Tone C2s  = new Tone(ToneFrequencies.C2s, "C2#");
    public static final Tone D2   = new Tone(ToneFrequencies.D2 , "D2");
    public static final Tone D2s  = new Tone(ToneFrequencies.D2s, "D2#");
    public static final Tone E2   = new Tone(ToneFrequencies.E2 , "E2");
    public static final Tone F2   = new Tone(ToneFrequencies.F2 , "F2");
    public static final Tone F2s  = new Tone(ToneFrequencies.F2s, "F2#");
    public static final Tone G2   = new Tone(ToneFrequencies.G2 , "G2");
    public static final Tone G2s  = new Tone(ToneFrequencies.G2s, "G2#");
    public static final Tone A2   = new Tone(ToneFrequencies.A2 , "A2");
    public static final Tone A2s  = new Tone(ToneFrequencies.A2s, "A2#");
    public static final Tone B2   = new Tone(ToneFrequencies.B2 , "B2");
    public static final Tone C3   = new Tone(ToneFrequencies.C3 , "C3");
    public static final Tone C3s  = new Tone(ToneFrequencies.C3s, "C3#");
    public static final Tone D3   = new Tone(ToneFrequencies.D3 , "D3");
    public static final Tone D3s  = new Tone(ToneFrequencies.D3s, "D3#");
    public static final Tone E3   = new Tone(ToneFrequencies.E3 , "E3");
    public static final Tone F3   = new Tone(ToneFrequencies.F3 , "F3");
    public static final Tone F3s  = new Tone(ToneFrequencies.F3s, "F3#");
    public static final Tone G3   = new Tone(ToneFrequencies.G3 , "G3");
    public static final Tone G3s  = new Tone(ToneFrequencies.G3s, "G3#");
    public static final Tone A3   = new Tone(ToneFrequencies.A3 , "A3");
    public static final Tone A3s  = new Tone(ToneFrequencies.A3s, "A3#");
    public static final Tone B3   = new Tone(ToneFrequencies.B3 , "B3");
    public static final Tone C4   = new Tone(ToneFrequencies.C4 , "C4");
    public static final Tone C4s  = new Tone(ToneFrequencies.C4s, "C4#");
    public static final Tone D4   = new Tone(ToneFrequencies.D4 , "D4");
    public static final Tone D4s  = new Tone(ToneFrequencies.D4s, "D4#");
    public static final Tone E4   = new Tone(ToneFrequencies.E4 , "E4");
    public static final Tone F4   = new Tone(ToneFrequencies.F4 , "F4");
    public static final Tone F4s  = new Tone(ToneFrequencies.F4s, "F4#");
    public static final Tone G4   = new Tone(ToneFrequencies.G4 , "G4");
    public static final Tone G4s  = new Tone(ToneFrequencies.G4s, "G4#");
    public static final Tone A4   = new Tone(ToneFrequencies.A4 , "A4");
    public static final Tone A4s  = new Tone(ToneFrequencies.A4s, "A4#");
    public static final Tone B4   = new Tone(ToneFrequencies.B4 , "B4");
    public static final Tone C5   = new Tone(ToneFrequencies.C5 , "C5");
    public static final Tone C5s  = new Tone(ToneFrequencies.C5s, "C5#");
    public static final Tone D5   = new Tone(ToneFrequencies.D5 , "D5");
    public static final Tone D5s  = new Tone(ToneFrequencies.D5s, "D5#");
    public static final Tone E5   = new Tone(ToneFrequencies.E5 , "E5");
    public static final Tone F5   = new Tone(ToneFrequencies.F5 , "F5");
    public static final Tone F5s  = new Tone(ToneFrequencies.F5s, "F5#");
    public static final Tone G5   = new Tone(ToneFrequencies.G5 , "G5");
    public static final Tone G5s  = new Tone(ToneFrequencies.G5s, "G5#");
    public static final Tone A5   = new Tone(ToneFrequencies.A5 , "A5");
    public static final Tone A5s  = new Tone(ToneFrequencies.A5s, "A5#");
    public static final Tone B5   = new Tone(ToneFrequencies.B5 , "B5");
    public static final Tone C6   = new Tone(ToneFrequencies.C6 , "C6");
    public static final Tone C6s  = new Tone(ToneFrequencies.C6s, "C6#");
    public static final Tone D6   = new Tone(ToneFrequencies.D6 , "D6");
    public static final Tone D6s  = new Tone(ToneFrequencies.D6s, "D6#");
    public static final Tone E6   = new Tone(ToneFrequencies.E6 , "E6");
    public static final Tone F6   = new Tone(ToneFrequencies.F6 , "F6");
    public static final Tone F6s  = new Tone(ToneFrequencies.F6s, "F6#");
    public static final Tone G6   = new Tone(ToneFrequencies.G6 , "G6");
    public static final Tone G6s  = new Tone(ToneFrequencies.G6s, "G6#");
    public static final Tone A6   = new Tone(ToneFrequencies.A6 , "A6");
    public static final Tone A6s  = new Tone(ToneFrequencies.A6s, "A6#");
    public static final Tone B6   = new Tone(ToneFrequencies.B6 , "B6");
    public static final Tone C7   = new Tone(ToneFrequencies.C7 , "C7");
    public static final Tone C7s  = new Tone(ToneFrequencies.C7s, "C7#");
    public static final Tone D7   = new Tone(ToneFrequencies.D7 , "D7");
    public static final Tone D7s  = new Tone(ToneFrequencies.D7s, "D7#");
    public static final Tone E7   = new Tone(ToneFrequencies.E7 , "E7");
    public static final Tone F7   = new Tone(ToneFrequencies.F7 , "F7");
    public static final Tone F7s  = new Tone(ToneFrequencies.F7s, "F7#");
    public static final Tone G7   = new Tone(ToneFrequencies.G7 , "G7");
    public static final Tone G7s  = new Tone(ToneFrequencies.G7s, "G7#");
    public static final Tone A7   = new Tone(ToneFrequencies.A7 , "A7");
    public static final Tone A7s  = new Tone(ToneFrequencies.A7s, "A7#");
    public static final Tone B7   = new Tone(ToneFrequencies.B7 , "B7");
    public static final Tone C8   = new Tone(ToneFrequencies.C8 , "C8");
    public static final Tone C8s  = new Tone(ToneFrequencies.C8s, "C8#");
    public static final Tone D8   = new Tone(ToneFrequencies.D8 , "D8");
    public static final Tone D8s  = new Tone(ToneFrequencies.D8s, "D8#");
    public static final Tone E8   = new Tone(ToneFrequencies.E8 , "E8");
    public static final Tone F8   = new Tone(ToneFrequencies.F8 , "F8");
    public static final Tone F8s  = new Tone(ToneFrequencies.F8s, "F8#");
    public static final Tone G8   = new Tone(ToneFrequencies.G8 , "G8");
    public static final Tone G8s  = new Tone(ToneFrequencies.G8s, "G8#");
    public static final Tone A8   = new Tone(ToneFrequencies.A8 , "A8");
    public static final Tone A8s  = new Tone(ToneFrequencies.A8s, "A8#");
    public static final Tone B8   = new Tone(ToneFrequencies.B8 , "B8");

    public static Tone fromString(@NonNull final String toneString) throws NumberFormatException {
        switch (toneString) {
            case "C0": return C0;
            case "C0#": return C0s;
            case "D0": return D0;
            case "D0#": return D0s;
            case "E0": return E0;
            case "F0": return F0;
            case "F0#": return F0s;
            case "G0": return G0;
            case "G0#": return G0s;
            case "A0": return A0;
            case "A0#": return A0s;
            case "B0": return B0;
            case "C1": return C1;
            case "C1#": return C1s;
            case "D1": return D1;
            case "D1#": return D1s;
            case "E1": return E1;
            case "F1": return F1;
            case "F1#": return F1s;
            case "G1": return G1;
            case "G1#": return G1s;
            case "A1": return A1;
            case "A1#": return A1s;
            case "B1": return B1;
            case "C2": return C2;
            case "C2#": return C2s;
            case "D2": return D2;
            case "D2#": return D2s;
            case "E2": return E2;
            case "F2": return F2;
            case "F2#": return F2s;
            case "G2": return G2;
            case "G2#": return G2s;
            case "A2": return A2;
            case "A2#": return A2s;
            case "B2": return B2;
            case "C3": return C3;
            case "C3#": return C3s;
            case "D3": return D3;
            case "D3#": return D3s;
            case "E3": return E3;
            case "F3": return F3;
            case "F3#": return F3s;
            case "G3": return G3;
            case "G3#": return G3s;
            case "A3": return A3;
            case "A3#": return A3s;
            case "B3": return B3;
            case "C4": return C4;
            case "C4#": return C4s;
            case "D4": return D4;
            case "D4#": return D4s;
            case "E4": return E4;
            case "F4": return F4;
            case "F4#": return F4s;
            case "G4": return G4;
            case "G4#": return G4s;
            case "A4": return A4;
            case "A4#": return A4s;
            case "B4": return B4;
            case "C5": return C5;
            case "C5#": return C5s;
            case "D5": return D5;
            case "D5#": return D5s;
            case "E5": return E5;
            case "F5": return F5;
            case "F5#": return F5s;
            case "G5": return G5;
            case "G5#": return G5s;
            case "A5": return A5;
            case "A5#": return A5s;
            case "B5": return B5;
            case "C6": return C6;
            case "C6#": return C6s;
            case "D6": return D6;
            case "D6#": return D6s;
            case "E6": return E6;
            case "F6": return F6;
            case "F6#": return F6s;
            case "G6": return G6;
            case "G6#": return G6s;
            case "A6": return A6;
            case "A6#": return A6s;
            case "B6": return B6;
            case "C7": return C7;
            case "C7#": return C7s;
            case "D7": return D7;
            case "D7#": return D7s;
            case "E7": return E7;
            case "F7": return F7;
            case "F7#": return F7s;
            case "G7": return G7;
            case "G7#": return G7s;
            case "A7": return A7;
            case "A7#": return A7s;
            case "B7": return B7;
            case "C8": return C8;
            case "C8#": return C8s;
            case "D8": return D8;
            case "D8#": return D8s;
            case "E8": return E8;
            case "F8": return F8;
            case "F8#": return F8s;
            case "G8": return G8;
            case "G8#": return G8s;
            case "A8": return A8;
            case "A8#": return A8s;
            case "B8": return B8;
        }
        throw new NumberFormatException();
    }

    @NonNull
    public static Tone[] getTonesBelowFrequency(final double highestFrequency) {
        // TODO: optimize
        int detectableToneCount = 0;
        for (Tone tone : fullToneList) {
            if (tone.frequency > highestFrequency) {
                break;
            }
            detectableToneCount++;
        }
        return Arrays.copyOf(fullToneList, detectableToneCount);
    }

    private static final Tone[] fullToneList = {
        Tone.A0,
        Tone.A0s,
        Tone.B0,
        Tone.C1,
        Tone.C1s,
        Tone.D1,
        Tone.D1s,
        Tone.E1,
        Tone.F1,
        Tone.F1s,
        Tone.G1,
        Tone.G1s,
        Tone.A1,
        Tone.A1s,
        Tone.B1,
        Tone.C2,
        Tone.C2s,
        Tone.D2,
        Tone.D2s,
        Tone.E2,
        Tone.F2,
        Tone.F2s,
        Tone.G2,
        Tone.G2s,
        Tone.A2,
        Tone.A2s,
        Tone.B2,
        Tone.C3,
        Tone.C3s,
        Tone.D3,
        Tone.D3s,
        Tone.E3,
        Tone.F3,
        Tone.F3s,
        Tone.G3,
        Tone.G3s,
        Tone.A3,
        Tone.A3s,
        Tone.B3,
        Tone.C4,
        Tone.C4s,
        Tone.D4,
        Tone.D4s,
        Tone.E4,
        Tone.F4,
        Tone.F4s,
        Tone.G4,
        Tone.G4s,
        Tone.A4,
        Tone.A4s,
        Tone.B4,
        Tone.C5,
        Tone.C5s,
        Tone.D5,
        Tone.D5s,
        Tone.E5,
        Tone.F5,
        Tone.F5s,
        Tone.G5,
        Tone.G5s,
        Tone.A5,
        Tone.A5s,
        Tone.B5,
        Tone.C6,
        Tone.C6s,
        Tone.D6,
        Tone.D6s,
        Tone.E6,
        Tone.F6,
        Tone.F6s,
        Tone.G6,
        Tone.G6s,
        Tone.A6,
        Tone.A6s,
        Tone.B6,
        Tone.C7,
        Tone.C7s,
        Tone.D7,
        Tone.D7s,
        Tone.E7,
        Tone.F7,
        Tone.F7s,
        Tone.G7,
        Tone.G7s,
    };
}
