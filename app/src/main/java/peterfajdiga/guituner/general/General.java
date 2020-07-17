package peterfajdiga.guituner.general;

public class General {
    private General() {}  // make class static

    /**
     * return the lowest power of two that is greater or equal to the given value
     */
    public static int ceilPow2(final int value) {
        int n = Integer.highestOneBit(value);
        if (n != value) {
            n *= 2;
        }
        return n;
    }

    /**
     * find index of greatest element in given array between indices start and end
     * @param values array in which to search
     * @param start  start index (inclusive)
     * @param end    end index (exclusive)
     * @return       index of max element
     */
    public static int max(final double[] values, final int start, final int end) {
        int maxIndex = -1;
        double maxValue = Double.NEGATIVE_INFINITY;
        for (int i = start; i < end; i++) {
            final double value = values[i];
            if (value > maxValue) {
                maxValue = value;
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    /**
     * set values from start to end in given array to 0
     * @param values array to modify
     * @param start  start index (inclusive)
     * @param end    end index (exclusive
     */
    public static void drop(final double[] values, final int start, final int end) {
        for (int i = start; i < end; i++) {
            values[i] = 0.0;
        }
    }

    /**
     * set values around index in given array to 0
     * @param values array to modify
     * @param index  center index
     * @param radius distance from center index
     */
    public static void dropAround(final double[] values, final int index, final int radius) {
        drop(
                values,
                getStart(index, radius),
                getEnd(index, radius, values.length)
        );
    }

    /**
     * @param index  center index
     * @param radius distance from center index
     * @return       inclusive start index (>= 0)
     */
    public static int getStart(final int index, final int radius) {
        int start = index - radius;
        if (start < 0) {
            start = 0;
        }
        return start;
    }

    /**
     * @param index  center index
     * @param radius distance from center index
     * @param length limit
     * @return       exclusive end index (<= length)
     */
    public static int getEnd(final int index, final int radius, final int length) {
        int end = index + radius + 1;
        if (end > length) {
            end = length;
        }
        return end;
    }
}
