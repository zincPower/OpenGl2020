package com.jiangpengyong.opengl_proving

import android.os.Bundle
import android.view.Surface
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity

/**
 * @author: jiang peng yong
 * @date: 2021/12/10 4:45 下午
 * @email: 56002982@qq.com
 * @desc: 水印绘制
 */
class WaterMarkActivity : AppCompatActivity() {

    var surface: SurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_mark)

        surface = findViewById(R.id.surface_view)
    }

}