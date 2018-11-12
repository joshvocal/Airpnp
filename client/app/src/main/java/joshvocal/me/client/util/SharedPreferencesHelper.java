package joshvocal.me.client.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesHelper {

    private static final String LAST_LAT_PREF_KEY = "last_lat";
    private static final String LAST_LNG_PREF_KEY = "last_lng";

    private final SharedPreferences mPrefs;

    public SharedPreferencesHelper(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getLastLat() {
        return mPrefs.getString(LAST_LAT_PREF_KEY, "0");
    }

    public String getLastLng() {
        return mPrefs.getString(LAST_LNG_PREF_KEY, "0");
    }

    public void setLastLatPrefKey(String lat) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(LAST_LAT_PREF_KEY, lat);
        editor.apply();
    }

    public void setLastLngPrefKey(String lng) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(LAST_LNG_PREF_KEY, lng);
        editor.apply();
    }
}
