package peterfajdiga.guituner.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class PitchView extends ScrollView {

    private static final int SNAP_DELTA_Y = 10;

    private OnFocusChangedListener onFocusChangedListener;
    private PitchViewInner display;
    private boolean allowToneSelection = true;
    private boolean fingerOnScreen = false;
    private int lastDeltaY;

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

    public void unselectTone() {
        display.selectedTone = null;
        display.invalidate();
        if (onFocusChangedListener != null) {
            onFocusChangedListener.onFocusChanged(0.0);
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
                break;
            }
            case MotionEvent.ACTION_UP: {
                fingerOnScreen = false;
                if (display.selectedTone != null && lastDeltaY < SNAP_DELTA_Y) {
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



    public interface OnFocusChangedListener {
        // when focusedFrequency == 0.0, there is no focus
        void onFocusChanged(double focusedFrequency);
    }

    public void setOnFocusChangedListener(final OnFocusChangedListener listener) {
        this.onFocusChangedListener = listener;
    }
}
