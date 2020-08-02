package com.zinc.buffer.model.ubo

import android.content.Context
import android.opengl.GLES30
import com.zinc.base.IModel
import com.zinc.base.model.MaxObjInfo
import com.zinc.base.utils.MatrixState
import com.zinc.base.utils.MatrixState.getFinalMatrix
import com.zinc.base.utils.MatrixState.mMatrix
import com.zinc.base.utils.OpenGlUtils
import com.zinc.base.utils.allocatFloatBuffer
import com.zinc.base.utils.buffer.UboUtils
import com.zinc.base.utils.buffer.VboUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * @author: Jiang Pengyong
 * @date: 2020/8/2 5:47 PM
 * @email: 56002982@qq.com
 * @des: 一致缓冲区对象
 */
class UboModel(context: Context, private val maxObjInfo: MaxObjInfo) : IModel {

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

    // 顶点纹理坐标属性引用
    private var maTexCoorHandle = 0

    // 一致块引用
    private var muBlockHandle = 0

    //顶点坐标数据缓冲
    private var mVertexBuffer: FloatBuffer? = null

    //顶点法向量数据缓冲
    private var mNormalBuffer: FloatBuffer? = null

    //顶点纹理坐标数据缓冲
    private var mTextureBuffer: FloatBuffer? = null

    private var vCount = 0

    private var uboId = 0

    init {
        initShader(context)
        initVertexData()
    }

    override fun initShader(context: Context) {
        val texture = OpenGlUtils.loadFromAssetsFile("vertex_uniform.glsl", context.resources)
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
//        maLightLocationHandle = GLES30.glGetUniformLocation(mProgram, "uLightLocation")
        // 获取程序中顶点纹理坐标属性引用
        maTexCoorHandle = GLES30.glGetAttribLocation(mProgram, "aTexCoor")
        // 获取程序中摄像机位置引用
//        maCameraHandle = GLES30.glGetUniformLocation(mProgram, "uCamera")

        initUBO()
    }

    override fun initVertexData() {
        mVertexBuffer = allocatFloatBuffer(maxObjInfo.vertexData)
        mTextureBuffer = allocatFloatBuffer(maxObjInfo.textureData)
        mNormalBuffer = allocatFloatBuffer(maxObjInfo.normalData)

        vCount = maxObjInfo.vertexData.size / 3
    }

    /**
     * 初始化一致缓冲对象
     */
    private fun initUBO() {
        // 1.获取一致块的索引
        muBlockHandle = GLES30.glGetUniformBlockIndex(mProgram, "MyDataBlock")

        // 2.获取一致块的尺寸
        val blockSize = UboUtils.obtainUniformBlockSize(mProgram, muBlockHandle)

        // 3.一致块内的成员索引
        val names = arrayOf("MyDataBlock.uLightLocation", "MyDataBlock.uCamera")
        val uIndices = UboUtils.obtainUniformBlockMemberIndex(mProgram, names)

        // 4.获取一致块内的成员偏移量
        val offset = UboUtils.obtainUniformBlockOffset(mProgram, uIndices)

        // 5.创建一致缓冲对象
        uboId = UboUtils.createUniformBlock(1)[0]

        // 6.将一致缓冲对象绑定到一致块
        UboUtils.bindUniformBlock(muBlockHandle, uboId)

        // 7. 开辟内存，进行数据传入
        val ubb = ByteBuffer.allocateDirect(blockSize)
        ubb.order(ByteOrder.nativeOrder())
        val uBlockBuffer = ubb.asFloatBuffer()
        // 7-1 将光源位置数据送入内存缓冲
        val data = MatrixState.lightLocation
        uBlockBuffer.position(offset[0] / 4)
        uBlockBuffer.put(data)
        // 7.2 将摄像机位置数据送入内存缓冲
        val data1 = MatrixState.cameraLocation
        uBlockBuffer.position(offset[1] / 4)
        uBlockBuffer.put(data1)
        // 7.3 设置缓冲起始偏移量
        uBlockBuffer.position(0)

        // 8.将光源位置、摄像机位置总数据内存缓冲中的数据送入一致缓冲
        UboUtils.sendData(blockSize, uBlockBuffer)
    }

    override fun draw(textureId: Int) {
        // 制定使用某套着色器程序
        GLES30.glUseProgram(mProgram)
        // 将最终变换矩阵传入着色器程序
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, getFinalMatrix(), 0)
        // 将位置、旋转变换矩阵传入着色器程序
        GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, mMatrix, 0)

        // !!! 一致块绑定一致缓冲
        UboUtils.bindUniformBlock(muBlockHandle, uboId)

        // 启用顶点位置数据数组
        GLES30.glEnableVertexAttribArray(maPositionHandle)
        // 启用顶点法向量数据数组
        GLES30.glEnableVertexAttribArray(maNormalHandle)
        // 启用顶纹理坐标数据数组
        GLES30.glEnableVertexAttribArray(maTexCoorHandle)

        // 将顶点位置数据送入渲染管线
        GLES30.glVertexAttribPointer(
            maPositionHandle,
            3,
            GLES30.GL_FLOAT,
            false,
            3 * 4,
            mVertexBuffer
        )
        // 将顶点法向量送入渲染管线
        GLES30.glVertexAttribPointer(
            maNormalHandle,
            3,
            GLES30.GL_FLOAT,
            false,
            3 * 4,
            mNormalBuffer
        )
        // 将顶点纹理数据送入渲染管线
        GLES30.glVertexAttribPointer(
            maTexCoorHandle,
            2,
            GLES30.GL_FLOAT,
            false,
            2 * 4,
            mTextureBuffer
        )

        // 绑定纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId)

        // 绘制加载的物体
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount)

    }

}