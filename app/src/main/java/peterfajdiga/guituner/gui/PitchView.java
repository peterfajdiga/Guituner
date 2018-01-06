package peterfajdiga.guituner.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class PitchView extends ScrollView {

    private PitchViewInner display;

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
        focusOnFrequency(frequency);
    }

    private void focusOnFrequency(final double frequency) {
        final int screenHeightHalf = getHeight() / 2;
        int y = (int)display.getFrequencyY(frequency) - screenHeightHalf;
        if (y < screenHeightHalf) {
            y = screenHeightHalf;
        } else if (y > display.height - screenHeightHalf) {
            y = display.height - screenHeightHalf;
        }
        smoothScrollTo(0, y);
    }
}
