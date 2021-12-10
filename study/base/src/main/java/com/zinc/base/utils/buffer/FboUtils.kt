package com.zinc.base.utils.buffer

import android.opengl.GLES20
import android.opengl.GLES30
import com.zinc.base.utils.buffer.FboUtils.bindFrameBuffer
import com.zinc.base.utils.buffer.FboUtils.bindRenderBuffer
import com.zinc.base.utils.buffer.FboUtils.bindRenderBufferToFrameBuffer
import com.zinc.base.utils.buffer.FboUtils.bindTextureToFrameBuffer
import com.zinc.base.utils.buffer.FboUtils.createFrameBuffer
import com.zinc.base.utils.buffer.FboUtils.createRenderBuffer
import com.zinc.base.utils.buffer.FboUtils.createTexture

/**
 * @author: Jiang Pengyong
 * @date: 2020/8/9 2:27 PM
 * @email: 56002982@qq.com
 * @des: FBO的操作
 *
 * 使用步骤：
 * 1、创建 FrameBuffer [createFrameBuffer]
 * 2、绑定 FrameBuffer [bindFrameBuffer]
 * 3、创建 RenderBuffer [createRenderBuffer]  (如果需要RenderBuffer)
 * 4、绑定 RenderBuffer [bindRenderBuffer]
 * 5、创建 Texture [createTexture]
 * 6、将 Texture 作为 FrameBuffer 颜色附件 [bindTextureToFrameBuffer]
 * 7、将 RenderBuffer 作为 FrameBuffer 深度缓冲附件 [bindRenderBufferToFrameBuffer]
 */
object FboUtils {

    /**
     * 创建 FBO 的颜色附件，和纹理的创建形式一样，只是需要指定其大小
     *
     * @param width 纹理的宽
     * @param height 纹理的高
     */
    fun createTexture(width: Int, height: Int): Int {

        // 1、创建
        val textureId = IntArray(1)
        GLES30.glGenTextures(
            1,
            textureId,
            0
        )

        // 2、绑定设置采样方式和拉伸方式
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId[0])
        GLES30.glTexParameterf(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_MAG_FILTER,
            GLES30.GL_LINEAR.toFloat()
        )
        GLES30.glTexParameterf(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_MIN_FILTER,
            GLES30.GL_LINEAR.toFloat()
        )
        GLES30.glTexParameterf(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_WRAP_S,
            GLES30.GL_CLAMP_TO_EDGE.toFloat()
        )
        GLES30.glTexParameterf(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_WRAP_T,
            GLES30.GL_CLAMP_TO_EDGE.toFloat()
        )

        // 3、设置颜色附件纹理格式
        GLES30.glTexImage2D(
            GLES30.GL_TEXTURE_2D,
            0,                   //层次
            GLES30.GL_RGBA,            //内部格式
            width,                     //宽度
            height,                    //高度
            0,                  //边界宽度
            GLES30.GL_RGBA,            //格式
            GLES30.GL_UNSIGNED_BYTE,   //每个像素数据格式
            null
        )

        // !! 此处不能解绑解绑
//        GLES30.glBindBuffer(GLES30.GL_TEXTURE_2D, 0)
        return textureId[0]
    }

    /**
     * 创建 FrameBuffer
     */
    fun createFrameBuffer(): Int {
        val frameBufferId = IntArray(1)
        GLES30.glGenFramebuffers(1, frameBufferId, 0)
        return frameBufferId[0]
    }

    /**
     * 绑定 FrameBuffer
     * @param frameBufferId 绑定的FrameBufferId
     */
    fun bindFrameBuffer(frameBufferId: Int) {
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferId)
    }

    /**
     * 解绑 FrameBuffer
     */
    fun unbindFrameBuffer() {
        bindFrameBuffer(0)
    }

    /**
     * 创建 RenderBuffer
     */
    fun createRenderBuffer(): Int {
        val renderBufferId = IntArray(1)
        GLES30.glGenRenderbuffers(1, renderBufferId, 0)
        return renderBufferId[0]
    }

    /**
     * 绑定 RenderBuffer
     */
    fun bindRenderBuffer(renderBufferId: Int = 0, width: Int = 0, height: Int = 0) {
        GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, renderBufferId)
        if (renderBufferId != 0) {
            GLES30.glRenderbufferStorage(
                GLES30.GL_RENDERBUFFER,
                GLES30.GL_DEPTH_COMPONENT16,
                width,
                height
            )
        }
    }

    /**
     * 解绑
     */
    fun unbindRenderBuffer() {
        GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, 0)
    }

    /**
     * 将纹理作为颜色附件绑定在帧缓冲
     */
    fun bindTextureToFrameBuffer(textureId: Int, frameBufferId: Int) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferId)
        GLES30.glFramebufferTexture2D(
            GLES30.GL_FRAMEBUFFER,
            GLES30.GL_COLOR_ATTACHMENT0,
            GLES30.GL_TEXTURE_2D,
            textureId,
            0
        )
    }

    /**
     * 将RenderBuffer作为深度附件绑定FrameBuffer
     */
    fun bindRenderBufferToFrameBuffer(renderDepthBufferId: Int, frameBufferId: Int) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferId)
        GLES30.glFramebufferRenderbuffer(
            GLES30.GL_FRAMEBUFFER,
            GLES30.GL_DEPTH_ATTACHMENT,
            GLES30.GL_RENDERBUFFER,
            renderDepthBufferId
        )
    }
}