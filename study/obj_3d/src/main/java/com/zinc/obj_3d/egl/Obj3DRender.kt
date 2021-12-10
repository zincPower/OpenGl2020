package com.zinc.obj_3d.egl

import android.content.Context
import com.zinc.base.control.StretchMode
import com.zinc.base.egl.BaseSceneRender
import com.zinc.base.utils.Load3DMaxObjUtils
//import com.zinc.base.utils.LoadUtil
import com.zinc.base.utils.MatrixState
import com.zinc.base.utils.TextureUtils
import com.zinc.obj_3d.R
import com.zinc.obj_3d.model.ControlObj3DInfo
import com.zinc.obj_3d.model.Obj3DModel
import com.zinc.obj_3d.model.ObjType

class Obj3DRender(context: Context, private val controlObj3DInfo: ControlObj3DInfo) :
    BaseSceneRender<Obj3DModel>(context) {

    private var textureId = 0

    override fun initData(context: Context) {
        // 加载纹理
        textureId = TextureUtils.obtainTexture(context, R.drawable.teapot, StretchMode.REPEAT)

        val maxObjInfo = when (controlObj3DInfo.type) {
            ObjType.TEAPOT -> {
                Load3DMaxObjUtils.load("teapot.obj", context.resources, textureFlip = true)
            }
        }

        // 创建圆柱骨架对象
        mData = Obj3DModel(context, maxObjInfo)
    }

    override fun getFar(): Float = 1000f

    override fun getNear(): Float = 2f

    override fun getSeeZ(): Float = 1f

    override fun drawData(data: Obj3DModel) {
        MatrixState.translate(0f, -20f, -100f)

        // 绕X轴转动
        MatrixState.rotate(xAngle, 1f, 0f, 0f)
        // 绕Y轴转动
        MatrixState.rotate(yAngle, 0f, 1f, 0f)
        // 绕Z轴转动
        MatrixState.rotate(zAngle, 0f, 0f, 1f)

        data.draw(textureId)
    }

}