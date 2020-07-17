package peterfajdiga.guituner.fourier;

import peterfajdiga.guituner.general.General;

public class Fourier {
    private Fourier() {}  // make class static

    private static Complex[] bitReverseCopy(final Complex[] original) {
        final int n = original.length;
        final int shift = Integer.numberOfLeadingZeros(n-1);
        final Complex[] copy = new Complex[n];
        for (int k = 0; k < n; k++) {
            final int k_rev = Integer.reverse(k) >>> shift;
            copy[k_rev] = original[k];
        }
        return copy;
    }

    public static Complex[] fft(final Complex[] input) {
        Complex[] A = bitReverseCopy(input);
        final int n = input.length;
        final int logn = Integer.numberOfTrailingZeros(n);
        for (int s = 0; s < logn; s++) {
            final int m = 2 << s;  // m = 2^(s+1)
            final Complex wm = Complex.n_root_neg(m);
            for (int k = 0; k < n; k += m) {
                Complex w = new Complex(1, 0);
                final int m_half = m/2;
                for (int j = 0; j < m_half; j++) {
                    final Complex t = w.times(A[k + j + m_half]);
                    A[k + j + m_half] = A[k+j].minus(t);
                    A[k+j].plusEquals(t);
                    w.timesEquals(wm);
                }
            }
        }
        return A;
    }

    public static Complex[] fft(final double[] input) {
        final int coefficientCount = input.length;
        final int n = General.ceilPow2(coefficientCount);
        Complex[] input_complex = new Complex[n];
        int i = 0;
        for (; i < coefficientCount; i++)
            input_complex[i] = new Complex(input[i], 0);
        for (; i < n; i++)
            input_complex[i] = new Complex(0, 0);
        return fft(input_complex);
    }

    public static Complex[] fft(final short[] input) {
        final int coefficientCount = input.length;
        final int n = General.ceilPow2(coefficientCount);
        Complex[] input_complex = new Complex[n];
        int i = 0;
        for (; i < coefficientCount; i++)
            input_complex[i] = new Complex(input[i], 0);
        for (; i < n; i++)
            input_complex[i] = new Complex(0, 0);
        return fft(input_complex);
    }
}
