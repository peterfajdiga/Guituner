package peterfajdiga.guituner.fourier;

public class Complex {
    public double re;
    public double im;

    public Complex(double real, double imag) {
        re = real;
        im = imag;
    }

    public double abs() {
        return Math.sqrt(re*re + im*im);
    }

    public Complex plus(final Complex b) {
        final double real = this.re + b.re;
        final double imag = this.im + b.im;
        return new Complex(real, imag);
    }

    public void plusEquals(final Complex b) {
        re += b.re;
        im += b.im;
    }

    public Complex minus(final Complex b) {
        final double real = this.re - b.re;
        final double imag = this.im - b.im;
        return new Complex(real, imag);
    }

    public void minusEquals(final Complex b) {
        re -= b.re;
        im -= b.im;
    }

    public Complex times(final Complex b) {
        final double real = this.re * b.re - this.im * b.im;
        final double imag = this.re * b.im + this.im * b.re;
        return new Complex(real, imag);
    }

    public void timesEquals(final Complex b) {
        final double real = this.re * b.re - this.im * b.im;
        final double imag = this.re * b.im + this.im * b.re;
        re = real;
        im = imag;
    }

    public static Complex n_root(final int n) {
        return new Complex(
            Math.cos(2.0 * Math.PI / n),
            Math.sin(2.0 * Math.PI / n)
        );
    }

    public static Complex n_root_neg(final int n) {
        return new Complex(
            Math.cos(-2.0 * Math.PI / n),
            Math.sin(-2.0 * Math.PI / n)
        );
    }
}
