package com.zinc.light.normal.egl

import android.content.Context
import com.zinc.base.egl.BaseSceneRender
import com.zinc.base.utils.MatrixState
import com.zinc.light.normal.model.ControlCubeInfo
import com.zinc.light.normal.model.Cube

/**
 * @author: Jiang Pengyong
 * @date: 2020/7/20 2:57 PM
 * @email: 56002982@qq.com
 * @des: 法向量渲染
 */
class NormalSceneRender(context: Context, private val controlCubeInfo: ControlCubeInfo) :
    BaseSceneRender<Cube>(context) {

    override fun initData(context: Context) {
        // 创建球对象
        mData = Cube(context, controlCubeInfo)
    }

    override fun drawData(data: Cube) {
        // 初始化光源位置
        MatrixState.setLightLocation(
            controlCubeInfo.lightPosition.x,
            controlCubeInfo.lightPosition.y,
            controlCubeInfo.lightPosition.z
        )

        // 绕X轴转动
        MatrixState.rotate(xAngle, 1f, 0f, 0f)
        // 绕Y轴转动
        MatrixState.rotate(yAngle, 0f, 1f, 0f)
        // 绕Z轴转动
        MatrixState.rotate(zAngle, 0f, 0f, 1f)

        // 绘制正方体👈
        MatrixState.pushMatrix()
        MatrixState.translate(-1.2f, 0f, 0f)
        data.draw()
        MatrixState.popMatrix()

        // 绘制正方体👉
        MatrixState.pushMatrix()
        MatrixState.translate(1.2f, 0f, 0f)
        data.draw()
        MatrixState.popMatrix()
    }

}