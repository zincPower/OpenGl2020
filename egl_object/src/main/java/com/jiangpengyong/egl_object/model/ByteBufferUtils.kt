package com.jiangpengyong.egl_object.model

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * @author: jiang peng yong
 * @date: 2021/12/10 4:36 下午
 * @email: 56002982@qq.com
 * @desc: ByteBuffer
 */
object ByteBufferUtils {

    /**
     * 申请 ByteBuffer
     * @param capacity 字节长度
     */
    fun allocateByteBuffer(capacity: Int): ByteBuffer {
        val allocateDirect = ByteBuffer.allocateDirect(capacity)
        allocateDirect.order(ByteOrder.nativeOrder())
        return allocateDirect
    }

    /**
     * 申请 FloatBuffer
     * @param capacity float个数
     */
    fun allocateFloatBuffer(capacity: Int): FloatBuffer {
        return allocateByteBuffer(capacity * 4).asFloatBuffer()
    }

    /**
     * 申请 FloatBuffer，同时放入数据
     * @param data 需要放入的数据
     */
    fun allocateFloatBuffer(data: FloatArray): FloatBuffer {
        val floatBuffer = allocateByteBuffer(data.size * 4)
            .asFloatBuffer()
        floatBuffer.put(data)
        floatBuffer.position(0)
        return floatBuffer
    }
}
