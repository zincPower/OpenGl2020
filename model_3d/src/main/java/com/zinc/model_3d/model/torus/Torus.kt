package com.zinc.model_3d.model.torus

import android.content.Context
import com.zinc.base.utils.allocateFloatBuffer
import com.zinc.model_3d.model.ControlModel3DInfo
import com.zinc.model_3d.model.Model3DBaseModel
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

/**
 * @author: Jiang Pengyong
 * @date: 2020/7/29 1:00 PM
 * @email: 56002982@qq.com
 * @des: 圆环
 */
class Torus(
    context: Context,             // 上下文
    private val rBig: Float,      // 圆环的半径
    private val rSmall: Float,    // 圆环条的半径
    private val nBig: Int,        // 圆环的份数
    private val nSmall: Int,      // 圆环条的份数
    private val textureId: Int,   // 纹理id
    controlModel3DInfo: ControlModel3DInfo
) : Model3DBaseModel(context, controlModel3DInfo) {

    init {
        initVertexData()
        initShader(context)
    }

    override fun initVertexData() {

        vCount = 3 * nBig * nSmall * 2

        // 小圆周每份的角度跨度
        val smallSpan: Float = CIRCLE_ANGLE / nSmall

        // 大圆周每份的角度跨度
        val bigSpan: Float = CIRCLE_ANGLE / nBig

        // 用于旋转的小圆半径
        val r: Float = (rBig - rSmall) / 2

        // 旋转轨迹形成的大圆周半径
        val R: Float = rSmall + r

        // 原始顶点列表（未卷绕）
        val alVertex = ArrayList<Float>()
        // 用于组织三角形面的顶点编号列表
        val alFaceIndex = ArrayList<Int>()

        var curSmallAngle = 0.0
        while (ceil(curSmallAngle) < 360 + smallSpan) {
            // 当前小圆弧度
            val a = Math.toRadians(curSmallAngle)
            var curBigAngle = 0.0

            while (ceil(curBigAngle) < 360 + bigSpan) {
                // 当前大圆弧度
                val u = Math.toRadians(curBigAngle)

                // 当前顶点 x,y,z
                val x = ((R + r * sin(a)) * sin(u)).toFloat()
                val y = (r * cos(a)).toFloat()
                val z = ((R + r * sin(a)) * cos(u)).toFloat()

                // 将计算出来的X、Y、Z坐标放入原始顶点列表
                alVertex.add(x)
                alVertex.add(y)
                alVertex.add(z)

                curBigAngle += bigSpan
            }
            curSmallAngle += smallSpan
        }

        // 按照卷绕成三角形的需要
        for (i in 0 until nSmall) {
            // 生成顶点编号列表
            for (j in 0 until nBig) {
                // 当前四边形第一顶点编号
                val index: Int = i * (nBig + 1) + j

                // 第一个三角形三个顶点的编号入列表
                alFaceIndex.add(index + 1)
                alFaceIndex.add(index + nBig + 1)
                alFaceIndex.add(index + nBig + 2)

                // 第二个三角形三个顶点的编号入列表
                alFaceIndex.add(index + 1)
                alFaceIndex.add(index)
                alFaceIndex.add(index + nBig + 1)
            }
        }
        // 存放按照卷绕顺序顶点坐标值的数组
        val vertices = FloatArray(vCount * 3)

        // 生成卷绕后的顶点坐标数组值
        cullVertex(alVertex, alFaceIndex, vertices)

        // 原纹理坐标列表（未卷绕）
        val alST = ArrayList<Float>()

        var angdegCol = 0f
        while (ceil(angdegCol.toDouble()) < 360 + smallSpan) {
            //对小圆按照等角度间距循环
            val t = angdegCol / 360 //当前角度对应的t坐标
            var angdegRow = 0f
            while (Math.ceil(angdegRow.toDouble()) < 360 + bigSpan) {
                //对大圆按照等角度间距循环
                val s = angdegRow / 360 //当前角度对应的s坐标
                alST.add(s)
                alST.add(t) //存入原始纹理坐标列表
                angdegRow += bigSpan
            }
            angdegCol += smallSpan
        }

        val textures: FloatArray = cullTexCoor(alST, alFaceIndex) //生成卷绕后纹理坐标数组值

        mVertexBuffer = allocateFloatBuffer(vertices)
        mTexCoorBuffer = allocateFloatBuffer(textures)
        mNormalBuffer = allocateFloatBuffer(vertices)
    }

    /**
     * 根据顶点编号生成卷绕后顶点坐标数组的方法
     *
     * @param alv 原始顶点列表
     * @param alFaceIndex 用于组织三角形面的顶点编号列表
     * @param vertices 存放卷绕后顶点坐标值的数组
     */
    private fun cullVertex(
        alv: ArrayList<Float>,
        alFaceIndex: ArrayList<Int>,
        vertices: FloatArray
    ) {
        var vCount = 0
        // 对顶点编号列表进行循环
        for (i in alFaceIndex) {
            vertices[vCount++] = alv[3 * i]
            vertices[vCount++] = alv[3 * i + 1]
            vertices[vCount++] = alv[3 * i + 2]
        }
    }

    /**
     * 根据顶点编号生成卷绕后顶点纹理坐标数组的方法
     *
     * @param alST 原始纹理坐标列表
     * @param  alTexIndex 用于组织三角形面的顶点编号列表
     */
    private fun cullTexCoor(
        alST: ArrayList<Float>,
        alTexIndex: ArrayList<Int>
    ): FloatArray {
        // 结果纹理坐标数组
        val textures = FloatArray(alTexIndex.size * 2)

        var stCount = 0
        for (i in alTexIndex) {
            textures[stCount++] = alST[2 * i]
            textures[stCount++] = alST[2 * i + 1]
        }

        return textures
    }

    override fun draw(textureId: Int) {
        super.draw(this.textureId)
    }

}