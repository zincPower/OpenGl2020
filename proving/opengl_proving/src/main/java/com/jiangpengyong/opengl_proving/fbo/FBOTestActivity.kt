package com.jiangpengyong.opengl_proving.fbo

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jiangpengyong.opengl_proving.R
import com.jiangpengyong.opengl_proving.fbo.egl.BlitFramebufferThread
import com.jiangpengyong.opengl_proving.fbo.egl.FBOThread
import com.jiangpengyong.opengl_proving.utils.AssetUtils
import java.io.File

/**
 * @author: jiang peng yong
 * @date: 2021/12/10 4:45 下午
 * @email: 56002982@qq.com
 * @desc: FBO 测试
 */
class FBOTestActivity : AppCompatActivity() {

    var ivBitmap: ImageView? = null
    var tvDraw2: TextView? = null
    var tvDraw3: TextView? = null

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_mark)

        ivBitmap = findViewById(R.id.iv_bitmap)
        tvDraw2 = findViewById(R.id.tv_draw_2)
        tvDraw3 = findViewById(R.id.tv_draw_3)

        tvDraw2?.setOnClickListener {
            val bitmap = AssetUtils.getImageFromAssetsFile(
                this,
                "process_image.jpeg"
            ) ?: return@setOnClickListener
            Thread(FBOThread(bitmap) { bitmap ->
                bitmap ?: return@FBOThread
                val file = File(cacheDir, "${System.currentTimeMillis()}.jpeg")
                file.createNewFile()
                val outputStream = file.outputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.close()
                Log.e("Jiang", "save: ${file.absoluteFile}")
                runOnUiThread {
                    ivBitmap?.setImageBitmap(bitmap)
                }
            }).start()

        }

        tvDraw3?.setOnClickListener {
            val bitmap = AssetUtils.getImageFromAssetsFile(
                this,
                "process_image.jpeg"
            ) ?: return@setOnClickListener
            Thread(BlitFramebufferThread(bitmap) { bitmap ->
                bitmap ?: return@BlitFramebufferThread
                val file = File(cacheDir, "${System.currentTimeMillis()}.jpeg")
                file.createNewFile()
                val outputStream = file.outputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.close()
                Log.e("Jiang", "save: ${file.absoluteFile}")
                runOnUiThread {
                    ivBitmap?.setImageBitmap(bitmap)
                }
            }).start()

        }

    }

}