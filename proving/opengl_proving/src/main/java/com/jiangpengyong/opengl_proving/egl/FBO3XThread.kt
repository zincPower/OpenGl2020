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

class FBO3XThread(
    bitmap: Bitmap,
    callback: (bitmap: Bitmap?) -> Unit
) : EglThread(bitmap, callback) {

    override fun init(program: Program) {
    }

    override fun obtainVertex() = "" +
            "#version 300 es\n" +
            "in vec4 vPosition;\n" +
            "in vec2 vCoordinate;\n" +
            "uniform mat4 vMatrix;\n" +
            "out vec2 aCoordinate;\n" +
            "void main(){\n" +
            "    gl_Position=vMatrix*vPosition;\n" +
            "    aCoordinate=vCoordinate.xy * 0.5 + 0.5;\n" +
            "}\n"

    override fun obtainFragment() = "" +
            "#version 300 es\n" +
            "precision mediump float;\n" +
            "uniform sampler2D vTexture;\n" +
            "out vec4 fragColor;\n" +
            "in vec2 aCoordinate;\n" +
            "void main(){\n" +
            "  vec4 color = texture(vTexture, aCoordinate);\n" +
            "  float colorR = (color.r + color.g + color.b) / 3.0;\n" +
            "  float colorG = (color.r + color.g + color.b) / 3.0;\n" +
            "  float colorB = (color.r + color.g + color.b) / 3.0;\n" +
            "  fragColor = vec4(colorR, colorG, colorB, color.a);\n" +
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
        blitTextures()
        OpenGlUtils.checkGlError("EGL THREAD0")
        drawInner()
        mFBO.unbindToFBO()
        OpenGlUtils.checkGlError("EGL THREAD1")
    }

    private fun drawInner() {
        mProgram.useProgram()

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        mTexture.bind()
        GLES20.glUniform1i(mvTextureHandle, 0)

        GLES20.glUniformMatrix4fv(
            mvMatrixHandle,
            1,
            false,
            mMatrixState.getFinalMatrix(),
            0
        )

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

    private fun blitTextures() {
        mTexture.initFBO()
        FboUtils.bindFrameBuffer(
            mTexture.fboId,
//            GLES30.GL_READ_FRAMEBUFFER
            GLES30.GL_DRAW_FRAMEBUFFER
        )
//        mTexture.bindToFBO(GLES30.GL_READ_FRAMEBUFFER)
        OpenGlUtils.checkGlError("EGL THREAD--02")

        //很重要，指定源帧缓冲区
        mFBO.initFBO()
        FboUtils.bindFrameBuffer(
            mFBO.fboId,
//            GLES30.GL_DRAW_FRAMEBUFFER
            GLES30.GL_READ_FRAMEBUFFER
        )
//        mFBO.bindToFBO(GLES30.GL_DRAW_FRAMEBUFFER)
        OpenGlUtils.checkGlError("EGL THREAD--01")

        GLES30.glReadBuffer(GLES20.GL_COLOR_ATTACHMENT0)
        GLES30.glBlitFramebuffer(
            0, 0, mFBO.width, mFBO.height,
            0, 0, mFBO.width / 2, mFBO.height / 2,
            GLES20.GL_COLOR_BUFFER_BIT, GLES20.GL_LINEAR
        )
        OpenGlUtils.checkGlError("EGL THREAD--03")

        GLES30.glReadBuffer(GLES30.GL_COLOR_ATTACHMENT1)
        GLES30.glBlitFramebuffer(
            0, 0, mFBO.width, mFBO.height,
            mFBO.width / 2, 0, mFBO.width, mFBO.height / 2,
            GLES20.GL_COLOR_BUFFER_BIT, GLES20.GL_LINEAR
        )
        OpenGlUtils.checkGlError("EGL THREAD--04")

        GLES30.glReadBuffer(GLES30.GL_COLOR_ATTACHMENT2)
        GLES30.glBlitFramebuffer(
            0, 0, mFBO.width, mFBO.height,
            0, mFBO.width / 2, mFBO.width / 2, mFBO.height,
            GLES20.GL_COLOR_BUFFER_BIT, GLES20.GL_LINEAR
        )
        OpenGlUtils.checkGlError("EGL THREAD--05")

        GLES30.glReadBuffer(GLES30.GL_COLOR_ATTACHMENT3)
        GLES30.glBlitFramebuffer(
            0, 0, mFBO.width, mFBO.height,
            mFBO.width / 2, mFBO.height / 2, mFBO.width, mFBO.height,
            GLES20.GL_COLOR_BUFFER_BIT, GLES20.GL_LINEAR
        )
        OpenGlUtils.checkGlError("EGL THREAD--06")
    }
}