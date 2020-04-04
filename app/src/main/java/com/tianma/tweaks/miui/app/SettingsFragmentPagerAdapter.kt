package com.tianma.tweaks.miui.app

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.tianma.tweaks.miui.app.base.BasePreferenceFragment

class SettingsFragmentPagerAdapter(fm: FragmentManager, var fragments: List<BasePreferenceFragment>) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int) = fragments[position]

    override fun getCount() = fragments.size

    override fun getPageTitle(position: Int): CharSequence? {
        return fragments[position].title
    }

}