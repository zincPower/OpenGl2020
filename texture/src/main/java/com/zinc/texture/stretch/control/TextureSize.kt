package com.zinc.texture.stretch.control

/**
 * @author: Jiang Pengyong
 * @date: 2020/7/22 10:40 AM
 * @email: 56002982@qq.com
 * @des: 纹理尺寸
 */
enum class TextureSize(val size: Size) {

    TEXTURE1_1(Size(1, 1)),
    TEXTURE4_2(Size(4, 2)),
    TEXTURE4_4(Size(4, 4))

}

/**
 * 尺寸
 */
data class Size(
    val width: Int,
    val height: Int
)