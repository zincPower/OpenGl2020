package com.zinc.base.utils.buffer

import android.opengl.GLES30
import java.nio.Buffer

/**
 * @author: Jiang Pengyong
 * @date: 2020/8/2 3:39 PM
 * @email: 56002982@qq.com
 * @des: 顶点缓冲区
 */
object VboUtils {

    /**
     * 创建缓冲区
     * @param size 缓冲区长度，默认为1
     * @return 缓冲区id
     */
    fun createBuffer(size: Int = 1): IntArray {
        val bufferIds = IntArray(size)
        GLES30.glGenBuffers(size, bufferIds, 0)
        return bufferIds
    }

    /**
     * 绑定缓冲区
     *
     * @param bufferId 缓冲区id
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
    fun bindBuffer(bufferId: Int, target: Int = GLES30.GL_ARRAY_BUFFER) {
        GLES30.glBindBuffer(target, bufferId)
    }

    /**
     * 解绑，即绑定回系统的默认缓冲id值0
     *
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
    fun unbindBuffer(target: Int = GLES30.GL_ARRAY_BUFFER) {
        bindBuffer(0, target)
    }

    /**
     * 对缓冲区送入数据
     *
     * @param size 输送入缓冲区的大小，单位为字节
     * @param data 需要送入缓冲的数据，若没有数据要送入缓冲区，其值可以为 null
     * @param usage： 指定缓冲区的用途
     * GL_STATIC_DRAW	在绘制时，缓冲区对象数据可以被修改一次，使用多次
     * GL_STATIC_READ	从 OpenGL ES 中读回的数据，缓冲区对象数据可以被修改一次，使用多次，且该数据可以从应用程序中查询
     * GL_STATIC_COPY	从 OpenGL ES 中读回的数据，缓冲区对象数据可以被修改一次，使用多次，该数据将直接作为绘制图元或者指定图像的信息来源
     * GL_DYNAMIC_DRAW	在绘制时，缓冲区对象数据可以被重复修改、使用多次
     * GL_DYNAMIC_READ	从 OpenGL ES 中读回的数据，缓冲区对象数据可以被重复修改、使用多次，且该数据可以从应用程序中查询
     * GL_DYNAMIC_COPY	从 OpenGL ES 中读回的数据，缓冲区对象数据可以被重复修改、使用多次，该数据将直接作为绘制图元或者指定图像的信息来源
     * GL_STREAM_DRAW	在绘制时，缓冲区对象数据可以被修改一次，使用少数几次
     * GL_STREAM_READ	从 OpenGL ES 中读回的数据，缓冲区对象数据可以被修改一次，使用少数几次，且该数据可以从应用程序中查询
     * GL_STREAM_COPY	从 OpenGL ES 中读回的数据，缓冲区对象数据可以被修改一次，使用少数几次，该数据将直接作为绘制图元或者指定图像的信息来源
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
    fun sendBufferData(
        size: Int,
        data: Buffer?,
        usage: Int = GLES30.GL_STATIC_DRAW,
        target: Int = GLES30.GL_ARRAY_BUFFER
    ) {
        GLES30.glBufferData(target, size, data, usage)
    }
}