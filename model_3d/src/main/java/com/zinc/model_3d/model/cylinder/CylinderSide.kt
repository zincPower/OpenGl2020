package com.zinc.model_3d.model.cylinder

import android.content.Context
import android.opengl.GLES30
import com.zinc.base.utils.allocatFloatBuffer
import com.zinc.model_3d.model.ControlModel3DInfo
import com.zinc.model_3d.model.DrawType
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

/**
 * @author: Jiang Pengyong
 * @date: 2020/7/23 11:57 AM
 * @email: 56002982@qq.com
 * @des: 圆柱侧面
 */
class CylinderSide(
    context: Context,
    private val height: Float,
    private val r: Float,
    private val n: Int,
    controlModel3DInfo: ControlModel3DInfo
) : Model3DBaseModel(context, controlModel3DInfo) {

    companion object {
        const val CIRCLE_ANGLE = 360.0f
    }

    init {
        initVertexData()
        initShader(context)
    }

    override fun getDrawType(): Int {
        return when (controlModel3DInfo.drawType) {
            DrawType.LINE -> GLES30.GL_LINE_STRIP
            DrawType.COLOR -> GLES30.GL_TRIANGLES
            DrawType.POINT -> GLES30.GL_POINTS
            DrawType.TEXTURE -> GLES30.GL_TRIANGLES
        }
    }

    override fun initVertexData() {
        val span: Float = CIRCLE_ANGLE / n

        // 顶点个数，共有3*n*4个三角形，每个三角形都有三个顶点
        vCount = 3 * n * 4

        val vertices = FloatArray(vCount * 3)
        val textures = FloatArray(vCount * 2)

        var count = 0
        var stCount = 0
        var curAngle = 0.0

        while (ceil(curAngle) < 360) {
            //生成每对三角形的顶点数据
            val radian = Math.toRadians(curAngle) //当前弧度
            val radianNext = Math.toRadians(curAngle + span.toDouble()) //下一弧度
            //底圆当前点---0
            vertices[count++] = (-r * sin(radian)).toFloat() //第一个三角形顶点1
            vertices[count++] = 0f
            vertices[count++] = (-r * cos(radian)).toFloat()

            textures[stCount++] = (radian / (2 * Math.PI)).toFloat() //纹理坐标
            textures[stCount++] = 1f

            //顶圆下一点---3
            vertices[count++] = (-r * sin(radianNext)).toFloat() //第一个三角形顶点2
            vertices[count++] = height
            vertices[count++] = (-r * cos(radianNext)).toFloat()
            textures[stCount++] = (radianNext / (2 * Math.PI)).toFloat() //纹理坐标
            textures[stCount++] = 0f

            //顶圆当前点---2
            vertices[count++] = (-r * sin(radian)).toFloat() //第一个三角形顶点3
            vertices[count++] = height
            vertices[count++] = (-r * cos(radian)).toFloat()
            textures[stCount++] = (radian / (2 * Math.PI)).toFloat() //纹理坐标
            textures[stCount++] = 0f

            //底圆当前点---0
            vertices[count++] = (-r * sin(radian)).toFloat() //第二个三角形顶点1
            vertices[count++] = 0f
            vertices[count++] = (-r * cos(radian)).toFloat()
            textures[stCount++] = (radian / (2 * Math.PI)).toFloat() //纹理坐标
            textures[stCount++] = 1f
            //底圆下一点---1
            vertices[count++] = (-r * sin(radianNext)).toFloat() //第二个三角形顶点2
            vertices[count++] = 0f
            vertices[count++] = (-r * cos(radianNext)).toFloat()
            textures[stCount++] = (radianNext / (2 * Math.PI)).toFloat() //纹理坐标
            textures[stCount++] = 1f
            //顶圆下一点---3
            vertices[count++] = (-r * sin(radianNext)).toFloat() //第二个三角形顶点3
            vertices[count++] = height
            vertices[count++] = (-r * cos(radianNext)).toFloat()
            textures[stCount++] = (radianNext / (2 * Math.PI)).toFloat() //纹理坐标
            textures[stCount++] = 0f

            curAngle += span
        }

        //法向量数据初始化
        val normals = FloatArray(vertices.size)
        for (i in vertices.indices) {
            if (i % 3 == 1) {
                normals[i] = 0f //法向量Y坐标分量为0
            } else {
                normals[i] = vertices[i] //法向量数组赋值
            }
        }

        // 顶点初始化
        mVertexBuffer = allocatFloatBuffer(vertices)
        // 法向量初始化
        mNormalBuffer = allocatFloatBuffer(normals)
        // st坐标数据初始化
        mTexCoorBuffer = allocatFloatBuffer(textures)

    }

}