package com.zinc.light.light.egl

import android.content.Context
import android.util.AttributeSet
import com.zinc.base.egl.BaseSurfaceView
import com.zinc.light.light.model.ControlBallInfo

/**
 * @author: Jiang Pengyong
 * @date: 2020/7/22 10:09 AM
 * @email: 56002982@qq.com
 * @des: 光照
 */
class LightSurfaceView : BaseSurfaceView<LightSceneRender, ControlBallInfo> {

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun getRender(context: Context, controlModel: ControlBallInfo): LightSceneRender {
        return LightSceneRender(context, controlModel)
    }

    override fun getControlModel(): ControlBallInfo {
        return ControlBallInfo()
    }

    override fun setControlModel(controlModel: ControlBallInfo) {
        mControlModel.isUseAmbient = controlModel.isUseAmbient
        mControlModel.isUsePositioningLight = controlModel.isUsePositioningLight
        mControlModel.isUseDiffuse = controlModel.isUseDiffuse
        mControlModel.isUseSpecular = controlModel.isUseSpecular
        mControlModel.roughness = controlModel.roughness
        mControlModel.lightPosition = controlModel.lightPosition
        mControlModel.isCalByFrag = controlModel.isCalByFrag
    }
}