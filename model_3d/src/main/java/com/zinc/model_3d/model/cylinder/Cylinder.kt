package com.zinc.model_3d.model.cylinder

import android.content.Context
import com.zinc.base.IModel
import com.zinc.base.utils.MatrixState
import com.zinc.model_3d.model.ControlModel3DInfo

/**
 * @author: Jiang Pengyong
 * @date: 2020/7/23 11:14 AM
 * @email: 56002982@qq.com
 * @des: 圆柱体
 */
class Cylinder(
    context: Context,        // 上下文
    private val height: Float,       // 高度
    r: Float,            // 半径
    n: Int,              // 份数
    private val topTextureId: Int,   // 顶部纹理
    private val bottomTextureId: Int, // 底部纹理
    controlModel3DInfo: ControlModel3DInfo
) : IModel {

    private var bottomCircle: Circle = Circle(context, r, n, controlModel3DInfo)
    private var topCircle: Circle = Circle(context, r, n, controlModel3DInfo)
    private var cylinderSide: CylinderSide = CylinderSide(context, height, r, n, controlModel3DInfo)

    override fun initShader(context: Context) {
    }

    override fun initVertexData() {
    }

    override fun draw(textureId: Int) {

        MatrixState.pushMatrix()
        // 平移变换
        MatrixState.translate(0f, height / 2, 0f)
        // 旋转变换
        MatrixState.rotate(-90f, 1f, 0f, 0f)
        // 绘制圆面
        topCircle.draw(topTextureId)
        // 恢复现场
        MatrixState.popMatrix()

        MatrixState.pushMatrix()
        // 平移变换
        MatrixState.translate(0f, -height / 2, 0f)
        // 旋转变换
        MatrixState.rotate(90f, 1f, 0f, 0f)
        // 绘制圆面
        bottomCircle.draw(bottomTextureId)
        // 恢复现场
        MatrixState.popMatrix()

        MatrixState.pushMatrix()
        MatrixState.translate(0f, -height / 2, 0f)
        // 绘制侧面
        cylinderSide.draw(topTextureId)
        // 恢复现场
        MatrixState.popMatrix()


    }

}