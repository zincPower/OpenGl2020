package com.jiangpengyong.opengl_proving.multi_surface_render

import android.graphics.SurfaceTexture
import android.os.Bundle
import android.view.TextureView
import androidx.activity.ComponentActivity
import com.jiangpengyong.opengl_proving.R
import kotlinx.android.synthetic.main.activity_multi_render.*

/**
 * @author: jiang peng yong
 * @date: 2022/1/28 4:50 下午
 * @email: 56002982@qq.com
 * @desc: 多 Surface 输出
 */
class MultiRenderActivity : ComponentActivity() {

    val renderThread = MultiRenderThread()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_render)

        texture_view_1?.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture?,
                width: Int,
                height: Int
            ) {
                surface?.let { surfaceTexture ->
                    renderThread.addSurface(width, height, surfaceTexture)
                }
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture?,
                width: Int,
                height: Int
            ) {
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                renderThread.release()
                return false
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
            }
        }

        texture_view_2?.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture?,
                width: Int,
                height: Int
            ) {
                surface?.let { surfaceTexture ->
                    renderThread.addSurface(width, height, surfaceTexture)
                }
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture?,
                width: Int,
                height: Int
            ) {
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                renderThread.release()
                return false
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
            }
        }

        texture_view_3?.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture?,
                width: Int,
                height: Int
            ) {
                surface?.let { surfaceTexture ->
                    renderThread.addSurface(width, height, surfaceTexture)
                }
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture?,
                width: Int,
                height: Int
            ) {
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                renderThread.release()
                return false
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
            }
        }

        btn_render?.setOnClickListener {
            renderThread.render()
        }
    }

}