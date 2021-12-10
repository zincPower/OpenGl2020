package com.zinc.obj_3d.egl

import android.content.Context
import android.util.AttributeSet
import com.zinc.base.egl.BaseSurfaceView
import com.zinc.obj_3d.model.ControlObj3DInfo

class Obj3DSurfaceView : BaseSurfaceView<Obj3DRender, ControlObj3DInfo> {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun getControlModel(): ControlObj3DInfo = ControlObj3DInfo()

    override fun setControlModel(controlModel: ControlObj3DInfo) {
        this.mControlModel.type = controlModel.type
    }

    override fun getRender(context: Context, controlModel: ControlObj3DInfo): Obj3DRender {
        return Obj3DRender(context, controlModel)
    }

}