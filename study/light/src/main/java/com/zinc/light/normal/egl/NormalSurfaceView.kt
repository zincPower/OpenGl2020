package com.zinc.light.normal.egl

import android.content.Context
import android.util.AttributeSet
import com.zinc.base.egl.BaseSurfaceView
import com.zinc.light.normal.model.ControlCubeInfo

/**
 * @author: Jiang Pengyong
 * @date: 2020/7/20 2:50 PM
 * @email: 56002982@qq.com
 * @des: 点法向量、面法向量
 */
class NormalSurfaceView : BaseSurfaceView<NormalSceneRender, ControlCubeInfo> {

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun getControlModel(): ControlCubeInfo = ControlCubeInfo()

    override fun setControlModel(controlModel: ControlCubeInfo) {
        mControlModel.roughness = controlModel.roughness
        mControlModel.lightPosition = controlModel.lightPosition
        mControlModel.isFaceNormal = controlModel.isFaceNormal
    }

    override fun getRender(context: Context, controlModel: ControlCubeInfo): NormalSceneRender {
        return NormalSceneRender(context, controlModel)
    }
}