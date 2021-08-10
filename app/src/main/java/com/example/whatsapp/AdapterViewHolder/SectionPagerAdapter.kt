package com.example.whatsapp.AdapterViewHolder

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.whatsapp.Fragment.ChatsFragment
import com.example.whatsapp.Fragment.UsersFragment


class SectionPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return ChatsFragment()
            1 -> return UsersFragment()


        }
        return null!!

    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return "Chats"
            1 -> return "Users"
        }

        return null!!
    }
    override fun getCount(): Int {
        return 2
    }

}