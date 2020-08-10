package peterfajdiga.guituner.gui.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import peterfajdiga.guituner.R;
import peterfajdiga.guituner.general.Pitch;

class PitchViewDisplay extends View {
    private static final boolean HIGH_F_ON_TOP = true;
    private static final float PITCH_OFFSET_X_RATIO = 0.5f;
    private static final float FREQ_OFFSET_X_RATIO = 0.94f;

    // these need to be multiplied by dp
    private static final float PITCH_FULL_WIDTH = 144.0f;
    private static final float LINE_TEXT_SPACING = 6.0f;
    private static final float PITCH_LINE_LENGTH = 96.0f;

    private static final Paint paint_pitch = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint paint_pitch_inactive = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint paint_freq = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint paint_freq_light = new Paint();

    private final float dp;
    private int width, height;
    private float edgePitchOffsetY, pitchLineStartLeftX, pitchLineStartRightX, pitchLineEndLeftX, pitchLineEndRightX;

    private final android.graphics.Rect textBounds = new Rect();

    private Pitch[] pitches;
    private Pitch highlightedPitch = null;
    private double detectedFrequency = 0.0;

    public PitchViewDisplay(final Context context) {
        super(context);

        final Resources r = context.getResources();
        dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics());

        paint_pitch.setTextSize(24 * dp);
        paint_pitch.setTextAlign(Paint.Align.CENTER);
        paint_pitch.setColor(r.getColor(R.color.primary_text));
        paint_pitch.setStrokeWidth(dp);

        paint_pitch_inactive.setTextSize(24 * dp);
        paint_pitch_inactive.setTextAlign(Paint.Align.CENTER);
        paint_pitch_inactive.setColor(r.getColor(R.color.secondary_text));
        paint_pitch_inactive.setStrokeWidth(dp);

        paint_freq.setTextSize(12 * dp);
        paint_freq.setTextAlign(Paint.Align.RIGHT);
        paint_freq.setColor(r.getColor(R.color.secondary_text));
        paint_freq.setStrokeWidth(dp);

