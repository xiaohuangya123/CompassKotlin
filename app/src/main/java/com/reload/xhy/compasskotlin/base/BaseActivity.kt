package com.reload.xhy.compasskotlin.base

import android.app.AlertDialog
import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.reload.xhy.compasskotlin.BuildConfig
import com.reload.xhy.compasskotlin.R
import com.reload.xhy.compasskotlin.model.UpdateInfo
import com.reload.xhy.compasskotlin.utils.HttpUtil.sendOkhttpRequest
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.jetbrains.anko.toast
import java.io.IOException

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        //判断是否要更新版本
        sendOkhttpRequest("http://www.gobagou.com:8088/compass/update.json",
            object : Callback{
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("Rxjava","failed.........")
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseJson = response.body()?.string()
                    Log.d("Rxjava", responseJson)
                    var gson = Gson()
                    var updateInfo = gson.fromJson(responseJson, UpdateInfo::class.java)

                    //获取手机安装apk的versionName,versionCode
                    var versionName = BuildConfig.VERSION_NAME
                    var versionCode = BuildConfig.VERSION_CODE


                    if(updateInfo.getData().getVersionCode() > versionCode){
                        runOnUiThread{
                            var dialog = AlertDialog.Builder(this@BaseActivity)
                            dialog.setTitle("更新版本")
                            dialog.setMessage("必须更新")
                            dialog.setNegativeButton("取消", object : DialogInterface.OnClickListener{
                                override fun onClick(dialog: DialogInterface?, which: Int) {
                                    finish()
                                }
                            })
                            dialog.setPositiveButton("更新", object : DialogInterface.OnClickListener{
                                override fun onClick(dialog: DialogInterface?, which: Int) {
                                    toast("更新中。。。")
                                }
                            })
                            dialog.show()
                        }
                    }
                }
            })
    }
}
