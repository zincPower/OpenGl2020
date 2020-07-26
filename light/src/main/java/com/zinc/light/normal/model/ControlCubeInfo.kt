package com.zinc.light.normal.model

import com.zinc.base.model.IControlModel

/**
 * @author: Jiang Pengyong
 * @date: 2020/7/20 2:53 PM
 * @email: 56002982@qq.com
 * @des: 正方体控制信息
 */
data class ControlCubeInfo(
    var roughness: Int = 50,            // 镜面粗糙度
    var lightPosition: LightPosition = LightPosition(), //光源位置
    var isFaceNormal: Boolean = false       // 是否使用面法向量
) : IControlModel

/**
 * @author: Jiang Pengyong
 * @date: 2020/7/20 2:55 PM
 * @email: 56002982@qq.com
 * @des: 光源位置
 */
data class LightPosition(
    var x: Float = 0f,  // x轴
    var y: Float = 0f,  // y轴
    var z: Float = 0f   // z轴
)