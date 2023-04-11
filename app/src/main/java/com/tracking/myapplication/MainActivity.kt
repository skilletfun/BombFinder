package com.tracking.myapplication

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.SystemClock
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.round


class MainActivity : AppCompatActivity(), SensorEventListener {
    val accelerometer = Accelerometer(doubleArrayOf(0.0, 0.0))
    val position = Position(doubleArrayOf(0.0, 0.0))

    var sensorA: Sensor? = null

    private val nano2sec = 0.000000001
    private var startTime: Double = System.nanoTime() * nano2sec
    private var endTime: Double = System.nanoTime() * nano2sec

    var filtX = 0.0
    var filtY = 0.0
    val alpha = 0.2

    var bomb: DoubleArray = DoubleArray(2) {0.0}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        val intent = intent
        val bombx: Double? = intent.extras?.getDouble("bombx")
        if (bombx != null) {
            bomb[0] = bombx
        }
        val bomby: Double? = intent.extras?.getDouble("bomby")
        if (bomby != null) {
            bomb[1] = bomby
        }

        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorA = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        sensorManager.registerListener(this, sensorA, SensorManager.SENSOR_DELAY_NORMAL)

        val btn = findViewById<Button>(R.id.reset)
        btn.setOnClickListener {
            resetPos()
        }
        startAnimation()
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION){
            endTime = System.nanoTime() * nano2sec
            val deltaTime = endTime - startTime

            filtX = lowPassFilter(event.values[0], filtX)
            filtY = lowPassFilter(event.values[1], filtY)

            accelerometer.setValues(doubleArrayOf(filtX, filtY))

            val vel_x = accelerometer.x + filtX * deltaTime
            val vel_y = accelerometer.y + filtY * deltaTime

            position.x = position.x + vel_x * deltaTime
            position.y = position.y + vel_y * deltaTime

            startTime = endTime

            val string: String = accuracy()

            val textView: TextView = findViewById(R.id.textView) as TextView
            textView.text = string

            if (string == "95%" || string == "96%" || string == "97%" || string == "98%" || string == "99%" || string == "100%") {
                end()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun lowPassFilter(value: Float, oldValue: Double): Double {
        return alpha * value + (1 - alpha) * oldValue
    }

    fun resetPos() {
        position.x = 0.0
        position.y = 0.0
    }

    fun accuracy(): String {
        val ax = solveAccuracy(position.x, bomb[0])
        val ay = solveAccuracy(position.y, bomb[1])
        val perc = round(((ax + ay)/2) *100).toString()
        return perc.substring(0,perc.length-2) + "%"
    }

    fun solveAccuracy(v1: Double, v2: Double): Double {
        return if (v1 > v2)
            solvePercents(v1 - v2)
        else
            solvePercents(v2 - v1)
    }

    fun solvePercents(v: Double): Double {
        if (v > 3.0) return 0.0
        return (3.0 - v)/3.0
    }

    fun startAnimation() {
        val part_radar = findViewById<ImageView>(R.id.imageView2)
        val animation = AnimationUtils.loadAnimation(
            this,
            R.anim.rotation)
        part_radar.startAnimation(animation)
    }

    fun end() {
        val part_radar = findViewById<ImageView>(R.id.imageView2)
        part_radar.clearAnimation()

        val text = "Congratulations!"
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()

        SystemClock.sleep(3000)
        val myIntent = Intent(this, StartActivity::class.java)
        startActivity(myIntent)
    }
}