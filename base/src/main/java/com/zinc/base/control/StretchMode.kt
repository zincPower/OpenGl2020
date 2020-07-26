package com.zinc.base.control

import android.opengl.GLES30

/**
 * 拉伸方式
 */
enum class StretchMode(val value: Int) {

    // 末端拉伸
    EDGE(GLES30.GL_CLAMP_TO_EDGE),

    // 重复
    REPEAT(GLES30.GL_REPEAT),

    // 镜像
    MIRROR(GLES30.GL_MIRRORED_REPEAT)

}