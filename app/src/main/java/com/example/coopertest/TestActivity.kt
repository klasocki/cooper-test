package com.example.coopertest

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_test.*
import java.text.SimpleDateFormat
import java.util.*


class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        val testLengthMinutes = 1
        object : CountDownTimer((testLengthMinutes * 60 * 1000).toLong(), 100) {

            override fun onTick(millisUntilFinished: Long) {
                timerView.text = SimpleDateFormat("mm:ss.S").format(
                    Date(millisUntilFinished)
                )
            }

            override fun onFinish() {
                timerView.text = "Done!"
                backToMain()
            }
        }.start()
    }

    fun backToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
