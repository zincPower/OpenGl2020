package com.jiangpengyong.egl_object.model

import android.opengl.GLES20

/**
 * 纹理拉伸方式
 */
enum class StretchMode(val value: Int) {
    EDGE(GLES20.GL_CLAMP_TO_EDGE),      // 末端拉伸
    REPEAT(GLES20.GL_REPEAT),           // 重复
    MIRROR(GLES20.GL_MIRRORED_REPEAT)   // 镜像
}