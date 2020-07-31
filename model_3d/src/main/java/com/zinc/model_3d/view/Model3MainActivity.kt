package com.zinc.model_3d.view

import android.os.Bundle
import com.zinc.base.BaseActivity
import com.zinc.model_3d.R
import com.zinc.model_3d.model.ControlModel3DInfo
import com.zinc.model_3d.model.DrawModel
import com.zinc.model_3d.model.DrawType
import kotlinx.android.synthetic.main.activity_model_3d_main.*

class Model3MainActivity : BaseActivity() {

    private val controlModel3DInfo = ControlModel3DInfo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_model_3d_main)

        rendering?.setOnCheckedChangeListener { _, buttonId ->
            controlModel3DInfo.drawType = when (buttonId) {
                R.id.point -> DrawType.POINT
                R.id.line -> DrawType.LINE
                R.id.color -> DrawType.COLOR
                R.id.texture -> DrawType.TEXTURE
                else -> DrawType.TEXTURE
            }
            surface.setControlModel(controlModel3DInfo)
        }

        draw_type?.setOnCheckedChangeListener { _, buttonId ->
            controlModel3DInfo.drawModel = when (buttonId) {
                R.id.cone -> DrawModel.CONE
                R.id.cylinder -> DrawModel.CYLINDER
                R.id.spring -> DrawModel.SPRING
                R.id.torus -> DrawModel.TORUS
                else -> DrawModel.CONE
            }
            surface.setControlModel(controlModel3DInfo)
        }

    }

}