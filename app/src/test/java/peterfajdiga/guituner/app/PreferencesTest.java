package peterfajdiga.guituner.app;

import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import peterfajdiga.guituner.general.Pitch;

public class PreferencesTest {
    @Test
    public void getShortcutPitches() {
        final SharedPreferencesMock sharedPreferencesMock = new SharedPreferencesMock();
        final Preferences preferences = new Preferences(sharedPreferencesMock);
        Pitch[] expected, actual;

        expected = new Pitch[]{
            Pitch.E2,
            Pitch.A2,
            Pitch.D3,
            Pitch.G3,
            Pitch.B3,
            Pitch.E4,
        };
        actual = preferences.getShortcutPitches();
        Assert.assertArrayEquals(expected, actual);

        sharedPreferencesMock.prefs.put("shortcutPitches", "A1# E4 B0 G4 G4#");
        expected = new Pitch[]{
            Pitch.A1s,
            Pitch.E4,
            Pitch.B0,
            Pitch.G4,
            Pitch.G4s,
        };
        actual = preferences.getShortcutPitches();
        Assert.assertArrayEquals(expected, actual);
    }

    private static class SharedPreferencesMock implements SharedPreferences {
        final Map<String, Object> prefs = new HashMap<>();

        @Override
        public Map<String, ?> getAll() {
            return prefs;
        }

        @Nullable
        @Override
        public String getString(final String key, @Nullable final String defValue) {
            if (contains(key)) {
                return (String)prefs.get(key);
            } else {
                return defValue;
            }
        }

        @Nullable
        @Override
        public Set<String> getStringSet(final String key, @Nullable final Set<String> defValues) {
            if (contains(key)) {
                return (Set<String>)prefs.get(key);
            } else {
                return defValues;
            }
        }

        @Override
        public int getInt(final String key, final int defValue) {
            if (contains(key)) {
                return (Integer)prefs.get(key);
            } else {
                return defValue;
            }
        }

        @Override
        public long getLong(final String key, final long defValue) {
            if (contains(key)) {
                return (Long)prefs.get(key);
            } else {
                return defValue;
            }
        }

        @Override
        public float getFloat(final String key, final float defValue) {
            if (contains(key)) {
                return (Float)prefs.get(key);
            } else {
                return defValue;
            }
        }

        @Override
        public boolean getBoolean(final String key, final boolean defValue) {
            if (contains(key)) {
                return (Boolean)prefs.get(key);
            } else {
                return defValue;
            }
        }

        @Override
        public boolean contains(final String key) {
            return prefs.containsKey(key);
        }

        @Override
        public Editor edit() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void registerOnSharedPreferenceChangeListener(final OnSharedPreferenceChangeListener listener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void unregisterOnSharedPreferenceChangeListener(final OnSharedPreferenceChangeListener listener) {
            throw new UnsupportedOperationException();
        }
    }
}
