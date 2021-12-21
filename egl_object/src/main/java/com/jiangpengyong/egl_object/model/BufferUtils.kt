package com.jiangpengyong.egl_object.model

import android.opengl.GLES30

/**
 * @author: jiang peng yong
 * @date: 2021/12/21 11:06 上午
 * @email: 56002982@qq.com
 * @desc: 缓冲工具
 */
object BufferUtils {

    fun createDrawBuffer(): Int {
        val bufferArray = IntArray(1)
        GLES30.glDrawBuffers(1, bufferArray, 0)
        return bufferArray[0]
    }

}