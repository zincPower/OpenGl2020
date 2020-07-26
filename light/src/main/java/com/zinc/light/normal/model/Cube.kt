package com.zinc.light.normal.model

import android.content.Context
import android.opengl.GLES30
import com.zinc.base.IModel
import com.zinc.base.utils.MatrixState
import com.zinc.base.utils.OpenGlUtils
import com.zinc.base.utils.allocatFloatBuffer
import java.nio.FloatBuffer

/**
 * @author: Jiang Pengyong
 * @date: 2020/7/20 2:11 PM
 * @email: 56002982@qq.com
 * @des: 立方体
 */
class Cube(context: Context, private val mControlCubeInfo: ControlCubeInfo) : IModel {

    companion object {
        // 尺寸
        const val UNIT_SIZE = 1

        // 半径
        const val LENGTH = 0.8f
    }

    // 自定义渲染管线着色器程序id
    private var mProgram = 0

    // 总变换矩阵引用
    private var muMVPMatrixHandle = 0

    //位置、旋转变换矩阵引用
    private var muMMatrixHandle = 0

    // 立方体的半径属性引用
    private var muLengthHandle = 0

    // 顶点位置属性引用
    private var maPositionHandle = 0

    //顶点法向量属性引用
    private var maNormalHandle = 0

    //光源位置属性引用
    private var muLightDirectionHandle = 0

    //摄像机位置属性引用
    private var maCameraHandle = 0

    // 粗糙度
    private var muRoughnessHandle = 0

    // 顶点坐标数据缓冲
    private var mVertexBuffer: FloatBuffer? = null

    //顶点法向量数据缓冲(面法向量)
    private var mFaceNormalBuffer: FloatBuffer? = null

    //顶点法向量数据缓冲(点法向量)
    private var mPointNormalBuffer: FloatBuffer? = null

    // 顶点数
    private var vCount = 0

    // 边长
    private var lengthHalf = 0f

    init {
        // 调用初始化顶点数据的方法
        initVertexData()
        // 调用初始化着色器的方法
        initShader(context)
    }

    /**
     * 初始化顶点数据的方法
     */
    override fun initVertexData() {
        //顶点坐标数据的初始化================begin============================
        lengthHalf = LENGTH * UNIT_SIZE
        // 顶点数，一个面由六个点确定，共六个面
        vCount = 6 * 6
        // 立方体前面
        val vertices = floatArrayOf(
            /**
             * 正面 向量向外
             *        +y
             *  1-----------0
             *  │          │
             *  │          │
             *  │          │ +x
             *  │          │
             *  │          │
             *  2-----------3
             */
            lengthHalf, lengthHalf, lengthHalf,  //0
            -lengthHalf, lengthHalf, lengthHalf,  //1
            -lengthHalf, -lengthHalf, lengthHalf,  //2
            lengthHalf, lengthHalf, lengthHalf,  //0
            -lengthHalf, -lengthHalf, lengthHalf,  //2
            lengthHalf, -lengthHalf, lengthHalf,  //3
            /**
             * 后面 向量向里
             *        +y
             *  3-----------0
             *  │          │
             *  │          │
             *  │          │ +x
             *  │          │
             *  │          │
             *  2-----------1
             */
            lengthHalf, lengthHalf, -lengthHalf,  //0
            lengthHalf, -lengthHalf, -lengthHalf,  //1
            -lengthHalf, -lengthHalf, -lengthHalf,  //2
            lengthHalf, lengthHalf, -lengthHalf,  //0
            -lengthHalf, -lengthHalf, -lengthHalf,  //2
            -lengthHalf, lengthHalf, -lengthHalf,  //3
            /**
             *  左面 向量向外
             *        +y
             *  1-----------0
             *  │          │
             *  │          │
             *  │          │ +z
             *  │          │
             *  │          │
             *  2-----------3
             */
            -lengthHalf, lengthHalf, lengthHalf,  //0
            -lengthHalf, lengthHalf, -lengthHalf,  //1
            -lengthHalf, -lengthHalf, -lengthHalf,  //2
            -lengthHalf, lengthHalf, lengthHalf,  //0
            -lengthHalf, -lengthHalf, -lengthHalf,  //2
            -lengthHalf, -lengthHalf, lengthHalf,  //3
            /**
             * 右面 向量向里
             *        +y
             *  3-----------0
             *  │          │
             *  │          │
             *  │          │ +z
             *  │          │
             *  │          │
             *  2-----------1
             */
            lengthHalf, lengthHalf, lengthHalf,  //0
            lengthHalf, -lengthHalf, lengthHalf,  //1
            lengthHalf, -lengthHalf, -lengthHalf,  //2
            lengthHalf, lengthHalf, lengthHalf,  //0
            lengthHalf, -lengthHalf, -lengthHalf,  //2
            lengthHalf, lengthHalf, -lengthHalf,  //3
            /**
             * 上面 向量向里
             *        +z
             *  3-----------0
             *  │          │
             *  │          │
             *  │          │ +x
             *  │          │
             *  │          │
             *  2-----------1
             */
            lengthHalf, lengthHalf, lengthHalf,  //0
            lengthHalf, lengthHalf, -lengthHalf,  //1
            -lengthHalf, lengthHalf, -lengthHalf,  //2
            lengthHalf, lengthHalf, lengthHalf,  //0
            -lengthHalf, lengthHalf, -lengthHalf,  //2
            -lengthHalf, lengthHalf, lengthHalf,  //3
            /**
             *  下面 向量向外
             *        +z
             *  1-----------0
             *  │          │
             *  │          │
             *  │          │ +x
             *  │          │
             *  │          │
             *  2-----------3
             */
            lengthHalf, -lengthHalf, lengthHalf,  //0
            -lengthHalf, -lengthHalf, lengthHalf,  //1
            -lengthHalf, -lengthHalf, -lengthHalf,  //2
            lengthHalf, -lengthHalf, lengthHalf,  //0
            -lengthHalf, -lengthHalf, -lengthHalf,  //2
            lengthHalf, -lengthHalf, -lengthHalf
        )

        mVertexBuffer = allocatFloatBuffer(vertices)

        val normals = floatArrayOf(
            // 正面
            0f, 0f, 1f,
            0f, 0f, 1f,
            0f, 0f, 1f,
            0f, 0f, 1f,
            0f, 0f, 1f,
            0f, 0f, 1f,

            // 后面
            0f, 0f, -1f,
            0f, 0f, -1f,
            0f, 0f, -1f,
            0f, 0f, -1f,
            0f, 0f, -1f,
            0f, 0f, -1f,

            // 左面
            -1f, 0f, 0f,
            -1f, 0f, 0f,
            -1f, 0f, 0f,
            -1f, 0f, 0f,
            -1f, 0f, 0f,
            -1f, 0f, 0f,

            // 右面
            1f, 0f, 0f,
            1f, 0f, 0f,
            1f, 0f, 0f,
            1f, 0f, 0f,
            1f, 0f, 0f,
            1f, 0f, 0f,

            // 上面
            0f, 1f, 0f,
            0f, 1f, 0f,
            0f, 1f, 0f,
            0f, 1f, 0f,
            0f, 1f, 0f,
            0f, 1f, 0f,

            // 下面
            0f, -1f, 0f,
            0f, -1f, 0f,
            0f, -1f, 0f,
            0f, -1f, 0f,
            0f, -1f, 0f,
            0f, -1f, 0f
        )
        //创建绘制顶点法向量缓冲
        mFaceNormalBuffer = allocatFloatBuffer(normals)
        mPointNormalBuffer = allocatFloatBuffer(vertices)
    }

