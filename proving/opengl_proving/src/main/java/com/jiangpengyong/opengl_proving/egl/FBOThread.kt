package com.jiangpengyong.opengl_proving.egl

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLES30
import android.opengl.GLUtils
import com.jiangpengyong.egl_object.egl.EglController
import com.jiangpengyong.egl_object.helper.TextureReaderHelper
import com.jiangpengyong.egl_object.model.ByteBufferUtils.allocateFloatBuffer
import com.jiangpengyong.egl_object.model.FboUtils
import com.jiangpengyong.egl_object.model.MatrixState
import com.jiangpengyong.egl_object.model.OpenGlUtils
import com.jiangpengyong.egl_object.model.Program
import com.jiangpengyong.egl_object.model.texture.Texture
import java.nio.FloatBuffer

class FBOThread(
    bitmap: Bitmap,
    callback: (bitmap: Bitmap?) -> Unit
) : EglThread(bitmap, callback) {

    override fun init(program: Program) {
    }

    override fun obtainVertex() = "" +
            "attribute vec4 vPosition;\n" +
            "attribute vec2 vCoordinate;\n" +
            "uniform mat4 vMatrix;\n" +
            "varying vec2 aCoordinate;\n" +
            "void main(){\n" +
            "    gl_Position=vMatrix*vPosition;\n" +
            "    aCoordinate=vCoordinate;\n" +
            "}\n"

    override fun obtainFragment() = "" +
            "precision mediump float;\n" +
            "uniform sampler2D vTexture;\n" +
            "varying vec2 aCoordinate;\n" +
            "void main(){\n" +
            "  vec4 color = texture2D(vTexture, aCoordinate);\n" +
            "  float colorR = (color.r + color.g + color.b) / 3.0;\n" +
            "  float colorG = (color.r + color.g + color.b) / 3.0;\n" +
            "  float colorB = (color.r + color.g + color.b) / 3.0;\n" +
            "  gl_FragColor = vec4(colorR, colorG, colorB, color.a);\n" +
            "}\n"

    private fun clear() {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        GLES20.glClear(
            GLES20.GL_COLOR_BUFFER_BIT
                    or GLES20.GL_DEPTH_BUFFER_BIT
                    or GLES20.GL_STENCIL_BUFFER_BIT
        )
    }

    override fun draw() {
        mFBO.bindToFBO()
        drawInner()
        mFBO.unbindToFBO()
    }

    private fun drawInner() {
        mProgram.useProgram()

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        mTexture.bind()
        GLES20.glUniform1i(mvTextureHandle, 0)

        GLES20.glUniformMatrix4fv(mvMatrixHandle, 1, false, mMatrixState.getFinalMatrix(), 0)

        GLES20.glEnableVertexAttribArray(mvPositionHandle)
        GLES20.glVertexAttribPointer(
            mvPositionHandle,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            mVerBuffer
        )

        GLES20.glEnableVertexAttribArray(mvCoordinateHandle)
        GLES20.glVertexAttribPointer(
            mvCoordinateHandle,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            mTexBuffer
        )

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glDisableVertexAttribArray(mvPositionHandle)
        GLES20.glDisableVertexAttribArray(mvCoordinateHandle)
    }
}