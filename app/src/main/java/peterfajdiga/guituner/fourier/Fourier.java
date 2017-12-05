package peterfajdiga.guituner.fourier;

public class Fourier {
    private Fourier() {}

    static Complex[] fft(final Complex[] input, final int step, final int start) {
        final int n = input.length / step;
        if (n == 1)
            return new Complex[]{input[start]};
        final int halfLength = n / 2;

        final Complex[] tableEven = fft(input, step*2, start);
        final Complex[] tableOdd  = fft(input, step*2, start+step);

        final Complex w = Complex.n_root(n);
        Complex wk = new Complex(1, 0);
        final Complex[] y = new Complex[n];
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
        return fft(input_complex, 1, 0);
    }

    static Complex[] fft(final short[] input) {
        final long startTime = System.currentTimeMillis();
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
        final Complex[] result = fft(input_complex, 1, 0);
        System.err.println(System.currentTimeMillis() - startTime);
        return result;
    }
}
