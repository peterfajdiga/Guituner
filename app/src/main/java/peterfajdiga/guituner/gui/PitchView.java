package peterfajdiga.guituner.gui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import java.util.Arrays;

import peterfajdiga.guituner.general.Tone;

public class PitchView extends ScrollView {

    private static final int SNAP_DELTA_Y = 10;
    private static final float TONE_PRESS_MOVE_TOLERANCE = 10;

    private OnFocusChangedListener onFocusChangedListener;
    private PitchViewInner display;
    private boolean allowToneSelection = true;
    private boolean fingerOnScreen = false;
    private int lastDeltaY;
    private float lastTouchX, lastTouchY;

    public PitchView(Context context) {
        super(context);
        init(context);
    }

    public PitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public PitchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    private void init(final Context context) {
        display = new PitchViewInner(context);
        addView(display);
    }


    public void setFrequency(final double frequency) {
        if (frequency <= 0.0) {
            return;
        }
        display.detectedFrequency = frequency;
        display.invalidate();
        if (display.selectedTone == null && !fingerOnScreen) {
            focusOnFrequency(frequency);
        }
    }

    public void setHighestFrequency(final double frequency) {
        int n_detectableTones = 0;
        for (Tone tone : PitchViewInner.toneList) {
            if (tone.frequency > frequency) break;
            n_detectableTones++;
        }
        display.tones = Arrays.copyOf(PitchViewInner.toneList, n_detectableTones);

        if (display.selectedTone != null && display.selectedTone.frequency > frequency) {
            removeFocus();
        }
    }