    /**
     * 初始化着色器
     */
    override fun initShader(context: Context) {
        // 加载顶点着色器的脚本内容
        val vertex = OpenGlUtils.loadFromAssetsFile("normal/vertex.glsl", context.resources)
        // 加载片元着色器的脚本内容
        val fragment = OpenGlUtils.loadFromAssetsFile("normal/fragment.glsl", context.resources)
        // 基于顶点着色器与片元着色器创建程序
        mProgram = OpenGlUtils.createProgram(vertex, fragment)
        // 获取程序中顶点位置属性引用
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition")
        // 获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix")
        //获取位置、旋转变换矩阵引用
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix")
        // 获取程序中立方体半径引用
        muLengthHandle = GLES30.glGetUniformLocation(mProgram, "uLength")
        //获取程序中顶点法向量属性引用
        maNormalHandle = GLES30.glGetAttribLocation(mProgram, "aNormal")
        //获取程序中光源位置引用
        muLightDirectionHandle = GLES30.glGetUniformLocation(mProgram, "uLightDirection")
        //获取程序中摄像机位置引用
        maCameraHandle = GLES30.glGetUniformLocation(mProgram, "uCamera")
        // 粗糙度
        muRoughnessHandle = GLES30.glGetUniformLocation(mProgram, "roughness")
    }

    /**
     * 绘制
     */
    override fun draw(textureId: Int) {
        //指定使用某套着色器程序
        GLES30.glUseProgram(mProgram)
        // 将最终变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(
            muMVPMatrixHandle,
            1,
            false,
            MatrixState.getFinalMatrix(),
            0
        )
        //将位置、旋转变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.mMatrix, 0)
        // 将半径尺寸传入渲染管线
        GLES30.glUniform1f(muLengthHandle, lengthHalf * 2)
        //将光源位置传入渲染管线
        GLES30.glUniform3fv(muLightDirectionHandle, 1, MatrixState.lightPositionFB)
        //将摄像机位置传入渲染管线
        GLES30.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB)
        // 粗糙度
        GLES30.glUniform1i(muRoughnessHandle, mControlCubeInfo.roughness)

        // 将顶点位置数据传入渲染管线
        GLES30.glVertexAttribPointer(
            maPositionHandle, 3, GLES30.GL_FLOAT,
            false, 3 * 4, mVertexBuffer
        )

        //将顶点法向量数据传入渲染管线
        if (mControlCubeInfo.isFaceNormal) {
            GLES30.glVertexAttribPointer(
                maNormalHandle,
                3,
                GLES30.GL_FLOAT,
                false,
                3 * 4,
                mFaceNormalBuffer
            )
        } else {
            GLES30.glVertexAttribPointer(
                maNormalHandle,
                3,
                GLES30.GL_FLOAT,
                false,
                3 * 4,
                mPointNormalBuffer
            )
        }

        // 启用顶点位置数据数组
        GLES30.glEnableVertexAttribArray(maPositionHandle)
        // 启用顶点法向量数据数组
        GLES30.glEnableVertexAttribArray(maNormalHandle)
        // 绘制立方体
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount)
    }

}