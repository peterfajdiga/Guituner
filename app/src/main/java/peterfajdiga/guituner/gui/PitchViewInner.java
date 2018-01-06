package peterfajdiga.guituner.gui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import peterfajdiga.guituner.general.Tone;

class PitchViewInner extends View {

    private static final int HEIGHT_MULT = 20;
    private static final float TONE_OFFSET_X_RATIO = 0.5f;
    private static final float FREQ_OFFSET_X_RATIO = 0.94f;

    // these need to be multiplied by dp
    private static final float EDGE_TONE_OFFSET_Y = 46.0f;
    private static final float LINE_TEXT_SPACING = 6.0f;
    private static final float TONE_LINE_LENGTH = 96.0f;
    private static final float SELECTION_WIDTH = 48.0f;

    private static final Paint paint_tone = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint paint_freq = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint paint_selection = new Paint();

    private final android.graphics.Rect textBounds = new Rect();

    private float dp;
    private int width;
    int height;

    double detectedFrequency = 0.0;
    Tone selectedTone = null;

    public PitchViewInner(Context context) {
        super(context);
        init(context);
    }


    private void init(final Context context) {
        final Resources r = context.getResources();
        dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics());

        paint_tone.setTextSize(24 * dp);
        paint_tone.setTextAlign(Paint.Align.CENTER);
        paint_tone.setColor(r.getColor(android.R.color.primary_text_light));
        paint_tone.setStrokeWidth(dp);

        paint_freq.setTextSize(12 * dp);
        paint_freq.setTextAlign(Paint.Align.RIGHT);
        paint_freq.setColor(r.getColor(android.R.color.tertiary_text_light));
        paint_freq.setStrokeWidth(dp);

