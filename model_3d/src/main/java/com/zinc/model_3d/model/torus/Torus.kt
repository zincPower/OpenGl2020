package com.zinc.model_3d.model.torus

import android.content.Context
import com.zinc.base.utils.allocatFloatBuffer
import com.zinc.model_3d.model.ControlModel3DInfo
import com.zinc.model_3d.model.Model3DBaseModel
import kotlin.math.ceil

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


    /**
     *                    ^ y
     *                    |
     *                    |       /
     *                    |     /  D（圆环大半径）
     *                    |
     *                    | /   u（角度）
     *  ------------------|--------------> x
     *                    |
     *                    |
     *                    |
     *                    |
     *                    |
     *                    |
     */
    override fun initVertexData() {

        vCount = 3 * nBig * nSmall * 2

        // 小圆周每份的角度跨度
        val smallSpan: Float = CIRCLE_ANGLE / nSmall

        // 大圆周每份的角度跨度
        val bigSpan: Float = CIRCLE_ANGLE / nBig

        // 用于旋转的小圆半径
        val A: Float = (rBig - rSmall) / 2

        // 旋转轨迹形成的大圆周半径
        val D: Float = rSmall + A

        // 原始顶点列表（未卷绕）
        val alVertix = ArrayList<Float>()
        // 用于组织三角形面的顶点编号列表
        val alFaceIndex = ArrayList<Int>()

        var curSmallAngle = 0.0
        while (ceil(curSmallAngle) < 360 + smallSpan) {
            // 对小圆按照等角度间距循环
            // 当前小圆弧度
            val a = Math.toRadians(curSmallAngle)
            var curBigAngle = 0f
            while (Math.ceil(curBigAngle.toDouble()) < 360 + bigSpan) {
                // 对大圆按照等角度间距循环
                val u = Math.toRadians(curBigAngle.toDouble()) //当前大圆弧度
                val y = (A * Math.cos(a)).toFloat() //按照公式计算当前顶点
                val x =
                    ((D + A * Math.sin(a)) * Math.sin(u)).toFloat() //的X、Y、Z坐标
                val z =
                    ((D + A * Math.sin(a)) * Math.cos(u)).toFloat()
                //将计算出来的X、Y、Z坐标放入原始顶点列表
                alVertix.add(x)
                alVertix.add(y)
                alVertix.add(z)
                curBigAngle += bigSpan
            }
            curSmallAngle += smallSpan
        }

        for (i in 0 until nSmall) { //按照卷绕成三角形的需要
            for (j in 0 until nBig) { //生成顶点编号列表
                val index: Int = i * (nBig + 1) + j //当前四边形第一顶点编号
                alFaceIndex.add(index + 1) //第一个三角形三个顶点的编号入列表
                alFaceIndex.add(index + nBig + 1)
                alFaceIndex.add(index + nBig + 2)
                alFaceIndex.add(index + 1) //第二个三角形三个顶点的编号入列表
                alFaceIndex.add(index)
                alFaceIndex.add(index + nBig + 1)
            }
        }
        val vertices = FloatArray(vCount * 3) //存放按照卷绕顺序顶点坐标值的数组


        cullVertex(alVertix, alFaceIndex, vertices) //生成卷绕后的顶点坐标数组值

        val alST: ArrayList<Float> = ArrayList<Float>() //原纹理坐标列表（未卷绕）


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

        mVertexBuffer = allocatFloatBuffer(vertices)
        mTexCoorBuffer = allocatFloatBuffer(textures)
        mNormalBuffer = allocatFloatBuffer(vertices)
    }

    // 根据顶点编号生成卷绕后顶点坐标数组的方法
    private fun cullVertex(
        alv: ArrayList<Float>,  //原始顶点列表
        alFaceIndex: ArrayList<Int>,  //用于组织三角形面的顶点编号列表
        vertices: FloatArray //存放卷绕后顶点坐标值的数组
    ) {
        var vCount = 0 //顶点计数器
        for (i in alFaceIndex) { //对顶点编号列表进行循环
            vertices[vCount++] = alv[3 * i] //将当前编号顶点的X坐标值存入最终数组
            vertices[vCount++] = alv[3 * i + 1] //将当前编号顶点的Y坐标值存入最终数组
            vertices[vCount++] = alv[3 * i + 2] //将当前编号顶点的Z坐标值存入最终数组
        }
    }

    private fun cullTexCoor( //根据顶点编号生成卷绕后顶点纹理坐标数组的方法
        alST: ArrayList<Float>,  //原始纹理坐标列表
        alTexIndex: ArrayList<Int> //用于组织三角形面的顶点编号列表
    ): FloatArray {
        val textures = FloatArray(alTexIndex.size * 2) //结果纹理坐标数组
        var stCount = 0 //纹理坐标计数器
        for (i in alTexIndex) { //对顶点编号列表进行循环
            textures[stCount++] = alST[2 * i] //将当前编号顶点的S坐标值存入最终数组
            textures[stCount++] = alST[2 * i + 1] //将当前编号顶点的T坐标值存入最终数组
        }
        return textures //返回结果纹理坐标数组
    }

    override fun draw(textureId: Int) {
        super.draw(this.textureId)
    }

}