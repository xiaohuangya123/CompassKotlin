package com.reload.xhy.compasskotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_reward.*

class RewardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reward)

        setSupportActionBar(id_reward_toolbar)
        //不显示标题
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(android.R.id.home == item?.itemId)
            onBackPressed()
        return true
    }
}
