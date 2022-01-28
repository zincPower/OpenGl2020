package com.jiangpengyong.opengl_proving.render

import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.Surface
import com.jiangpengyong.egl_object.egl.EglController
import com.jiangpengyong.egl_object.model.Program
import com.jiangpengyong.egl_object.model.texture.Texture
import kotlin.random.Random

class RenderThread {

    companion object {
        const val INIT = 1
        const val RENDER = 2
        const val RELEASE = 3
    }

    private var handlerThread: HandlerThread? = null
    private var handler: RenderHandler? = null

    init {
        handlerThread = HandlerThread("RenderThread")
            .apply {
                start()
                handler = RenderHandler(looper)
            }
    }

    fun init(width: Int, height: Int, surfaceTexture: SurfaceTexture) {
        log("init: $width x $height , $surfaceTexture")
        handler?.obtainMessage(INIT)
            ?.apply {
                arg1 = width
                arg2 = height
                obj = surfaceTexture
                handler?.sendMessage(this)
            }
    }

    fun release() {
        log("release")
        handler?.obtainMessage(RELEASE)
            ?.apply {
                handler?.sendMessage(this)
            }
    }

    fun render() {
        log("render")
        handler?.obtainMessage(RENDER)
            ?.apply {
                handler?.sendMessage(this)
            }

    }

    class RenderHandler(looper: Looper) : Handler(looper) {
        private val mEglController = EglController()
        private val mProgram = Program()
        private val mTexture = Texture()

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                INIT -> handleInit(msg.arg1, msg.arg2, msg.obj as SurfaceTexture)
                RENDER -> handleRender()
                RELEASE -> handleRelease()
            }
        }

        private fun handleRelease() {
            log("handleRelease")
            release()
        }

        private fun handleRender() {
            log("handleRender")
            GLES20.glClearColor(Random.nextFloat(), Random.nextFloat(), Random.nextFloat(), 1f)
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
            mEglController.update()
        }

        private fun handleInit(width: Int, height: Int, surfaceTexture: SurfaceTexture) {
            log("handleInit")
            mEglController.init()
            if (!mEglController.attachWindow(Surface(surfaceTexture))) {
                release()
                return
            }
            GLES20.glViewport(0, 0, width, height)
            clear()
        }

        private fun clear() {
            GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
            GLES20.glClear(
                GLES20.GL_COLOR_BUFFER_BIT
                        or GLES20.GL_DEPTH_BUFFER_BIT
                        or GLES20.GL_STENCIL_BUFFER_BIT
            )
        }

        private fun release() {
            mTexture.release()
            mProgram.release()
            mEglController.release()
        }

        private fun log(msg: String) {
            Log.e("RenderHandler", "[${Thread.currentThread()}] $msg")
        }
    }

    private fun log(msg: String) {
        Log.e("RenderThread", "[${Thread.currentThread()}] $msg")
    }
}