//package com.jiangpengyong.egl_object.model.pbo
//
//import android.graphics.Bitmap
//import android.opengl.GLES30
//import android.opengl.GLUtils
//import com.jiangpengyong.egl_object.log.Logger
//import java.nio.ByteBuffer
//import java.nio.ByteOrder
//
///**
// * @author: jiang peng yong
// * @date: 2021/12/10 4:34 下午
// * @email: 56002982@qq.com
// * @desc: pbo 传输数据
// */
//class UnpackPixelBufferObject {
//
//    init {
//        System.loadLibrary("native-lib")
//    }
//
//    companion object {
//        private const val NOT_INIT = -1
//    }
//
//    // 宽
//    var width: Int = NOT_INIT
//        private set
//
//    // 高
//    var height: Int = NOT_INIT
//        private set
//
//    // pbo 的 id
//    private var pboId: IntArray = intArrayOf(NOT_INIT)
//
//    // 图像格式
//    private var format: Int = NOT_INIT
//
//    // 图像类型
//    private var type: Int = NOT_INIT
//
//    /**
//     * 传入纹理或帧缓冲区
//     */
//    fun unpack(bitmap: Bitmap): Boolean {
//        this.width = bitmap.width
//        this.height = bitmap.height
//
//        this.format = GLUtils.getInternalFormat(bitmap)
//        this.type = GLUtils.getType(bitmap)
//
//        createPBO()
//        // 绑定 pbo
//        GLES30.glBindBuffer(GLES30.GL_PIXEL_UNPACK_BUFFER, pboId[0])
//
//        // 映射缓冲区
//        val pboBuffer = (GLES30.glMapBufferRange(
//            GLES30.GL_PIXEL_UNPACK_BUFFER,
//            0,
//            getSize(),
//            GLES30.GL_MAP_WRITE_BIT or GLES30.GL_MAP_INVALIDATE_BUFFER_BIT
//        ) as? ByteBuffer)?.order(ByteOrder.nativeOrder())
//        if (pboBuffer == null) {
//            Logger.e("PBO buffer is null.")
//            return false
//        }
//        pboBuffer.position(0)
//
//        bitmap.copyPixelsToBuffer(pboBuffer)
//
//        GLES30.glUnmapBuffer(GLES30.GL_PIXEL_UNPACK_BUFFER)
//
//        return true
//    }
//
//    fun use(textureId: Int): Boolean {
//        if (textureId <= 0) {
//            Logger.e("Texture is invalid.[$textureId]")
//            return false
//        }
//
//        val pboId = getPBOId()
//        if (pboId == NOT_INIT) {
//            Logger.e("PBO is not init, please use unpack function first.[$pboId]")
//            return false
//        }
//
//        if (format == NOT_INIT) {
//            Logger.e("Format is not init, please use unpack function first.[$format]")
//            return false
//        }
//
//        if (type == NOT_INIT) {
//            Logger.e("Type is not init, please use unpack function first.[$type]")
//            return false
//        }
//
//        // 激活纹理
//        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
//        // 绑定纹理
//        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId)
//        // 绑定 pbo
//        GLES30.glBindBuffer(GLES30.GL_PIXEL_UNPACK_BUFFER, pboId)
//
//        // 将缓冲区数据输入
//        glTexSubImage2D(
//            GLES30.GL_TEXTURE_2D,
//            0,
//            0,
//            0,
//            width,
//            height,
//            format,
//            type
//        )
//
//        // 解绑 pbo
//        GLES30.glBindBuffer(GLES30.GL_PIXEL_UNPACK_BUFFER, 0)
//        // 解绑纹理
//        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
//
//        return true
//    }
//
//    fun release() {
//        deletePBO()
//        width = NOT_INIT
//        height = NOT_INIT
//        format = NOT_INIT
//        type = NOT_INIT
//    }
//
//    /**
//     * 创建 PBO
//     */
//    private fun createPBO() {
//        // 如果已经创建，则不进行创建
//        if (getPBOId() != NOT_INIT) {
//            deletePBO()
//        }
//        // 申请 pbo
//        GLES30.glGenBuffers(1, pboId, 0)
//        // 绑定 pbo
//        GLES30.glBindBuffer(GLES30.GL_PIXEL_UNPACK_BUFFER, pboId[0])
//        // 申请空间
//        GLES30.glBufferData(GLES30.GL_PIXEL_UNPACK_BUFFER, getSize(), null, GLES30.GL_STREAM_DRAW)
//        // 解绑 pbo
//        GLES30.glBindBuffer(GLES30.GL_PIXEL_UNPACK_BUFFER, 0)
//    }
//
//    /**
//     * 删除 PBO
//     */
//    private fun deletePBO() {
//        if (getPBOId() == NOT_INIT) {
//            return
//        }
//        GLES30.glDeleteBuffers(1, pboId, 0)
//        pboId[0] = NOT_INIT
//    }
//
//    private fun getPBOId(): Int {
//        return pboId[0]
//    }
//
//    private fun getSize(): Int {
//        if (width == NOT_INIT || height == NOT_INIT) {
//            return 0
//        }
//        return width * height * 4
//    }
//
//    private external fun glTexSubImage2D(
//        glTexture2d: Int,
//        level: Int,
//        x: Int,
//        y: Int,
//        width: Int,
//        height: Int,
//        format: Int,
//        type: Int
//    )
//}