        paint_freq_light.setColor(r.getColor(R.color.secondary_text));
        paint_freq_light.setStrokeWidth(1);
    }

    public void setDetectedFrequency(final double frequency) {
        this.detectedFrequency = frequency;
        invalidate();
    }

    void setPitches(@NonNull final Pitch[] pitches) {
        this.pitches = pitches;
    }

    public Pitch getPitchFromY(final float y) {
        return getNearestPitch(getFrequencyFromY(y));
    }

    public void selectPitch(final Pitch pitch) {
        highlightedPitch = pitch;
        invalidate();
    }

    public void unselectPitch() {
        highlightedPitch = null;
        invalidate();
    }

    @Nullable
    public Pitch getHighlightedPitch() {
        return highlightedPitch;
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        width = View.MeasureSpec.getSize(widthMeasureSpec);
        height = (int)(PITCH_FULL_WIDTH* dp * pitches.length) + heightMeasureSpec;
        edgePitchOffsetY = (float)heightMeasureSpec / 2;
        setMeasuredDimension(width, height);

        paint_pitch.getTextBounds(Pitch.A4s.toString(), 0, Pitch.A4s.toString().length(), textBounds);
        final float x = width *PITCH_OFFSET_X_RATIO;
        pitchLineStartLeftX = getTextLeft(x, paint_pitch) - LINE_TEXT_SPACING * dp;
        pitchLineStartRightX = getTextRight(x, paint_pitch) + LINE_TEXT_SPACING * dp;
        pitchLineEndLeftX = pitchLineStartLeftX - PITCH_LINE_LENGTH;
        pitchLineEndRightX = pitchLineStartRightX + PITCH_LINE_LENGTH;
    }

    @Override
    protected void onDraw(final Canvas canvas) {

        // draw detected frequency
        if (detectedFrequency > 0.0) {
            final String freqText = String.format("%.1f Hz", detectedFrequency);
            final float freqX = width * FREQ_OFFSET_X_RATIO;
            final float freqY = getFrequencyY(detectedFrequency);

            paint_freq.getTextBounds(freqText, 0, freqText.length(), textBounds);

            canvas.drawText(freqText, freqX, getCenteredY(freqY), paint_freq);

            canvas.drawLine(
                0.0f, freqY,
                pitchLineEndLeftX, freqY,
                paint_freq
            );
            canvas.drawLine(
                pitchLineEndLeftX, freqY,
                pitchLineEndRightX, freqY,
                paint_freq_light
            );
            canvas.drawLine(
                pitchLineEndRightX, freqY,
                getTextLeft(freqX, paint_freq) - LINE_TEXT_SPACING * dp, freqY,
                paint_freq
            );
            canvas.drawLine(
                getTextRight(freqX, paint_freq) + LINE_TEXT_SPACING * dp, freqY,
                width, freqY,
                paint_freq
            );

            canvas.drawPath(getTriangle(pitchLineEndLeftX, freqY, true ), paint_freq);
            canvas.drawPath(getTriangle(pitchLineEndRightX, freqY, false), paint_freq);
        }

        // draw pitches
        final float pitchX = width *PITCH_OFFSET_X_RATIO;
        for (Pitch pitch : pitches) {
            final float pitchY = getFrequencyY(pitch.frequency);

            final Paint paint = highlightedPitch == null || highlightedPitch == pitch ? paint_pitch : paint_pitch_inactive;
            paint.getTextBounds(pitch.toString(), 0, pitch.toString().length(), textBounds);

            canvas.drawText(pitch.toString(), pitchX, getCenteredY(pitchY), paint);

            canvas.drawLine(
                pitchLineStartLeftX - PITCH_LINE_LENGTH, pitchY,
                pitchLineStartLeftX, pitchY,
                paint
            );
            canvas.drawLine(
                pitchLineStartRightX, pitchY,
                pitchLineStartRightX + PITCH_LINE_LENGTH, pitchY,
                paint
            );
        }
    }

    private Path getTriangle(float x, final float y, final boolean pointRight) {
        final float size = 4.0f * dp;  // TODO: define constant
        final Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(x, y);
        if (pointRight) {
            x -= size;
        } else {
            x += size;
        }
        path.lineTo(x, y - size);
        path.lineTo(x, y + size);
        path.close();
        return path;
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
            case LEFT:
                return textX;
            case CENTER:
                return textX - textWidth / 2.0f;
            case RIGHT:
                return textX - textWidth;
        }
    }

    // getTextBounds must be called before this
    private float getTextRight(final float textX, final Paint paint) {
        final int textWidth = textBounds.right + textBounds.left;
        switch (paint.getTextAlign()) {
            case LEFT:
                return textX + textWidth;
            case CENTER:
                return textX + textWidth / 2.0f;
            default:
            case RIGHT:
                return textX;
        }
    }

    float getFrequencyY(final double frequency) {
        final float minY = edgePitchOffsetY;
        final float maxY = height - edgePitchOffsetY;

        final double minLogFreq = Math.log(pitches[0].frequency);
        final double maxLogFreq = Math.log(pitches[pitches.length-1].frequency);
        final double logFreq = Math.log(frequency);

        double freq01 = (logFreq - minLogFreq) / (maxLogFreq - minLogFreq);
        if (HIGH_F_ON_TOP) {
            freq01 = 1 - freq01;
        }
        return minY + (float)freq01 * (maxY - minY);
    }

    private double getFrequencyFromY(final float y) {
        final float minY = edgePitchOffsetY;
        final float maxY = height - edgePitchOffsetY;

        final double minLogFreq = Math.log(pitches[0].frequency);
        final double maxLogFreq = Math.log(pitches[pitches.length-1].frequency);

        double freq01 = (y - minY) / (maxY - minY);
        if (HIGH_F_ON_TOP) {
            freq01 = 1 - freq01;
        }
        final double logFreq = (freq01 * (maxLogFreq - minLogFreq)) + minLogFreq;
        return Math.exp(logFreq);
    }

    private Pitch getNearestPitch(final double frequency) {
        // TODO: optimise
        double minDiff = Double.POSITIVE_INFINITY;
        Pitch nearestPitch = null;
        for (Pitch pitch : pitches) {
            final double diff = Math.abs(pitch.frequency - frequency);
            if (diff < minDiff) {
                minDiff = diff;
                nearestPitch = pitch;
            }
        }
        return nearestPitch;
    }
}
