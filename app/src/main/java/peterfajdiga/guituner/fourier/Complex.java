package peterfajdiga.guituner.fourier;

class Complex {
    double re;
    double im;

    Complex(double real, double imag) {
        re = real;
        im = imag;
    }

    Complex(final Complex original) {
        this.re = original.re;
        this.im = original.im;
    }

    public double abs() {
        return Math.sqrt(re*re + im*im);
    }

    public String toString() {
        final double tRe = Math.round(re * 100000) / 100000;
        final double tIm = Math.round(im * 100000) / 100000;
        if (tIm == 0) return tRe + "";
        if (tRe == 0) return tIm + "i";
        if (tIm <  0) return tRe + "-" + (-tIm) + "i";
        return tRe + "+" + tIm + "i";
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
                Math.cos(2 * Math.PI / n),
                Math.sin(2 * Math.PI / n)
        );
    }

    public static Complex n_root_neg(final int n) {
        return new Complex(
                Math.cos(-2 * Math.PI / n),
                Math.sin(-2 * Math.PI / n)
        );
    }
}
