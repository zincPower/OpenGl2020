package com.jiangpengyong.opengl_proving.fbo.egl

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLES30
import android.util.Log
import com.jiangpengyong.egl_object.egl.EglController
import com.jiangpengyong.egl_object.helper.TextureReaderHelper
import com.jiangpengyong.egl_object.model.*
import com.jiangpengyong.egl_object.model.texture.Texture
import java.nio.FloatBuffer

class BlitFramebufferThread(
    private val bitmap: Bitmap,
    private val callback: (bitmap: Bitmap?) -> Unit
) : Runnable {

    private val mProgram = Program()
    private val mTexture = Texture()
    private val mEglController = EglController()

    private var mSamplerLoc = -1

    private var mMVPMatLoc = -1

    private var aPosition = -1

    private var aTexCoord = -1

    // 顶点
    private var mVerBuffer: FloatBuffer? = null

    // 纹理
    private var mTexBuffer: FloatBuffer? = null

    // 矩阵
    private val mMatrixState: MatrixState = MatrixState()

    private val mAttachTexture: Array<Texture> = Array(4) { Texture() }
    private val mFBO = Texture()

    private var mFBOId = -1

    private var inputWidth = 0
    private var inputHeight = 0

    private var outputWidth = 0
    private var outputHeight = 0

    /**
     *  纹理坐标
     *  (0, 1)  (1, 1)
     *  P2       P3
     *   -------
     *  ｜\    ｜
     *  ｜ \   ｜
     *  ｜  \  ｜
     *  ｜   \ ｜
     *  ｜    \｜
     *   -------
     *  P0      P1
     * (0, 0)  (1, 0)
     */
    private fun obtainTexturePosition() = floatArrayOf(
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 0.0f,
        1.0f, 1.0f
    )

    /**
     * 顶点坐标
     *  (-1, 1)  (1, 1)
     *  P2       P3
     *   -------
     *  ｜\    ｜
     *  ｜ \   ｜
     *  ｜  \  ｜
     *  ｜   \ ｜
     *  ｜    \｜
     *   -------
     *  P0      P1
     * (-1,-1)  (1,-1)
     */
    private fun obtainVertexPosition() = floatArrayOf(
        -1f, -1f,
        -1f, 1f,
        1f, -1f,
        1f, 1f
    )

    private fun obtainVertex() = "" +
            "#version 300 es\n" +
            "in vec4 a_position;\n" +
            "in vec2 a_texCoord;\n" +
            "uniform mat4 u_MVPMatrix;\n" +
            "out vec2 v_texCoord;\n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = u_MVPMatrix * a_position;\n" +
            "    v_texCoord = a_texCoord;\n" +
            "}";

    private fun obtainFragment() = "" +
            "#version 300 es\n" +
            "precision mediump float;\n" +
            "in vec2 v_texCoord;\n" +
            "layout(location = 0) out vec4 outColor0;\n" +
            "layout(location = 1) out vec4 outColor1;\n" +
            "layout(location = 2) out vec4 outColor2;\n" +
            "layout(location = 3) out vec4 outColor3;\n" +
            "uniform sampler2D s_Texture;\n" +
            "void main()\n" +
            "{\n" +
            "    vec4 outputColor = texture(s_Texture, v_texCoord);\n" +
            "    outColor0 = outputColor;\n" +
            "    outColor1 = vec4(outputColor.r, 0.0, 0.0, 1.0);\n" +
            "    outColor2 = vec4(0.0, outputColor.g, 0.0, 1.0);\n" +
            "    outColor3 = vec4(0.0, 0.0, outputColor.b, 1.0);\n" +
            "}";

    override fun run() {
        inputWidth = bitmap.width
        inputHeight = bitmap.height

        outputWidth = inputWidth * 2
        outputHeight = inputHeight * 2

        mEglController.init()
        if (!mEglController.attachPBuffer(outputWidth, outputHeight)) {
            release()
            return
        }

        init()
        draw()

        mFBO.initTexture(outputWidth, outputHeight)
        mFBO.bindToFBO()
        blitTextures()
        mFBO.unbindToFBO()

        callback.invoke(TextureReaderHelper.getBitmap(mFBO))

        release()
    }

    private fun init() {
        mProgram.createProgram(obtainVertex(), obtainFragment())

        mTexture.initTexture(bitmap = bitmap, isNeedRecycleBitmap = true)
        mVerBuffer = ByteBufferUtils.allocateFloatBuffer(obtainVertexPosition())
        mTexBuffer = ByteBufferUtils.allocateFloatBuffer(obtainTexturePosition())

        mSamplerLoc = mProgram.getUniformLocation("s_Texture")
        mMVPMatLoc = mProgram.getUniformLocation("u_MVPMatrix")
        aPosition = mProgram.getAttribLocation("a_position")
        aTexCoord = mProgram.getAttribLocation("a_texCoord")

        mMatrixState.setProjectOrtho(
            -1f, 1f,
            -1f, 1f,
            3f, 6f
        )
        mMatrixState.setCamera(
            0f, 0f, 5.0f,
            0f, 0f, 0f,
            0f, 1.0f, 0.0f
        )
        initFBO(inputWidth, inputHeight)
    }

    private fun draw() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFBOId)
        GLES20.glViewport(0, 0, inputWidth, inputHeight)
        clear()

        GLES30.glDrawBuffers(attachments.size, attachments, 0)

        GLES20.glUseProgram(mProgram.programId())

        GLES20.glUniformMatrix4fv(
            mMVPMatLoc,
            1,
            false,
            mMatrixState.getFinalMatrix(),
            0
        )

        GLES20.glEnableVertexAttribArray(aPosition)
        GLES20.glVertexAttribPointer(
            aPosition,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            mVerBuffer
        )

        GLES20.glEnableVertexAttribArray(aTexCoord)
        GLES20.glVertexAttribPointer(
            aTexCoord,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            mTexBuffer
        )

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        mTexture.bind()
        GLES20.glUniform1i(mSamplerLoc, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glDisableVertexAttribArray(aPosition)
        GLES20.glDisableVertexAttribArray(aTexCoord)

        GLES20.glBindFramebuffer(GLES30.GL_DRAW_FRAMEBUFFER, 0)
        GLES20.glViewport(0, 0, outputWidth, outputHeight)
        clear()
    }

    private fun release() {
        mTexture.release()
        FboUtils.deleteFrameBuffer(mFBOId)
        mProgram.release()
        mEglController.release()
    }

    private fun blitTextures() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFBOId)

        GLES30.glReadBuffer(GLES30.GL_COLOR_ATTACHMENT0)
        GLES30.glBlitFramebuffer(
            0, 0, inputWidth, inputHeight,
            0, 0, outputWidth, outputHeight,
            GLES20.GL_COLOR_BUFFER_BIT, GLES20.GL_LINEAR
        )

