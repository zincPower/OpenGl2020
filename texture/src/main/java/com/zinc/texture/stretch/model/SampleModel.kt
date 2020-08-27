package com.zinc.texture.stretch.model

import android.content.Context
import com.zinc.base.IModel

/**
 * @author: Jiang Pengyong
 * @date: 2020/8/22 6:01 PM
 * @email: 56002982@qq.com
 * @des: 采样模式
 */
class SampleModel(context: Context) : IModel {

    init {
        initShader(context)
        initVertexData()
    }

    override fun initShader(context: Context) {

    }

    override fun initVertexData() {
    }

    override fun draw(textureId: Int) {

    }


}