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
        return max(values, 0, values.length);
    }

    // find index of max element
    // start is inclusive
    // end is exclusive
    public static int max(final double[] values, final int start, final int end) {
        int maxIndex = Integer.MIN_VALUE;
        double maxValue = Double.MIN_VALUE;
        for (int i = start; i < end; i++) {
            final double value = values[i];
            if (value > maxValue) {
                maxValue = value;
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    // find index of max element
    public static int maxAround(final double[] values, final int index, final int radius) {
        return max(
                values,
                getStart(index, radius),
                getEnd(index, radius, values.length)
        );
    }

    // set values to 0
    // start is inclusive
    // end is exclusive
    public static void drop(final double[] values, final int start, final int end) {
        for (int i = start; i <= end; i++) {
            values[i] = 0.0;
        }
    }

    // set values to 0
    public static void dropAround(final double[] values, final int index, final int radius) {
        drop(
                values,
                getStart(index, radius),
                getEnd(index, radius, values.length)
        );
    }

    // to be used as inclusive start
    public static int getStart(final int index, final int radius) {
        int start = index - radius;
        if (start < 0) {
            start = 0;
        }
        return start;
    }

    // to be used as exclusive end
    public static int getEnd(final int index, final int radius, final int length) {
        int end = index + radius + 1;
        if (end > length) {
            end = length;
        }
        return end;
    }
}
