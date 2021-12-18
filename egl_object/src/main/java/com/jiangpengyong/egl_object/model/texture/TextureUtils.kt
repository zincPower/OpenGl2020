package com.jiangpengyong.egl_object.model.texture

import android.opengl.GLES20
import com.jiangpengyong.egl_object.log.Logger

/**
 * @author: jiang peng yong
 * @date: 2021/12/10 4:35 下午
 * @email: 56002982@qq.com
 * @desc: 纹理工具
 */
object TextureUtils {

    /**
     * 创建需要指定其大小的纹理
     *
     * @param width 纹理的宽
     * @param height 纹理的高
     */
    fun createTexture(width: Int, height: Int, size: Int = 1): IntArray {
        if (size <= 0) {
            return IntArray(size)
        }

        // 1、创建
        val textureIds = IntArray(size)
        GLES20.glGenTextures(
            size,
            textureIds,
            0
        )

        textureIds.forEach { id ->
            // 2、绑定设置采样方式和拉伸方式
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id)
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE
            )

            // 3、设置颜色附件纹理格式
            GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D,
                0,                   //层次
                GLES20.GL_RGBA,            //内部格式
                width,                     //宽度
                height,                    //高度
                0,                  //边界宽度
                GLES20.GL_RGBA,            //格式
                GLES20.GL_UNSIGNED_BYTE,   //每个像素数据格式
                null
            )
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)

        Logger.i("Create Texture.[textureId: $textureIds, size: $width x $height]")

        return textureIds
    }

    /**
     * 释放
     */
    fun release(textureId: Int) {
        Logger.i("Release Texture.[textureId: $textureId]")
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        val textureIdArray = intArrayOf(textureId)
        GLES20.glDeleteTextures(1, textureIdArray, 0)
    }

    /**
     * 释放
     */
    fun release(textureIdList: ArrayList<Int>) {
        Logger.i("Release Texture.[textureId: $textureIdList]")
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        val textureIdArray = textureIdList.toIntArray()
        GLES20.glDeleteTextures(textureIdList.size, textureIdArray, 0)
    }

    /**
     * 释放
     */
    fun release(textureIdArray: IntArray?) {
        textureIdArray ?: return
        Logger.i("Release Texture.[textureId: $textureIdArray]")
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glDeleteTextures(textureIdArray.size, textureIdArray, 0)
    }
}