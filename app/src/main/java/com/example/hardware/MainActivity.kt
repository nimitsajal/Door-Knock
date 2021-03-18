package com.example.hardware

import android.content.DialogInterface
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.ToggleButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)

    lateinit var sensorEventListener: SensorEventListener
    lateinit var sensorManager: SensorManager
    lateinit var proxSensor: Sensor
    private lateinit var cameraManager: CameraManager
    private lateinit var cameraId: String
    private lateinit var toggleButton: ToggleButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val values = findViewById<TextView>(R.id.values)
        val a: Array<Double> = arrayOf(0.0, 0.0)

        sensorManager = getSystemService<SensorManager>()!!

        proxSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorEventListener = object : SensorEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onSensorChanged(event: SensorEvent?) {
                var value_x = event!!.values[0]
                var value_y = event!!.values[1]
                var value_z = event!!.values[2]
                var value = value_x + value_y + value_z




                values.text = value_x.toString()

                val camManager = getSystemService(CAMERA_SERVICE) as CameraManager
                var cameraId: String? = null
                cameraId = camManager.cameraIdList[0]

                a[0] = a[1]
                a[1] = value_x.toDouble()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if((a[1] - a[0]) > 0.05){
                        values.text = "CHANGED"
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                            camManager.setTorchMode(cameraId, true) //Turn ON
//                    try {
//                        cameraId = camManager.cameraIdList[0]
//                        camManager.setTorchMode(cameraId, true) //Turn ON
//                    }
//                    catch (e: CameraAccessException) {
//                        e.printStackTrace()
//                    }
                        }
                    }

                    camManager.setTorchMode(cameraId, false) //Turn OFF
                }


                Log.d("SENSOR", """
                    onSensorChanged: $value_x
                    a[0] = ${a[0]}
                    a[1] = ${a[1]}
                """.trimIndent())
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                //
            }
        }






//        if(sensorManager == null){
//            Toast.makeText(this, "Cannot Detect Sensors", Toast.LENGTH_SHORT).show()
//            finish()
//        }
//        else{
//            val sensors = sensorManager.getSensorList(Sensor.TYPE_ALL)
//            sensors.forEach {
//                Log.d("SENSOR", """
//                    ${it.name}  |   ${it.stringType}    |   ${it.vendor}
//                """.trimIndent())
//            }
//        }
    }

    private fun showNoFlashError() {
        val alert = AlertDialog.Builder(this)
                .create()
        alert.setTitle("Oops!")
        alert.setMessage("Flash not available in this device...")
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK") { _, _ -> finish() }
        alert.show()
    }

//    private fun switchFlashLight(status: Boolean) {
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                cameraManager.setTorchMode(cameraId, status)
//            }
//        } catch (e: CameraAccessException) {
//            e.printStackTrace()
//        }
//    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(
                sensorEventListener, proxSensor, 1000 * 1000
        )
    }

    override fun onPause() {
        sensorManager.unregisterListener(sensorEventListener)
        super.onPause()
    }
}