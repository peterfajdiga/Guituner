package peterfajdiga.guituner.app.parts;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import peterfajdiga.guituner.app.Preferences;
import peterfajdiga.guituner.gui.views.PitchView;
import peterfajdiga.guituner.gui.views.ToneShortcutsBar;

public class TuningGui {
    private final Context context;
    private final Preferences preferences;
    private final LayoutInflater layoutInflater;
    private final PitchView pitchView;
    private final ToneShortcutsBar toneShortcutsBar;
    private final ToneShortcuts toneShortcuts;
    private final ShortcutTonesPreferenceDialog shortcutTonesPreferenceDialog;
    private final CustomTuningDialog customTuningDialog;

    public TuningGui(@NonNull final Context context,
              @NonNull final Preferences preferences,
              @NonNull final LayoutInflater layoutInflater,
              @NonNull final PitchView pitchView,
              @NonNull ToneShortcutsBar toneShortcutsBar) {
        this.context = context;
        this.preferences = preferences;
        this.layoutInflater = layoutInflater;
        this.pitchView = pitchView;
        this.toneShortcutsBar = toneShortcutsBar;
        this.customTuningDialog = new CustomTuningDialog(context);
        this.toneShortcuts = new ToneShortcuts(preferences, layoutInflater, pitchView, toneShortcutsBar);
        this.shortcutTonesPreferenceDialog = new ShortcutTonesPreferenceDialog(context, preferences, toneShortcuts, customTuningDialog);
        toneShortcuts.setShortcutTonesPreferenceDialog(shortcutTonesPreferenceDialog);
    }
}
