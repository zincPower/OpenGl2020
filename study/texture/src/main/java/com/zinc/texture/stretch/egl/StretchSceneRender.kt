package com.zinc.texture.stretch.egl

import android.content.Context
import com.zinc.base.control.StretchMode
import com.zinc.base.egl.BaseSceneRender
import com.zinc.base.utils.TextureUtils
import com.zinc.texture.R
import com.zinc.texture.stretch.control.TextureSize
import com.zinc.texture.stretch.model.ControlStretchInfo
import com.zinc.texture.stretch.model.StretchDrawer

/**
 * @author: Jiang Pengyong
 * @date: 2020/7/22 10:08 AM
 * @email: 56002982@qq.com
 * @des: 拉伸方式
 */
class StretchSceneRender(context: Context, private val controlStretchInfo: ControlStretchInfo) :
    BaseSceneRender<StretchDrawer>(context) {

    // 系统分配的拉伸纹理id
    private var mTextureEdgeId = 0

    // 系统分配的重复纹理id
    private var mTextureRepeatId = 0

    // 系统分配的镜像纹理id
    private var mTextureMirrorId = 0

    // 纹理矩形数组
    private val mStretchDrawerList: Array<StretchDrawer?> = arrayOfNulls(3)

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

        mStretchDrawerList[0] = StretchDrawer(context, TextureSize.TEXTURE1_1)
        mStretchDrawerList[1] = StretchDrawer(context, TextureSize.TEXTURE4_2)
        mStretchDrawerList[2] = StretchDrawer(context, TextureSize.TEXTURE4_4)
    }

    override fun preDraw() {
        super.preDraw()

        mData = when (controlStretchInfo.textureSize) {
            TextureSize.TEXTURE4_4 -> mStretchDrawerList[2]
            TextureSize.TEXTURE1_1 -> mStretchDrawerList[0]
            TextureSize.TEXTURE4_2 -> mStretchDrawerList[1]
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

    override fun drawData(data: StretchDrawer) {
        //绘制当前纹理矩形
        data.draw(mCurrTextureId)
    }

}