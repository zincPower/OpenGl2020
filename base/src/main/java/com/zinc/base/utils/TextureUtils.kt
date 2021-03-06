package com.zinc.base.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.opengl.GLUtils
import com.zinc.base.control.StretchMode
import java.io.IOException
import java.io.InputStream


/**
 * @author: Jiang Pengyong
 * @date: 2020/7/22 11:08 AM
 * @email: 56002982@qq.com
 * @des: 纹理工具
 *
 * 内容包括：
 * 1、获取纹理[obtainTexture]
 * 2、从 drawable 加载 bitmap [loadBitmap]
 */
object TextureUtils {

    /**
     * 获取纹理
     * @param context 上下文
     * @param drawable 纹理
     * @param mode 拉伸模式
     * @return 返回的纹理id，加载失败返回-1
     */
    fun obtainTexture(
        context: Context,
        drawable: Int,
        mode: StretchMode,
        minSample: Int = GLES30.GL_NEAREST,
        magSample: Int = GLES30.GL_LINEAR,
        isNeedRecycle: Boolean = true
    ): Int {
        val bitmap = loadBitmap(context, drawable) ?: return -1
        return initTexture(
            mode,
            bitmap,
            minSample,
            magSample,
            isNeedRecycle
        )
    }

    /**
     * 加载bitmap
     */
    fun loadBitmap(context: Context, drawable: Int): Bitmap? {
        val inputStream: InputStream = context.resources.openRawResource(drawable)
        return try {
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 初始化纹理
     * @param mode 拉伸模式
     * @param bitmap 纹理图
     * @param isNeedRecycle 是否需要回收纹理图
     * @return 纹理id
     */
    fun initTexture(
        mode: StretchMode,
        bitmap: Bitmap,
        minSample: Int = GLES30.GL_NEAREST,
        magSample: Int = GLES30.GL_LINEAR,
        isNeedRecycle: Boolean = true
    ): Int {
        // 用于记录生成的纹理id
        val textures = IntArray(1)
        GLES30.glGenTextures(
            1,           //产生的纹理id的数量
            textures,       //纹理id的数组
            0        //偏移量
        )

        val textureId = textures[0]

        // 绑定纹理id，后面的操作就只会对这纹理操作
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId)
        // 设置MIN采样方式
        GLES30.glTexParameteri(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_MIN_FILTER,
            minSample
        )
        // 设置MAG采样方式
        GLES30.glTexParameteri(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_MAG_FILTER,
            magSample
        )
        when (mode) {
            StretchMode.EDGE -> {
                // S轴为截取拉伸方式
                GLES30.glTexParameteri(
                    GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_WRAP_S,
                    GLES30.GL_CLAMP_TO_EDGE
                )
                // T轴为截取拉伸方式
                GLES30.glTexParameteri(
                    GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_WRAP_T,
                    GLES30.GL_CLAMP_TO_EDGE
                )
            }
            StretchMode.MIRROR -> {
                // S轴为镜像重复拉伸方式
                GLES30.glTexParameteri(
                    GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_WRAP_S,
                    GLES30.GL_MIRRORED_REPEAT
                )
                // T轴为镜像重复拉伸方式
                GLES30.glTexParameteri(
                    GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_WRAP_T,
                    GLES30.GL_MIRRORED_REPEAT
                )
            }
            StretchMode.REPEAT -> {
                // S轴为重复拉伸方式
                GLES30.glTexParameteri(
                    GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_WRAP_S,
                    GLES30.GL_REPEAT
                )
                // T轴为重复拉伸方式
                GLES30.glTexParameteri(
                    GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_WRAP_T,
                    GLES30.GL_REPEAT
                )
            }
        }

        GLUtils.texImage2D(
            GLES30.GL_TEXTURE_2D,  //纹理类型
            0,
            GLUtils.getInternalFormat(bitmap),
            bitmap,  //纹理图像
            GLUtils.getType(bitmap),
            0 //纹理边框尺寸
        )

        if (isNeedRecycle) {
            bitmap.recycle() //纹理加载成功后释放图片
        }

        return textureId
    }

}