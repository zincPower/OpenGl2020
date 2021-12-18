package com.jiangpengyong.egl_object.model.texture

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import com.jiangpengyong.egl_object.log.Logger
import com.jiangpengyong.egl_object.model.Constant.NOT_INIT
import com.jiangpengyong.egl_object.model.FboUtils
import com.jiangpengyong.egl_object.model.StretchMode

/**
 * @author: jiang peng yong
 * @date: 2021/12/10 4:35 下午
 * @email: 56002982@qq.com
 * @desc: 纹理
 */
class Texture(
    private val textureType: TextureType = TextureType.SAMPLE_2D,
    private val stretchMode: StretchMode = StretchMode.EDGE,
    private val minSample: Int = GLES20.GL_NEAREST,
    private val magSample: Int = GLES20.GL_LINEAR
) {

    companion object {
        // 纹理个数
        private const val TEXTURE_NUM = 1

        private const val SYSTEM_TEXTURE = 0
    }

    // 纹理Id
    var textureId = NOT_INIT
        private set

    // FBO Id
    var fboId = NOT_INIT
        private set

    // 宽
    var width: Int = NOT_INIT

    // 高
    var height: Int = NOT_INIT

    /**
     * 初始化纹理
     * @param width 纹理宽度
     * @param height 纹理高度
     * @param bitmap 纹理图
     * @param isNeedRecycleBitmap 是否需要回收
     */
    fun initTexture(
        width: Int = NOT_INIT,
        height: Int = NOT_INIT,
        bitmap: Bitmap? = null,
        isNeedRecycleBitmap: Boolean = false
    ) {
        if (textureId != NOT_INIT) {
            Logger.e("Texture id is not NOT_INIT. Please delete texture first.[$textureId]")
            return
        }

        val textureIdArray = intArrayOf(NOT_INIT)

        GLES20.glGenTextures(
            TEXTURE_NUM,          // 产生的纹理id的数量
            textureIdArray,       // 纹理id的数组
            0              // 偏移量
        )

        textureId = textureIdArray[0]

        GLES20.glBindTexture(textureType.value, textureId)
        // 设置MIN采样方式
        GLES20.glTexParameteri(
            textureType.value,
            GLES20.GL_TEXTURE_MIN_FILTER,
            minSample
        )
        // 设置MAG采样方式
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MAG_FILTER,
            magSample
        )

        // S轴为截取拉伸方式
        GLES20.glTexParameteri(
            textureType.value,
            GLES20.GL_TEXTURE_WRAP_S,
            stretchMode.value
        )
        // T轴为截取拉伸方式
        GLES20.glTexParameteri(
            textureType.value,
            GLES20.GL_TEXTURE_WRAP_T,
            stretchMode.value
        )

        // 如果 bitmap 不是为空，说明是需要进行初始化bitmap
        if (bitmap != null) {
            if (bitmap.isRecycled) {
                Logger.e("Bitmap is recycled.")
            } else {
                this.width = bitmap.width
                this.height = bitmap.height
                GLUtils.texImage2D(
                    GLES20.GL_TEXTURE_2D,  //纹理类型
                    0,
                    GLUtils.getInternalFormat(bitmap),
                    bitmap,                 //纹理图像
                    GLUtils.getType(bitmap),
                    0               //纹理边框尺寸
                )
                if (isNeedRecycleBitmap) {
                    bitmap.recycle()
                }
            }
        } else if (width != NOT_INIT && height != NOT_INIT) {
            this.width = width
            this.height = height
            // 设置颜色附件纹理格式
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

        // 解绑
        GLES20.glBindTexture(
            textureType.value,
            SYSTEM_TEXTURE
        )

        Logger.i("Create Texture.[textureId: $textureId, size: $width x $height]")
    }

    /**
     * 是否初始化
     * @return true：已经初始化，false：未初始化
     */
    fun isInit(): Boolean {
        return textureId != NOT_INIT
    }

    /**
     * 是否可复用
     * @param width 新的宽度
     * @param height 新的高度
     * @return true 可复用，false 不可复用
     */
    fun isReusable(width: Int, height: Int): Boolean {
        if (!isInit()) {
            return false
        }
        return width == this.width && height == this.height
    }

    /**
     * 绑定
     */
    fun bind() {
        if (textureId < SYSTEM_TEXTURE) {
            Logger.e("Texture id is invalid.Please call initTexture function first.[$textureId]")
            return
        }
        GLES20.glBindTexture(textureType.value, textureId)
    }

    /**
     * 解绑
     */
    fun unbind() {
        FboUtils.unbindFrameBuffer()
        GLES20.glBindTexture(
            textureType.value,
            SYSTEM_TEXTURE
        )
    }

    /**
     * 解绑 FBO
     */
    fun unbindToFBO() {
        FboUtils.unbindFrameBuffer()
    }

    /**
     * 绑定 FBO
     */
    fun bindToFBO() {
        if (textureId == NOT_INIT) {
            Logger.e("Bind to FBO should create texture first.")
            return
        }

        if (fboId == NOT_INIT) {
            fboId = FboUtils.createFrameBuffer()
        }

        FboUtils.bindFrameBuffer(fboId)
        FboUtils.bindTextureToFrameBuffer(textureId, fboId)
    }

    /**
     * 释放 FBO
     */
    private fun releaseFBO() {
        if (fboId == NOT_INIT) {
            return
        }
        FboUtils.unbindFrameBuffer()
        FboUtils.deleteFrameBuffer(fboId)
        fboId = NOT_INIT
    }

    /**
     * 释放
     */
    fun release() {
        if (!isInit()) {
            return
        }
        releaseFBO()
        GLES20.glBindTexture(
            textureType.value,
            SYSTEM_TEXTURE
        )
        val textureIdArray = intArrayOf(textureId)
        GLES20.glDeleteTextures(1, textureIdArray, 0)
        Logger.i("Delete Texture.[Texture Id: $textureId]")
        textureId = NOT_INIT

        width = NOT_INIT
        height = NOT_INIT
    }

    override fun toString(): String {
        return "textureId: $textureId, fboId: $fboId, size: $width x $height"
    }

}