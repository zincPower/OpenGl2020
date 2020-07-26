package com.zinc.model_3d.model.cylinder

import android.content.Context
import android.opengl.GLES30
import com.zinc.base.IModel
import com.zinc.base.utils.MatrixState
import com.zinc.base.utils.OpenGlUtils
import com.zinc.model_3d.model.ControlModel3DInfo
import java.nio.FloatBuffer

/**
 * @author: Jiang Pengyong
 * @date: 2020/7/23 10:51 AM
 * @email: 56002982@qq.com
 * @des: 3d模型基类
 */
abstract class Model3DBaseModel(context: Context, val controlModel3DInfo: ControlModel3DInfo) :
    IModel {
    // 自定义渲染管线着色器程序id
    protected var mProgram = 0

    // 总变换矩阵引用
    protected var muMVPMatrixHandle = 0

    // 顶点位置属性引用
    protected var maPositionHandle = 0

    // 顶点纹理坐标属性引用
    protected var maTexCoorHandle = 0

    // 变换矩阵属性引用
    protected var muMMatrixHandle = 0

    // 摄像机位置属性引用
    protected var maCameraHandle = 0

    // 顶点法向量属性引用
    protected var maNormalHandle = 0

    // 光源位置属性引用
    protected var maLightLocationHandle = 0

    // 绘制类型
    protected var muDrawType = 0

    // 顶点坐标数据缓冲
    protected var mVertexBuffer: FloatBuffer? = null

    // 顶点纹理坐标数据缓冲
    protected var mTexCoorBuffer: FloatBuffer? = null

    // 顶点法向量数据缓冲
    protected var mNormalBuffer: FloatBuffer? = null

    // 顶点数量
    protected var vCount = 0

    override fun initShader(context: Context) {
        // 加载顶点着色器的脚本内容
        val vertex = OpenGlUtils.loadFromAssetsFile("vertex.glsl", context.resources)
        // 加载片元着色器的脚本内容
        val fragment = OpenGlUtils.loadFromAssetsFile("fragment.glsl", context.resources)
        // 基于顶点着色器与片元着色器创建程序
        mProgram = OpenGlUtils.createProgram(vertex, fragment)
        // 获取程序中顶点位置属性引用id
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition")
        // 获取程序中顶点纹理坐标属性引用id
        maTexCoorHandle = GLES30.glGetAttribLocation(mProgram, "aTexCoor")
        // 获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix")
        // 获取程序中顶点法向量属性引用id
        maNormalHandle = GLES30.glGetAttribLocation(mProgram, "aNormal")
        // 获取程序中摄像机位置引用id
        maCameraHandle = GLES30.glGetUniformLocation(mProgram, "uCamera")
        // 获取程序中光源位置引用id
        maLightLocationHandle = GLES30.glGetUniformLocation(mProgram, "uLightLocation")
        // 获取位置、旋转变换矩阵引用id
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix")
        // 获取绘制类型引用id
        muDrawType = GLES30.glGetUniformLocation(mProgram, "uDrawType")
    }

    override fun draw(textureId: Int) {
        // 制定使用某套shader程序
        GLES30.glUseProgram(mProgram)

        //将最终变换矩阵传入shader程序
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0)

        //将位置、旋转变换矩阵传入shader程序
        GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.mMatrix, 0)

        //将摄像机位置传入shader程序
        GLES30.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB)

        //将光源位置传入shader程序
        GLES30.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB)

        GLES30.glUniform1i(muDrawType, controlModel3DInfo.drawType.value)

        //传送顶点位置数据
        GLES30.glVertexAttribPointer(
            maPositionHandle,
            3,
            GLES30.GL_FLOAT,
            false,
            3 * 4,
            mVertexBuffer
        )

        //传送顶点纹理坐标数据
        GLES30.glVertexAttribPointer(
            maTexCoorHandle,
            2,
            GLES30.GL_FLOAT,
            false,
            2 * 4,
            mTexCoorBuffer
        )

        //传送顶点法向量数据
        GLES30.glVertexAttribPointer(
            maNormalHandle,
            4,
            GLES30.GL_FLOAT,
            false,
            3 * 4,
            mNormalBuffer
        )

        //启用顶点位置数据
        GLES30.glEnableVertexAttribArray(maPositionHandle)
        //启用顶点纹理数据
        GLES30.glEnableVertexAttribArray(maTexCoorHandle)
        //启用顶点法向量数据
        GLES30.glEnableVertexAttribArray(maNormalHandle)

        //绑定纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId)

        //绘制纹理矩形
        GLES30.glDrawArrays(getDrawType(), 0, vCount)
    }

    abstract fun getDrawType(): Int

}