package com.jiangpengyong.opengl_proving.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.IOException
import java.io.InputStream

object AssetUtils {

    fun getImageFromAssetsFile(context: Context, fileName: String): Bitmap? {
        val assetManager = context.resources.assets
        var inputStream: InputStream? = null
        return try {
            inputStream = assetManager.open(fileName)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            inputStream?.close()
        }
    }
}