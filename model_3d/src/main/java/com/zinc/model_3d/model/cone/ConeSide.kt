package com.zinc.model_3d.model.cone

import android.content.Context
import com.zinc.base.utils.VectorUtil
import com.zinc.base.utils.allocateFloatBuffer
import com.zinc.model_3d.model.ControlModel3DInfo
import com.zinc.model_3d.model.Model3DBaseModel
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

/**
 * @author: Jiang Pengyong
 * @date: 2020/7/27 11:31 PM
 * @email: 56002982@qq.com
 * @des: 圆锥侧边
 */
class ConeSide(
    context: Context,
    private val height: Float,
    private val r: Float,
    private val n: Int,
    controlModel3DInfo: ControlModel3DInfo
) : Model3DBaseModel(context, controlModel3DInfo) {

    init {
        initVertexData()
        initShader(context)
    }

    override fun initVertexData() {
        val span = 360.0f / n
        vCount = 3 * n * 4

        // 顶点坐标数组
        val vertices = FloatArray(vCount * 3)
        // 顶点纹理坐标值数组
        val textures = FloatArray(vCount * 2)
        // 法向量数组
        val normals = FloatArray(vertices.size)

        // 顶点坐标计数器
        var count = 0
        // 纹理坐标计数器
        var stCount = 0
        // 法向量计数器
        var norCount = 0

        var curAngle = 0f
        while (ceil(curAngle.toDouble()) < 360) {
            //按照一定的角度跨度切分
            val radian = Math.toRadians(curAngle.toDouble())
            val radianNext = Math.toRadians(curAngle + span.toDouble())

            //圆锥面中心最高点坐标
            vertices[count++] = 0f
            vertices[count++] = height
            vertices[count++] = 0f
            //圆锥面中心最高点纹理坐标
            textures[stCount++] = 0.5f
            textures[stCount++] = 0f

            vertices[count++] = (r * sin(radian)).toFloat() //当前弧度对应点坐标
            vertices[count++] = 0f
            vertices[count++] = (r * cos(radian)).toFloat()
            textures[stCount++] = (radian / (2 * Math.PI)).toFloat() //当前弧度对应点纹理坐标
            textures[stCount++] = 1f

            vertices[count++] = (r * sin(radianNext)).toFloat() //下一弧度对应点坐标
            vertices[count++] = 0f
            vertices[count++] = (r * cos(radianNext)).toFloat()
            textures[stCount++] = (radianNext / (2 * Math.PI)).toFloat() //下一弧度对应点纹理坐标
            textures[stCount++] = 1f

            curAngle += span
        }

        var i = 0
        while (i < vertices.size) {
            //法向量数据的初始化
            //如果当前的顶点为圆锥的最高点
            if (vertices[i] == 0f && vertices[i + 1] == height && vertices[i + 2] == 0f) { //如果当前的顶点为圆锥的中心最高点
                normals[norCount++] = 0f
                normals[norCount++] = 1f
                normals[norCount++] = 0f
            } else { //当前的顶点为底面圆周上的点
                val norXYZ: FloatArray = VectorUtil.calConeNormal( //通过3个顶点的坐标求出法向量
                    0f, 0f, 0f,  //底面圆的中心点
                    vertices[i], vertices[i + 1], vertices[i + 2],  //当前底面圆周顶点坐标
                    0f, height, 0f
                ) //圆锥中心最高点坐标
                normals[norCount++] = norXYZ[0] //将法向量X、Y、Z 3个分量的值
                normals[norCount++] = norXYZ[1] //存入法向量数据数组
                normals[norCount++] = norXYZ[2]
            }
            i += 3
        }

        mVertexBuffer = allocateFloatBuffer(vertices)
        mNormalBuffer = allocateFloatBuffer(normals)
        mTexCoorBuffer = allocateFloatBuffer(textures)

    }

}