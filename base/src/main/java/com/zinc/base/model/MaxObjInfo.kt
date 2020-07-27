package com.zinc.base.model

/**
 * @author: Jiang Pengyong
 * @date: 2020/7/26 4:17 PM
 * @email: 56002982@qq.com
 * @des: 3ds max obj 解析出来的信息
 */
data class MaxObjInfo(
    val isLoadSuccess: Boolean,
    val isContainVertex: Boolean = false,
    val isContainTexture: Boolean = false,
    val isContainNormal: Boolean = false,
    val vertexData: FloatArray = FloatArray(0),
    val textureData: FloatArray = FloatArray(0),
    val normalData: FloatArray = FloatArray(0)
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MaxObjInfo

        if (isLoadSuccess != other.isLoadSuccess) return false
        if (isContainVertex != other.isContainVertex) return false
        if (isContainTexture != other.isContainTexture) return false
        if (isContainNormal != other.isContainNormal) return false
        if (!vertexData.contentEquals(other.vertexData)) return false
        if (!textureData.contentEquals(other.textureData)) return false
        if (!normalData.contentEquals(other.normalData)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isLoadSuccess.hashCode()
        result = 31 * result + isContainVertex.hashCode()
        result = 31 * result + isContainTexture.hashCode()
        result = 31 * result + isContainNormal.hashCode()
        result = 31 * result + vertexData.contentHashCode()
        result = 31 * result + textureData.contentHashCode()
        result = 31 * result + normalData.contentHashCode()
        return result
    }
}