package com.zinc.light.light.model

import android.content.Context
import android.opengl.GLES30
import com.zinc.base.IModel
import com.zinc.base.utils.MatrixState
import com.zinc.base.utils.OpenGlUtils
import com.zinc.base.utils.allocatFloatBuffer
import java.lang.Math.toRadians
import java.nio.FloatBuffer
import kotlin.math.cos
import kotlin.math.sin

class Ball(context: Context, controlBallInfo: ControlBallInfo) : IModel {

    companion object {
        // 横向切分
        const val ANGLE_SPAN = 10.0

        // 尺寸
        const val UNIT_SIZE = 1

        // 半径
        const val R = 0.8f
    }

    private var vertexBuffer: FloatBuffer? = null
    private var normalBuffer: FloatBuffer? = null

    private val mControlBallInfo: ControlBallInfo = controlBallInfo

    private var vCount = 0

    // 自定义渲染管线着色器程序id
    private var mProgram = 0

    // 总变换矩阵引用
    private var muMVPMatrixHandle = 0

    // 位置、旋转变换矩阵引用
    private var muMMatrixHandle = 0

    // 顶点位置属性引用
    private var maPositionHandle = 0

    // 顶点法向量属性引用
    private var maNormalHandle = 0

    // 光源位置属性引用
    private var muLightLocationHandle = 0

    // 光源方向属性引用
    private var muLightDirectionHandle = 0

    // 光源控制
    private var muIsUseAmbientHandle = 0
    private var muIsUseDiffuseHandle = 0
    private var muIsUseSpecularHandle = 0
    private var muIsUsePositioningLight = 0

    // 粗糙度
    private var muRoughnessHandle = 0

    // 摄像机位置属性引用
    private var maCameraHandle = 0

    // 是否使用
    private var muIsCalculateByFragHandle = 0


    // ======================================片元着色器 start======================================

    // 位置、旋转变换矩阵引用
    private var muMMatrixFragHandle = 0

    // 光源位置属性引用
    private var muLightLocationFragHandle = 0

    // 光源方向属性引用
    private var muLightDirectionFragHandle = 0

    // 摄像机位置属性引用
    private var maCameraFragHandle = 0

    private var muIsUsePositioningFragLight = 0

    // 粗糙度
    private var muRoughnessFragHandle = 0

    // ======================================片元着色器 end========================================

    init {
        initVertexData()
        initShader(context)
    }

    override fun initVertexData() {
        // 顶点坐标数据的初始化================begin============================
        val vertex = ArrayList<Float>() // 存放顶点坐标的ArrayList

        var vAngle = -90
        val r = R * UNIT_SIZE

        while (vAngle < 90) {   // 从 -90 到 90
            var hAngle = 0
            while (hAngle <= 360) {     // 从 0 到 360
                // 纵向横向各到一个角度后计算对应的此点在球面上的坐标
                // 将角度转换为弧度
                val x0 = r * cos(toRadians(vAngle.toDouble())) * cos(toRadians(hAngle.toDouble()))
                val y0 = r * cos(toRadians(vAngle.toDouble())) * sin(toRadians(hAngle.toDouble()))
                val z0 = r * sin(toRadians(vAngle.toDouble()))

                val x1 = r * cos(toRadians(vAngle.toDouble())) * cos(toRadians(hAngle + ANGLE_SPAN))
                val y1 = r * cos(toRadians(vAngle.toDouble())) * sin(toRadians(hAngle + ANGLE_SPAN))
                val z1 = r * sin(toRadians(vAngle.toDouble()))

                val x2 =
                    r * cos(toRadians(vAngle + ANGLE_SPAN)) * cos(toRadians(hAngle + ANGLE_SPAN))
                val y2 =
                    r * cos(toRadians(vAngle + ANGLE_SPAN)) * sin(toRadians(hAngle + ANGLE_SPAN))
                val z2 = r * sin(toRadians(vAngle + ANGLE_SPAN))

                val x3 = r * cos(toRadians(vAngle + ANGLE_SPAN)) * cos(toRadians(hAngle.toDouble()))
                val y3 = r * cos(toRadians(vAngle + ANGLE_SPAN)) * sin(toRadians(hAngle.toDouble()))
                val z3 = r * sin(toRadians(vAngle + ANGLE_SPAN))

                // 将计算出来的XYZ坐标加入存放顶点坐标的ArrayList
                vertex.add(x1.toFloat())
                vertex.add(y1.toFloat())
                vertex.add(z1.toFloat())
                vertex.add(x3.toFloat())
                vertex.add(y3.toFloat())
                vertex.add(z3.toFloat())
                vertex.add(x0.toFloat())
                vertex.add(y0.toFloat())
                vertex.add(z0.toFloat())
                vertex.add(x1.toFloat())
                vertex.add(y1.toFloat())
                vertex.add(z1.toFloat())
                vertex.add(x2.toFloat())
                vertex.add(y2.toFloat())
                vertex.add(z2.toFloat())
                vertex.add(x3.toFloat())
                vertex.add(y3.toFloat())
                vertex.add(z3.toFloat())
                hAngle = (hAngle + ANGLE_SPAN).toInt()
            }
            vAngle = (vAngle + ANGLE_SPAN).toInt()
        }

        vCount = vertex.size / 3

        val vertices = FloatArray(vertex.size)
        vertex.forEachIndexed { index, vertical ->
            vertices[index] = vertical
        }

        // 创建顶点坐标数据缓冲
        vertexBuffer = allocatFloatBuffer(vertices)
        // 创建法向量数据缓冲
        normalBuffer = allocatFloatBuffer(vertices)
    }

