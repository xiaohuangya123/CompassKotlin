package com.reload.xhy.compasskotlin

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class TextActivity : AppCompatActivity() {

    lateinit var sensorManager: SensorManager
    lateinit var sensorEventListener: SensorEventListener
    internal var `val`: Float = 0.toFloat()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                `val` = event.values[0]
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

            }
        }
    }
}
