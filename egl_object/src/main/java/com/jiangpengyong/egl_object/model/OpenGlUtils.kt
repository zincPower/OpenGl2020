package com.jiangpengyong.egl_object.model

import android.app.ActivityManager
import android.content.Context
import android.opengl.GLES20
import com.jiangpengyong.egl_object.log.Logger

/**
 * @author: Jiang Pengyong
 * @date: 2020/8/2 1:33 PM
 * @email: 56002982@qq.com
 * @des:  OpenGl 工具类
 */
object OpenGlUtils {

    /**
     * 检查每一步操作是否有错误的方法
     * @param op 操作的名称
     */
    fun checkGlError(op: String) {
        if (!Constant.CHECK_ERROR) {
            return
        }

        var error: Int
        while (GLES20.glGetError().also { error = it } != GLES20.GL_NO_ERROR) {
            Logger.e("$op: glError $error")
        }
    }

    /**
     * 获取egl的版本
     * @return 支持的版本，-1表示获取不到
     */
    fun checkSupportVersion(context: Context): Int {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val deviceConfigurationInfo = activityManager?.deviceConfigurationInfo
        return deviceConfigurationInfo?.reqGlEsVersion ?: Constant.NOT_INIT
    }
}