package com.zinc.texture.sample.egl

import android.content.Context
import android.opengl.GLES30
import com.zinc.base.control.StretchMode
import com.zinc.base.egl.BaseSceneRender
import com.zinc.base.utils.MatrixState
import com.zinc.base.utils.MatrixState.popMatrix
import com.zinc.base.utils.MatrixState.pushMatrix
import com.zinc.base.utils.MatrixState.rotate
import com.zinc.base.utils.MatrixState.translate
import com.zinc.base.utils.TextureUtils
import com.zinc.texture.R
import com.zinc.texture.sample.SampleDrawer
import com.zinc.texture.sample.model.SampleControlModel
import com.zinc.texture.sample.model.SampleType

class SampleRender(
    context: Context,
    private val sampleControlModel: SampleControlModel
) :
    BaseSceneRender<SampleDrawer>(context) {
    private var textureId = IntArray(8)

    private var curMinTexId = 0
    private var curMagTexId = 0

    override fun initData(context: Context) {
        mData = SampleDrawer(context)

        textureId[0] = TextureUtils.obtainTexture(
            context,
            R.drawable.sample_32,
            StretchMode.EDGE,
            minSample = GLES30.GL_NEAREST,
            magSample = GLES30.GL_NEAREST
        )
        textureId[1] = TextureUtils.obtainTexture(
            context,
            R.drawable.sample_32,
            StretchMode.EDGE,
            minSample = GLES30.GL_LINEAR,
            magSample = GLES30.GL_LINEAR
        )
        textureId[2] = TextureUtils.obtainTexture(
            context,
            R.drawable.sample_32,
            StretchMode.EDGE,
            minSample = GLES30.GL_NEAREST,
            magSample = GLES30.GL_LINEAR
        )
        textureId[3] = TextureUtils.obtainTexture(
            context,
            R.drawable.sample_32,
            StretchMode.EDGE,
            minSample = GLES30.GL_LINEAR,
            magSample = GLES30.GL_NEAREST
        )

        textureId[4] = TextureUtils.obtainTexture(
            context,
            R.drawable.sample_256,
            StretchMode.EDGE,
            minSample = GLES30.GL_NEAREST,
            magSample = GLES30.GL_NEAREST
        )
        textureId[5] = TextureUtils.obtainTexture(
            context,
            R.drawable.sample_256,
            StretchMode.EDGE,
            minSample = GLES30.GL_LINEAR,
            magSample = GLES30.GL_LINEAR
        )
        textureId[6] = TextureUtils.obtainTexture(
            context,
            R.drawable.sample_256,
            StretchMode.EDGE,
            minSample = GLES30.GL_NEAREST,
            magSample = GLES30.GL_LINEAR
        )
        textureId[7] = TextureUtils.obtainTexture(
            context,
            R.drawable.sample_256,
            StretchMode.EDGE,
            minSample = GLES30.GL_LINEAR,
            magSample = GLES30.GL_NEAREST
        )

    }

    override fun drawData(data: SampleDrawer) {

        curMinTexId = when (sampleControlModel.smallSampleType) {
            SampleType.GL_NEAREST_GL_NEAREST -> textureId[0]
            SampleType.GL_LINEAR_GL_NEAREST -> textureId[3]
            SampleType.GL_NEAREST_GL_LINEAR -> textureId[2]
            SampleType.GL_LINEAR_GL_LINEAR -> textureId[1]
        }

        curMagTexId = when (sampleControlModel.bigSampleType) {
            SampleType.GL_NEAREST_GL_NEAREST -> textureId[4]
            SampleType.GL_LINEAR_GL_NEAREST -> textureId[7]
            SampleType.GL_NEAREST_GL_LINEAR -> textureId[6]
            SampleType.GL_LINEAR_GL_LINEAR -> textureId[5]
        }

        //绘制小纹理矩形
        pushMatrix()
        translate(0f, 1f, 1f)
        rotate(-20f, 0f, 0f, 1f)
        MatrixState.scale(0.3f, 0.3f, 0.3f)
        data.draw(curMinTexId)
        popMatrix()

        //绘制大纹理矩形
        pushMatrix()
        translate(0f, -0.3f, 1f)
        rotate(-20f, 0f, 0f, 1f)
        data.draw(curMagTexId)
        popMatrix()
    }
}