package peterfajdiga.guituner.fourier;

class Complex {
    double re;
    double im;

    Complex(double real, double imag) {
        re = real;
        im = imag;
    }

    public double abs() {
        return Math.sqrt(re*re + im*im);
    }

    public String toString() {
        double tRe = Math.round(re * 100000) / 100000;
        double tIm = Math.round(im * 100000) / 100000;
        if (tIm == 0) return tRe + "";
        if (tRe == 0) return tIm + "i";
        if (tIm <  0) return tRe + "-" + (-tIm) + "i";
        return tRe + "+" + tIm + "i";
    }

    public Complex plus(final Complex b) {
        double real = this.re + b.re;
        double imag = this.im + b.im;
        return new Complex(real, imag);
    }

    public Complex minus(final Complex b) {
        double real = this.re - b.re;
        double imag = this.im - b.im;
        return new Complex(real, imag);
    }

    public Complex times(final Complex b) {
        double real = this.re * b.re - this.im * b.im;
        double imag = this.re * b.im + this.im * b.re;
        return new Complex(real, imag);
    }

    public static Complex n_root(int n) {
        return new Complex(
                Math.cos(2 * Math.PI / n),
                Math.sin(2 * Math.PI / n)
        );
    }
}
