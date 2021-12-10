package com.zinc.light

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zinc.light.light.view.LightActivity
import com.zinc.light.normal.view.NormalActivity
import kotlinx.android.synthetic.main.activity_light_main.*

class LightMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_light_main)

        light?.setOnClickListener {
            startActivity(Intent(this@LightMainActivity, LightActivity::class.java))
        }

        normal?.setOnClickListener {
            startActivity(Intent(this@LightMainActivity, NormalActivity::class.java))
        }
    }

}