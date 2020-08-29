package com.zinc.texture.sample.model

data class SampleControlModel(
    var bigSampleType: SampleType,
    var smallSampleType: SampleType
)

enum class SampleType {
    GL_NEAREST_GL_NEAREST,
    GL_LINEAR_GL_LINEAR,
    GL_NEAREST_GL_LINEAR,
    GL_LINEAR_GL_NEAREST,
}