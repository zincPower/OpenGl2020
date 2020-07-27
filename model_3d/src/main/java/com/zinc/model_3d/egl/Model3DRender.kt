package com.zinc.model_3d.egl

import android.content.Context
import com.zinc.base.control.StretchMode
import com.zinc.base.egl.BaseSceneRender
import com.zinc.base.utils.MatrixState
import com.zinc.base.utils.TextureUtils
import com.zinc.model_3d.R
import com.zinc.model_3d.model.ControlModel3DInfo
import com.zinc.model_3d.model.cylinder.Cylinder
import javax.microedition.khronos.opengles.GL10

class Model3DRender(context: Context, private val controlModel3DInfo: ControlModel3DInfo) :
    BaseSceneRender<Cylinder>(context) {

    override fun initData(context: Context) {
        // 加载纹理
        val textureId1 =
            TextureUtils.obtainTexture(context, R.drawable.texture1, StretchMode.REPEAT)
        val textureId2 =
            TextureUtils.obtainTexture(context, R.drawable.texture2, StretchMode.REPEAT)
        // 创建圆柱骨架对象
        mData = Cylinder(
            context,
            1.2f,
            0.8f,
            36,
            textureId1,
            textureId1,
            textureId2,
            controlModel3DInfo
        )
    }

    override fun drawData(data: Cylinder) {
        // 绕X轴转动
        MatrixState.rotate(xAngle, 1f, 0f, 0f)
        // 绕Y轴转动
        MatrixState.rotate(yAngle, 0f, 1f, 0f)
        // 绕Z轴转动
        MatrixState.rotate(zAngle, 0f, 0f, 1f)

        data.draw()
    }
}