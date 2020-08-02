package com.zinc.buffer.egl

import android.content.Context
import android.util.AttributeSet
import com.zinc.base.egl.BaseSurfaceView
import com.zinc.buffer.model.ControlBufferInfo

class BufferSurfaceView : BaseSurfaceView<BufferRender, ControlBufferInfo> {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun getControlModel(): ControlBufferInfo = ControlBufferInfo()

    override fun setControlModel(controlModel: ControlBufferInfo) {
        this.mControlModel.type = controlModel.type
    }

    override fun getRender(context: Context, controlModel: ControlBufferInfo): BufferRender {
        return BufferRender(context, controlModel)
    }

}