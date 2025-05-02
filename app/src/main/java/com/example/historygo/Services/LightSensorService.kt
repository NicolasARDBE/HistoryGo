package com.example.historygo.Services

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class LightSensorService(context: Context) {
    private var sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var lightSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    private var lightEventListener: SensorEventListener? = null

    fun registerLightSensorListener(listener:(lightValue: Float)->Unit) {
        lightEventListener = object :SensorEventListener{
            override fun onSensorChanged(event: SensorEvent?) {
                if (event!=null&&event.sensor.type==Sensor.TYPE_LIGHT){
                    val lightValue= event.values[0]
                    listener(lightValue)
                }
            }
            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
                //nada
            }
        }
        lightSensor.let {
                sensor -> sensorManager.registerListener(lightEventListener,sensor,SensorManager.SENSOR_DELAY_NORMAL)
        }
    }
    fun unregisterLightSensorListener(){
        lightEventListener?.let {
            sensorManager.unregisterListener(it)
            lightEventListener = null
        }
    }
}