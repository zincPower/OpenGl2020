package com.zinc.buffer.model.fbo

import android.content.Context
import android.opengl.GLES30
import com.zinc.base.IModel
import com.zinc.base.utils.MatrixState
import com.zinc.base.utils.OpenGlUtils
import com.zinc.base.utils.allocateFloatBuffer
import java.nio.FloatBuffer

/**
 * @author: Jiang Pengyong
 * @date: 2020/8/9 7:02 PM
 * @email: 56002982@qq.com
 * @des: 绘制纹理
 */
class TextureModel(context: Context, val ratio: Float) : IModel {

    // 自定义渲染管线程序id
    var mProgram = -1

    // 总变换矩阵引用
    var muMVPMatrixHandle = -1

    // 顶点位置属性引用
    var maPositionHandle = -1

    // 顶点纹理坐标属性引用
    var maTexCoorHandle = -1

    // 顶点坐标数据缓冲
    var mVertexBuffer: FloatBuffer? = null

    // 顶点纹理坐标数据缓冲
    var mTexCoorBuffer: FloatBuffer? = null

    var vCount = 0

    init {
        initVertexData()
        initShader(context)
    }

    override fun initShader(context: Context) {
        val vertex = OpenGlUtils.loadFromAssetsFile("vertex_tex.glsl", context.resources)
        val fragment = OpenGlUtils.loadFromAssetsFile("fragment_tex.glsl", context.resources)

        mProgram = OpenGlUtils.createProgram(vertex, fragment)
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition")
        maTexCoorHandle = GLES30.glGetAttribLocation(mProgram, "aTexCoor")
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix")
    }

    override fun initVertexData() {
        vCount = 6

        val vertices = floatArrayOf(
            -ratio, 1f, 0f,
            -ratio, -1f, 0f,
            ratio, -1f, 0f,
            ratio, -1f, 0f,
            ratio, 1f, 0f,
            -ratio, 1f, 0f
        )
        mVertexBuffer = allocateFloatBuffer(vertices)

        val texture = floatArrayOf(
            0f, 1f,
            0f, 0f,
            1f, 0f,
            1f, 0f,
            1f, 1f,
            0f, 1f
        )
        mTexCoorBuffer = allocateFloatBuffer(texture)
    }

    override fun draw(textureId: Int) {
        GLES30.glUseProgram(mProgram)
        MatrixState.pushMatrix()

        MatrixState.translate(0f, 0f, 1f)
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0)

        GLES30.glVertexAttribPointer(
            maPositionHandle,
            3,
            GLES30.GL_FLOAT,
            false,
            3 * 4,
            mVertexBuffer
        )
        GLES30.glVertexAttribPointer(
            maTexCoorHandle,
            2,
            GLES30.GL_FLOAT,
            false,
            2 * 4,
            mTexCoorBuffer
        )

        GLES30.glEnableVertexAttribArray(maPositionHandle)
        GLES30.glEnableVertexAttribArray(maTexCoorHandle)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId)

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount)
        MatrixState.popMatrix()
    }
}