    /**
     * 初始化着色器
     */
    override fun initShader(context: Context) {
        val vertex = OpenGlUtils.loadFromAssetsFile("light/vertex.glsl", context.resources)
        val fragment = OpenGlUtils.loadFromAssetsFile("light/fragment.glsl", context.resources)

        mProgram = OpenGlUtils.createProgram(vertex, fragment)
        // 获取程序中顶点位置属性引用
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition")
        // 获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix")
        // 获取位置、旋转变换矩阵引用
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix")
        // 获取程序中顶点法向量属性引用
        maNormalHandle = GLES30.glGetAttribLocation(mProgram, "aNormal")
        // 获取程序中光源位置引用
        muLightLocationHandle = GLES30.glGetUniformLocation(mProgram, "uLightLocation")
        // 获取程序中摄像机位置一致变量引用
        maCameraHandle = GLES30.glGetUniformLocation(mProgram, "uCamera")

        //获取程序中光源方向引用
        muLightDirectionHandle = GLES30.glGetUniformLocation(mProgram, "uLightDirection")

        // 粗糙度
        muRoughnessHandle = GLES30.glGetUniformLocation(mProgram, "roughness")

        muIsCalculateByFragHandle = GLES30.glGetUniformLocation(mProgram, "isCalculateByFrag")

        // 获取程序中光源控制
        muIsUseAmbientHandle = GLES30.glGetUniformLocation(mProgram, "uIsUseAmbient")
        muIsUseDiffuseHandle = GLES30.glGetUniformLocation(mProgram, "uIsUseDiffuse")
        muIsUseSpecularHandle = GLES30.glGetUniformLocation(mProgram, "uIsUseSpecular")
        muIsUsePositioningLight = GLES30.glGetUniformLocation(mProgram, "uIsUsePositioningLight")

        // 位置、旋转变换矩阵引用
        muMMatrixFragHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrixFrag")

        // 光源位置属性引用
        muLightLocationFragHandle = GLES30.glGetUniformLocation(mProgram, "uLightLocationFrag")

        // 光源方向属性引用
        muLightDirectionFragHandle = GLES30.glGetUniformLocation(mProgram, "uLightDirectionFrag")

        // 摄像机位置属性引用
        maCameraFragHandle = GLES30.glGetUniformLocation(mProgram, "uCameraFrag")

        muIsUsePositioningFragLight =
            GLES30.glGetUniformLocation(mProgram, "uIsUsePositioningLightFrag")

        // 粗糙度
        muRoughnessFragHandle = GLES30.glGetUniformLocation(mProgram, "roughnessFrag")
    }

    /**
     * 绘制
     */
    override fun draw(textureId: Int) {
        // 指定使用某套着色器程序
        GLES30.glUseProgram(mProgram)
        // 将最终变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0)
        // 将位置、旋转变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.mMatrix, 0)
        // 将光源位置传入渲染管线
        GLES30.glUniform3fv(muLightLocationHandle, 1, MatrixState.lightPositionFB)
        // 将光源方向传入渲染管线
        GLES30.glUniform3fv(muLightDirectionHandle, 1, MatrixState.lightPositionFB)
        // 将摄像机位置传入渲染管线
        GLES30.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB)
        // 是否使用环境光
        GLES30.glUniform1i(muIsUseAmbientHandle, mControlBallInfo.isUseAmbient)
        // 是否使用散射光
        GLES30.glUniform1i(muIsUseDiffuseHandle, mControlBallInfo.isUseDiffuse)
        // 是否使用镜面光
        GLES30.glUniform1i(muIsUseSpecularHandle, mControlBallInfo.isUseSpecular)
        // 是否使用定向光
        GLES30.glUniform1i(muIsUsePositioningLight, mControlBallInfo.isUsePositioningLight)
        // 粗糙度
        GLES30.glUniform1i(muRoughnessHandle, mControlBallInfo.roughness)
        // 是否使用片元计算
        GLES30.glUniform1i(muIsCalculateByFragHandle, mControlBallInfo.isCalByFrag)


        // 位置、旋转变换矩阵
        GLES30.glUniformMatrix4fv(muMMatrixFragHandle, 1, false, MatrixState.mMatrix, 0)
        // 光源位置属性
        GLES30.glUniform3fv(muLightLocationFragHandle, 1, MatrixState.lightPositionFB)
        // 光源方向属性
        GLES30.glUniform3fv(muLightDirectionFragHandle, 1, MatrixState.lightPositionFB)
        // 摄像机位置属性引用
        GLES30.glUniform3fv(maCameraFragHandle, 1, MatrixState.cameraFB)
        GLES30.glUniform1i(muIsUsePositioningFragLight, mControlBallInfo.isUsePositioningLight)
        // 粗糙度
        GLES30.glUniform1i(muRoughnessFragHandle, mControlBallInfo.roughness)


        // 将顶点位置数据传入渲染管线
        GLES30.glVertexAttribPointer(
            maPositionHandle,
            3,
            GLES30.GL_FLOAT,
            false,
            3 * 4,
            vertexBuffer
        )

        // 将顶点法向量数据传入渲染管线
        GLES30.glVertexAttribPointer(
            maNormalHandle,
            3,
            GLES30.GL_FLOAT,
            false,
            3 * 4,
            normalBuffer
        )

        // 启用顶点位置数据数组
        GLES30.glEnableVertexAttribArray(maPositionHandle)
        // 启用顶点法向量数据数组
        GLES30.glEnableVertexAttribArray(maNormalHandle)
        // 绘制球
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount)
    }

}