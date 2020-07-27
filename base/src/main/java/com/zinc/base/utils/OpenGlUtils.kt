package com.zinc.base.utils

import android.content.res.Resources
import android.opengl.GLES30
import android.util.Log
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.nio.charset.Charset

/**
 * OpenGl 工具类
 * 内容包含：
 * 1、从 assert 加载内容[loadFromAssetsFile]
 * 2、
 */
object OpenGlUtils {

    /**
     * 从assert加载内容
     *
     * @param fileName 文件名
     * @param resource 资源
     */
    fun loadFromAssetsFile(fileName: String, resource: Resources): String {
        return try {
            val inputStream = resource.assets.open(fileName)
            val byteArrayOutputStream = ByteArrayOutputStream()

            var ch: Int
            while (inputStream.read().also { ch = it } != -1) {
                byteArrayOutputStream.write(ch)
            }

            val buff = byteArrayOutputStream.toByteArray()
            byteArrayOutputStream.close()
            inputStream.close()
            val result = String(buff, Charset.forName("UTF-8"))
            return result.replace("\\r\\n", "\n")
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * 从assert打开文件流
     *
     * @param fileName 文件名
     * @param resource 资源
     */
    fun getAssetsBufferReader(fileName: String, resource: Resources): BufferedReader? {
        return try {
            val inputStream = resource.assets.open(fileName)
            val inputStreamReader = InputStreamReader(inputStream)
            BufferedReader(inputStreamReader)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 创建shader程序的方法
     *
     * @param vertexSource 顶点着色器
     * @param fragmentSource 片元着色器
     * @return 返回0，则失败，否则返回>0
     *
     */
    fun createProgram(vertexSource: String?, fragmentSource: String?): Int {
        // 如果顶点着色器或片元着色器为空则返回0，即失败
        if (vertexSource.isNullOrEmpty() || fragmentSource.isNullOrEmpty()) {
            return 0
        }

        // 加载顶点着色器
        val vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexSource)
        if (vertexShader == 0) {
            return 0
        }

        // 加载片元着色器
        val pixelShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentSource)
        if (pixelShader == 0) {
            return 0
        }

        // 创建程序
        var program = GLES30.glCreateProgram()
        // 若程序创建成功则向程序中加入顶点着色器与片元着色器
        if (program != 0) {
            // 向程序中加入顶点着色器
            GLES30.glAttachShader(program, vertexShader)
            checkGlError("glAttachShader")
            // 向程序中加入片元着色器
            GLES30.glAttachShader(program, pixelShader)
            checkGlError("glAttachShader")
            // 链接程序
            GLES30.glLinkProgram(program)
            // 存放链接成功program数量的数组
            val linkStatus = IntArray(1)
            // 获取program的链接情况
            GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0)
            // 若链接失败则报错并删除程序
            if (linkStatus[0] != GLES30.GL_TRUE) {
                Log.e("ES30_ERROR", "Could not link program: ")
                Log.e("ES30_ERROR", GLES30.glGetProgramInfoLog(program))
                GLES30.glDeleteProgram(program)
                program = 0
            }
        }
        return program
    }

    /**
     * 加载制定shader的方法
     *
     * @param shaderType shader的类型  GLES30.GL_VERTEX_SHADER   GLES30.GL_FRAGMENT_SHADER
     * @param source shader的脚本字符串
     */
    private fun loadShader(
        shaderType: Int,
        source: String
    ): Int {
        // 创建一个新shader
        var shader = GLES30.glCreateShader(shaderType)
        // 若创建成功则加载shader
        if (shader != 0) {
            // 加载shader的源代码
            GLES30.glShaderSource(shader, source)
            // 编译shader
            GLES30.glCompileShader(shader)
            // 存放编译成功shader数量的数组
            val compiled = IntArray(1)
            // 获取Shader的编译情况
            GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0)
            // 若编译失败则显示错误日志并删除此shader
            if (compiled[0] == 0) {
                Log.e("ES30_ERROR", "Could not compile shader $shaderType:")
                Log.e("ES30_ERROR", GLES30.glGetShaderInfoLog(shader))
                GLES30.glDeleteShader(shader)
                shader = 0
            }
        }
        return shader
    }

    /**
     * 检查每一步操作是否有错误的方法
     *
     * @param op 操作的名称
     */
    fun checkGlError(op: String) {
        var error: Int
        while (GLES30.glGetError().also { error = it } != GLES30.GL_NO_ERROR) {
            Log.e("ES30_ERROR", "$op: glError $error")
            throw RuntimeException("$op: glError $error")
        }
    }

}