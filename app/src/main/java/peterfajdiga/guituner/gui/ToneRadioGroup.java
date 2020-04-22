package peterfajdiga.guituner.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

public class ToneRadioGroup extends RadioGroup {
    private final Context context;

    public ToneRadioGroup(final Context context) {
        super(context);
        this.context = context;
    }

    public ToneRadioGroup(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void addTonesStrings(@NonNull String[] tonesStrings) {
        for (final String tonesString : tonesStrings) {
            addView(createRadioButton(tonesString));
        }
    }

    @NonNull
    private View createRadioButton(@NonNull final String tonesString) {
        final RadioButton button = new RadioButton(context);
        button.setText(tonesString);
        return button;
    }
}
