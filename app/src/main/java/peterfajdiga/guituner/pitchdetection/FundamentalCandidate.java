package peterfajdiga.guituner.pitchdetection;

class FundamentalCandidate {
    static final double MAX_ERROR = 2.0;
    private static final int MULTS = 5;

    double sum;
    int count;

    FundamentalCandidate(final double value) {
        sum = value;
        count = 1;
    }

    double avg() {
        return sum / count;
    }

    // return true if added
    boolean addHarmonic(final double harmonic) {
        final double avg = avg();
        for (int mult = 1; mult <= MULTS; mult++) {
            final double adjustedHarmonic = harmonic / mult;
            if (Math.abs(avg - adjustedHarmonic) <= MAX_ERROR) {
                sum += adjustedHarmonic;
                count++;
                return true;
            }
        }
        for (int mult = 1; mult <= MULTS; mult++) {
            final double adjustedHarmonic = harmonic * mult;
            if (Math.abs(avg - adjustedHarmonic) <= MAX_ERROR) {
                sum += adjustedHarmonic;
                count++;
                sum /= mult;
                return true;
            }
        }
        return false;
    }
}
