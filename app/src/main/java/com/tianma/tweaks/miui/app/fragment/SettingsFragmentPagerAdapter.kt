package com.tianma.tweaks.miui.app.fragment

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class SettingsFragmentPagerAdapter(
    fm: FragmentManager,
    private var fragments: List<BaseSettingsFragment>
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int) = fragments[position]

    override fun getCount() = fragments.size

    override fun getPageTitle(position: Int): CharSequence? {
        return fragments[position].title
    }

}