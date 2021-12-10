package com.zinc.base.utils.buffer

import android.opengl.GLES30
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * @author: Jiang Pengyong
 * @date: 2020/8/2 8:24 PM
 * @email: 56002982@qq.com
 * @des: 映射缓冲区对象
 */
object MboUtils {

    /**
     * 创建映射缓冲区对象
     */
    fun createMappingBuffer(size: Int = 1): IntArray {
        return VboUtils.createBuffer(size)
    }

    /**
     * 输入float类型数据
     *
     * @return 成功返回true，失败返回false
     */
    fun sendFloatData(
        data: FloatArray,
        target: Int = GLES30.GL_ARRAY_BUFFER,
        access: Int = GLES30.GL_MAP_WRITE_BIT or GLES30.GL_MAP_INVALIDATE_BUFFER_BIT
    ): Boolean {
        val buffer = GLES30.glMapBufferRange(
            target,
            0,
            data.size * 4,
            access
        ) as? ByteBuffer ?: return false

        buffer.order(ByteOrder.nativeOrder())
        val mappingBuffer = buffer.asFloatBuffer()
        mappingBuffer.put(data)
        mappingBuffer.position(0)

        return true
    }

    /**
     * 解绑
     *
     * @return 成功则返回true，失败false
     */
    fun unbindMappingBuffer(target: Int = GLES30.GL_ARRAY_BUFFER): Boolean {
        return GLES30.glUnmapBuffer(target)
    }

}