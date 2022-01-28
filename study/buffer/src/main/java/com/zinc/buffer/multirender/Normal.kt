package com.zinc.buffer.multirender

// 表示法向量的类，此类的一个对象表示一个法向量
class Normal(// 法向量在XYZ轴上的分量
    var nx: Float,
    var ny: Float,
    var nz: Float
) {
    override fun equals(other: Any?): Boolean {
        // 若两个法向量 XYZ 三个分量的差都小于指定的阈值则认为这两个法向量相等
        return if (other is Normal) {
            val tn = other
            Math.abs(nx - tn.nx) < DIFF && Math.abs(ny - tn.ny) < DIFF && Math.abs(
                ny - tn.ny
            ) < DIFF
        } else {
            false
        }
    }

    // 由于要用到 HashSet，因此一定要重写 hashCode 方法
    override fun hashCode(): Int {
        return 1
    }

    companion object {
        // 判断两个法向量是否相同的阈值
        const val DIFF = 0.0000001f

        // 求法向量平均值的工具方法
        @JvmStatic
        fun getAverage(sn: Set<Normal>): FloatArray {
            // 存放法向量和的数组
            val result = FloatArray(3)
            // 把集合中所有的法向量求和
            for (n in sn) {
                result[0] += n.nx
                result[1] += n.ny
                result[2] += n.nz
            }
            // 将求和后的法向量规格化
            return LoadUtil.vectorNormal(result)
        }
    }
}