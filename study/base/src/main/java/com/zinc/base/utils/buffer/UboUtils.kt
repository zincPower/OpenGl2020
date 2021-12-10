package com.zinc.base.utils.buffer

import android.opengl.GLES30
import java.nio.Buffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

/**
 * @author: Jiang Pengyong
 * @date: 2020/8/2 5:50 PM
 * @email: 56002982@qq.com
 * @des: 一致缓冲区
 */
object UboUtils {

    /**
     * 获取一致块的尺寸
     *
     * @param program 程序
     * @param blockIndex 一致块引用
     */
    fun obtainUniformBlockSize(program: Int, blockHandle: Int): Int {
        val blockSizes = IntArray(1)
        GLES30.glGetActiveUniformBlockiv(
            program,
            blockHandle,
            GLES30.GL_UNIFORM_BLOCK_DATA_SIZE,
            blockSizes,
            0
        )
        return blockSizes[0]
    }

    /**
     * 获取一致块成员索引
     *
     * @param program 程序
     * @param names 需要索引获取的一致块属性名称
     */
    fun obtainUniformBlockMemberIndex(program: Int, names: Array<String>): IntArray {
        val index = IntArray(names.size)
        GLES30.glGetUniformIndices(program, names, index, 0)
        return index
    }

    /**
     * 获取一致块成员偏移量
     *
     * @param program 程序
     * @param index 一致块属性索引
     */
    fun obtainUniformBlockOffset(program: Int, index: IntArray): IntArray {
        val offset = IntArray(index.size)
        GLES30.glGetActiveUniformsiv(
            program,
            index.size,
            index,
            0,
            GLES30.GL_UNIFORM_OFFSET,
            offset,
            0
        )
        return offset
    }

    /**
     * 创建一致缓冲对象
     */
    fun createUniformBlock(size: Int = 1): IntArray {
        return VboUtils.createBuffer(size)
    }

    /**
     * 将一致缓冲对象绑定到一致块
     */
    fun bindUniformBlock(blockIndex: Int, uboId: Int) {
        GLES30.glBindBufferBase(GLES30.GL_UNIFORM_BUFFER, blockIndex, uboId)
    }

    /**
     * 输送数据
     */
    fun sendData(blockSize: Int, blockBuffer: Buffer?, usage: Int = GLES30.GL_DYNAMIC_DRAW) {
        VboUtils.sendBufferData(
            blockSize,
            blockBuffer,
            target = GLES30.GL_UNIFORM_BUFFER,
            usage = usage
        )
    }

}