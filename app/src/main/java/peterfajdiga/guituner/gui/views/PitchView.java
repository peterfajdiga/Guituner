package peterfajdiga.guituner.gui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;

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
        final Tone[] tones = getDetectableTones(frequency);
        try {
            display.setTones(tones);
        } catch (final PitchViewDisplay.HighlightedToneRemovedException e) {
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

    @Nullable
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

    @NonNull
    private static Tone[] getDetectableTones(final double highestFrequency) {
        // TODO: optimize
        int detectableToneCount = 0;
        for (Tone tone : fullToneList) {
            if (tone.frequency > highestFrequency) {
                break;
            }
            detectableToneCount++;
        }
        return Arrays.copyOf(fullToneList, detectableToneCount);
    }

    private static final Tone[] fullToneList = {
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
