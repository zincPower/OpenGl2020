package com.zinc.base

import android.content.Context

/**
 * @author: Jiang Pengyong
 * @date: 2020/7/20 6:03 PM
 * @email: 56002982@qq.com
 * @des: 模型基础
 */
interface IModel {

    fun initShader(context: Context)

    fun initVertexData()

    fun draw(textureId: Int = -1)

}
