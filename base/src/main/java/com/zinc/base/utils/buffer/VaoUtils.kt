package com.zinc.base.utils.buffer

import android.opengl.GLES30

/**
 * @author: Jiang Pengyong
 * @date: 2020/8/2 4:39 PM
 * @email: 56002982@qq.com
 * @des: 顶点数组对象
 */
object VaoUtils {

    /**
     * 创建顶点数组对象
     * @param size 顶点数组对象数量
     */
    fun createVertexArray(size: Int = 1): IntArray {
        val vaoId = IntArray(size)
        GLES30.glGenVertexArrays(size, vaoId, 0)
        return vaoId
    }

    /**
     * 绑定顶点数组对象
     *
     * @param vaoId 顶点数组对象id
     */
    fun bindVertexArray(vaoId: Int) {
        GLES30.glBindVertexArray(vaoId)
    }

    /**
     * 解绑顶点数组对象
     */
    fun unbindVertexArray() {
        GLES30.glBindVertexArray(0)
    }


    /**
     * 输入float类型数据
     *
     * @param bufferId 缓冲id
     * @param handle 着色器索引id
     * @param size 一组数据的个数
     * @param target
     * GL_ARRAY_BUFFER	            数组缓冲(默认)
     * GL_ELEMENT_ARRAY_BUFFER	    元素数组缓冲
     * GL_PIXEL_PACK_BUFFER	        像素打包缓冲
     * GL_PIXEL_UNPACK_BUFFER	    像素解包缓冲
     * GL_COPY_READ_BUFFER	        复制只读缓冲
     * GL_COPY_WRITE_BUFFER	        复制可写缓冲
     * GL_TRANSFORM_FEEDBACK_BUFFER	变换反馈缓冲
     * GL_UNIFORM_BUFFER	        一致变量缓冲
     */
    fun sendFloatData(
        bufferId: Int,
        handle: Int,
        size: Int,
        target: Int = GLES30.GL_ARRAY_BUFFER
    ) {
        sendData(bufferId, handle, size, size * 4, GLES30.GL_FLOAT, target)
    }

    /**
     * 输入数据
     *
     * @param bufferId 缓冲id
     * @param handle 着色器索引id
     * @param size 一组数据的个数
     * @param stride 一组数据的跨度，单位字节
     * @param type 类型
     * @param target
     * GL_ARRAY_BUFFER	            数组缓冲(默认)
     * GL_ELEMENT_ARRAY_BUFFER	    元素数组缓冲
     * GL_PIXEL_PACK_BUFFER	        像素打包缓冲
     * GL_PIXEL_UNPACK_BUFFER	    像素解包缓冲
     * GL_COPY_READ_BUFFER	        复制只读缓冲
     * GL_COPY_WRITE_BUFFER	        复制可写缓冲
     * GL_TRANSFORM_FEEDBACK_BUFFER	变换反馈缓冲
     * GL_UNIFORM_BUFFER	        一致变量缓冲
     */
    fun sendData(
        bufferId: Int,
        handle: Int,
        size: Int,
        stride: Int,
        type: Int,
        target: Int = GLES30.GL_ARRAY_BUFFER
    ) {
        GLES30.glBindBuffer(target, bufferId)
        GLES30.glVertexAttribPointer(
            handle,
            size,
            type,
            false,
            stride,
            0
        )
    }

}