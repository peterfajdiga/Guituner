package peterfajdiga.guituner.gui.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.Rect;
import android.os.Build;
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
    private static final float PITCH_FULL_RANGE_Y = 144.0f;
    private static final float LINE_TEXT_SPACING = 6.0f;
    private static final float PITCH_LINE_LENGTH = 32.0f;
    private static final float FREQ_ARROW_TRIANGLE_SIZE = 4.0f;
    private static final float HIGHLIGHT_BG_HEIGHT = 48.0f;  // TODO: get from resources instead

    private static final Paint paint_pitch = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint paint_pitch_inactive = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint paint_freq = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint paint_freq_light = new Paint();
    private static final Paint paint_highlight_bg = new Paint();

    private final float dp;
    private int width, height;
    private float edgePitchOffsetY, pitchLineStartLeftX, pitchLineStartRightX, pitchLineEndLeftX, pitchLineEndRightX;

    private final android.graphics.Rect textBounds = new Rect();
    private Picture pitchLabelsCache, pitchLabelsCacheInactive;

    private Pitch[] pitches;
    private Pitch highlightedPitch = null;
    private double detectedFrequency = 0.0;

    public PitchViewDisplay(final Context context) {
        super(context);

        final Resources r = context.getResources();
        dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics());

        paint_highlight_bg.setColor(r.getColor(R.color.background));

        paint_pitch.setTextSize(24 * dp);
        paint_pitch.setTextAlign(Paint.Align.CENTER);
        paint_pitch.setColor(r.getColor(R.color.primary_text));
        paint_pitch.setStrokeWidth(dp);

        paint_pitch_inactive.setTextSize(24 * dp);
        paint_pitch_inactive.setTextAlign(Paint.Align.CENTER);
        paint_pitch_inactive.setColor(r.getColor(R.color.dimmed_text));
        paint_pitch_inactive.setStrokeWidth(dp);

        paint_freq.setTextSize(12 * dp);
        paint_freq.setTextAlign(Paint.Align.CENTER);
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
        if (pitch == highlightedPitch) {
            return;
        }
        highlightedPitch = pitch;
        invalidate();
    }

    public void unselectPitch() {
        selectPitch(null);
    }

    @Nullable
    public Pitch getHighlightedPitch() {
        return highlightedPitch;
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = (int)(PITCH_FULL_RANGE_Y* dp * pitches.length) + heightMeasureSpec;
        edgePitchOffsetY = (float)heightMeasureSpec / 2;
        setMeasuredDimension(width, height);

        getPitchTextBounds(Pitch.As4, paint_pitch, textBounds);
        final float x = width * PITCH_OFFSET_X_RATIO;
        pitchLineStartLeftX = getTextLeft(x, paint_pitch) - LINE_TEXT_SPACING * dp;
        pitchLineStartRightX = getTextRight(x, paint_pitch) + LINE_TEXT_SPACING * dp;
        pitchLineEndLeftX = pitchLineStartLeftX - PITCH_LINE_LENGTH * dp;
        pitchLineEndRightX = pitchLineStartRightX + PITCH_LINE_LENGTH * dp;

        pitchLabelsCache = null;
        pitchLabelsCacheInactive = null;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        if (highlightedPitch == null) {
            if (pitchLabelsCache == null) {
                pitchLabelsCache = new Picture();
                final Canvas cacheCanvas = pitchLabelsCache.beginRecording(width, height);
                drawPitchLabels(cacheCanvas, paint_pitch);
                pitchLabelsCache.endRecording();
            }
            pitchLabelsCache.draw(canvas);

        } else {
            if (pitchLabelsCacheInactive == null) {
                pitchLabelsCacheInactive = new Picture();
                final Canvas cacheCanvas = pitchLabelsCacheInactive.beginRecording(width, height);
                drawPitchLabels(cacheCanvas, paint_pitch_inactive);
                pitchLabelsCacheInactive.endRecording();
            }
            pitchLabelsCacheInactive.draw(canvas);

            drawHighlightBackground(canvas, highlightedPitch, paint_highlight_bg);
            drawPitchLabel(canvas, highlightedPitch, paint_pitch);
        }

        if (detectedFrequency > 0.0) {
            drawDetectedFrequency(canvas);
        }
    }

    private void drawDetectedFrequency(@NonNull final Canvas canvas) {
        final String freqText = String.format("%.1f Hz", detectedFrequency);
        final float triangleWidth = FREQ_ARROW_TRIANGLE_SIZE * dp;
        final float freqLineEndLeftX = 0.0f;
        final float freqLineStartLeftX = pitchLineEndLeftX - triangleWidth;
        final float freqLineStartRightX = pitchLineEndRightX + triangleWidth;
        final float freqLineEndRightX = width;
        final float freqX = (freqLineStartRightX + freqLineEndRightX) / 2.0f;
        final float freqY = getFrequencyY(detectedFrequency);

        paint_freq.getTextBounds(freqText, 0, freqText.length(), textBounds);

        // ->-<
        canvas.drawLine(
            freqLineEndLeftX, freqY,
            freqLineStartLeftX, freqY,
            paint_freq
        );
        canvas.drawPath(getTriangle(pitchLineEndLeftX, freqY, true), paint_freq);
        canvas.drawLine(
            pitchLineEndLeftX, freqY,
            pitchLineEndRightX, freqY,
            paint_freq_light
        );
        canvas.drawPath(getTriangle(pitchLineEndRightX, freqY, false), paint_freq);

        // -Hz-
        canvas.drawLine(
            freqLineStartRightX, freqY,
            getTextLeft(freqX, paint_freq) - LINE_TEXT_SPACING * dp, freqY,
            paint_freq
        );
        canvas.drawText(freqText, freqX, getCenteredY(freqY), paint_freq);
        canvas.drawLine(
            getTextRight(freqX, paint_freq) + LINE_TEXT_SPACING * dp, freqY,
            freqLineEndRightX, freqY,
            paint_freq
        );

    }

    private void drawPitchLabels(@NonNull final Canvas canvas, final @NonNull Paint paint) {
        for (Pitch pitch : pitches) {
            drawPitchLabel(canvas, pitch, paint);
        }
    }

    private void drawPitchLabel(@NonNull final Canvas canvas, @NonNull final Pitch pitch, @NonNull final Paint paint) {
        final float pitchX = width * PITCH_OFFSET_X_RATIO;
        final float pitchY = getFrequencyY(pitch.frequency);

        getPitchTextBounds(pitch, paint, textBounds);
        canvas.drawText(pitch.toCharSequence(), 0, pitch.toCharSequence().length(), pitchX, getCenteredY(pitchY), paint);

        canvas.drawLine(
            pitchLineStartLeftX - PITCH_LINE_LENGTH * dp, pitchY,
            pitchLineStartLeftX, pitchY,
            paint
        );
        canvas.drawLine(
            pitchLineStartRightX, pitchY,
            pitchLineStartRightX + PITCH_LINE_LENGTH * dp, pitchY,
            paint
        );
    }

    private void drawHighlightBackground(@NonNull final Canvas canvas, @NonNull final Pitch pitch, @NonNull final Paint paint) {
        final float pitchY = getFrequencyY(pitch.frequency);
        final float dy = HIGHLIGHT_BG_HEIGHT * dp / 2.0f;
        canvas.drawRect(0.0f, pitchY-dy, width, pitchY+dy, paint);
    }

    private Path getTriangle(float x, final float y, final boolean pointRight) {
        final float size = FREQ_ARROW_TRIANGLE_SIZE * dp;
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

    private static void getPitchTextBounds(@NonNull final Pitch pitch, @NonNull final Paint paint, @NonNull final android.graphics.Rect bounds) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            final CharSequence pitchString = pitch.toCharSequence();
            paint.getTextBounds(pitchString, 0, pitchString.length(), bounds);
        } else {
            final String pitchString = pitch.toString();
            paint.getTextBounds(pitchString, 0, pitchString.length(), bounds);
        }
    }
}