        paint_selection.setColor(Color.LTGRAY);
    }


    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        // Compute the height required to render the view
        // Assume Width will always be MATCH_PARENT.
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = width * HEIGHT_MULT;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        width  = canvas.getWidth();   // probably unnecessary
        height = canvas.getHeight();  // probably unnecessary


        // draw tones
        final float toneX = width * TONE_OFFSET_X_RATIO;
        for (Tone tone : tones) {
            final float toneY = getFrequencyY(tone.frequency);

            if (tone == selectedTone) {
                final float selectionHalfWidth = SELECTION_WIDTH * dp / 2.0f;
                canvas.drawRect(0, toneY - selectionHalfWidth, width, toneY + selectionHalfWidth, paint_selection);
            }

            paint_tone.getTextBounds(tone.name, 0, tone.name.length(), textBounds);

            canvas.drawText(tone.name, toneX, getCenteredY(toneY), paint_tone);

            final float toneLineLeftStartX = getTextLeft(toneX, paint_tone) - LINE_TEXT_SPACING * dp;
            final float toneLineRightStartX = getTextRight(toneX, paint_tone) + LINE_TEXT_SPACING * dp;
            canvas.drawLine(
                    toneLineLeftStartX - TONE_LINE_LENGTH, toneY,
                    toneLineLeftStartX, toneY,
                    paint_tone
            );
            canvas.drawLine(
                    toneLineRightStartX, toneY,
                    toneLineRightStartX + TONE_LINE_LENGTH, toneY,
                    paint_tone
            );
        }


        // draw detected frequency
        if (detectedFrequency > 0.0) {
            final String freqText = String.format("%.1f Hz", detectedFrequency);
            final float freqX = width * FREQ_OFFSET_X_RATIO;
            final float freqY = getFrequencyY(detectedFrequency);

            paint_freq.getTextBounds(freqText, 0, freqText.length(), textBounds);

            canvas.drawText(freqText, freqX, getCenteredY(freqY), paint_freq);

            canvas.drawLine(
                    0.0f, freqY,
                    getTextLeft(freqX, paint_freq) - LINE_TEXT_SPACING * dp, freqY,
                    paint_freq
            );
            canvas.drawLine(
                    getTextRight(freqX, paint_freq) + LINE_TEXT_SPACING * dp, freqY,
                    width, freqY,
                    paint_freq
            );
        }
    }

    // getTextBounds must be called before this
    private float getCenteredY(final float y) {
        return y - (textBounds.bottom + textBounds.top) / 2.0f;
    }

    // getTextBounds must be called before this
    private float getTextLeft(final float textX, final Paint paint) {
        final int textWidth = textBounds.right + textBounds.left;
        switch (paint.getTextAlign()) {
            default:
            case LEFT: return textX;
            case CENTER: return textX - textWidth / 2.0f;
            case RIGHT: return textX - textWidth;
        }
    }

    // getTextBounds must be called before this
    private float getTextRight(final float textX, final Paint paint) {
        final int textWidth = textBounds.right + textBounds.left;
        switch (paint.getTextAlign()) {
            case LEFT: return textX + textWidth;
            case CENTER: return textX + textWidth / 2.0f;
            default:
            case RIGHT: return textX;
        }
    }

    float getFrequencyY(final double frequency) {
        final float minY = EDGE_TONE_OFFSET_Y * dp;
        final float maxY = height - EDGE_TONE_OFFSET_Y * dp;

        final double minLogFreq = Math.log(tones[0].frequency);
        final double maxLogFreq = Math.log(tones[tones.length-1].frequency);
        final double logFreq = Math.log(frequency);

        final double freq01 = (logFreq - minLogFreq) / (maxLogFreq - minLogFreq);
        return minY + (float)freq01 * (maxY - minY);
    }

    private double getFrequencyFromY(final float y) {
        final float minY = EDGE_TONE_OFFSET_Y * dp;
        final float maxY = height - EDGE_TONE_OFFSET_Y * dp;

        final double minLogFreq = Math.log(tones[0].frequency);
        final double maxLogFreq = Math.log(tones[tones.length-1].frequency);

        final double freq01 = (y - minY) / (maxY - minY);
        final double logFreq = (freq01 * (maxLogFreq - minLogFreq)) + minLogFreq;
        return Math.exp(logFreq);
    }

    void selectToneByY(final float y) {
        final double frequency = getFrequencyFromY(y);
        // find nearest Tone
        double minDiff = Double.MAX_VALUE;
        for (Tone tone : tones) {
            final double diff = Math.abs(tone.frequency - frequency);
            if (diff < minDiff) {
                minDiff = diff;
                selectedTone = tone;
            }
        }
        invalidate();
    }



    private static final Tone[] tones = {
            Tone.A0,
            Tone.A0s,
            Tone.B0,
            Tone.C1,
            Tone.C1s,
            Tone.D1,
            Tone.D1s,
            Tone.E1,
            Tone.F1,
            Tone.F1s,
            Tone.G1,
            Tone.G1s,
            Tone.A1,
            Tone.A1s,
            Tone.B1,
            Tone.C2,
            Tone.C2s,
            Tone.D2,
            Tone.D2s,
            Tone.E2,
            Tone.F2,
            Tone.F2s,
            Tone.G2,
            Tone.G2s,
            Tone.A2,
            Tone.A2s,
            Tone.B2,
            Tone.C3,
            Tone.C3s,
            Tone.D3,
            Tone.D3s,
            Tone.E3,
            Tone.F3,
            Tone.F3s,
            Tone.G3,
            Tone.G3s,
            Tone.A3,
            Tone.A3s,
            Tone.B3,
            Tone.C4,
            Tone.C4s,
            Tone.D4,
            Tone.D4s,
            Tone.E4,
            Tone.F4,
            Tone.F4s,
            Tone.G4,
            Tone.G4s,
            Tone.A4,
            Tone.A4s,
            Tone.B4,
            Tone.C5,
            Tone.C5s,
            Tone.D5,
            Tone.D5s,
            Tone.E5,
            Tone.F5,
            Tone.F5s,
            Tone.G5,
            Tone.G5s,
            Tone.A5,
            Tone.A5s,
            Tone.B5,
            Tone.C6,
            Tone.C6s,
            Tone.D6,
            Tone.D6s,
            Tone.E6,
            Tone.F6,
            Tone.F6s,
            Tone.G6,
            Tone.G6s,
            Tone.A6,
            Tone.A6s,
            Tone.B6,
            Tone.C7,
            Tone.C7s,
            Tone.D7,
            Tone.D7s,
            Tone.E7,
            Tone.F7,
            Tone.F7s,
            Tone.G7,
            Tone.G7s
    };
}
