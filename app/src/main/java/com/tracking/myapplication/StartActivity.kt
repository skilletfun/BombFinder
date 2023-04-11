package com.tracking.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import java.lang.Math.random


class StartActivity : AppCompatActivity() {
    var bomb: DoubleArray = DoubleArray(2) {0.0}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_start)
        generate()
    }

    fun generate() {
        val btn_bomb = findViewById(R.id.startButton) as Button
        btn_bomb.setOnClickListener {
            val group = findViewById(R.id.radiuses) as RadioGroup

            val radioButtonID: Int = group.getCheckedRadioButtonId()
            val radioButton: RadioButton = group.findViewById(radioButtonID)
            val idx: Int = group.indexOfChild(radioButton)
            val radius = idx + 1

            var xsign = -1
            if (random() >= 0.5) {
                xsign = 1
            }

            var ysign = -1
            if (random() >= 0.5) {
                ysign = 1
            }

            val bx = random() * radius.toDouble()
            val by = radius - bx

            bomb[0] = bx * xsign
            bomb[1] = by * ysign

            val myIntent = Intent(this, MainActivity::class.java)
            myIntent.putExtra("bombx", bomb[0])
            myIntent.putExtra("bomby", bomb[1])
            startActivity(myIntent)
        }
    }
}