package peterfajdiga.guituner.gui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import peterfajdiga.guituner.general.Tone;

// TODO: consider using ListView or RecyclerView instead
public class ToneShortcutsBar extends LinearLayout {
    private Receiver receiver;

    public ToneShortcutsBar(final Context context) {
        super(context);
    }

    public ToneShortcutsBar(final Context context, final @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ToneShortcutsBar(final Context context, final @Nullable AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ToneShortcutsBar(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setReceiver(final Receiver receiver) {
        this.receiver = receiver;
    }

    public void setupTones(@NonNull final Tone[] tones, @NonNull final LayoutInflater layoutInflater, final int layoutResourceId) {
        final int n = tones.length;
        ensureCorrectChildCount(layoutInflater, layoutResourceId, n);
        for (int i = 0; i < n; i++) {
            final Button button = (Button)getChildAt(i);
            setupToneShortcutButton(button, tones[i]);
        }
    }

    private void ensureCorrectChildCount(final LayoutInflater layoutInflater, final int layoutResourceId, final int count) {
        final int currentCount = getChildCount();
        final int childrenToAdd = count - currentCount;
        for (int i = 0; i < childrenToAdd; i++) {
            layoutInflater.inflate(layoutResourceId, this, true);
        }
        for (int i = 0; i > childrenToAdd; i--) {
            removeViewAt(0);
        }
    }

    private void setupToneShortcutButton(@NonNull final Button button, @NonNull final Tone tone) {
        button.setText(tone.toString());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (receiver != null) {
                    receiver.OnToneClick(tone);
                }
            }
        });
        button.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                if (receiver == null) {
                    return false;
                }
                return receiver.OnToneLongClick(tone);
            }
        });
    }

    public void removeHighlight() {
        final int n = getChildCount();
        for (int i = 0; i < n; i++) {
            getChildAt(i).setSelected(false);
        }
        getChildAt(2).setSelected(true);
    }

    public interface Receiver {
        void OnToneClick(Tone tone);
        boolean OnToneLongClick(Tone tone);
    }
}
