package com.reload.xhy.compasskotlin.fragment

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.reload.xhy.compasskotlin.R
import com.reload.xhy.compasskotlin.compass.CompassView
import com.reload.xhy.compasskotlin.utils.Utils
import kotlinx.android.synthetic.main.fragment_compass.*
import java.math.BigDecimal

class CompassFragment : BaseFragment() {

    //获取指南针方向的传感器
    lateinit var sensorManager : SensorManager
    lateinit var sensorEventListener: SensorEventListener

    //连续客户端的定位监听
    var continueCount = 1
    lateinit var locationClientContinue: AMapLocationClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //compassFragment的布局加载
        var view = inflater.inflate(R.layout.fragment_compass,container,false)
        val compassView = view.findViewById<CompassView>(R.id.id_compassView)

        //注册传感器，实现compassView转动
        registSensor(compassView)
        //运用高德API获取具体经纬度，地址
        getLocationInfo()
        return view
    }

    //运用高德API获取具体经纬度，地址
    private fun getLocationInfo(){
        locationClientContinue = AMapLocationClient(activity?.applicationContext)
        //使用连续的定位方式  默认连续
        var locationClientOption = AMapLocationClientOption()
        // 地址信息
        locationClientOption.setNeedAddress(true)
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否使用设备传感器 默认值：false
        locationClientOption.setSensorEnable(false)

        locationClientContinue?.setLocationOption(locationClientOption)
        locationClientContinue?.setLocationListener { location ->
            continueCount++
            val callBackTime = System.currentTimeMillis()
            val sb = StringBuffer()
            sb.append("持续定位完成 $continueCount\n")
            sb.append("回调时间: " + Utils.formatUTC(callBackTime, null) + "\n")
            if (null == location) {
                sb.append("定位失败：location is null !")
            } else {
                sb.append(Utils.getLocationStr(location))
                //更新界面信息
                updateInfo(location)
            }
//            Log.d("Rxjava", sb.toString())
        }
        locationClientContinue?.startLocation()
    }

    //更新界面信息
    private fun updateInfo(location : AMapLocation){
        val latitude = formatToDuFenMiao(location.latitude)
        id_latitude.text = latitude
        val longitude = formatToDuFenMiao(location.longitude)
        id_longitude.text = longitude

        id_location_tv.text = location.city
        id_altitude.text = location.altitude.toInt().toString() + "m"
        id_speed.text = String.format("%.2f",(location.speed*3.6)) + "km/h"
        id_priovider.text = location.provider
        id_satelliteNum.text = location.satellites.toString()

        //存储经纬度信息用于给明信片添加水印
        var editor = activity?.getSharedPreferences("data",0)?.edit()
        editor?.putString("latitude", latitude)
        editor?.putString("longitude",longitude)
        editor?.putString("city",location.city)
        editor?.apply()
    }

    //注册传感器，实现compassView转动
    private fun registSensor(compassView : CompassView){
        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorEventListener = object : SensorEventListener{
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }

            override fun onSensorChanged(event: SensorEvent?) {
                compassView.setDegreeValue(event!!.values[0])
            }
        }
        sensorManager.registerListener(sensorEventListener
                ,sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
                , SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onDestroy() {
        super.onDestroy()
        //释放传感器
        sensorManager.unregisterListener(sensorEventListener)
        //停止定位后，本地定位服务并不会被销毁
        locationClientContinue.stopLocation()
        //销毁定位客户端，同时销毁本地定位服务。
        locationClientContinue.onDestroy()
    }

    private fun formatToDuFenMiao(value: Double): String {
        val du = Math.floor(Math.abs(value)).toInt()    //获取整数部分
        val temp = getdPoint(Math.abs(value)) * 60
        val fen = Math.floor(temp).toInt() //获取整数部分
        val miao = Math.floor(getdPoint(temp) * 60).toInt()

        val duStr: String
        val fenStr: String
        val miaoStr: String

        if (0 == du) {
            duStr = "00"
        } else {
            duStr = Integer.toString(du)
        }
        if (0 == fen) {
            fenStr = "00"
        } else {
            fenStr = Integer.toString(fen)
        }
        if (0 == miao) {
            miaoStr = "00"
        } else {
            miaoStr = Integer.toString(miao)
        }
        return if (value < 0) "-$duStr°$fenStr′$miaoStr″" else "$duStr°$fenStr′$miaoStr″"
    }

    //获取小数部分
    private fun getdPoint(num: Double): Double {
        val fInt = num.toInt()
        val b1 = BigDecimal(java.lang.Double.toString(num))
        val b2 = BigDecimal(Integer.toString(fInt))
        return b1.subtract(b2).toFloat().toDouble()
    }
}