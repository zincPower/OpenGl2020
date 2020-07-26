package com.zinc.texture.stretch.view

import android.os.Bundle
import com.zinc.base.BaseActivity
import com.zinc.texture.R
import com.zinc.base.control.StretchMode
import com.zinc.texture.stretch.control.TextureSize
import com.zinc.texture.stretch.model.ControlStretchInfo
import kotlinx.android.synthetic.main.activity_stretch.*

/**
 * @author: Jiang Pengyong
 * @date: 2020/7/21 3:21 PM
 * @email: 56002982@qq.com
 * @des: 拉伸方式
 */
class StretchActivity : BaseActivity() {

    private val controlStretchInfo = ControlStretchInfo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stretch)

        stretch?.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.edge -> {
                    controlStretchInfo.stretchMode = StretchMode.EDGE
                }
                R.id.repeat -> {
                    controlStretchInfo.stretchMode = StretchMode.REPEAT
                }
                R.id.mirror -> {
                    controlStretchInfo.stretchMode = StretchMode.MIRROR
                }
            }
            surface_view?.setControlModel(controlStretchInfo)
        }

        texture?.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.texture_11 -> {
                    controlStretchInfo.textureSize = TextureSize.TEXTURE1_1
                }
                R.id.texture_42 -> {
                    controlStretchInfo.textureSize = TextureSize.TEXTURE4_2
                }
                R.id.texture_44 -> {
                    controlStretchInfo.textureSize = TextureSize.TEXTURE4_4
                }
            }
            surface_view?.setControlModel(controlStretchInfo)
        }

    }

}