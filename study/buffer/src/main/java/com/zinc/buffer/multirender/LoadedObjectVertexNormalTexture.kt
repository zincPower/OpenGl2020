package com.zinc.buffer.multirender

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLES30
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

//加载后的物体——仅携带顶点信息，颜色随机
@SuppressLint("NewApi")
class LoadedObjectVertexNormalTexture(
    var context: Context,
    vertices: FloatArray,
    normals: FloatArray,
    texCoors: FloatArray
) {
    // 自定义渲染管线着色器程序 id
    var mProgram = 0

    // 总变换矩阵引用
    var muMVPMatrixHandle = 0

    // 位置、旋转变换矩阵
    var muMMatrixHandle = 0

    // 顶点位置属性引用
    var maPositionHandle = 0

    // 顶点法向量属性引用
    var maNormalHandle = 0

    // 光源位置属性引用
    private var maLightLocationHandle = 0

    // 摄像机位置属性引用
    var maCameraHandle = 0

    // 顶点纹理坐标属性引用
    var maTexCoorHandle = 0

    // 顶点着色器代码脚本
    var mVertexShader: String? = null

    // 片元着色器代码脚本
    var mFragmentShader: String? = null

    // 顶点坐标数据缓冲
    var mVertexBuffer: FloatBuffer? = null

    // 顶点法向量数据缓冲
    var mNormalBuffer: FloatBuffer? = null

    // 顶点纹理坐标数据缓冲
    private var mTexCoorBuffer: FloatBuffer? = null
    var vCount = 0

    //初始化顶点数据的方法
    private fun initVertexData(vertices: FloatArray, normals: FloatArray, texCoors: FloatArray) {
        //顶点坐标数据的初始化================begin============================
        vCount = vertices.size / 3

        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        val vbb = ByteBuffer.allocateDirect(vertices.size * 4)
        vbb.order(ByteOrder.nativeOrder()) //设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer() //转换为Float型缓冲
        mVertexBuffer?.put(vertices) //向缓冲区中放入顶点坐标数据
        mVertexBuffer?.position(0) //设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点坐标数据的初始化================end============================

        //顶点法向量数据的初始化================begin============================
        val cbb = ByteBuffer.allocateDirect(normals.size * 4)
        cbb.order(ByteOrder.nativeOrder()) //设置字节顺序
        mNormalBuffer = cbb.asFloatBuffer() //转换为Float型缓冲
        mNormalBuffer?.put(normals) //向缓冲区中放入顶点法向量数据
        mNormalBuffer?.position(0) //设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点着色数据的初始化================end============================

        //顶点纹理坐标数据的初始化================begin============================
        val tbb = ByteBuffer.allocateDirect(texCoors.size * 4)
        tbb.order(ByteOrder.nativeOrder()) //设置字节顺序
        mTexCoorBuffer = tbb.asFloatBuffer() //转换为Float型缓冲
        mTexCoorBuffer?.put(texCoors) //向缓冲区中放入顶点纹理坐标数据
        mTexCoorBuffer?.position(0) //设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点纹理坐标数据的初始化================end============================
    }

    //初始化着色器的方法
    private fun initShader() {
        //加载顶点着色器的脚本内容
        mVertexShader = ShaderUtil.loadFromAssetsFile("multirender/vertex.glsl", context.resources)
        //加载片元着色器的脚本内容
        mFragmentShader = ShaderUtil.loadFromAssetsFile("multirender/frag.glsl", context.resources)
        //基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader)
        //获取程序中顶点位置属性引用
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition")
        //获取程序中顶点颜色属性引用
        maNormalHandle = GLES30.glGetAttribLocation(mProgram, "aNormal")
        //获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix")
        //获取位置、旋转变换矩阵引用
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix")
        //获取程序中光源位置引用
        maLightLocationHandle = GLES30.glGetUniformLocation(mProgram, "uLightLocation")
        //获取程序中顶点纹理坐标属性引用
        maTexCoorHandle = GLES30.glGetAttribLocation(mProgram, "aTexCoor")
        //获取程序中摄像机位置引用
        maCameraHandle = GLES30.glGetUniformLocation(mProgram, "uCamera")
    }

    fun drawSelf(texId: Int) {
        //指定使用某套着色器程序
        GLES30.glUseProgram(mProgram)
        MatrixState.pushMatrix() //保护场景
        //将最终变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.finalMatrix, 0)
        //将位置、旋转变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.mMatrix, 0)
        //将光源位置传入渲染管线
        GLES30.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB)
        //将摄像机位置传入渲染管线
        GLES30.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB)
        // 将顶点位置数据传入渲染管线
        GLES30.glVertexAttribPointer(
            maPositionHandle,
            3,
            GLES30.GL_FLOAT,
            false,
            3 * 4,
            mVertexBuffer
        )
        //将顶点法向量数据传入渲染管线
        GLES30.glVertexAttribPointer(
            maNormalHandle,
            3,
            GLES30.GL_FLOAT,
            false,
            3 * 4,
            mNormalBuffer
        )
        //将顶点纹理数据传入渲染管线
        GLES30.glVertexAttribPointer(
            maTexCoorHandle,
            2,
            GLES30.GL_FLOAT,
            false,
            2 * 4,
            mTexCoorBuffer
        )
        //启用顶点位置、法向量、纹理坐标数据
        GLES30.glEnableVertexAttribArray(maPositionHandle)
        GLES30.glEnableVertexAttribArray(maNormalHandle)
        GLES30.glEnableVertexAttribArray(maTexCoorHandle)
        //绑定纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId)
        //绘制加载的物体
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount)
        MatrixState.popMatrix() //恢复场景
    }

    init {
        //初始化顶点数据的方法
        initVertexData(vertices, normals, texCoors)
        //初始化着色器
        initShader()
    }
}