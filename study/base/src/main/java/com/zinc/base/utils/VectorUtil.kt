package com.zinc.base.utils

import kotlin.math.sqrt

/**
 * @author: Jiang Pengyong
 * @date: 2020/8/2 1:33 PM
 * @email: 56002982@qq.com
 * @des: 向量计算工具
 */
object VectorUtil {

    /**
     * 计算圆锥面指定棱顶点法向量的方法
     *
     * C(x3, y3, z3)
     * |\
     * | \
     * |  \
     * |   \ ➚ d
     * |    \
     * |     \
     * |______\
     * A       B(x1, y1, z1)
     * (x0, y0, z0)
     */
    fun calConeNormal(
        x0: Float, y0: Float, z0: Float,
        x1: Float, y1: Float, z1: Float,
        x2: Float, y2: Float, z2: Float
    ): FloatArray {
        // 向量AB
        val a = floatArrayOf(x1 - x0, y1 - y0, z1 - z0)
        // 向量AC
        val b = floatArrayOf(x2 - x0, y2 - y0, z2 - z0)
        // 向量BC
        val c = floatArrayOf(x2 - x1, y2 - y1, z2 - z1)
        // 叉积得出平面ABC的法向量k（垂直于平面）
        val k = crossTwoVectors(a, b)
        // 将c和k做叉乘，得出垂直于BC的法向量k
        val d = crossTwoVectors(c, k)
        return normalizeVector(d) //返回规格化后的法向量
    }

    /**
     * 向量规格化的方法
     */
    fun normalizeVector(vec: FloatArray): FloatArray {
        // 求向量的模
        val mod = module(vec)
        // 规格化后的向量
        return floatArrayOf(vec[0] / mod, vec[1] / mod, vec[2] / mod)
    }

    /**
     * 求向量的模的方法
     */
    fun module(vec: FloatArray): Float {
        return sqrt(vec[0] * vec[0] + vec[1] * vec[1] + (vec[2] * vec[2]).toDouble()).toFloat()
    }

    /**
     * 求两个向量叉积的方法
     */
    fun crossTwoVectors(
        a: FloatArray,
        b: FloatArray
    ): FloatArray {
        // 叉积的X分量
        val x = a[1] * b[2] - a[2] * b[1]
        // 叉积的Y分量
        val y = a[2] * b[0] - a[0] * b[2]
        // 叉积的Z分量
        val z = a[0] * b[1] - a[1] * b[0]
        // 叉积向量
        return floatArrayOf(x, y, z)
    }

}