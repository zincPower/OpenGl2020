package com.zinc.buffer.model.vao

import android.content.Context
import android.opengl.GLES30
import com.zinc.base.IModel
import com.zinc.base.model.MaxObjInfo
import com.zinc.base.utils.MatrixState
import com.zinc.base.utils.MatrixState.getFinalMatrix
import com.zinc.base.utils.MatrixState.mMatrix
import com.zinc.base.utils.OpenGlUtils
import com.zinc.base.utils.allocateFloatBuffer
import com.zinc.base.utils.buffer.VaoUtils
import com.zinc.base.utils.buffer.VboUtils
import java.nio.FloatBuffer

/**
 * @author: Jiang Pengyong
 * @date: 2020/8/2 4:56 PM
 * @email: 56002982@qq.com
 * @des: 顶点数组对象
 */
class VaoModel(context: Context, private val maxObjInfo: MaxObjInfo) : IModel {

    // 自定义渲染管线着色器程序id
    private var mProgram = 0

    // 总变换矩阵引用
    private var muMVPMatrixHandle = 0

    // 位置、旋转变换矩阵
    private var muMMatrixHandle = 0

    // 顶点位置属性引用
    private var maPositionHandle = 0

    // 顶点法向量属性引用
    private var maNormalHandle = 0

    // 光源位置属性引用
    private var maLightLocationHandle = 0

    // 摄像机位置属性引用
    private var maCameraHandle = 0

    // 顶点纹理坐标属性引用
    private var maTexCoorHandle = 0

//    //顶点坐标数据缓冲
//    private var mVertexBuffer: FloatBuffer? = null
//
//    //顶点法向量数据缓冲
//    private var mNormalBuffer: FloatBuffer? = null
//
//    //顶点纹理坐标数据缓冲
//    private var mTextureBuffer: FloatBuffer? = null

    private var mVertexBufferId = 0
    private var mNormalBufferId = 0
    private var mTextureBufferId = 0

    private var vCount = 0

    private var vaoId = 0

    init {
        initShader(context)
        initVertexData()
    }

    override fun initShader(context: Context) {
        val texture = OpenGlUtils.loadFromAssetsFile("vertex.glsl", context.resources)
        val fragment = OpenGlUtils.loadFromAssetsFile("fragment.glsl", context.resources)

        mProgram = OpenGlUtils.createProgram(texture, fragment)

        // 获取程序中顶点位置属性引用
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition")
        // 获取程序中顶点颜色属性引用
        maNormalHandle = GLES30.glGetAttribLocation(mProgram, "aNormal")
        // 获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix")
        // 获取位置、旋转变换矩阵引用
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix")
        // 获取程序中光源位置引用
        maLightLocationHandle = GLES30.glGetUniformLocation(mProgram, "uLightLocation")
        // 获取程序中顶点纹理坐标属性引用
        maTexCoorHandle = GLES30.glGetAttribLocation(mProgram, "aTexCoor")
        // 获取程序中摄像机位置引用
        maCameraHandle = GLES30.glGetUniformLocation(mProgram, "uCamera")
    }

    override fun initVertexData() {
        // 创建缓冲区
        val vboBuffersId = VboUtils.createBuffer(3)

        // 顶点缓冲id
        mVertexBufferId = vboBuffersId[0]
        // 法向量缓冲id
        mNormalBufferId = vboBuffersId[1]
        // 纹理缓冲id
        mTextureBufferId = vboBuffersId[2]

        val mVertexBuffer = allocateFloatBuffer(maxObjInfo.vertexData)
        VboUtils.bindBuffer(mVertexBufferId)
        VboUtils.sendBufferData(maxObjInfo.vertexData.size * 4, mVertexBuffer)

        val mNormalBuffer = allocateFloatBuffer(maxObjInfo.normalData)
        VboUtils.bindBuffer(mNormalBufferId)
        VboUtils.sendBufferData(maxObjInfo.normalData.size * 4, mNormalBuffer)

        val mTextureBuffer = allocateFloatBuffer(maxObjInfo.textureData)
        VboUtils.bindBuffer(mTextureBufferId)
        VboUtils.sendBufferData(maxObjInfo.textureData.size * 4, mTextureBuffer)

        vCount = maxObjInfo.vertexData.size / 3

        initVAO()
    }

    /**
     * 初始化顶点数组对象
     */
    private fun initVAO() {
        vaoId = VaoUtils.createVertexArray(1)[0]
        VaoUtils.bindVertexArray(vaoId)

        // 启用顶点位置、法向量、纹理坐标数据
        GLES30.glEnableVertexAttribArray(maPositionHandle)
        GLES30.glEnableVertexAttribArray(maNormalHandle)
        GLES30.glEnableVertexAttribArray(maTexCoorHandle)

        // 将顶点数据送入顶点数组对象
        VaoUtils.sendFloatData(mVertexBufferId, maPositionHandle, 3)

        // 将法向量数据送入顶点数组对象
        VaoUtils.sendFloatData(mNormalBufferId, maNormalHandle, 3)

        // 将纹理数据送入顶点数组对象
        VaoUtils.sendFloatData(mTextureBufferId, maTexCoorHandle, 2)

        // 绑定到系统默认缓冲
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
        VaoUtils.unbindVertexArray()
    }

    override fun draw(textureId: Int) {
        // 制定使用某套着色器程序
        GLES30.glUseProgram(mProgram)
        // 将最终变换矩阵传入着色器程序
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, getFinalMatrix(), 0)
        // 将位置、旋转变换矩阵传入着色器程序
        GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, mMatrix, 0)
        // 将光源位置传入着色器程序
        GLES30.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB)
        // 将摄像机位置传入着色器程序
        GLES30.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB)

        // !!! 绑定顶点数组对象
        VaoUtils.bindVertexArray(vaoId)

        // 绑定纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId)

        // 绘制加载的物体
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount)

        // !!! 解绑顶点数组对象
        VaoUtils.unbindVertexArray()
    }

}