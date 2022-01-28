package com.jiangpengyong.opengl_proving.render

import android.graphics.SurfaceTexture
import android.os.Bundle
import android.view.TextureView
import androidx.activity.ComponentActivity
import com.jiangpengyong.opengl_proving.R
import com.jiangpengyong.opengl_proving.utils.AssetUtils
import kotlinx.android.synthetic.main.activity_render.*

class RenderActivity : ComponentActivity() {

    val renderThread = RenderThread()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_render)

        texture_view?.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture?,
                width: Int,
                height: Int
            ) {
                surface?.let { surfaceTexture ->
                    renderThread.init(width, height, surfaceTexture)
//                    surfaceTexture.setOnFrameAvailableListener { surfaceTexture ->
//                        surfaceTexture ?: return@setOnFrameAvailableListener
//                        renderThread.render()
//                    }
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