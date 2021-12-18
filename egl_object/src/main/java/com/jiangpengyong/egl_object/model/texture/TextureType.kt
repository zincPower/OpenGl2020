package com.jiangpengyong.egl_object.model.texture

import android.opengl.GLES11Ext
import android.opengl.GLES20

/**
 * @author: jiang peng yong
 * @date: 2021/12/10 4:35 下午
 * @email: 56002982@qq.com
 * @desc: 纹理类型
 */
enum class TextureType(val value: Int) {
    SAMPLE_2D(GLES20.GL_TEXTURE_2D),                    // 2d 纹理
    EXTERNAL_OES(GLES11Ext.GL_TEXTURE_EXTERNAL_OES),    // 摄像头或视屏纹理
}