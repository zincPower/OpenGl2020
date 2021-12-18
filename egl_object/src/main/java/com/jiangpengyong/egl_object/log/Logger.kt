package com.jiangpengyong.egl_object.log

import android.util.Log
import com.jiangpengyong.egl_object.BuildConfig

/**
 * @author: Jiang Pengyong
 * @date: 2020/8/20 7:02 PM
 * @email: 56002982@qq.com
 * @des: 日志打印
 */
object Logger {

    private const val TAG = "Render"
    private var SHOW = BuildConfig.DEBUG

    fun i(msg: String) {
        if (!SHOW) {
            return
        }
        Log.i(TAG, "Thread: ${Thread.currentThread()}. $msg")
    }

    fun d(msg: String) {
        if (!SHOW) {
            return
        }
        Log.d(TAG, "Thread: ${Thread.currentThread()}. $msg")
    }

    fun w(msg: String) {
        if (!SHOW) {
            return
        }
        Log.w(TAG, "Thread: ${Thread.currentThread()}. $msg")
    }

    fun e(msg: String) {
        if (!SHOW) {
            return
        }
        Log.e(TAG, "Thread: ${Thread.currentThread()}. $msg")
    }

    fun e(e: Throwable, TAG: String = Logger.TAG) {
        if (!SHOW) {
            return
        }
        Log.e(TAG, "Thread: ${Thread.currentThread()}. ${e.message}", e)
    }
}