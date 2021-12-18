package com.jiangpengyong.egl_object.egl

import android.view.Surface
import com.jiangpengyong.egl_object.log.Logger

/**
 * @author: jiang peng yong
 * @date: 2021/12/10 4:32 下午
 * @email: 56002982@qq.com
 * @desc: egl 生命周期控制
 */
class EglController {
    
    companion object {
        private const val TAG = "EglController"
    }

    // 是否启动
    var isRunning = false
        private set

    // egl 辅助
    private var mEglHelper = EglHelper()

    /**
     * 启动
     */
    fun init() {
        if (isRunning) {
            Logger.e("[$TAG]Current thread had create egl environment.")
            return
        }
        isRunning = mEglHelper.init()
        Logger.i("[$TAG]Egl create result: $isRunning")
    }

    /**
     * 绑定可视 window
     * @param surface 窗口
     * @return true 绑定成功; false 绑定失败
     */
    fun attachWindow(surface: Surface): Boolean {
        if (!isRunning) {
            Logger.e("Current thread didn't create egl environment.")
            return false
        }
        return mEglHelper.attachWindow(surface)
    }

    /**
     * 启动
     * @param width 缓冲区宽
     * @param height 缓冲区高
     * @return 是否启动成功 true：启动成功 false：启动失败
     */
    fun attachPBuffer(width: Int, height: Int): Boolean {
        if (!isRunning) {
            Logger.e("Current thread had create egl environment.")
            return true
        }

        return mEglHelper.attachPBuffer(width = width, height = height)
    }

    /**
     * 解绑可视 window
     * @param surface 窗口
     * @return true 绑定成功; false 绑定失败
     */
    fun detachWindow(): Boolean {
        if (!isRunning) {
            Logger.e("Current thread didn't create egl environment.")
            return false
        }
        return mEglHelper.detachWindow()
    }

    /**
     * 释放
     */
    fun release() {
        if (!isRunning) {
            Logger.e("Current thread didn't create egl environment.")
            return
        }
        isRunning = false
        mEglHelper.destroy()
    }

    /**
     * 更新
     */
    fun update() {
        if (!isRunning) {
            Logger.e("Current thread didn't create egl environment.")
            return
        }
        mEglHelper.swapBuffers()
    }
}