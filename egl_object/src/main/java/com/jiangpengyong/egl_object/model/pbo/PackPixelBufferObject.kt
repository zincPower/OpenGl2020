//package com.jiangpengyong.egl_object.model.pbo
//
//import android.graphics.Bitmap
//import android.opengl.GLES30
//import java.nio.ByteBuffer
//
///**
// * @author: jiang peng yong
// * @date: 2021/12/10 4:34 下午
// * @email: 56002982@qq.com
// * @desc: pbo 读取数据
// */
//class PackPixelBufferObject(
//    val width: Int,     // 宽
//    val height: Int     // 高
//) {
//
//    companion object {
//        private const val NOT_INIT = -1
//    }
//
//    // pbo 的 id
//    private var pboId: IntArray = intArrayOf(NOT_INIT)
//
//    init {
//        System.loadLibrary("native-lib")
//    }
//
//    fun obtainBitmap(): Bitmap? {
//        createPBO()
//        // 绑定 pbo
//        GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, getPBOId())
//
//        // 读取像素至 pbo，因为进行绑定了 pbo
//        glReadPixels(
//            0,
//            0,
//            width,
//            height,
//            GLES30.GL_RGBA,
//            GLES30.GL_UNSIGNED_BYTE
//        )
//
//        // 进行映射缓冲区
//        val byteBuffer = GLES30.glMapBufferRange(
//            GLES30.GL_PIXEL_PACK_BUFFER,
//            0,
//            getSize(),
//            GLES30.GL_MAP_READ_BIT
//        ) as ByteBuffer
//
//        // 解除映射
//        GLES30.glUnmapBuffer(GLES30.GL_PIXEL_PACK_BUFFER)
//        // 解除 pbo 绑定
//        GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, 0)
//
//        // 将 buffer 转为 bitmap
//        byteBuffer.position(0)
//        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//        bitmap?.copyPixelsFromBuffer(byteBuffer)
//
//        return bitmap
//    }
//
//    /**
//     * 释放
//     */
//    fun release() {
//        deletePBO()
//    }
//
//    /**
//     * 创建 PBO
//     */
//    private fun createPBO() {
//        // 检测是否已经创建 pbo
//        if (getPBOId() != NOT_INIT) {
//            deletePBO()
//        }
//        // 创建 pbo
//        GLES30.glGenBuffers(1, pboId, 0)
//
//        // 绑定 pbo ，后续操作都会针对该 pbo
//        GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, pboId[0])
//        // 申请 pbo 的缓冲空间
//        GLES30.glBufferData(GLES30.GL_PIXEL_PACK_BUFFER, getSize(), null, GLES30.GL_STREAM_READ)
//        // 解绑 pbo
//        GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, 0)
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
//    /**
//     * 获取 pbo
//     */
//    private fun getPBOId(): Int {
//        return pboId[0]
//    }
//
//    /**
//     * 获取缓冲区长度
//     */
//    private fun getSize(): Int {
//        if (width == NOT_INIT || height == NOT_INIT) {
//            return 0
//        }
//        return width * height * 4
//    }
//
//    private external fun glReadPixels(
//        x: Int,
//        y: Int,
//        width: Int,
//        height: Int,
//        format: Int,
//        type: Int
//    )
//}