package com.zinc.texture.sample

import android.content.Context
import android.opengl.GLES30
import com.zinc.base.IModel
import com.zinc.base.utils.MatrixState.getFinalMatrix
import com.zinc.base.utils.OpenGlUtils
import com.zinc.base.utils.allocateFloatBuffer
import java.nio.FloatBuffer


/**
 * @author: Jiang Pengyong
 * @date: 2020/8/22 6:01 PM
 * @email: 56002982@qq.com
 * @des: 采样模式
 */
class SampleDrawer(context: Context) : IModel {

    private var mProgram = 0
    private var muMVPMatrixHandle = 0
    private var maPositionHandle = 0
    private var maTexCoorHandle = 0
    private var mVertexShader: String? = null
    private var mFragmentShader: String? = null

    private var mVertexBuffer: FloatBuffer? = null
    private var mTexCoorBuffer: FloatBuffer? = null

    private var vCount = 0

    init {
        initShader(context)
        initVertexData()
    }

    override fun initShader(context: Context) {
        //加载顶点着色器的脚本内容
        mVertexShader = OpenGlUtils.loadFromAssetsFile("vertex.glsl", context.resources)
        //加载片元着色器的脚本内容
        mFragmentShader = OpenGlUtils.loadFromAssetsFile("fragment.glsl", context.resources)
        //基于顶点着色器与片元着色器创建程序
        mProgram = OpenGlUtils.createProgram(mVertexShader, mFragmentShader)
        //获取程序中顶点位置属性引用
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition")
        //获取程序中顶点纹理坐标属性引用
        maTexCoorHandle = GLES30.glGetAttribLocation(mProgram, "aTexCoor")
        //获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix")
    }

    override fun initVertexData() {
        //顶点坐标数据的初始化================begin============================
        vCount = 6
        val vertices = floatArrayOf( //较大的纹理矩形
            -0.8f, 0.8f, 0f,
            -0.8f, -0.8f, 0f,
            0.8f, -0.8f, 0f,
            0.8f, -0.8f, 0f,
            0.8f, 0.8f, 0f,
            -0.8f, 0.8f, 0f
        )

        mVertexBuffer = allocateFloatBuffer(vertices)

        val texCoor = floatArrayOf(
            0f, 0f,
            0f, 1f,
            1f, 1f,
            1f, 1f,
            1f, 0f,
            0f, 0f
        )
        mTexCoorBuffer = allocateFloatBuffer(texCoor)
    }

    override fun draw(textureId: Int) {
        GLES30.glUseProgram(mProgram)
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, getFinalMatrix(), 0)

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
    }
}