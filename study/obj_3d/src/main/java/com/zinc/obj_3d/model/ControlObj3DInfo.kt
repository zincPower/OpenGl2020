package com.zinc.obj_3d.model

/**
 * @author: Jiang Pengyong
 * @date: 2020/7/26 4:13 PM
 * @email: 56002982@qq.com
 * @des: 控制3d模型的信息
 */
data class ControlObj3DInfo(var type: ObjType = ObjType.TEAPOT)

/**
 * @author: Jiang Pengyong
 * @date: 2020/7/26 4:13 PM
 * @email: 56002982@qq.com
 * @des: 3d模型类型
 */
enum class ObjType {
    TEAPOT
}