package com.zinc.light.light.egl

import android.content.Context
import com.zinc.base.egl.BaseSceneRender
import com.zinc.base.utils.MatrixState
import com.zinc.light.light.model.Ball
import com.zinc.light.light.model.ControlBallInfo

class LightSceneRender(context: Context, private val controlBallInfo: ControlBallInfo) :
    BaseSceneRender<Ball>(context) {

    override fun initData(context: Context) {
        // 创建球对象
        mData = Ball(context, controlBallInfo)
    }

    override fun drawData(data: Ball) {
        // 初始化光源位置
        MatrixState.setLightLocation(
            controlBallInfo.lightPosition.x,
            controlBallInfo.lightPosition.y,
            controlBallInfo.lightPosition.z
        )

        // 绕X轴转动
        MatrixState.rotate(xAngle, 1f, 0f, 0f)
        // 绕Y轴转动
        MatrixState.rotate(yAngle, 0f, 1f, 0f)
        // 绕Z轴转动
        MatrixState.rotate(zAngle, 0f, 0f, 1f)

        // 绘制球👈
        MatrixState.pushMatrix()
        MatrixState.translate(-1.2f, 0f, 0f)
        data.draw()
        MatrixState.popMatrix()

        // 绘制球👉
        MatrixState.pushMatrix()
        MatrixState.translate(1.2f, 0f, 0f)
        data.draw()
        MatrixState.popMatrix()
    }

}