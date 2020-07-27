package com.zinc.base.utils

import android.content.res.Resources
import com.zinc.base.model.MaxObjInfo
import java.io.BufferedReader

/**
 * @author: Jiang Pengyong
 * @date: 2020/7/26 4:16 PM
 * @email: 56002982@qq.com
 * @des: 加载 3ds max obj文件的工具
 *
 * 3ds max obj 文件格式如下：
 * ============================文件开始================================
 * # ZincPower
 * #
 * v -19.990179 -34.931675 -18.201921
 * v 20.111662 -34.931675 -18.201921
 * ……
 * v 20.111662 27.748880 21.994425
 * # 8 vertices
 * vt 0.000000 0.000000 0.000000
 * vt 1.000000 0.000000 0.000000
 * ……
 * vt 1.000000 1.000000 0.000000
 * # 12 texture vertices
 * vn 0.000000 0.000000 -1.570796
 * vn 0.000000 0.000000 -1.570796
 * ……
 * vn 0.000000 0.000000 1.570796
 * # 8 vertex normals
 * g (null)
 * f 1/10/1 3/12/3 4/11/4
 * f 4/11/4 2/9/2 1/10/1
 * ……
 * f 5/4/5 7/3/7 3/1/3
 * # 12 faces
 * g
 * ==============================文件结束==============================
 * 符号说明：
 * # -----> 注释
 * v -----> 顶点坐标
 * vt ----> 顶点纹理坐标
 * vn ----> 顶点法向量
 * g -----> 以第一个g开始，表示一组的开始，再出现一个g，表示一组的结束
 * f -----> 一个面
 */
object Load3DMaxObjUtils {

    private const val VERTEX = "v"
    private const val NORMAL = "vn"
    private const val TEXTURE = "vt"
    private const val FACE = "f"

    /**
     * 从 assets 加载资源
     * @param assetsFilename 文件名
     * @param resources 资源
     * @param textureSize 纹理需要多少个维度。默认为二个维度，即ST。如果需要三个维度，传入3，则可以得到STP。
     * @param textureFlip 纹理坐标是否需要反转，需要的话，T会进行反转
     * @return max obj
     */
    fun load(
        assetsFilename: String,
        resources: Resources,
        textureSize: Int = 2,
        textureFlip: Boolean = false
    ): MaxObjInfo {
        val bufferedReader =
            OpenGlUtils.getAssetsBufferReader(assetsFilename, resources)
        bufferedReader ?: return MaxObjInfo(false)
        return load(bufferedReader, textureSize, textureFlip)
    }

