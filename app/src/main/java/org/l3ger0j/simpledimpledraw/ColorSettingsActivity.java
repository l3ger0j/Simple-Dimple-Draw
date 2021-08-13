package org.l3ger0j.simpledimpledraw;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceFragmentCompat;

import com.jaredrummler.android.colorpicker.ColorPreferenceCompat;

public class ColorSettingsActivity extends AppCompatActivity {

    int settings = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout settingActivity = new LinearLayout(this);
        settingActivity.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams settingActivityLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setId(settings);
        settingActivity.addView(frameLayout, settingActivityLayoutParam);

        setContentView(settingActivity, settingActivityLayoutParam);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState , String rootKey) {
            setPreferencesFromResource(R.xml.color_preference , rootKey);
        }
    }
}