package com.reload.xhy.compasskotlin.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import com.reload.xhy.compasskotlin.fragment.CompassFragment
import com.reload.xhy.compasskotlin.fragment.MyFragment
import com.reload.xhy.compasskotlin.fragment.PostcardFragment

class MyViewPagerAadpter(fm :FragmentManager) : FragmentPagerAdapter(fm){

    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> return CompassFragment()
            1 -> return PostcardFragment()
            2 -> return MyFragment()
        }
        return CompassFragment()
    }

    override fun getCount(): Int {
        return 3
    }
}