package peterfajdiga.guituner.gui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import androidx.annotation.NonNull;

import peterfajdiga.guituner.general.Tone;

public class PitchView extends ScrollView {
    private static final int SNAP_DELTA_Y = 10;
    private static final float TONE_PRESS_MOVE_TOLERANCE = 10;

    private PitchView.OnFocusChangedListener onFocusChangedListener;
    private PitchViewDisplay display;
    private boolean allowToneSelection = true;
    private boolean fingerOnScreen = false;
    private int lastDeltaY;
    private float lastTouchX, lastTouchY;

    public PitchView(final Context context) {
        super(context);
        init(context);
    }

    public PitchView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PitchView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public PitchView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(final Context context) {
        display = new PitchViewDisplay(context);
        addView(display);
    }

    public void setFrequency(final double frequency) {
        if (frequency <= 0.0) {
            return;
        }
        display.setDetectedFrequency(frequency);
        if (!isInFocusedMode() && !fingerOnScreen) {
            centerOnFrequency(frequency);
        }
    }

    public void setHighestFrequency(final double frequency) {
        display.setHighestFrequency(frequency);
        final Tone focusedTone = getFocusedTone();
        if (focusedTone != null && focusedTone.frequency > frequency) {
            removeFocus();
        }
    }

    public void focusOn(final Tone tone) {
        setFocus(tone);
        centerOnFocus();
    }

    private void centerOnFrequency(final double frequency) {
        final int screenHeightHalf = getHeight() / 2;
        final int y = Math.round(display.getFrequencyY(frequency) - screenHeightHalf);
        allowToneSelection = false;
        smoothScrollTo(0, y);
    }

    private void centerOnFocus() {
        centerOnFrequency(getFocusedTone().frequency);
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
                    final float y = event.getY() + getScrollY();
                    setFocusedToneFromClick(display.getToneFromY(y), event.getX(), event.getY());
                    centerOnFocus();
                } else if (isInFocusedMode() && lastDeltaY < SNAP_DELTA_Y) {
                    centerOnFocus();
                }
                break;
            }
        }
        return retval;
    }

    @Override
    protected void onScrollChanged(
        final int l,
        final int t,
        final int oldl,
        final int oldt
    ) {
        if (allowToneSelection) {
            focusOnCenter();
            lastDeltaY = Math.abs(t - oldt);
            if (lastDeltaY < SNAP_DELTA_Y && !fingerOnScreen) {
                centerOnFocus();
            }
        }
    }

    private void setFocus(final Tone tone) {
        display.selectTone(tone);
        if (onFocusChangedListener != null) {
            onFocusChangedListener.onFocusChanged(tone.frequency);
        }
    }

    private void setFocusedToneFromClick(@NonNull final Tone tone, final float clickX, final float clickY) {
        display.selectTone(tone);
        if (onFocusChangedListener != null) {
            onFocusChangedListener.onFocusChanged(tone.frequency, clickX, clickY);
        }
    }

    private void focusOnCenter() {
        final int y = getScrollY() + getHeight() / 2;
        setFocus(display.getToneFromY(y));
    }

    public void removeFocus() {
        display.unselectTone();
        if (onFocusChangedListener != null) {
            onFocusChangedListener.onFocusRemoved();
        }
    }

    private boolean isInFocusedMode() {
        return getFocusedTone() != null;
    }

    private Tone getFocusedTone() {
        return display.getHighlightedTone();
    }

    public void setOnFocusChangedListener(final OnFocusChangedListener listener) {
        onFocusChangedListener = listener;
    }

    public interface OnFocusChangedListener {
        void onFocusChanged(double focusedFrequency);
        void onFocusChanged(double focusedFrequency, float clickX, float clickY);  // for animation
        void onFocusRemoved();
    }
}
