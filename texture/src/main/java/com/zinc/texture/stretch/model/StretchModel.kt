package com.zinc.texture.stretch.model

import android.content.Context
import android.opengl.GLES30
import com.zinc.base.IModel
import com.zinc.base.utils.MatrixState
import com.zinc.base.utils.OpenGlUtils
import com.zinc.base.utils.allocateFloatBuffer
import com.zinc.texture.stretch.control.TextureSize
import java.nio.FloatBuffer


/**
 * @author: Jiang Pengyong
 * @date: 2020/7/22 10:56 AM
 * @email: 56002982@qq.com
 * @des: 拉伸模型
 */
class StretchModel(context: Context, private val textureSize: TextureSize) : IModel {

    // 自定义渲染管线着色器程序id
    private var mProgram = 0

    // 总变换矩阵引用
    private var muMVPMatrixHandle = 0

    // 顶点位置属性引用
    private var maPositionHandle = 0

    // 顶点纹理坐标属性引用
    private var maTexCoorHandle = 0

    // 顶点坐标数据缓冲
    private var mVertexBuffer: FloatBuffer? = null

    // 顶点纹理坐标数据缓冲
    private var mTexCoorBuffer: FloatBuffer? = null

    // s纹理坐标范围
    private var sRange = textureSize.size.width.toFloat()

    // t纹理坐标范围
    private var tRange = textureSize.size.height.toFloat()

    // 顶点数量
    private var vCount = 0

    init {
        initShader(context)
        initVertexData()
    }

    override fun initShader(context: Context) {
        val vertex = OpenGlUtils.loadFromAssetsFile("vertex.glsl", context.resources)
        val fragment = OpenGlUtils.loadFromAssetsFile("fragment.glsl", context.resources)
        mProgram = OpenGlUtils.createProgram(vertex, fragment)
        // 获取程序中顶点位置属性引用
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition")
        // 获取程序中顶点纹理坐标属性引用
        maTexCoorHandle = GLES30.glGetAttribLocation(mProgram, "aTexCoor")
        // 获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix")
    }

    override fun initVertexData() {
        val vertices = floatArrayOf(
            -1f, 1f, 0f,
            -1f, -1f, 0f,
            1f, -1f, 0f,
            1f, -1f, 0f,
            1f, 1f, 0f,
            -1f, 1f, 0f
        )
        vCount = vertices.size / 3
        mVertexBuffer = allocateFloatBuffer(vertices)

        val texCoor = floatArrayOf(
            0f, 0f,
            0f, tRange,
            sRange, tRange,
            sRange, tRange,
            sRange, 0f,
            0f, 0f
        )
        mTexCoorBuffer = allocateFloatBuffer(texCoor)
    }

    override fun draw(textureId: Int) {
        GLES30.glUseProgram(mProgram)
        GLES30.glUniformMatrix4fv(
            muMVPMatrixHandle,
            1,
            false,
            MatrixState.getFinalMatrix(),
            0
        )

        //将顶点位置数据传送进渲染管线
        GLES30.glVertexAttribPointer(
            maPositionHandle,
            3,
            GLES30.GL_FLOAT,
            false,
            3 * 4,
            mVertexBuffer
        )
        //启用顶点位置数据数组
        GLES30.glEnableVertexAttribArray(maPositionHandle)

        //将纹理坐标数据传送进渲染管线
        GLES30.glVertexAttribPointer(
            maTexCoorHandle,
            2,
            GLES30.GL_FLOAT,
            false,
            2 * 4,
            mTexCoorBuffer
        )
        //启用顶点纹理坐标数据数组
        GLES30.glEnableVertexAttribArray(maTexCoorHandle)

        // 绑定纹理
        // 设置使用的纹理编号
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        // 绑定指定的纹理id
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId)

        // 绘制
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount)
    }

}