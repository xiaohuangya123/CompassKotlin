package com.reload.xhy.compasskotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.startActivity
import java.util.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Glide.with(this).load(R.drawable.benz).into(id_splash_img)

        var timer = Timer()
        var timerTask = object : TimerTask(){
            override fun run() {
                startActivity<MainActivity>()
                this@SplashActivity.finish()
            }
        }
        timer.schedule(timerTask, 2000)
    }

}
