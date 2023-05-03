package com.tracking.myapplication

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.Image
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.*


class MainActivity : AppCompatActivity(), SensorEventListener {
    val position = Position(doubleArrayOf(0.0, 0.0))

    var sensorA: Sensor? = null
//    var sensorR: Sensor? = null

    private val nano2sec = 0.000000001
    private var startTime: Double = System.nanoTime() * nano2sec

    var rotation = 0.0f

    var step_now = false
    var find_now = false

    var bomb1: DoubleArray = DoubleArray(2) {0.0}
    var bomb2: DoubleArray = DoubleArray(2) {0.0}
    var bomb3: DoubleArray = DoubleArray(2) {0.0}

    var bombs: BooleanArray = BooleanArray(3){false}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

//        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
//        sensorR = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
//        sensorManager.registerListener(this, sensorR, SensorManager.SENSOR_DELAY_NORMAL)

        var btn = findViewById<Button>(R.id.reset)
        btn.setOnClickListener {
            resetPos()
        }

        btn = findViewById<Button>(R.id.start)
        btn.setOnClickListener {
            startFind()
        }

        btn = findViewById<Button>(R.id.gotostart)
        btn.setOnClickListener {
            goToStart()
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (find_now) {
            if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
                if (event.values[1] > 1.0f) {
                    if (!step_now && System.nanoTime() * nano2sec - startTime > 2.0) {
                        startTime = System.nanoTime() * nano2sec
                        step_now = true
                    }
                }
                else {
                    if (step_now) {
                        step_now = false
                        solvePosition()
                    }
                }
            }
        }
        val Rot = FloatArray(9)
        val Iot = FloatArray(9)
        val gr = FloatArray(9)
        val geo = FloatArray(9)
        val values = FloatArray(3)
        SensorManager.getRotationMatrix(Rot, Iot, gr, geo)
        SensorManager.getOrientation(Rot, values)

        rotation = values[0]
        var str_rotation = round(rotation * 180 / Math.PI).toString()
        str_rotation = str_rotation.substring(0, str_rotation.length-2)
        val a = findViewById<TextView>(R.id.angleValue)
        val x = (round(position.x * 10)/10).toString()
        val y = (round(position.y * 10)/10).toString()
        val pos = "α: " + str_rotation + "°; X: " + x + "; Y: " + y
        a.text = pos

        if (find_now) {
            val level = updateSignal()
            if (level == 7) { bombFinded() }
            if (bombs.contentEquals(BooleanArray(3){true})) { goToStart() }
        }
    }

    fun startFind() {
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorA = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        sensorManager.registerListener(this, sensorA, SensorManager.SENSOR_DELAY_NORMAL)

        val x1 = findViewById<TextView>(R.id.x1)
        val y1 = findViewById<TextView>(R.id.y1)
        bomb1[0] = x1.text.toString().toDouble()
        bomb1[1] = y1.text.toString().toDouble()

        val x2 = findViewById<TextView>(R.id.x2)
        val y2 = findViewById<TextView>(R.id.y2)
        bomb2[0] = x2.text.toString().toDouble()
        bomb2[1] = y2.text.toString().toDouble()

        val x3 = findViewById<TextView>(R.id.x3)
        val y3 = findViewById<TextView>(R.id.y3)
        bomb3[0] = x3.text.toString().toDouble()
        bomb3[1] = y3.text.toString().toDouble()

        x1.visibility = View.GONE
        x2.visibility = View.GONE
        x3.visibility = View.GONE
        y1.visibility = View.GONE
        y2.visibility = View.GONE
        y3.visibility = View.GONE

        var t = findViewById<TextView>(R.id.textView)
        t.visibility = View.GONE
        t = findViewById<TextView>(R.id.textView2)
        t.visibility = View.GONE
        t = findViewById<TextView>(R.id.textView3)
        t.visibility = View.GONE

        val signal = findViewById<ImageView>(R.id.signal)
        signal.visibility = View.VISIBLE

        find_now = true
    }

    fun solvePosition() {
        position.x += 0.9 * sin(rotation)
        position.y += 0.9 * cos(rotation)
//        Log.d("POSX", position.x.toString())
//        Log.d("POSY", position.y.toString())
    }

    fun resetPos() {
        position.x = 0.0
        position.y = 0.0
    }

    fun updateSignal(): Int {
        val signal = findViewById<ImageView>(R.id.signal)
        var level = 0

        if (!bombs[0]) {
            val d1 = (abs(position.x - bomb1[0]) + abs(position.y - bomb1[1]))/2
            level = solveLevel(d1)
            if (level == 7) { bombs[0] = true }
        }

        if (!bombs[1]) {
            val d2 = (abs(position.x - bomb2[0]) + abs(position.y - bomb2[1]))/2
            val level2 = solveLevel(d2)
            level = max(level, level2)
            if (level2 == 7) { bombs[1] = true }
        }

        if (!bombs[2]) {
            val d3 = (abs(position.x - bomb3[0]) + abs(position.y - bomb3[1]))/2
            val level3 = solveLevel(d3)
            level = max(level, level3)
            if (level3 == 7) { bombs[2] = true }
        }

        signal.setImageResource(getDrawableByLevel(level))
        return level
    }

    fun solveLevel(value: Double): Int {
        return if (value >= 2.8) {
            0
        } else if (value >= 2.4) {
            1
        } else if (value >= 2.0) {
            2
        } else if (value >= 1.6) {
            3
        } else if (value >= 1.2) {
            4
        } else if (value >= 0.8) {
            5
        } else if (value >= 0.4) {
            6
        } else {
            7
        }
    }

    fun getDrawableByLevel(value: Int): Int {
        if (value == 0) {
            return R.drawable.lvl0
        } else if (value == 1) {
            return R.drawable.lvl1
        } else if (value == 2) {
            return R.drawable.lvl2
        } else if (value == 3) {
            return R.drawable.lvl3
        } else if (value == 4) {
            return R.drawable.lvl4
        } else if (value == 5){
            return R.drawable.lvl5
        } else if (value == 6){
            return R.drawable.lvl6
        } else {
            return R.drawable.lvl7
        }
    }

    fun bombFinded() {
        val text = "Вы нашли закладку!"
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()
    }

    fun goToStart() {
        resetPos()
        rotation = 0.0f
        step_now = false
        bomb1 = DoubleArray(2) {0.0}
        bomb2 = DoubleArray(2) {0.0}
        bomb3 = DoubleArray(2) {0.0}
        bombs = BooleanArray(3){false}

        val x1 = findViewById<TextView>(R.id.x1)
        val y1 = findViewById<TextView>(R.id.y1)
        val x2 = findViewById<TextView>(R.id.x2)
        val y2 = findViewById<TextView>(R.id.y2)
        val x3 = findViewById<TextView>(R.id.x3)
        val y3 = findViewById<TextView>(R.id.y3)

        x1.visibility = View.VISIBLE
        x2.visibility = View.VISIBLE
        x3.visibility = View.VISIBLE
        y1.visibility = View.VISIBLE
        y2.visibility = View.VISIBLE
        y3.visibility = View.VISIBLE

        var t = findViewById<TextView>(R.id.textView)
        t.visibility = View.VISIBLE
        t = findViewById<TextView>(R.id.textView2)
        t.visibility = View.VISIBLE
        t = findViewById<TextView>(R.id.textView3)
        t.visibility = View.VISIBLE

        val signal = findViewById<ImageView>(R.id.signal)
        signal.visibility = View.INVISIBLE

        find_now = false
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}