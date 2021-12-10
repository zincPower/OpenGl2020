package com.zinc.texture.stretch.egl

import android.content.Context
import android.util.AttributeSet
import com.zinc.base.egl.BaseSurfaceView
import com.zinc.texture.stretch.model.ControlStretchInfo

/**
 * @author: Jiang Pengyong
 * @date: 2020/7/22 12:57 PM
 * @email: 56002982@qq.com
 * @des:
 */
class StretchSurfaceView : BaseSurfaceView<StretchSceneRender, ControlStretchInfo> {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun getControlModel(): ControlStretchInfo {
        return ControlStretchInfo()
    }

    override fun setControlModel(controlModel: ControlStretchInfo) {
        mControlModel.textureSize = controlModel.textureSize
        mControlModel.stretchMode = controlModel.stretchMode
    }

    override fun getRender(context: Context, controlModel: ControlStretchInfo): StretchSceneRender {
        return StretchSceneRender(context, controlModel)
    }

}