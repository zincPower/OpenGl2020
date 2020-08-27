package com.zinc.buffer.egl.simple

import android.content.Context
import com.zinc.base.IModel
import com.zinc.base.control.StretchMode
import com.zinc.base.egl.BaseSceneRender
import com.zinc.base.utils.Load3DMaxObjUtils
import com.zinc.base.utils.MatrixState
import com.zinc.base.utils.TextureUtils
import com.zinc.buffer.R
import com.zinc.buffer.model.ControlBufferInfo
import com.zinc.buffer.model.ubo.UboModel

class BufferRender(context: Context, private val controlBufferInfo: ControlBufferInfo) :
    BaseSceneRender<IModel>(context) {

    private var textureId = 0

    override fun initData(context: Context) {
        // 加载纹理
        textureId = TextureUtils.obtainTexture(context, R.drawable.teapot, StretchMode.REPEAT)

        val maxObjInfo = Load3DMaxObjUtils.load("teapot.obj", context.resources, textureFlip = true)

//        mData = when (controlBufferInfo.type) {
//            BufferType.VBO -> VboModel(context, maxObjInfo)
//            BufferType.VAO -> VaoModel(context, maxObjInfo)
//            BufferType.UBO -> UboModel(context, maxObjInfo)
//        }

        mData = UboModel(context, maxObjInfo)
    }

    override fun drawData(data: IModel) {
        MatrixState.translate(0f, -20f, -100f)

        // 绕X轴转动
        MatrixState.rotate(xAngle, 1f, 0f, 0f)
        // 绕Y轴转动
        MatrixState.rotate(yAngle, 0f, 1f, 0f)
        // 绕Z轴转动
        MatrixState.rotate(zAngle, 0f, 0f, 1f)

        data.draw(textureId)
    }

    override fun getFar(): Float = 1000f

    override fun getNear(): Float = 2f

    override fun getSeeZ(): Float = 1f




}