package com.zinc.model_3d.model

import com.zinc.base.model.IControlModel

/**
 * @author: Jiang Pengyong
 * @date: 2020/7/23 10:44 AM
 * @email: 56002982@qq.com
 * @des: 3d模型控制信息
 */
data class ControlModel3DInfo(var drawType: DrawType = DrawType.TEXTURE) : IControlModel

/**
 * @author: Jiang Pengyong
 * @date: 2020/7/23 10:41 AM
 * @email: 56002982@qq.com
 * @des: 绘制类型
 */
enum class DrawType(val value: Int) {
    // 点
    POINT(1),

    // 线
    LINE(2),

    // 颜色
    COLOR(3),

    // 纹理
    TEXTURE(4)
}

/**
 * @author: Jiang Pengyong
 * @date: 2020/7/23 10:44 AM
 * @email: 56002982@qq.com
 * @des: 绘制模式类型
 */
enum class DrawModel {
    // 圆柱体
    CYLINDER

}