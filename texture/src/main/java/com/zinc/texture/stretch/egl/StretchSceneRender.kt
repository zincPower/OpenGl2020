package com.zinc.texture.stretch.egl

import android.content.Context
import com.zinc.base.control.StretchMode
import com.zinc.base.egl.BaseSceneRender
import com.zinc.base.utils.TextureUtils
import com.zinc.texture.R
import com.zinc.texture.stretch.control.TextureSize
import com.zinc.texture.stretch.model.ControlStretchInfo
import com.zinc.texture.stretch.model.StretchModel

/**
 * @author: Jiang Pengyong
 * @date: 2020/7/22 10:08 AM
 * @email: 56002982@qq.com
 * @des: 拉伸方式
 */
class StretchSceneRender(context: Context, private val controlStretchInfo: ControlStretchInfo) :
    BaseSceneRender<StretchModel>(context) {

    // 系统分配的拉伸纹理id
    private var mTextureEdgeId = 0

    // 系统分配的重复纹理id
    private var mTextureRepeatId = 0

    // 系统分配的镜像纹理id
    private var mTextureMirrorId = 0

    // 纹理矩形数组
    private val mStretchModelList: Array<StretchModel?> = arrayOfNulls(3)

    // 当前纹理id
    private var mCurrTextureId = 0

    override fun initData(context: Context) {
        mTextureEdgeId = TextureUtils.obtainTexture(
            context,
            R.drawable.stretch_dog,
            StretchMode.EDGE
        )
        mTextureRepeatId = TextureUtils.obtainTexture(
            context,
            R.drawable.stretch_dog,
            StretchMode.REPEAT
        )
        mTextureMirrorId = TextureUtils.obtainTexture(
            context,
            R.drawable.stretch_dog,
            StretchMode.MIRROR
        )

        mStretchModelList[0] = StretchModel(context, TextureSize.TEXTURE1_1)
        mStretchModelList[1] = StretchModel(context, TextureSize.TEXTURE4_2)
        mStretchModelList[2] = StretchModel(context, TextureSize.TEXTURE4_4)
    }

    override fun preDraw() {
        super.preDraw()

        mData = when (controlStretchInfo.textureSize) {
            TextureSize.TEXTURE4_4 -> mStretchModelList[2]
            TextureSize.TEXTURE1_1 -> mStretchModelList[0]
            TextureSize.TEXTURE4_2 -> mStretchModelList[1]
        }

        mCurrTextureId = when (controlStretchInfo.stretchMode) {
            StretchMode.EDGE -> mTextureEdgeId
            StretchMode.MIRROR -> mTextureMirrorId
            StretchMode.REPEAT -> mTextureRepeatId
        }

    }

    override fun isUseProjectFrustum(): Boolean {
        return false
    }

    override fun drawData(data: StretchModel) {
        //绘制当前纹理矩形
        data.draw(mCurrTextureId)
    }

}