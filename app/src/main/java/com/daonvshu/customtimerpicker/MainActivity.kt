package com.daonvshu.customtimerpicker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val dateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())

    private var lastSt = 0L
    private var lastEt = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hello.setOnClickListener {
            CustomTimerPickerView(this).showPicker { st, et ->
                lastSt = st
                lastEt = et
                hello.text = "${dateFormat.format(st)} ~ ${dateFormat.format(et)}"
            }.setTime(lastSt, lastEt)
        }
    }
}