    /**
     * 加载3D模型
     * @param bufferedReader 文件流
     * @param textureSize 纹理需要多少个维度。默认为二个维度，即ST。如果需要三个维度，传入3，则可以得到STP。
     * @param textureFlip 纹理坐标是否需要反转，需要的话，T会进行反转
     * @return max obj
     */
    fun load(
        bufferedReader: BufferedReader,
        textureSize: Int = 2,
        textureFlip: Boolean = false
    ): MaxObjInfo {

        // 行数据
        var line: String?
        // 顶点数据
        val vertexData = arrayListOf<Float>()
        // 法向量数据
        val normalData = arrayListOf<Float>()
        // 纹理数据
        val textureData = arrayListOf<Float>()

        // 组成面的顶点数据
        val vertexResultData = arrayListOf<Float>()
        // 组成面的纹理数据
        val textureResultData = arrayListOf<Float>()
        // 组成面的法向量数据
        val normalResultData = arrayListOf<Float>()

        var isSuccess = true

        loop@ while (bufferedReader.readLine().also { line = it } != null) {

            val conLine = line ?: continue

            // 如果注释行跳过
            if (conLine.startsWith("#") || conLine.isEmpty()) {
                continue
            }

            // 解析一行的数据
            val temps = conLine.split("[ ]+".toRegex())
            // 如果没有数据则跳过
            if (temps.isEmpty()) {
                continue
            }
            when (temps[0].trim()) {
                VERTEX -> {     // 顶点  v 20.111662 27.748880 21.994425
                    if (temps.size < 4) {
                        isSuccess = false
                        break@loop
                    }

                    try {
                        vertexData.add(temps[1].toFloat())
                        vertexData.add(temps[2].toFloat())
                        vertexData.add(temps[3].toFloat())
                    } catch (e: Exception) {
                        e.printStackTrace()
                        isSuccess = false
                        break@loop
                    }
                }
                NORMAL -> {     // 法向量  vn 0.000000 0.000000 -1.570796
                    if (temps.size < 4) {
                        isSuccess = false
                        break@loop
                    }

                    try {
                        normalData.add(temps[1].toFloat())
                        normalData.add(temps[2].toFloat())
                        normalData.add(temps[3].toFloat())
                    } catch (e: Exception) {
                        e.printStackTrace()
                        isSuccess = false
                        break@loop
                    }
                }
                TEXTURE -> {    // 纹理 (纹理数量)  vt 1.000000 1.000000 0.000000
                    if (temps.size < (textureSize + 1)) {
                        isSuccess = false
                        break@loop
                    }

                    try {
                        var index = 1
                        while (index < (textureSize + 1)) {
                            if (index == 2 && textureFlip) {
                                textureData.add(1 - temps[index++].toFloat())
                            } else {
                                textureData.add(temps[index++].toFloat())
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        isSuccess = false
                        break@loop
                    }
                }
                FACE -> {       // 面 "f 1/10/1 3/12/3 4/11/4"
                    if (temps.size < 4) {
                        isSuccess = false
                        break@loop
                    }

                    // ================================= 顶点 start =================================
                    // OpenGL es 所有的面都是三角形，每行三组数据，代表 第一个顶点 第一个顶点 第三个顶点
                    // 第一个顶点  "1/10/1"
                    val p1 = temps[1].split("/".toRegex())
                    // 第一个顶点  "3/12/3"
                    val p2 = temps[2].split("/".toRegex())
                    // 第三个顶点  "4/11/4"
                    val p3 = temps[3].split("/".toRegex())
                    if (p1.size < 3 || p2.size < 3 || p3.size < 3) {
                        isSuccess = false
                        break@loop
                    }

                    // 第一个点
                    try {
                        // p1 行号是从 1 开始，所以要 减一
                        val index1 = p1[0].toInt() - 1
                        // x
                        vertexResultData.add(vertexData[index1 * 3])
                        // y
                        vertexResultData.add(vertexData[index1 * 3 + 1])
                        // z
                        vertexResultData.add(vertexData[index1 * 3 + 2])

                        // p2 行号是从 1 开始，所以要 减一
                        val index2 = p2[0].toInt() - 1
                        // x
                        vertexResultData.add(vertexData[index2 * 3])
                        // y
                        vertexResultData.add(vertexData[index2 * 3 + 1])
                        // z
                        vertexResultData.add(vertexData[index2 * 3 + 2])

                        // p3 行号是从 1 开始，所以要 减一
                        val index3 = p3[0].toInt() - 1
                        // x
                        vertexResultData.add(vertexData[index3 * 3])
                        // y
                        vertexResultData.add(vertexData[index3 * 3 + 1])
                        // z
                        vertexResultData.add(vertexData[index3 * 3 + 2])
                    } catch (e: Exception) {
                        e.printStackTrace()
                        isSuccess = false
                        break@loop
                    }
                    // ================================= 顶点 end ===================================

                    // ================================= 纹理 start =================================
                    try {
                        // p1 行号是从 1 开始，所以要 减一
                        val index1 = p1[1].toInt() - 1
                        // s
                        textureResultData.add(textureData[index1 * textureSize])
                        // t
                        textureResultData.add(textureData[index1 * textureSize + 1])
                        if (textureSize > 2) {
                            // p
                            textureResultData.add(textureData[index1 * textureSize + 2])
                        }

                        // p2 行号是从 1 开始，所以要 减一
                        val index2 = p2[1].toInt() - 1
                        // s
                        textureResultData.add(textureData[index2 * textureSize])
                        // t
                        textureResultData.add(textureData[index2 * textureSize + 1])
                        if (textureSize > 2) {
                            // p
                            textureResultData.add(textureData[index2 * textureSize + 2])
                        }

                        // p3 行号是从 1 开始，所以要 减一
                        val index3 = p3[1].toInt() - 1
                        // s
                        textureResultData.add(textureData[index3 * textureSize])
                        // t
                        textureResultData.add(textureData[index3 * textureSize + 1])
                        if (textureSize > 2) {
                            // p
                            textureResultData.add(textureData[index3 * textureSize + 2])
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        isSuccess = false
                        break@loop
                    }
                    // ================================= 纹理 end ===================================

                    // ================================= 法向量 start ===============================
                    try {
                        // p1 行号是从 1 开始，所以要 减一
                        val index1 = p1[2].toInt() - 1
                        // x
                        normalResultData.add(normalData[index1 * 3])
                        // y
                        normalResultData.add(normalData[index1 * 3 + 1])
                        // z
                        normalResultData.add(normalData[index1 * 3 + 2])

                        // p2 行号是从 1 开始，所以要 减一
                        val index2 = p2[2].toInt() - 1
                        // x
                        normalResultData.add(normalData[index2 * 3])
                        // y
                        normalResultData.add(normalData[index2 * 3 + 1])
                        // z
                        normalResultData.add(normalData[index2 * 3 + 2])

                        // p3 行号是从 1 开始，所以要 减一
                        val index3 = p3[2].toInt() - 1
                        // x
                        normalResultData.add(normalData[index3 * 3])
                        // y
                        normalResultData.add(normalData[index3 * 3 + 1])
                        // z
                        normalResultData.add(normalData[index3 * 3 + 2])
                    } catch (e: Exception) {
                        e.printStackTrace()
                        isSuccess = false
                        break@loop
                    }
                    // ================================= 法向量 end =================================
                }
            }
        }

        return if (isSuccess) {  // 成功
            val va = vertexResultData.toFloatArray()
            val ta = textureResultData.toFloatArray()
            val na = normalResultData.toFloatArray()

            MaxObjInfo(
                true,
                vertexResultData.isNotEmpty(),
                textureResultData.isNotEmpty(),
                normalResultData.isNotEmpty(),
                vertexData = va,
                textureData = ta,
                normalData = na
            )
        } else {
            MaxObjInfo(false)
        }

    }

}