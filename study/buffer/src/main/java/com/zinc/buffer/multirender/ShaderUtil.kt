package com.zinc.buffer.multirender

import android.content.res.Resources
import android.opengl.GLES20
import android.util.Log
import java.io.ByteArrayOutputStream

// 加载顶点 Shader 与片元 Shader 的工具类
object ShaderUtil {
    // 加载制定 shader 的方法
    private fun loadShader(
        shaderType: Int,    //shader的类型  GLES20.GL_VERTEX_SHADER   GLES20.GL_FRAGMENT_SHADER
        source: String?     //shader的脚本字符串
    ): Int {
        // 创建一个新 shader
        var shader = GLES20.glCreateShader(shaderType)
        // 若创建成功则加载 shader
        if (shader != 0) {
            // 加载 shader 的源代码
            GLES20.glShaderSource(shader, source)
            // 编译 shader
            GLES20.glCompileShader(shader)
            // 存放编译成功 shader 数量的数组
            val compiled = IntArray(1)
            // 获取 Shader 的编译情况
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
            if (compiled[0] == 0) { //若编译失败则显示错误日志并删除此shader
                Log.e("ES30_ERROR", "Could not compile shader $shaderType:")
                Log.e("ES30_ERROR", GLES20.glGetShaderInfoLog(shader))
                GLES20.glDeleteShader(shader)
                shader = 0
            }
        }
        return shader
    }

    // 创建 shader 程序的方法
    @JvmStatic
    fun createProgram(
        vertexSource: String?,
        fragmentSource: String?
    ): Int {
        // 加载顶点着色器
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource)
        if (vertexShader == 0) {
            return 0
        }

        // 加载片元着色器
        val pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource)
        if (pixelShader == 0) {
            return 0
        }

        // 创建程序
        var program = GLES20.glCreateProgram()
        // 若程序创建成功则向程序中加入顶点着色器与片元着色器
        if (program != 0) {
            // 向程序中加入顶点着色器
            GLES20.glAttachShader(program, vertexShader)
            checkGlError("glAttachShader")
            // 向程序中加入片元着色器
            GLES20.glAttachShader(program, pixelShader)
            checkGlError("glAttachShader")
            // 链接程序
            GLES20.glLinkProgram(program)
            // 存放链接成功 program 数量的数组
            val linkStatus = IntArray(1)
            // 获取 program 的链接情况
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
            // 若链接失败则报错并删除程序
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e("ES30_ERROR", "Could not link program: ")
                Log.e("ES30_ERROR", GLES20.glGetProgramInfoLog(program))
                GLES20.glDeleteProgram(program)
                program = 0
            }
        }
        return program
    }

    // 检查每一步操作是否有错误的方法
    private fun checkGlError(op: String) {
        var error: Int
        while (GLES20.glGetError().also { error = it } != GLES20.GL_NO_ERROR) {
            Log.e("ES30_ERROR", "$op: glError $error")
            throw RuntimeException("$op: glError $error")
        }
    }

    // 从 sh 脚本中加载 shader 内容的方法
    @JvmStatic
    fun loadFromAssetsFile(
        fname: String?,
        r: Resources
    ): String? {
        var result: String? = null
        try {
            val `in` = r.assets.open(fname!!)
            var ch = 0
            val baos = ByteArrayOutputStream()
            while (`in`.read().also { ch = it } != -1) {
                baos.write(ch)
            }
            val buff = baos.toByteArray()
            baos.close()
            `in`.close()
            result = String(buff)
            result = result.replace("\\r\\n".toRegex(), "\n")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
}