    private void focusOnFrequency(final double frequency) {
        final int screenHeightHalf = getHeight() / 2;
        final int y = (int)display.getFrequencyY(frequency) - screenHeightHalf;
        allowToneSelection = false;
        smoothScrollTo(0, y);
    }


    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        final boolean retval = super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                fingerOnScreen = true;
                allowToneSelection = true;
                lastDeltaY = 0;
                lastTouchX = event.getX();
                lastTouchY = event.getY();
                break;
            }
            case MotionEvent.ACTION_UP: {
                fingerOnScreen = false;
                if (Math.abs(event.getX() - lastTouchX) + Math.abs(event.getY() - lastTouchY) < TONE_PRESS_MOVE_TOLERANCE) {
                    display.selectToneByY(event.getY() + getScrollY());
                    focusOnFrequency(display.selectedTone.frequency);
                    dispatchFocusChanged();
                } else if (display.selectedTone != null && lastDeltaY < SNAP_DELTA_Y) {
                    focusOnFrequency(display.selectedTone.frequency);
                }
                break;
            }
        }
        return retval;
    }

    @Override
    protected void onScrollChanged (final int l,
                                    final int t,
                                    final int oldl,
                                    final int oldt) {
        if (allowToneSelection) {
            selectCenterTone();
            lastDeltaY = Math.abs(t - oldt);
            if (lastDeltaY < SNAP_DELTA_Y && !fingerOnScreen) {
                focusOnFrequency(display.selectedTone.frequency);
            }
        }
    }

    private void selectCenterTone() {
        final int y = getScrollY() + getHeight() / 2;
        display.selectToneByY(y);
        dispatchFocusChanged();
    }

    // don't call dispatchFocusChanged afterwards
    public void removeFocus() {
        display.selectedTone = null;
        if (onFocusChangedListener != null) {
            onFocusChangedListener.onFocusChanged(0.0);
        }
        display.invalidate();
    }
    public void setFocus(final Tone tone) {
        display.selectedTone = tone;
        focusOnFrequency(tone.frequency);
        dispatchFocusChanged();
    }
    private void dispatchFocusChanged() {
        if (onFocusChangedListener != null) {
            onFocusChangedListener.onFocusChanged(display.selectedTone.frequency);
        }
    }



    public interface OnFocusChangedListener {
        // when focusedFrequency == 0.0, there is no focus
        void onFocusChanged(double focusedFrequency);
    }

    public void setOnFocusChangedListener(final OnFocusChangedListener listener) {
        this.onFocusChangedListener = listener;
    }



    private static class PitchViewInner extends View {

        private static final boolean HIGH_F_ON_TOP = true;
        private static final float TONE_OFFSET_X_RATIO = 0.5f;
        private static final float FREQ_OFFSET_X_RATIO = 0.94f;

        // these need to be multiplied by dp
        private static final float TONE_FULL_WIDTH = 144.0f;
        private static final float LINE_TEXT_SPACING = 6.0f;
        private static final float TONE_LINE_LENGTH = 96.0f;

        private static final Paint paint_tone = new Paint(Paint.ANTI_ALIAS_FLAG);
        private static final Paint paint_tone_inactive = new Paint(Paint.ANTI_ALIAS_FLAG);
        private static final Paint paint_freq = new Paint(Paint.ANTI_ALIAS_FLAG);
        private static final Paint paint_freq_light = new Paint();

        private float dp;
        private int width, height;
        private float edgeToneOffsetY, toneLineStartLeftX, toneLineStartRightX, toneLineEndLeftX, toneLineEndRightX;

        private final android.graphics.Rect textBounds = new Rect();

        private double detectedFrequency = 0.0;
        private Tone[] tones = toneList;
        private Tone selectedTone = null;

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

            paint_tone_inactive.setTextSize(24 * dp);
            paint_tone_inactive.setTextAlign(Paint.Align.CENTER);
            paint_tone_inactive.setColor(r.getColor(android.R.color.tertiary_text_light));
            paint_tone_inactive.setStrokeWidth(dp);

            paint_freq.setTextSize(12 * dp);
            paint_freq.setTextAlign(Paint.Align.RIGHT);
            paint_freq.setColor(r.getColor(android.R.color.tertiary_text_light));
            paint_freq.setStrokeWidth(dp);

            paint_freq_light.setColor(r.getColor(android.R.color.tertiary_text_light));
            paint_freq_light.setStrokeWidth(1);
        }


        @Override
        protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
            width = MeasureSpec.getSize(widthMeasureSpec);
            height = (int)(TONE_FULL_WIDTH * dp * tones.length) + heightMeasureSpec;
            edgeToneOffsetY = (float)heightMeasureSpec / 2;
            setMeasuredDimension(width, height);

            paint_tone.getTextBounds(Tone.A4s.name, 0, Tone.A4s.name.length(), textBounds);
            final float x = width * TONE_OFFSET_X_RATIO;
            toneLineStartLeftX = getTextLeft(x, paint_tone) - LINE_TEXT_SPACING * dp;
            toneLineStartRightX = getTextRight(x, paint_tone) + LINE_TEXT_SPACING * dp;
            toneLineEndLeftX = toneLineStartLeftX - TONE_LINE_LENGTH;
            toneLineEndRightX = toneLineStartRightX + TONE_LINE_LENGTH;
        }

        @Override
        protected void onDraw(Canvas canvas) {

            // draw detected frequency
            if (detectedFrequency > 0.0) {
                final String freqText = String.format("%.1f Hz", detectedFrequency);
                final float freqX = width * FREQ_OFFSET_X_RATIO;
                final float freqY = getFrequencyY(detectedFrequency);

                paint_freq.getTextBounds(freqText, 0, freqText.length(), textBounds);

                canvas.drawText(freqText, freqX, getCenteredY(freqY), paint_freq);

                canvas.drawLine(
                        0.0f, freqY,
                        toneLineEndLeftX, freqY,
                        paint_freq
                );
                canvas.drawLine(
                        toneLineEndLeftX, freqY,
                        toneLineEndRightX, freqY,
                        paint_freq_light
                );
                canvas.drawLine(
                        toneLineEndRightX, freqY,
                        getTextLeft(freqX, paint_freq) - LINE_TEXT_SPACING * dp, freqY,
                        paint_freq
                );
                canvas.drawLine(
                        getTextRight(freqX, paint_freq) + LINE_TEXT_SPACING * dp, freqY,
                        width, freqY,
                        paint_freq
                );

                canvas.drawPath(getTriangle(toneLineEndLeftX , freqY, true ), paint_freq);
                canvas.drawPath(getTriangle(toneLineEndRightX, freqY, false), paint_freq);
            }

            // draw tones
            final float toneX = width * TONE_OFFSET_X_RATIO;
            for (Tone tone : tones) {
                final float toneY = getFrequencyY(tone.frequency);

                final Paint paint = selectedTone == null || selectedTone == tone ? paint_tone : paint_tone_inactive;
                paint.getTextBounds(tone.name, 0, tone.name.length(), textBounds);

                canvas.drawText(tone.name, toneX, getCenteredY(toneY), paint);

                canvas.drawLine(
                        toneLineStartLeftX - TONE_LINE_LENGTH, toneY,
                        toneLineStartLeftX, toneY,
                        paint
                );
                canvas.drawLine(
                        toneLineStartRightX, toneY,
                        toneLineStartRightX + TONE_LINE_LENGTH, toneY,
                        paint
                );
            }
        }

        private Path getTriangle(float x, float y, final boolean pointRight) {
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
            final float minY = edgeToneOffsetY;
            final float maxY = height - edgeToneOffsetY;

            final double minLogFreq = Math.log(tones[0].frequency);
            final double maxLogFreq = Math.log(tones[tones.length-1].frequency);
            final double logFreq = Math.log(frequency);

            double freq01 = (logFreq - minLogFreq) / (maxLogFreq - minLogFreq);
            if (HIGH_F_ON_TOP) {
                freq01 = 1 - freq01;
            }
            return minY + (float)freq01 * (maxY - minY);
        }

        private double getFrequencyFromY(final float y) {
            final float minY = edgeToneOffsetY;
            final float maxY = height - edgeToneOffsetY;

            final double minLogFreq = Math.log(tones[0].frequency);
            final double maxLogFreq = Math.log(tones[tones.length-1].frequency);

            double freq01 = (y - minY) / (maxY - minY);
            if (HIGH_F_ON_TOP) {
                freq01 = 1 - freq01;
            }
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



        private static final Tone[] toneList = {
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
}
