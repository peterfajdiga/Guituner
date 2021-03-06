package peterfajdiga.guituner.pitchdetection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import peterfajdiga.guituner.fourier.Complex;
import peterfajdiga.guituner.fourier.Fourier;
import peterfajdiga.guituner.general.General;

public class PitchDetectorHarmony implements PitchDetector {
    private static final double NOISE_THRESHOLD = 10.0;
    private static final double NOISE_THRESHOLD_FOCUSED = 1.0;
    private static final double MIN_FREQUENCY = 20.0;
    private static final int MAX_HARMONICS = 24;
    private static final int HARMONICS_DROP_RADIUS_INV = 64;
    private static final double FUNDAMENTAL_WEIGHT_MIN_DETECTED = 0.5;
    private static final int FOCUSED_BIN_RADIUS_INV = 102;

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
        final List<Double> harmonics = getHarmonics(freqSpace);

        if (harmonics.isEmpty()) {
            throw new PitchDetector.NoPitchFoundException();
        }
        return findFundamental(harmonics);
    }

    @NonNull
    private List<Double> getHarmonics(@NonNull final Complex[] freqSpace) {
        final int halfN = freqSpace.length / 2;

        double sum = 0.0;
        final double[] absolutes = new double[halfN];
        for (int i = 0; i < halfN; i++) {
            absolutes[i] = freqSpace[i].abs();
            sum += absolutes[i];
        }
        double floor = sum / halfN;

        final int FOCUSED_BIN_RADIUS = halfN / FOCUSED_BIN_RADIUS_INV;
        final int HARMONICS_DROP_RADIUS = halfN / HARMONICS_DROP_RADIUS_INV;

        final int startIndex;
        final List<Double> harmonics = new ArrayList<>();
        if (focusedMode) {
            final double binWidth = (double)sampleRate / (double)freqSpace.length;
            final int focusedIndex = (int)Math.round(focusedFrequency / binWidth);
            final int maxBin = General.max(absolutes, focusedIndex, FOCUSED_BIN_RADIUS);
            if (maxBin >= 0 && absolutes[maxBin] / floor > NOISE_THRESHOLD_FOCUSED) {
                harmonics.add(getFrequency(freqSpace, maxBin));
                General.dropAround(absolutes, maxBin, HARMONICS_DROP_RADIUS);
            }
            startIndex = General.getStart(focusedIndex, FOCUSED_BIN_RADIUS);
        } else {
            startIndex = 0;
        }

        final int endIndex = absolutes.length;
        while (harmonics.size() < MAX_HARMONICS) {
            final int maxBin = General.max(absolutes, startIndex, endIndex);
            if (maxBin < 0 || absolutes[maxBin] / floor < NOISE_THRESHOLD) {
                break;  // there are no more harmonics
            }
            harmonics.add(getFrequency(freqSpace, maxBin));
            General.dropAround(absolutes, maxBin, HARMONICS_DROP_RADIUS);
        }
        return harmonics;
    }

    private double getFrequency(final Complex[] X, final int index) {
        final int n_positiveFrequencies = X.length / 2;
        final double binWidth = (double)sampleRate / X.length;
        final double midFrequency = index * binWidth;
        if (index <= 0 || index >= n_positiveFrequencies - 1) {
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

    private static final double TAU_CONST_A = Math.sqrt(6.0) / 24.0;
    private static final double TAU_CONST_B = Math.sqrt(2.0 / 3.0);

    private static double tau(final double x) {
        return 0.25 * Math.log(3.0*x*x + 6.0*x + 1.0) - TAU_CONST_A * Math.log((x + 1.0 - TAU_CONST_B) / (x + 1.0 + TAU_CONST_B));
    }

    private double findFundamental(@NonNull final List<Double> harmonics) {
        final List<FundamentalCandidate> fundamentalCandidates = getFundamentalCandidates(harmonics);
        final FundamentalCandidate strongestCandidate = getStrongestFundamentalCandidate(fundamentalCandidates);
        final double fundamental = strongestCandidate != null ? strongestCandidate.getFundamental() : Double.POSITIVE_INFINITY;
        final double minDetected = minValidFrequency(harmonics);

        if (Math.abs(minDetected - fundamental) < FundamentalCandidate.MAX_ERROR) {
            // the lowest detected frequency and the calculated fundamental frequency are roughly the same
            return FUNDAMENTAL_WEIGHT_MIN_DETECTED * minDetected + (1.0 - FUNDAMENTAL_WEIGHT_MIN_DETECTED) * fundamental;
        }

        if (focusedMode) {
            final double focusedFrequencyRadius = (double)sampleRate / (double)FOCUSED_BIN_RADIUS_INV / 2.0;
            final boolean fundamentalLegal = Math.abs(fundamental - focusedFrequency) < focusedFrequencyRadius;
            final boolean minDetectedLegal = Math.abs(minDetected - focusedFrequency) < focusedFrequencyRadius;
            if (fundamentalLegal && !minDetectedLegal) {
                return fundamental;
            }
            if (minDetectedLegal && !fundamentalLegal) {
                return minDetected;
            }
        }
        return Math.min(minDetected, fundamental);
    }

    private static double minValidFrequency(@NonNull final List<Double> frequencies) {
        double min = Double.POSITIVE_INFINITY;
        for (final double v : frequencies) {
            if (v > MIN_FREQUENCY && v < min) {
                min = v;
            }
        }
        return min;
    }

    @NonNull
    private static List<FundamentalCandidate> getFundamentalCandidates(@NonNull final List<Double> harmonics) {
        final List<FundamentalCandidate> candidates = new ArrayList<>();
        for (final double value0 : harmonics) {
            for (final double value1 : harmonics) {
                final double delta = Math.abs(value1 - value0);
                if (delta < MIN_FREQUENCY) {
                    continue;
                }
                addHarmonicToFundamentalCandidates(candidates, delta);
            }
        }
        return candidates;
    }

    private static void addHarmonicToFundamentalCandidates(@NonNull final List<FundamentalCandidate> candidates, final double harmonic) {
        for (final FundamentalCandidate candidate : candidates) {
            if (candidate.addHarmonic(harmonic)) {
                return;
            }
        }

        // harmonic was not added to any existing fundamental candidates, so create a new one
        candidates.add(new FundamentalCandidate(harmonic));
    }

    @Nullable
    private static FundamentalCandidate getStrongestFundamentalCandidate(@NonNull final List<FundamentalCandidate> candidates) {
        int maxStrength = Integer.MIN_VALUE;
        FundamentalCandidate strongestCandidate = null;
        for (FundamentalCandidate candidate : candidates) {
            final int strength = candidate.getHarmonicCount();
            if (strength > maxStrength) {
                maxStrength = strength;
                strongestCandidate = candidate;
            }
        }
        return strongestCandidate;
    }
}
