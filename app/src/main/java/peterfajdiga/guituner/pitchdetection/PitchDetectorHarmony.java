package peterfajdiga.guituner.pitchdetection;

import java.util.ArrayList;
import java.util.List;

import peterfajdiga.guituner.fourier.Complex;
import peterfajdiga.guituner.fourier.Fourier;
import peterfajdiga.guituner.general.General;

public class PitchDetectorHarmony implements PitchDetector {
    private static final double NOISE_THRESHOLD = 20.0;
    private static final double NOISE_THRESHOLD_FOCUSED = 2.0;
    private static final double MIN_FREQUENCY = 20.0;
    private static final int MAX_HARMONICS = 24;
    private static final int HARMONICS_DROP_RADIUS = 16;
    private static final double FUNDAMENTAL_WEIGHT_GCD = 0.5;
    private static final double FUNDAMENTAL_WEIGHT_MINFREQ = 0.5;
    private static final int FOCUSED_BIN_RADIUS = 10;

    private final int sampleRate;
    private boolean focusedMode = false;
    private double focusedFrequency;

    public PitchDetectorHarmony(final int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public void setFocusedFrequency(final double focusedFrequency) {
        this.focusedMode = true;
        this.focusedFrequency = focusedFrequency;
    }

    public void removeFocusedFrequency() {
        this.focusedMode = false;
    }

    public double findPitch(final short[] buffer) throws PitchDetector.NoPitchFoundException {
        if (buffer.length == 0) {
            // invalid buffer
            throw new PitchDetector.NoPitchFoundException();
        }

        final Complex[] freqSpace = Fourier.fft(buffer);
        final int halfN = buffer.length / 2;

        double sum = 0.0;
        final double[] values = new double[halfN];
        for (int i = 0; i < halfN; i++) {
            values[i] = freqSpace[i].abs();
            sum += values[i];
        }
        final double floor = sum / freqSpace.length;

        final int startIndex;
        final List<Double> harmonics = new ArrayList<>();
        if (focusedMode) {
            final double binWidth = (double)sampleRate / freqSpace.length;
            final int focusedIndex = (int)Math.round(focusedFrequency / binWidth);
            final int maxBin = General.max(values, focusedIndex, FOCUSED_BIN_RADIUS);
            if (maxBin >= 0 && values[maxBin] / floor > NOISE_THRESHOLD_FOCUSED) {
                harmonics.add(getFrequency(freqSpace, maxBin));
                General.dropAround(values, maxBin, HARMONICS_DROP_RADIUS);
            }
            startIndex = General.getStart(focusedIndex, FOCUSED_BIN_RADIUS);
        } else {
            startIndex = 0;
        }

        final int endIndex = values.length;
        while (harmonics.size() < MAX_HARMONICS) {
            final int maxBin = General.max(values, startIndex, endIndex);
            if (maxBin < 0 || values[maxBin] / floor < NOISE_THRESHOLD) {
                break;  // no more harmonics
            }
            harmonics.add(getFrequency(freqSpace, maxBin));
            General.dropAround(values, maxBin, HARMONICS_DROP_RADIUS);
        }

        if (harmonics.isEmpty()) {
            throw new PitchDetector.NoPitchFoundException();
        }
        return findFundamental(buffer, harmonics);
    }

    private double getFrequency(final Complex[] X, final int index) {
        final int n_positiveFrequencies = X.length / 2;
        final double binWidth = (double)sampleRate / X.length;
        final double midFrequency = index * binWidth;
        if (index <= 0 || index >= n_positiveFrequencies-1) {
            return midFrequency;
        }
        // Quinn's Second Estimator Method for bin interpolation
        final double iAbsSq = X[index].re * X[index].re + X[index].im * X[index].im;
        final double ap = (X[index+1].re * X[index].re + X[index+1].im * X[index].im) / iAbsSq;
        final double am = (X[index-1].re * X[index].re + X[index-1].im * X[index].im) / iAbsSq;
        final double dp = -ap / (1 - ap);
        final double dm = am / (1 - am);
        final double d = (dp + dm) / 2 + tau(dp * dp) - tau(dm * dm);
        return (index + d) * binWidth;
    }

    private static final double TAU_CONST_A = Math.sqrt(6.0)/24.0;
    private static final double TAU_CONST_B = Math.sqrt(2.0/3.0);
    private double tau(final double x) {
        return 0.25 * Math.log(3.0*x*x + 6.0*x + 1.0) - TAU_CONST_A * Math.log((x + 1.0 - TAU_CONST_B) / (x + 1.0 + TAU_CONST_B));
    }

    private double findFundamental(final short[] buffer, final List<Double> values) {
        final List<FundamentalCandidate> candidates = new ArrayList<>();
        for (double value0 : values) {
            for (double value1 : values) {
                final double delta = Math.abs(value1 - value0);
                if (delta < MIN_FREQUENCY) {
                    continue;
                }
                boolean addedToExistingCandidate = false;
                for (FundamentalCandidate candidate : candidates) {
                    if (candidate.add(delta)) {
                        addedToExistingCandidate = true;
                        break;
                    }
                }
                if (!addedToExistingCandidate) {
                    candidates.add(new FundamentalCandidate(delta));
                }
            }
        }

        // return candidate with highest count
        int maxCount = Integer.MIN_VALUE;
        double gcd = Double.MAX_VALUE;
        for (FundamentalCandidate candidate : candidates) {
            if (candidate.count > maxCount) {
                maxCount = candidate.count;
                gcd = candidate.avg();
            }
        }

        double minFreq = Double.MAX_VALUE;
        for (Double value : values) {
            if (value > MIN_FREQUENCY && value < minFreq) {
                minFreq = value;
            }
        }

        if (Math.abs(minFreq - gcd) < FundamentalCandidate.MAX_ERROR) {
            return FUNDAMENTAL_WEIGHT_MINFREQ * minFreq + FUNDAMENTAL_WEIGHT_GCD * gcd;
        }

        if (focusedMode) {
            final double focusedFrequencyRadius = FOCUSED_BIN_RADIUS * ((double)sampleRate / buffer.length);
            final boolean gcdLegal     = General.nearlyEqual(gcd    , focusedFrequency, focusedFrequencyRadius);
            final boolean minFreqLegal = General.nearlyEqual(minFreq, focusedFrequency, focusedFrequencyRadius);
            if (gcdLegal && !minFreqLegal) {
                return gcd;
            }
            if (minFreqLegal && !gcdLegal) {
                return minFreq;
            }
        }
        if (minFreq < gcd) {
            return minFreq;
        }
        return gcd;
    }
}