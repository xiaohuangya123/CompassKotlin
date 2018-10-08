package com.reload.xhy.compasskotlin

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import com.reload.xhy.compasskotlin.adapter.MyViewPagerAadpter
import com.reload.xhy.compasskotlin.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //初次加载设置toolbar背景色于指南针页背景色一致
        id_toolbar.setBackgroundColor(resources.getColor(R.color.colorLightBlack))

        //最下方导航栏点击事件添加
        addNavigationListener()
        //viewPager左右滑动事件添加
        addViewPagerScrollListener()
        //点击赞赏按钮跳转页面
        goRewardPage()

    }

    //点击赞赏按钮跳转页面
    private fun goRewardPage(){
        id_toolbar_reward_btn.setOnClickListener {
            startActivity<RewardActivity>()
        }
    }

    //viewPager左右滑动事件添加
    private fun addViewPagerScrollListener(){
        id_viewpager.offscreenPageLimit = 1
        id_viewpager.adapter = MyViewPagerAadpter(supportFragmentManager)
        id_viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                id_navigation.menu.getItem(position).setChecked(true)
                //设置不同页面的toolbar颜色
                setToolbarAndStatusbar(position)
            }
        })
    }

    //最下方导航栏点击事件添加
    private fun addNavigationListener(){
        val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.id_navigation_compass -> {
                    setToolbarAndStatusbar(0)
                    id_viewpager.setCurrentItem(0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.id_navigation_postcard -> {
                    setToolbarAndStatusbar(1)
                    id_viewpager.setCurrentItem(1)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.id_navigation_my -> {
                    setToolbarAndStatusbar(2)
                    id_viewpager.setCurrentItem(2)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }
        id_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    //设置不同页面toolbar,statusbar颜色，toolbar文字内容
    private fun setToolbarAndStatusbar(position :Int){
        //设置toolbar,statusbar颜色
        when(position){
            0-> {
                id_toolbar_title_tv.text = resources.getText(R.string.title_compass)
                id_toolbar.setBackgroundColor(resources.getColor(R.color.colorLightBlack))
                Utils.setWindowStatusBarColor(this@MainActivity,R.color.colorLightBlack)
            }
            1-> {
                id_toolbar_title_tv.text = resources.getText(R.string.title_postcard)
                id_toolbar.setBackgroundColor(resources.getColor(R.color.colorAccent))
                Utils.setWindowStatusBarColor(this@MainActivity,R.color.colorAccent)
            }
            2-> {
                id_toolbar_title_tv.text = resources.getText(R.string.title_my)
                id_toolbar.setBackgroundColor(resources.getColor(R.color.colorLightBlue))
                Utils.setWindowStatusBarColor(this@MainActivity,R.color.colorLightBlue)
            }
        }
    }

    /**
     * 在fragment中申请运行时权限是，无法回调fragment中的onRequestPermissionsResult方法，
     *会回调activity中相应方法，所以需要在MainActivity中重写该方法，并指明回调Fragment
     *中该方法
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var fragmentList : List<Fragment> = supportFragmentManager.fragments
        if(fragmentList == null){
            return
        }
        for(fragment in fragmentList){
            fragment.onRequestPermissionsResult(requestCode,permissions,grantResults)
        }
    }

    //点击返回键，杀死应用
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            finish()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}
