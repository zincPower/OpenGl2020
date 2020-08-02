package com.zinc.buffer.model

/**
 * @author: Jiang Pengyong
 * @date: 2020/7/26 4:13 PM
 * @email: 56002982@qq.com
 * @des: 控制缓冲区的信息
 */
data class ControlBufferInfo(var type: BufferType = BufferType.VBO)

/**
 * @author: Jiang Pengyong
 * @date: 2020/7/26 4:13 PM
 * @email: 56002982@qq.com
 * @des: 缓冲区类型
 */
enum class BufferType {
    VBO     // 顶点缓冲区
}