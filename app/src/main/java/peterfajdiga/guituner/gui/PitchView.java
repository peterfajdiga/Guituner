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
                    if (onFocusChangedListener != null) {
                        onFocusChangedListener.onFocusChanged(display.selectedTone.frequency);
                    }
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
        if (onFocusChangedListener != null) {
            onFocusChangedListener.onFocusChanged(display.selectedTone.frequency);
        }
    }

    public void removeFocus() {
        display.selectedTone = null;
        if (onFocusChangedListener != null) {
            onFocusChangedListener.onFocusRemoved();
        }
        display.invalidate();
    }
    public void setFocus(final Tone tone) {
        display.selectedTone = tone;
        focusOnFrequency(tone.frequency);
        if (onFocusChangedListener != null) {
            onFocusChangedListener.onFocusChanged(tone.frequency);
        }
    }



    public interface OnFocusChangedListener {
        void onFocusChanged(double focusedFrequency);
        void onFocusRemoved();
    }

    public void setOnFocusChangedListener(final OnFocusChangedListener listener) {
        this.onFocusChangedListener = listener;
    }

}
