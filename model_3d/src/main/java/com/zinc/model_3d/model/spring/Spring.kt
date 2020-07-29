package com.zinc.model_3d.model.spring

import android.content.Context
import android.opengl.GLES30
import com.zinc.base.utils.allocatFloatBuffer
import com.zinc.model_3d.model.ControlModel3DInfo
import com.zinc.model_3d.model.DrawType
import com.zinc.model_3d.model.Model3DBaseModel
import kotlin.math.ceil

class Spring(
    context: Context,             // 上下文
    private val height: Float,    // 螺旋管高度
    private val circleNum: Float, // 螺旋管圈数
    private val rBig: Float,      // 圆环的半径
    private val rSmall: Float,    // 圆环条的半径
    private val nBig: Int,        // 圆环的份数
    private val nSmall: Int,      // 圆环条的份数
    private val textureId: Int,   // 纹理id
    controlModel3DInfo: ControlModel3DInfo
) : Model3DBaseModel(context, controlModel3DInfo) {

    init {
        initShader(context)
        initVertexData()
    }

    override fun initVertexData() {

        vCount = 3 * nBig * nSmall * 2 //顶点个数

        // 大圆周总度数
        val angdegTotal: Float = circleNum * CIRCLE_ANGLE

        // 小圆周每份的角度跨度
        val angdegColSpan: Float = 360.0f / nSmall

        // 大圆周每份的角度跨度
        val angdegRowSpan: Float = angdegTotal / nBig

        val A = (rBig - rSmall) / 2 //用于旋转的小圆半径

        val D = rSmall + A //旋转轨迹形成的大圆周半径

        val alVertix = ArrayList<Float>() //原始顶点列表（未卷绕）

        val alFaceIndex: ArrayList<Int> = ArrayList<Int>() //用于组织三角形面的顶点编号列表

        var curSmallAngle = 0.0
        while (ceil(curSmallAngle) < 360 + angdegColSpan) {
            // 对小圆按照等角度间距循环
            // 当前小圆周弧度
            val a = Math.toRadians(curSmallAngle)
            var curBigAngle = 0f
            while (Math.ceil(curBigAngle.toDouble()) < angdegTotal + angdegRowSpan) {
                //对大圆按照等角度间距循环
                val yVec: Float = curBigAngle / angdegTotal * height //根据大圆周旋转角度折算出小圆中心Y坐标
                val u = Math.toRadians(curBigAngle.toDouble()) //当前大圆周弧度
                val y = (A * Math.cos(a)).toFloat() //按照公式计算当前顶点
                val x =
                    ((D + A * Math.sin(a)) * Math.sin(u)).toFloat() //的X、Y、Z坐标
                val z =
                    ((D + A * Math.sin(a)) * Math.cos(u)).toFloat()
                //将计算出来的X、Y、Z坐标放入原始顶点列表
                alVertix.add(x)
                alVertix.add(y + yVec)
                alVertix.add(z)
                curBigAngle += angdegRowSpan
            }
            curSmallAngle += angdegColSpan
        }

        //索引
        for (i in 0 until nSmall) {
            for (j in 0 until nBig) {
                val index: Int = i * (nBig + 1) + j //当前索引
                //卷绕索引
                alFaceIndex.add(index + 1) //下一列---1
                alFaceIndex.add(index + nBig + 1) //下一列---2
                alFaceIndex.add(index + nBig + 2) //下一行下一列---3
                alFaceIndex.add(index + 1) //下一列---1
                alFaceIndex.add(index) //当前---0
                alFaceIndex.add(index + nBig + 1) //下一列---2
            }
        }
        //计算卷绕顶点和平均法向量
        val vertices = FloatArray(vCount * 3)
        cullVertex(alVertix, alFaceIndex, vertices)

        //纹理
        val alST = ArrayList<Float>() //原纹理坐标列表（未卷绕）

        curSmallAngle = 0.0
        while (ceil(curSmallAngle) < 360 + angdegColSpan) {
            val t = curSmallAngle / 360 //t坐标
            var curBigAngle = 0f
            while (ceil(curBigAngle.toDouble()) < angdegTotal + angdegRowSpan) {
                val s = curBigAngle / angdegTotal //s坐标
                //将计算出来的ST坐标加入存放顶点坐标的ArrayList
                alST.add(s)
                alST.add(t.toFloat())
                curBigAngle += angdegRowSpan
            }
            curSmallAngle += angdegColSpan
        }

        //计算卷绕后纹理坐标
        val textures = cullTexCoor(alST, alFaceIndex)

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

    override fun getDrawType(): Int {
        return when (controlModel3DInfo.drawType) {
            DrawType.LINE -> GLES30.GL_LINE_LOOP
            DrawType.COLOR -> GLES30.GL_TRIANGLES
            DrawType.POINT -> GLES30.GL_POINTS
            DrawType.TEXTURE -> GLES30.GL_TRIANGLES
        }
    }

}