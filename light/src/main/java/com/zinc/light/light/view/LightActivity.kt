package com.zinc.light.light.view

import android.os.Bundle
import android.widget.SeekBar
import com.zinc.base.BaseActivity
import com.zinc.light.R
import com.zinc.light.light.model.ControlBallInfo
import kotlinx.android.synthetic.main.activity_light.*

/**
 * @author: Jiang Pengyong
 * @date: 2020/7/20 5:40 PM
 * @email: 56002982@qq.com
 * @des: 环境光、散射光、镜面光
 */
class LightActivity : BaseActivity() {

    private val controlBallInfo: ControlBallInfo = ControlBallInfo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_light)

        surface_view?.setControlModel(controlBallInfo)

        light?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.direction_light -> {
                    controlBallInfo.isUsePositioningLight = 0
                }
                R.id.positioning_light -> {
                    controlBallInfo.isUsePositioningLight = 1
                }
            }
            surface_view?.setControlModel(controlBallInfo)
        }

        ambient?.setOnCheckedChangeListener { buttonView, isChecked ->
            controlBallInfo.isUseAmbient = if (isChecked) {
                1
            } else {
                0
            }
            surface_view?.setControlModel(controlBallInfo)
        }

        diffuse?.setOnCheckedChangeListener { buttonView, isChecked ->
            controlBallInfo.isUseDiffuse = if (isChecked) {
                1
            } else {
                0
            }
            surface_view?.setControlModel(controlBallInfo)
        }

        specular?.setOnCheckedChangeListener { buttonView, isChecked ->
            controlBallInfo.isUseSpecular = if (isChecked) {
                1
            } else {
                0
            }
            surface_view?.setControlModel(controlBallInfo)
        }

        roughness?.progress = 50
        roughness?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                controlBallInfo.roughness = if (progress <= 0) {
                    1
                } else {
                    progress
                }
                surface_view?.setControlModel(controlBallInfo)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        x_light?.progress = 50
        x_light?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                controlBallInfo.lightPosition.x = progress / 10f - 5f
                surface_view?.setControlModel(controlBallInfo)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        y_light?.progress = 50
        y_light?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                controlBallInfo.lightPosition.y = progress / 10f - 5f
                surface_view?.setControlModel(controlBallInfo)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        z_light?.progress = 50
        z_light?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                controlBallInfo.lightPosition.z = progress / 10f - 5f
                surface_view?.setControlModel(controlBallInfo)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        isCalByFrag?.setOnCheckedChangeListener { _, isChecked ->
            controlBallInfo.isCalByFrag = if (isChecked) {
                1
            } else {
                0
            }
            surface_view?.setControlModel(controlBallInfo)
        }
    }

}