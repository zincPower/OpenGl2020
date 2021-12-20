package com.jiangpengyong.egl_object.model

import android.opengl.GLES20
import com.jiangpengyong.egl_object.log.Logger

/**
 * @author: jiang peng yong
 * @date: 2021/12/10 4:36 下午
 * @email: 56002982@qq.com
 * @desc: FBO的操作
 *
 * 使用步骤：
 * 1、创建 FrameBuffer [createFrameBuffer]
 * 2、绑定 FrameBuffer [bindFrameBuffer]
 * 3、创建 Texture [Texture]
 * 4、将 Texture 作为 FrameBuffer 颜色附件 [bindTextureToFrameBuffer]
 */
object FboUtils {

    /**
     * 创建 FrameBuffer
     */
    fun createFrameBuffer(): Int {
        val frameBufferId = IntArray(1)
        GLES20.glGenFramebuffers(1, frameBufferId, 0)
        Logger.i("Create Frame Buffer.[FrameBufferId: ${frameBufferId[0]}]")
        return frameBufferId[0]
    }

    /**
     * 绑定 FrameBuffer
     * @param frameBufferId 绑定的FrameBufferId
     */
    fun bindFrameBuffer(
        frameBufferId: Int,
        frameBufferTarget: Int = GLES20.GL_FRAMEBUFFER
    ) {
        GLES20.glBindFramebuffer(frameBufferTarget, frameBufferId)
    }

    /**
     * 解绑 FrameBuffer
     */
    fun unbindFrameBuffer() {
        bindFrameBuffer(0)
    }

    /**
     * 删除 FrameBuffer
     */
    fun deleteFrameBuffer(fboId: Int) {
        Logger.i("Delete Frame Buffer.[FrameBufferId: $fboId]")
        GLES20.glDeleteFramebuffers(1, intArrayOf(fboId), 0)
    }

    /**
     * 绑定 RenderBuffer
     */
    fun bindRenderBuffer(renderBufferId: Int = 0, width: Int = 0, height: Int = 0) {
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, renderBufferId)
        if (renderBufferId != 0) {
            GLES20.glRenderbufferStorage(
                GLES20.GL_RENDERBUFFER,
                GLES20.GL_DEPTH_COMPONENT16,
                width,
                height
            )
        }
    }

    /**
     * 将纹理作为颜色附件绑定在帧缓冲
     */
    fun bindTextureToFrameBuffer(
        textureId: Int,
        frameBufferId: Int,
        frameBufferTarget: Int = GLES20.GL_FRAMEBUFFER,
        attachment: Int = GLES20.GL_COLOR_ATTACHMENT0
    ) {
        GLES20.glBindFramebuffer(frameBufferTarget, frameBufferId)
        GLES20.glFramebufferTexture2D(
            GLES20.GL_FRAMEBUFFER,
            attachment,
            GLES20.GL_TEXTURE_2D,
            textureId,
            0
        )
    }
}