package peterfajdiga.guituner.app.parts;

import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import peterfajdiga.guituner.R;
import peterfajdiga.guituner.app.Preferences;
import peterfajdiga.guituner.general.Tone;
import peterfajdiga.guituner.gui.views.PitchView;
import peterfajdiga.guituner.gui.views.ToneShortcutsBar;

class ToneShortcuts {
    private final Preferences preferences;
    private final LayoutInflater layoutInflater;
    private final PitchView pitchView;
    private final ToneShortcutsBar toneShortcutsBar;
    private final ShortcutTonesPreferenceDialog shortcutTonesPreferenceDialog;

    ToneShortcuts(
            @NonNull final Preferences preferences,
            @NonNull final LayoutInflater layoutInflater,
            @NonNull final PitchView pitchView,
            @NonNull final ToneShortcutsBar toneShortcutsBar,
            @NonNull final ShortcutTonesPreferenceDialog shortcutTonesPreferenceDialog
    ) {
        this.preferences = preferences;
        this.layoutInflater = layoutInflater;
        this.pitchView = pitchView;
        this.toneShortcutsBar = toneShortcutsBar;
        this.shortcutTonesPreferenceDialog = shortcutTonesPreferenceDialog;
        initialize();
    }

    private void initialize() {
        updateToneShortcuts();
        toneShortcutsBar.setReceiver(new ToneShortcutsBar.Receiver() {
            @Override
            public void OnToneClick(final Tone tone) {
                pitchView.focusOn(tone);
            }

            @Override
            public boolean OnToneLongClick(final Tone tone) {
                shortcutTonesPreferenceDialog.show();
                return true;
            }
        });
    }

    void updateToneShortcuts() {
        toneShortcutsBar.setupTones(preferences.getShortcutTones(), layoutInflater, R.layout.button_tone_shortcut);
    }
}
