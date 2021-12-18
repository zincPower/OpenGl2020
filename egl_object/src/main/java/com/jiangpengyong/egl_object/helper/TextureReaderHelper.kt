package com.jiangpengyong.egl_object.helper

import android.graphics.Bitmap
import android.opengl.GLES20
import com.jiangpengyong.egl_object.model.texture.Texture
import java.nio.IntBuffer

/**
 * @author: jiang peng yong
 * @date: 2021/12/10 4:33 下午
 * @email: 56002982@qq.com
 * @desc: 纹理读取
 */
object TextureReaderHelper {

    /**
     * 获取纹理的图，会将 Texture 和 FBO 进行绑定，读取
     * @param texture 纹理
     */
    fun getBitmap(texture: Texture): Bitmap? {
        texture.bindToFBO()
        val bitmap = getBitmap(texture.width, texture.height)
        texture.unbindToFBO()
        return bitmap
    }

    /**
     * 从 OpenGL 中获取 Bitmap,
     * @param width 纹理的宽
     * @param height 纹理的高
     */
    fun getBitmap(width: Int, height: Int): Bitmap? {

        val size = width * height
        val bitmapBuffer = IntBuffer.allocate(size)
        bitmapBuffer.position(0)

        GLES20.glReadPixels(
            0, 0,
            width, height,
            GLES20.GL_RGBA,
            GLES20.GL_UNSIGNED_BYTE,
            bitmapBuffer
        )

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(bitmapBuffer)

        return bitmap
    }
}