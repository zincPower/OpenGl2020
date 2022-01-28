package com.zinc.buffer.multirender

import android.app.Activity
import com.zinc.buffer.multirender.MySurfaceView
import android.os.Bundle
import android.view.WindowManager
import android.content.pm.ActivityInfo
import android.view.Window

class Sample1_6_Activity : Activity() {
    private var mGLSurfaceView: MySurfaceView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 设置为全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        // 设置为横屏模式
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // 初始化GLSurfaceView
        mGLSurfaceView = MySurfaceView(this)
        setContentView(mGLSurfaceView)
        // 获取焦点
        mGLSurfaceView!!.requestFocus()
        // 设置为可触控
        mGLSurfaceView!!.isFocusableInTouchMode = true
    }

    override fun onResume() {
        super.onResume()
        mGLSurfaceView!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        mGLSurfaceView!!.onPause()
    }
}