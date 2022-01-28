package com.jiangpengyong.opengl_proving.multi_surface_render

import android.graphics.SurfaceTexture
import android.opengl.EGLSurface

/**
 * @author: jiang peng yong
 * @date: 2022/1/28 4:58 下午
 * @email: 56002982@qq.com
 * @desc: Surface 信息
 */
data class SurfaceInfo(
    val width: Int,
    val height: Int,
    val surfaceTexture: SurfaceTexture,
    var eglSurface: EGLSurface
) {

}