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

abstract class EglThread(
    private val bitmap: Bitmap,
    private val callback: (bitmap: Bitmap?) -> Unit
) : Runnable {

    protected val mProgram = Program()
    protected val mTexture = Texture()
    protected val mFBO = Texture()
    protected val mEglController = EglController()

    // 绘制坐标
    protected var mvPositionHandle = -1

    // 纹理
    protected var mvTextureHandle = -1

    // 纹理坐标
    protected var mvCoordinateHandle = -1

    // 顶点
    protected var mVerBuffer: FloatBuffer? = null

    // 纹理
    protected var mTexBuffer: FloatBuffer? = null

    // 矩阵
    protected var mMatrixState: MatrixState = MatrixState()

    // 矩阵
    protected var mvMatrixHandle = -1

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
    protected fun obtainTexturePosition() = floatArrayOf(
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
    protected fun obtainVertexPosition() = floatArrayOf(
        -1f, -1f,
        -1f, 1f,
        1f, -1f,
        1f, 1f
    )

    abstract fun obtainVertex(): String
    abstract fun obtainFragment(): String
    abstract fun init(program: Program)
    abstract fun draw()

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
        mVerBuffer = allocateFloatBuffer(obtainVertexPosition())
        mTexBuffer = allocateFloatBuffer(obtainTexturePosition())

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

        init(mProgram)
    }

    private fun release() {
        mTexture.release()
        mFBO.release()
        mProgram.release()
        mEglController.release()
    }

}