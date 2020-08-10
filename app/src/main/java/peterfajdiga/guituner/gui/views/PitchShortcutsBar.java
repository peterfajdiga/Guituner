package peterfajdiga.guituner.gui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import peterfajdiga.guituner.general.Pitch;

// TODO: consider using ListView or RecyclerView instead
public class PitchShortcutsBar extends LinearLayout {
    private Receiver receiver;

    public PitchShortcutsBar(final Context context) {
        super(context);
    }

    public PitchShortcutsBar(final Context context, final @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PitchShortcutsBar(final Context context, final @Nullable AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PitchShortcutsBar(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setReceiver(final Receiver receiver) {
        this.receiver = receiver;
    }

    public void setupPitches(@NonNull final Pitch[] pitches, @NonNull final LayoutInflater layoutInflater, final int layoutResourceId) {
        final int n = pitches.length;
        ensureCorrectChildCount(layoutInflater, layoutResourceId, n);
        for (int i = 0; i < n; i++) {
            final Button button = (Button)getChildAt(i);
            setupPitchShortcutButton(button, pitches[i]);
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

    private void setupPitchShortcutButton(@NonNull final Button button, @NonNull final Pitch pitch) {
        button.setText(pitch.toCharSequence());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (receiver != null) {
                    receiver.OnPitchClick(pitch);
                }
            }
        });
        button.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                if (receiver == null) {
                    return false;
                }
                return receiver.OnPitchLongClick(pitch);
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
        void OnPitchClick(Pitch pitch);
        boolean OnPitchLongClick(Pitch pitch);
    }
}
