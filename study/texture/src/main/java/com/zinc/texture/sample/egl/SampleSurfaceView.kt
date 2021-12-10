package com.zinc.texture.sample.egl

import android.content.Context
import android.util.AttributeSet
import com.zinc.base.egl.BaseSurfaceView
import com.zinc.texture.sample.model.SampleControlModel
import com.zinc.texture.sample.model.SampleType

class SampleSurfaceView : BaseSurfaceView<SampleRender, SampleControlModel> {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun getControlModel(): SampleControlModel {
        return SampleControlModel(
            SampleType.GL_LINEAR_GL_LINEAR,
            SampleType.GL_LINEAR_GL_LINEAR
        )
    }

    override fun setControlModel(controlModel: SampleControlModel) {
        this.mControlModel.bigSampleType = controlModel.bigSampleType
        this.mControlModel.smallSampleType = controlModel.smallSampleType
    }

    override fun getRender(context: Context, controlModel: SampleControlModel): SampleRender {
        return SampleRender(context, controlModel)
    }
}