package peterfajdiga.guituner.app.parts;

import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import peterfajdiga.guituner.R;
import peterfajdiga.guituner.app.Preferences;
import peterfajdiga.guituner.general.Pitch;
import peterfajdiga.guituner.gui.views.PitchView;
import peterfajdiga.guituner.gui.views.PitchShortcutsBar;

public class PitchShortcuts {
    private final Preferences preferences;
    private final LayoutInflater layoutInflater;
    private final PitchView pitchView;
    private final PitchShortcutsBar pitchShortcutsBar;

    public PitchShortcuts(
        @NonNull final Preferences preferences,
        @NonNull final LayoutInflater layoutInflater,
        @NonNull final PitchView pitchView,
        @NonNull final PitchShortcutsBar pitchShortcutsBar
    ) {
        this.preferences = preferences;
        this.layoutInflater = layoutInflater;
        this.pitchView = pitchView;
        this.pitchShortcutsBar = pitchShortcutsBar;
    }

    public void initialize() {
        updatePitchShortcuts();
        pitchShortcutsBar.setReceiver(new PitchShortcutsBar.Receiver() {
            @Override
            public void OnPitchClick(final Pitch pitch) {
                pitchView.focusOn(pitch);
            }

            @Override
            public boolean OnPitchLongClick(final Pitch pitch) {
                return false;
            }
        });
    }

    void updatePitchShortcuts() {
        pitchShortcutsBar.setupPitches(preferences.getTuningPitches(), layoutInflater, R.layout.button_pitch_shortcut);
    }
}
