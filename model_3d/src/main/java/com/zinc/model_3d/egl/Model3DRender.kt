package com.zinc.model_3d.egl

import android.content.Context
import android.text.method.Touch
import com.zinc.base.IModel
import com.zinc.base.control.StretchMode
import com.zinc.base.egl.BaseSceneRender
import com.zinc.base.utils.MatrixState
import com.zinc.base.utils.TextureUtils
import com.zinc.model_3d.R
import com.zinc.model_3d.model.ControlModel3DInfo
import com.zinc.model_3d.model.DrawModel
import com.zinc.model_3d.model.cone.Cone
import com.zinc.model_3d.model.cylinder.Cylinder
import com.zinc.model_3d.model.spring.Spring
import com.zinc.model_3d.model.torus.Torus
import javax.microedition.khronos.opengles.GL10

class Model3DRender(context: Context, private val controlModel3DInfo: ControlModel3DInfo) :
    BaseSceneRender<IModel>(context) {

    private var cone: IModel? = null
    private var torus: IModel? = null
    private var spring: IModel? = null
    private var cylinder: IModel? = null

    override fun initData(context: Context) {
        // 加载纹理
        val textureId1 =
            TextureUtils.obtainTexture(context, R.drawable.texture1, StretchMode.REPEAT)
        val textureId2 =
            TextureUtils.obtainTexture(context, R.drawable.texture2, StretchMode.REPEAT)

        cone = Cone(
            context,
            1.2f,
            0.8f,
            36,
            textureId1,
            textureId2,
            controlModel3DInfo
        )

        torus = Torus(
            context,
            1.5f,
            1f,
            36,
            36,
            textureId2,
            controlModel3DInfo
        )

        spring = Spring(
            context,
            3f,
            3f,
            1.5f,
            1f,
            36,
            36,
            textureId2,
            controlModel3DInfo
        )

        cylinder = Cylinder(
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

    override fun preDraw() {
        mData = when (controlModel3DInfo.drawModel) {
            DrawModel.CONE -> cone
            DrawModel.TORUS -> torus
            DrawModel.SPRING -> spring
            DrawModel.CYLINDER -> cylinder
        }
    }

    override fun drawData(data: IModel) {
        // 绕X轴转动
        MatrixState.rotate(xAngle, 1f, 0f, 0f)
        // 绕Y轴转动
        MatrixState.rotate(yAngle, 0f, 1f, 0f)
        // 绕Z轴转动
        MatrixState.rotate(zAngle, 0f, 0f, 1f)

        data.draw()
    }
}