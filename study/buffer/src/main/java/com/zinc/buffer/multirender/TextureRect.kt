package com.zinc.buffer.multirender

import com.zinc.buffer.multirender.ShaderUtil.loadFromAssetsFile
import com.zinc.buffer.multirender.ShaderUtil.createProgram
import com.zinc.buffer.multirender.MatrixState.pushMatrix
import com.zinc.buffer.multirender.MatrixState.finalMatrix
import com.zinc.buffer.multirender.MatrixState.popMatrix
import android.annotation.SuppressLint
import android.content.Context
import com.zinc.buffer.multirender.MySurfaceView
import com.zinc.buffer.multirender.ShaderUtil
import android.opengl.GLES30
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

//纹理三角形
@SuppressLint("NewApi")
class TextureRect(context: Context, ratio: Float) {
    //自定义渲染管线程序id
    private var mProgram = 0

    //总变换矩阵引用id
    private var muMVPMatrixHandle = 0

    //顶点位置属性引用id
    private var maPositionHandle = 0

    //顶点纹理坐标属性引用id
    private var maTexCoorHandle = 0

    //顶点着色器代码脚本
    private var mVertexShader: String? = null

    //片元着色器代码脚本
    private var mFragmentShader: String? = null

    //顶点坐标数据缓冲
    private var mVertexBuffer: FloatBuffer? = null

    //顶点纹理坐标数据缓冲
    private var mTexCoorBuffer: FloatBuffer? = null
    private var vCount = 0

    //调用初始化顶点数据
    private fun initVertexData(ratio: Float) {
        //顶点坐标数据的初始化================begin============================
        vCount = 6
        val UNIT_SIZE = 0.48f
        val vertices = floatArrayOf(
            -ratio * UNIT_SIZE, UNIT_SIZE, 0f,
            -ratio * UNIT_SIZE, -UNIT_SIZE, 0f,
            ratio * UNIT_SIZE, -UNIT_SIZE, 0f,
            ratio * UNIT_SIZE, -UNIT_SIZE, 0f,
            ratio * UNIT_SIZE, UNIT_SIZE, 0f,
            -ratio * UNIT_SIZE, UNIT_SIZE, 0f
        )

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

        //顶点纹理坐标数据的初始化================begin============================
        val texCoor = floatArrayOf(
            0f, (1 - 0).toFloat(), 0f, 1 - 1.0f, 1.0f, 1 - 1.0f,
            1.0f, 1 - 1.0f, 1.0f, (1 - 0).toFloat(), 0f, (1 - 0
                    ).toFloat()
        )
        //创建顶点纹理坐标数据缓冲
        val cbb = ByteBuffer.allocateDirect(texCoor.size * 4)
        cbb.order(ByteOrder.nativeOrder()) //设置字节顺序
        mTexCoorBuffer = cbb.asFloatBuffer() //转换为Float型缓冲
        mTexCoorBuffer?.put(texCoor) //向缓冲区中放入顶点着色数据
        mTexCoorBuffer?.position(0) //设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点纹理坐标数据的初始化================end============================
    }

    //初始化着色器
    private fun initShader(context: Context) {
        //加载顶点着色器的脚本内容
        mVertexShader = loadFromAssetsFile("multirender/vertex_tex.glsl", context.resources)
        //加载片元着色器的脚本内容
        mFragmentShader = loadFromAssetsFile("multirender/frag_tex.glsl", context.resources)
        //基于顶点着色器与片元着色器创建程序
        mProgram = createProgram(mVertexShader, mFragmentShader)
        //获取程序中顶点位置属性引用id
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition")
        //获取程序中顶点纹理坐标属性引用id
        maTexCoorHandle = GLES30.glGetAttribLocation(mProgram, "aTexCoor")
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix")
    }

    fun drawSelf(texId: Int) {
        //指定使用某套shader程序
        GLES30.glUseProgram(mProgram)
        pushMatrix() //保护现场
        //将最终变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, finalMatrix, 0)
        //将顶点位置数据送入渲染管线
        GLES30.glVertexAttribPointer(
            maPositionHandle,
            3,
            GLES30.GL_FLOAT,
            false,
            3 * 4,
            mVertexBuffer
        )
        //将顶点纹理数据送入渲染管线
        GLES30.glVertexAttribPointer(
            maTexCoorHandle,
            2,
            GLES30.GL_FLOAT,
            false,
            2 * 4,
            mTexCoorBuffer
        )
        GLES30.glEnableVertexAttribArray(maPositionHandle) //允许顶点位置数据数组
        GLES30.glEnableVertexAttribArray(maTexCoorHandle) //允许顶点纹理数据数组
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0) //激活纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId) //绑定纹理
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount) //执行绘制
        popMatrix() //恢复现场
    }

    init {
        //调用初始化顶点数据的方法
        initVertexData(ratio)
        //调用初始化着色器 的方法
        initShader(context)
    }
}