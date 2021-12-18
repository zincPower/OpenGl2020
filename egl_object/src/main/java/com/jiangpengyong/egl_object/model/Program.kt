package com.jiangpengyong.egl_object.model

import android.opengl.GLES20
import com.jiangpengyong.egl_object.log.Logger
import com.jiangpengyong.egl_object.model.Constant.NOT_INIT

/**
 * @author: Jiang Pengyong
 * @date: 2020/9/28 4:58 PM
 * @email: 56002982@qq.com
 * @des: 程序
 */
class Program {

    private var mProgramId: Int = NOT_INIT

    private var mVertexShader: Int = NOT_INIT
    private var mFragmentShader: Int = NOT_INIT

    /**
     * 是否初始化了
     * @return true：已经初始化，false：未初始化
     */
    fun isInit(): Boolean {
        return mProgramId != NOT_INIT
    }

    /**
     * 创建shader程序的方法
     */
    fun createProgram(
        vertexShaderSource: String,
        fragmentShaderSource: String
    ) {
        if (isInit()) {
            Logger.e("Program had init.[$mProgramId]")
            return
        }

        // 如果顶点着色器或片元着色器为空则返回0，即失败
        if (vertexShaderSource.isEmpty() || fragmentShaderSource.isEmpty()) {
            return
        }

        // 加载顶点着色器
        mVertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderSource)
        if (mVertexShader == NOT_INIT) {
            Logger.e("Vertex shader loader failure.[$mVertexShader]")
            return
        }

        // 加载片元着色器
        mFragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderSource)
        if (mFragmentShader == NOT_INIT) {
            Logger.e("Fragment shader loader failure.[$mVertexShader]")
            return
        }

        // 创建程序
        mProgramId = GLES20.glCreateProgram()
        // 若程序创建成功则向程序中加入顶点着色器与片元着色器
        if (mProgramId != NOT_INIT) {
            // 向程序中加入顶点着色器
            GLES20.glAttachShader(mProgramId, mVertexShader)
            // 向程序中加入片元着色器
            GLES20.glAttachShader(mProgramId, mFragmentShader)
            // 链接程序
            GLES20.glLinkProgram(mProgramId)
            // 存放链接成功program数量的数组
            val linkStatus = IntArray(1)
            // 获取program的链接情况
            GLES20.glGetProgramiv(mProgramId, GLES20.GL_LINK_STATUS, linkStatus, 0)
            // 若链接失败则报错并删除程序
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Logger.e("Could not link program: \n ${GLES20.glGetProgramInfoLog(mProgramId)}")
                GLES20.glDeleteProgram(mProgramId)
                mProgramId = NOT_INIT
            }
        }
    }

    fun getUniformLocation(attributeName: String): Int {
        if (!isInit()) {
            Logger.e("Program id is invalid.Please call createProgram function first.[$mProgramId]")
            return NOT_INIT
        }
        return GLES20.glGetUniformLocation(mProgramId, attributeName)
    }

    fun getAttribLocation(attributeName: String): Int {
        if (!isInit()) {
            Logger.e("Program id is invalid.Please call createProgram function first.[$mProgramId]")
            return NOT_INIT
        }
        return GLES20.glGetAttribLocation(mProgramId, attributeName)
    }

    /**
     * 使用程序
     */
    fun useProgram() {
        if (!isInit()) {
            Logger.e("Program id is invalid.Please call createProgram function first.[$mProgramId]")
            return
        }
        GLES20.glUseProgram(mProgramId)
    }

    /**
     * 释放
     */
    fun release() {
        if (!isInit()) {
            return
        }

        if (mVertexShader != NOT_INIT) {
            GLES20.glDetachShader(mProgramId, mVertexShader)
            mVertexShader = NOT_INIT
        }
        if (mFragmentShader != NOT_INIT) {
            GLES20.glDetachShader(mProgramId, mFragmentShader)
            mFragmentShader = NOT_INIT
        }
        GLES20.glUseProgram(0)
        GLES20.glDeleteProgram(mProgramId)
        mProgramId = NOT_INIT
    }

    /**
     * 加载制定shader的方法
     *
     * @param shaderType shader的类型  GLES20.GL_VERTEX_SHADER   GLES20.GL_FRAGMENT_SHADER
     * @param source shader的脚本字符串
     */
    private fun loadShader(
        shaderType: Int,
        source: String
    ): Int {
        // 创建一个新shader
        var shader = GLES20.glCreateShader(shaderType)
        // 若创建成功则加载shader
        if (shader != 0) {
            // 加载shader的源代码
            GLES20.glShaderSource(shader, source)
            // 编译shader
            GLES20.glCompileShader(shader)
            // 存放编译成功shader数量的数组
            val compiled = IntArray(1)
            // 获取Shader的编译情况
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
            // 若编译失败则显示错误日志并删除此shader
            if (compiled[0] == 0) {
                Logger.e("Could not compile shader $shaderType:\n ${GLES20.glGetShaderInfoLog(shader)}")
                GLES20.glDeleteShader(shader)
                shader = 0
            }
        }
        return shader
    }

}