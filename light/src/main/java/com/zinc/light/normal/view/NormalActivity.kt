package com.zinc.light.normal.view

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.zinc.base.BaseActivity
import com.zinc.light.R
import com.zinc.light.normal.model.ControlCubeInfo
import kotlinx.android.synthetic.main.activity_normal.*

/**
 * @author: Jiang Pengyong
 * @date: 2020/7/20 5:40 PM
 * @email: 56002982@qq.com
 * @des: 面法向量、点法向量
 */
class NormalActivity : BaseActivity() {

    private val controlCubeInfo: ControlCubeInfo = ControlCubeInfo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_normal)

        surface_view?.setControlModel(controlCubeInfo)

        normal?.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.face_normal -> {
                    controlCubeInfo.isFaceNormal = true
                }
                R.id.point_normal -> {
                    controlCubeInfo.isFaceNormal = false
                }
            }
            surface_view?.setControlModel(controlCubeInfo)
        }

        roughness?.progress = 50
        roughness?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                controlCubeInfo.roughness = if (progress <= 0) {
                    1
                } else {
                    progress
                }
                surface_view?.setControlModel(controlCubeInfo)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        x_light?.progress = 50
        x_light?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                controlCubeInfo.lightPosition.x = progress / 10f - 5f
                surface_view?.setControlModel(controlCubeInfo)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        y_light?.progress = 50
        y_light?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                controlCubeInfo.lightPosition.y = progress / 10f - 5f
                surface_view?.setControlModel(controlCubeInfo)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        z_light?.progress = 50
        z_light?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                controlCubeInfo.lightPosition.z = progress / 10f - 5f
                surface_view?.setControlModel(controlCubeInfo)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}