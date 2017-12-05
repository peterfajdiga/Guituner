package peterfajdiga.guituner.fourier;

public class Fourier {
    private Fourier() {}

    static Complex[] fft(final Complex[] input) {
        if (input.length == 1)
            return input;

        final int halfLength = input.length / 2;

        final Complex[] tableEvenIn = new Complex[halfLength];
        for (int i=0; i < tableEvenIn.length; i++)
            tableEvenIn[i] = input[i * 2];
        final Complex[] tableOddIn = new Complex[halfLength];
        for (int i=0; i < halfLength; i++)
            tableOddIn[i] = input[i * 2 + 1];

        final Complex[] tableEven = fft(tableEvenIn);
        final Complex[] tableOdd  = fft(tableOddIn);

        final Complex w = Complex.n_root(input.length);
        Complex wk = new Complex(1, 0);
        final Complex[] y = new Complex[input.length];
        for (int k=0; k < halfLength; k++) {
            Complex tmp     = wk.times(tableOdd[k]);
            y[k]            = tableEven[k].plus(tmp);
            y[k+halfLength] = tableEven[k].minus(tmp);
            wk = wk.times(w);
        }

        return y;
    }

    static Complex[] fft(final double[] input) {
        final int coefficientCount = input.length;
        int n = Integer.highestOneBit(coefficientCount);
        if (n != coefficientCount)
            n *= 2;
        Complex[] input_complex = new Complex[n];
        int i = 0;
        for (; i < coefficientCount; i++)
            input_complex[i] = new Complex(input[i], 0);
        for (; i < n; i++)
            input_complex[i] = new Complex(0, 0);
        return fft(input_complex);
    }

    static Complex[] fft(final short[] input) {
        final int coefficientCount = input.length;
        int n = Integer.highestOneBit(coefficientCount);
        if (n != coefficientCount)
            n *= 2;
        Complex[] input_complex = new Complex[n];
        int i = 0;
        for (; i < coefficientCount; i++)
            input_complex[i] = new Complex(input[i], 0);
        for (; i < n; i++)
            input_complex[i] = new Complex(0, 0);
        return fft(input_complex);
    }
}
