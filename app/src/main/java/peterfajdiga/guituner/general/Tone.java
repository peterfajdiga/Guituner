package peterfajdiga.guituner.general;

import androidx.annotation.NonNull;

import java.util.Arrays;

public enum Tone {
    // C0 (16.35),
    // C0s(17.32),
    // D0 (18.35),
    // D0s(19.45),
    // E0 (20.60),
    // F0 (21.83),
    // F0s(23.12),
    // G0 (24.50),
    // G0s(25.96),
    A0 (27.50),
    A0s(29.14),
    B0 (30.87),
    C1 (32.70),
    C1s(34.65),
    D1 (36.71),
    D1s(38.89),
    E1 (41.20),
    F1 (43.65),
    F1s(46.25),
    G1 (49.00),
    G1s(51.91),
    A1 (55.00),
    A1s(58.27),
    B1 (61.74),
    C2 (65.41),
    C2s(69.30),
    D2 (73.42),
    D2s(77.78),
    E2 (82.41),
    F2 (87.31),
    F2s(92.50),
    G2 (98.00),
    G2s(103.83),
    A2 (110.00),
    A2s(116.54),
    B2 (123.47),
    C3 (130.81),
    C3s(138.59),
    D3 (146.83),
    D3s(155.56),
    E3 (164.81),
    F3 (174.61),
    F3s(185.00),
    G3 (196.00),
    G3s(207.65),
    A3 (220.00),
    A3s(233.08),
    B3 (246.94),
    C4 (261.63),
    C4s(277.18),
    D4 (293.66),
    D4s(311.13),
    E4 (329.63),
    F4 (349.23),
    F4s(369.99),
    G4 (392.00),
    G4s(415.30),
    A4 (440.00),
    A4s(466.16),
    B4 (493.88),
    C5 (523.25),
    C5s(554.37),
    D5 (587.33),
    D5s(622.25),
    E5 (659.25),
    F5 (698.46),
    F5s(739.99),
    G5 (783.99),
    G5s(830.61),
    A5 (880.00),
    A5s(932.33),
    B5 (987.77),
    C6 (1046.50),
    C6s(1108.73),
    D6 (1174.66),
    D6s(1244.51),
    E6 (1318.51),
    F6 (1396.91),
    F6s(1479.98),
    G6 (1567.98),
    G6s(1661.22),
    A6 (1760.00),
    A6s(1864.66),
    B6 (1975.53),
    C7 (2093.00),
    C7s(2217.46),
    D7 (2349.32),
    D7s(2489.02),
    E7 (2637.02),
    F7 (2793.83),
    F7s(2959.96),
    G7 (3135.96),
    G7s(3322.44),
    A7 (3520.00),
    A7s(3729.31),
    B7 (3951.07),
    C8 (4186.01),
    C8s(4434.92),
    D8 (4698.63),
    D8s(4978.03),
    E8 (5274.04),
    F8 (5587.65),
    F8s(5919.91),
    G8 (6271.93),
    G8s(6644.88),
    A8 (7040.00),
    A8s(7458.62),
    B8 (7902.13);

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
