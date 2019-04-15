package com.tianma.tweaks.miui.app.base;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BasePreferenceFragment extends PreferenceFragmentCompat {

    @Override
    protected RecyclerView.Adapter onCreateAdapter(PreferenceScreen preferenceScreen) {

        return super.onCreateAdapter(preferenceScreen);
    }

    private void setAllPreferencesToAvoidHavingExtraSpace(Preference preference) {
        preference.setIconSpaceReserved(false);
        if (preference instanceof PreferenceGroup) {
            for (int i = 0; i < ((PreferenceGroup) preference).getPreferenceCount(); i++) {
                setAllPreferencesToAvoidHavingExtraSpace(((PreferenceGroup) preference).getPreference(i));
            }
        }
    }

    @Override
    public void setPreferenceScreen(PreferenceScreen preferenceScreen) {
        if (preferenceScreen != null) {
            setAllPreferencesToAvoidHavingExtraSpace(preferenceScreen);
        }
        super.setPreferenceScreen(preferenceScreen);
    }
}
