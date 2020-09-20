package com.akusuka.favoriteofgithubers.adapter

import android.content.Context
import androidx.annotation.Nullable
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.akusuka.favoriteofgithubers.FollowFragment
import com.akusuka.favoriteofgithubers.R
import com.akusuka.favoriteofgithubers.RepositoryFragment

class DetailPagerAdapter(private val mContext: Context, fm: FragmentManager, private val username: String) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    companion object{
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.following,
            R.string.followers,
            R.string.repository
        )
    }

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment =
                FollowFragment.newInstance(
                    username,
                    FollowFragment.FOLLOWING
                )
            1 -> fragment =
                FollowFragment.newInstance(
                    username,
                    FollowFragment.FOLLOWERS
                )
            2 -> fragment =
                RepositoryFragment.newInstance(
                    username
                )
        }
        return fragment as Fragment
    }

    @Nullable
    override fun getPageTitle(position: Int): CharSequence? {
        return mContext.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return 3
    }
}