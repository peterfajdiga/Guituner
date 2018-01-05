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
}
