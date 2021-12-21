package com.jiangpengyong.opengl_proving.fbo.egl

import android.graphics.Bitmap
import android.opengl.GLES20
import com.jiangpengyong.egl_object.egl.EglController
import com.jiangpengyong.egl_object.helper.TextureReaderHelper
import com.jiangpengyong.egl_object.model.ByteBufferUtils
import com.jiangpengyong.egl_object.model.MatrixState
import com.jiangpengyong.egl_object.model.Program
import com.jiangpengyong.egl_object.model.texture.Texture
import java.nio.FloatBuffer

class FBOThread(
    private val bitmap: Bitmap,
    private val callback: (bitmap: Bitmap?) -> Unit
) : Runnable {

    private fun obtainVertex() = "" +
            "attribute vec4 vPosition;\n" +
            "attribute vec2 vCoordinate;\n" +
            "uniform mat4 vMatrix;\n" +
            "varying vec2 aCoordinate;\n" +
            "void main(){\n" +
            "    gl_Position=vMatrix*vPosition;\n" +
            "    aCoordinate=vCoordinate;\n" +
            "}\n"

    private fun obtainFragment() = "" +
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

    private val mProgram = Program()
    private val mTexture = Texture()
    private val mFBO = Texture()
    private val mEglController = EglController()

    // 绘制坐标
    private var mvPositionHandle = -1

    // 纹理
    private var mvTextureHandle = -1

    // 纹理坐标
    private var mvCoordinateHandle = -1

    // 顶点
    private var mVerBuffer: FloatBuffer? = null

    // 纹理
    private var mTexBuffer: FloatBuffer? = null

    // 矩阵
    private var mMatrixState: MatrixState = MatrixState()

    // 矩阵
    private var mvMatrixHandle = -1

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

    override fun run() {
        val width = bitmap.width
        val height = bitmap.height

        mEglController.init()
        if (!mEglController.attachPBuffer(width, height)) {
            release()
            return
        }

        GLES20.glViewport(0, 0, width, height)
        clear()
        init()
        draw()
        callback.invoke(TextureReaderHelper.getBitmap(mFBO))
        release()
    }

    private fun clear() {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        GLES20.glClear(
            GLES20.GL_COLOR_BUFFER_BIT
                    or GLES20.GL_DEPTH_BUFFER_BIT
                    or GLES20.GL_STENCIL_BUFFER_BIT
        )
    }

    private fun init() {
        mProgram.createProgram(obtainVertex(), obtainFragment())

        mFBO.initTexture(bitmap.width, bitmap.height)
        mTexture.initTexture(bitmap = bitmap, isNeedRecycleBitmap = true)
        mVerBuffer = ByteBufferUtils.allocateFloatBuffer(obtainVertexPosition())
        mTexBuffer = ByteBufferUtils.allocateFloatBuffer(obtainTexturePosition())

        mvPositionHandle = mProgram.getAttribLocation("vPosition")
        mvCoordinateHandle = mProgram.getAttribLocation("vCoordinate")
        mvMatrixHandle = mProgram.getUniformLocation("vMatrix")
        mvTextureHandle = mProgram.getUniformLocation("vTexture")

        // 设置正交投影（只有这个范围内的才会进行裁剪）
        mMatrixState.setProjectOrtho(
            -1f, 1f,
            -1f, 1f,
            3f, 6f
        )

        // 设置相机
        mMatrixState.setCamera(
            0f, 0f, 5.0f,
            0f, 0f, 0f,
            0f, 1.0f, 0.0f
        )
    }

    private fun release() {
        mTexture.release()
        mFBO.release()
        mProgram.release()
        mEglController.release()
    }


    private fun draw() {
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