package com.zinc.model_3d.model.cylinder

import android.content.Context
import com.zinc.base.utils.allocateFloatBuffer
import com.zinc.model_3d.model.ControlModel3DInfo
import com.zinc.model_3d.model.Model3DBaseModel
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

    init {
        initVertexData()
        initShader(context)
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
            // 当前弧度
            val radian = Math.toRadians(curAngle)
            // 下一弧度
            val radianNext = Math.toRadians(curAngle + span.toDouble())

            /**
             * 纹理               (y = r*cos(α),)
             *                       P1              P3    (1, 0)
             * ---------------------------------------------> S
             * ｜                     ｜             ∕｜
             * ｜                     ｜            ∕ ｜
             * ｜                     ｜           ∕  ｜
             * ｜                     ｜          ∕   ｜
             * ｜                     ｜         ∕    ｜
             * ｜                     ｜        ∕     ｜
             * ｜                     ｜       ∕      ｜
             * ｜----------------------------—∕-----------------
             * ｜          (0.5, 0.5) ｜     ∕        ｜
             * ｜                     ｜ α  ∕         ｜
             * ｜                     ｜   ∕          ｜
             * ｜                     ｜  ∕           ｜
             * ｜                     ｜ ∕            ｜
             * ｜                     ｜∕             ｜
             * ｜----------------------------——-----------------
             * v  (0, 1)             P2              P4
             *
             * T
             */

            // ============================= 第一个三角形 ====================================
            // P1
            vertices[count++] = (r * sin(radian)).toFloat()
            vertices[count++] = height
            vertices[count++] = (r * cos(radian)).toFloat()
            textures[stCount++] = (curAngle / 360).toFloat()
            textures[stCount++] = 0f

            // P2
            vertices[count++] = (r * sin(radian)).toFloat()
            vertices[count++] = 0f
            vertices[count++] = (r * cos(radian)).toFloat()
            // 纹理坐标
            textures[stCount++] = (curAngle / 360).toFloat()
            textures[stCount++] = 1f

            // P3
            vertices[count++] = (r * sin(radianNext)).toFloat()
            vertices[count++] = height
            vertices[count++] = (r * cos(radianNext)).toFloat()
            textures[stCount++] = ((curAngle + span) / 360).toFloat()
            textures[stCount++] = 0f

            // ============================= 第二个三角形 ====================================
            // P2
            vertices[count++] = (r * sin(radian)).toFloat()
            vertices[count++] = 0f
            vertices[count++] = (r * cos(radian)).toFloat()
            textures[stCount++] = (curAngle / 360).toFloat()
            textures[stCount++] = 1f

            // P4
            vertices[count++] = (r * sin(radianNext)).toFloat()
            vertices[count++] = 0f
            vertices[count++] = (r * cos(radianNext)).toFloat()
            textures[stCount++] = ((curAngle + span) / 360).toFloat()
            textures[stCount++] = 1f

            // P3
            vertices[count++] = (r * sin(radianNext)).toFloat()
            vertices[count++] = height
            vertices[count++] = (r * cos(radianNext)).toFloat()
            textures[stCount++] = ((curAngle + span) / 360).toFloat()
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
        mVertexBuffer = allocateFloatBuffer(vertices)
        // 法向量初始化
        mNormalBuffer = allocateFloatBuffer(normals)
        // st坐标数据初始化
        mTexCoorBuffer = allocateFloatBuffer(textures)

    }

}