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
    boolean add(final double value) {
        final double avg = avg();
        for (int mult = 1; mult <= MULTS; mult++) {
            final double adjustedValue = value / mult;
            if (Math.abs(avg - adjustedValue) <= MAX_ERROR) {
                sum += adjustedValue;
                count++;
                return true;
            }
        }
        for (int mult = 1; mult <= MULTS; mult++) {
            final double adjustedValue = value * mult;
            if (Math.abs(avg - adjustedValue) <= MAX_ERROR) {
                sum += adjustedValue;
                count++;
                sum /= mult;
                return true;
            }
        }
        return false;
    }
}
