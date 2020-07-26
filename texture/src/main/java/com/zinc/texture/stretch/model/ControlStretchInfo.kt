package com.zinc.texture.stretch.model

import com.zinc.base.model.IControlModel
import com.zinc.base.control.StretchMode
import com.zinc.texture.stretch.control.TextureSize

/**
 * @author: Jiang Pengyong
 * @date: 2020/7/22 10:32 AM
 * @email: 56002982@qq.com
 * @des: 拉伸信息
 */
class ControlStretchInfo(
    var stretchMode: StretchMode = StretchMode.EDGE,
    var textureSize: TextureSize = TextureSize.TEXTURE1_1
) : IControlModel