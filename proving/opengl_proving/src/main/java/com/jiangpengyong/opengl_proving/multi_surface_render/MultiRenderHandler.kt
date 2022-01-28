package com.jiangpengyong.opengl_proving.multi_surface_render

import android.annotation.SuppressLint
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.Surface
import com.jiangpengyong.egl_object.log.Logger
import com.jiangpengyong.egl_object.model.Program
import com.jiangpengyong.egl_object.model.texture.Texture
import com.jiangpengyong.opengl_proving.multi_surface_render.MultiRenderThread.Companion.ADD_SURFACE
import com.jiangpengyong.opengl_proving.multi_surface_render.MultiRenderThread.Companion.RELEASE
import com.jiangpengyong.opengl_proving.multi_surface_render.MultiRenderThread.Companion.REMOVE_SURFACE
import com.jiangpengyong.opengl_proving.multi_surface_render.MultiRenderThread.Companion.RENDER
import kotlin.random.Random

class MultiRenderHandler(looper: Looper) : Handler(looper) {
    private val surfaceInfoMap: HashMap<Int, SurfaceInfo> = HashMap()

    private val mEglController = MultiRenderEglController()
    private val mProgram = Program()
    private val mTexture = Texture()

    override fun handleMessage(msg: Message) {
        when (msg.what) {
            ADD_SURFACE -> handleAddSurface(msg.arg1, msg.arg2, msg.obj as SurfaceTexture)
            REMOVE_SURFACE -> handleRemoveSurface(msg.obj as SurfaceTexture)
            RENDER -> handleRender()
            RELEASE -> handleRelease()
        }
    }

    private fun handleRemoveSurface(surfaceTexture: SurfaceTexture) {
        log("handleRemoveSurface")
    }

    private fun handleRelease() {
        log("handleRelease")
        release()
    }

    private fun handleRender() {
        log("handleRender")
        surfaceInfoMap.forEach {
            mEglController.bindSurface(it.value.eglSurface)
            GLES20.glViewport(0, 0, it.value.width, it.value.height)

            GLES20.glClearColor(Random.nextFloat(), Random.nextFloat(), Random.nextFloat(), 1f)
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
            mEglController.update(it.value.eglSurface)
        }
    }

    @SuppressLint("Recycle")
    private fun handleAddSurface(width: Int, height: Int, surfaceTexture: SurfaceTexture) {
        log("handleAddSurface")
        if (!mEglController.isRunning) {
            mEglController.init()
        }

        if (surfaceInfoMap.containsKey(surfaceTexture.hashCode())) {
            Logger.e("Surface Texture has been add in egl.[${surfaceTexture.hashCode()}]")
            return
        }

        val mEglSurface = mEglController.createWindowSurface(Surface(surfaceTexture))
        if (mEglSurface == null) {
            Logger.e("EglSurface create failure.")
            release()
            return
        } else {
            Logger.i("EglSurface create success.")
        }

        val surfaceInfo = SurfaceInfo(
            width = width,
            height = height,
            surfaceTexture = surfaceTexture,
            eglSurface = mEglSurface
        )

        surfaceInfoMap[surfaceTexture.hashCode()] = surfaceInfo
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