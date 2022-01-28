package com.jiangpengyong.opengl_proving.multi_surface_render

import android.graphics.SurfaceTexture
import android.os.HandlerThread
import android.util.Log

/**
 * @author: jiang peng yong
 * @date: 2022/1/28 4:49 下午
 * @email: 56002982@qq.com
 * @desc: 多 Surface 输出
 */
class MultiRenderThread {

    companion object {
        const val ADD_SURFACE = 1
        const val RENDER = 2
        const val REMOVE_SURFACE = 3
        const val RELEASE = 4
    }

    private var handlerThread: HandlerThread? = null
    private var handlerMulti: MultiRenderHandler? = null

    init {
        handlerThread = HandlerThread("RenderThread")
            .apply {
                start()
                handlerMulti = MultiRenderHandler(looper)
            }
    }

    fun addSurface(
        width: Int,
        height: Int,
        surfaceTexture: SurfaceTexture
    ) {
        log("init: $width x $height , $surfaceTexture")
        handlerMulti?.obtainMessage(ADD_SURFACE)
            ?.apply {
                arg1 = width
                arg2 = height
                obj = surfaceTexture
                handlerMulti?.sendMessage(this)
            }
    }

    fun removeSurface(
        surfaceTexture: SurfaceTexture
    ) {
        handlerMulti?.obtainMessage(REMOVE_SURFACE)
            ?.apply {
                obj = surfaceTexture
                handlerMulti?.sendMessage(this)
            }
    }

    fun release() {
        log("release")
        handlerMulti?.obtainMessage(RELEASE)
            ?.apply {
                handlerMulti?.sendMessage(this)
            }
    }

    fun render() {
        log("render")
        handlerMulti?.obtainMessage(RENDER)
            ?.apply {
                handlerMulti?.sendMessage(this)
            }

    }

    private fun log(msg: String) {
        Log.e("RenderThread", "[${Thread.currentThread()}] $msg")
    }
}