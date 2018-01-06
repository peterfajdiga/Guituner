package peterfajdiga.guituner.general;

public class General {

    private General() {}  // make class static

    // finds the nearest power of two above given value
    public static int ceilPow2(final int value) {
        int n = Integer.highestOneBit(value);
        if (n != value) {
            n *= 2;
        }
        return n;
    }

    // find index of max element
    public static int max(final double[] values) {
        int maxIndex = Integer.MIN_VALUE;
        double maxValue = Double.MIN_VALUE;
        for (int i = 0; i < values.length; i++) {
            final double value = values[i];
            if (value > maxValue) {
                maxValue = value;
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    // set values to 0
    public static void drop(final double[] values, final int index, final int radius) {
        for (int i = index-radius; i < index+radius; i++) {
            if (i >= 0 && i < values.length) {
                values[i] = 0.0;
            }
        }
    }
}
