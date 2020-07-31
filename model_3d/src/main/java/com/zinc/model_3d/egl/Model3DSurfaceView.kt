package com.zinc.model_3d.egl

import android.content.Context
import android.util.AttributeSet
import com.zinc.base.egl.BaseSurfaceView
import com.zinc.model_3d.model.ControlModel3DInfo

class Model3DSurfaceView : BaseSurfaceView<Model3DRender, ControlModel3DInfo> {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun getControlModel(): ControlModel3DInfo {
        return ControlModel3DInfo()
    }

    override fun setControlModel(controlModel: ControlModel3DInfo) {
        mControlModel.drawType = controlModel.drawType
        mControlModel.drawModel = controlModel.drawModel
    }

    override fun getRender(context: Context, controlModel: ControlModel3DInfo): Model3DRender {
        return Model3DRender(context, controlModel)
    }
}