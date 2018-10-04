package com.reload.xhy.compasskotlin

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.util.Util
import com.reload.xhy.compasskotlin.adapter.MyViewPagerAadpter
import com.reload.xhy.compasskotlin.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //最下方导航栏点击事件添加
        addNavigationListener()
        //viewPager左右滑动事件添加
        addViewPagerScrollListener()

    }

    //viewPager左右滑动事件添加
    private fun addViewPagerScrollListener(){
        id_viewpager.adapter = MyViewPagerAadpter(supportFragmentManager)
        id_viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                id_navigation.menu.getItem(position).setChecked(true)
            }
        })
    }

    //最下方导航栏点击事件添加
    private fun addNavigationListener(){
        val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.id_navigation_compass -> {
                    id_viewpager.setCurrentItem(0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.id_navigation_postcard -> {
                    id_viewpager.setCurrentItem(1)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.id_navigation_my -> {
                    id_viewpager.setCurrentItem(2)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }
        id_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

}