//        GLES30.glReadBuffer(GLES30.GL_COLOR_ATTACHMENT1)
//        GLES30.glBlitFramebuffer(
//            0, 0, inputWidth, inputHeight,
//            inputWidth, 0, outputWidth, outputHeight / 2,
//            GLES20.GL_COLOR_BUFFER_BIT, GLES20.GL_LINEAR
//        )
//
        GLES30.glReadBuffer(GLES30.GL_COLOR_ATTACHMENT2)
        GLES30.glBlitFramebuffer(
            0, 0, inputWidth, inputHeight,
            0, inputHeight, outputWidth / 2, outputHeight,
            GLES20.GL_COLOR_BUFFER_BIT, GLES20.GL_LINEAR
        )
//
//        GLES30.glReadBuffer(GLES30.GL_COLOR_ATTACHMENT3)
//        GLES30.glBlitFramebuffer(
//            0, 0, inputWidth, inputHeight,
//            inputWidth, inputHeight, outputWidth, outputHeight,
//            GLES20.GL_COLOR_BUFFER_BIT, GLES20.GL_LINEAR
//        )
    }

    private fun initFBO(width: Int, height: Int) {
        val fbo = IntArray(1)
        GLES20.glGenFramebuffers(1, fbo, 0)
        mFBOId = fbo[0]
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFBOId)

        mAttachTexture.forEachIndexed { index, texture ->
            texture.initTexture(width, height)
            GLES20.glFramebufferTexture2D(
                GLES30.GL_DRAW_FRAMEBUFFER,
                attachments[index],
                GLES20.GL_TEXTURE_2D,
                texture.textureId,
                0
            )
        }

        if (GLES20.GL_FRAMEBUFFER_COMPLETE != GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER)) {
            Log.e(TAG, "FrameBuffer is create failure.")
            return
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        return
    }

    private fun clear() {
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glClear(
            GLES20.GL_COLOR_BUFFER_BIT
                    or GLES20.GL_DEPTH_BUFFER_BIT
                    or GLES20.GL_STENCIL_BUFFER_BIT
        )
    }

    companion object {
        private const val TAG = "FBO Blit"

        private val attachments = intArrayOf(
            GLES30.GL_COLOR_ATTACHMENT0,
            GLES30.GL_COLOR_ATTACHMENT1,
            GLES30.GL_COLOR_ATTACHMENT2,
            GLES30.GL_COLOR_ATTACHMENT3
        )
    }
}
