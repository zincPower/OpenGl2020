package com.zinc.light.light.model

import com.zinc.base.model.IControlModel

data class ControlBallInfo(
    var isUsePositioningLight: Int = 1,
    var isUseAmbient: Int = 1,
    var isUseDiffuse: Int = 1,
    var isUseSpecular: Int = 1,
    var roughness: Int = 50,
    var lightPosition: LightPosition = LightPosition(),
    var isCalByFrag: Int = 0
) : IControlModel

data class LightPosition(
    var x: Float = 0f,
    var y: Float = 0f,
    var z: Float = 0f
)