package peterfajdiga.guituner.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LabelledRadioGroup extends RadioGroup {
    private final Context context;
    private Receiver receiver;

    public LabelledRadioGroup(final Context context) {
        super(context);
        this.context = context;
        initialize();
    }

    public LabelledRadioGroup(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initialize();
    }

    private void initialize() {
        setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final RadioGroup group, final int checkedId) {
                if (receiver == null) {
                    return;
                }
                final RadioButton checkedButton = group.findViewById(checkedId);
                final String label = (String)checkedButton.getText();
                receiver.onCheckedChanged(label);
            }
        });
    }

    public void setReceiver(@NonNull final Receiver receiver) {
        this.receiver = receiver;
    }

    public void addRadioButtons(@NonNull String[] labels, @Nullable final String selectedLabel) {
        for (final String label : labels) {
            final View buttonView = createRadioButton(label);
            addView(buttonView);
            if (label.equals(selectedLabel)) {
                check(buttonView.getId());
            }
        }
    }

    @NonNull
    private View createRadioButton(@NonNull final String label) {
        final RadioButton button = new RadioButton(context);
        button.setText(label);
        return button;
    }

    public interface Receiver {
        void onCheckedChanged(String label);
    }
}
