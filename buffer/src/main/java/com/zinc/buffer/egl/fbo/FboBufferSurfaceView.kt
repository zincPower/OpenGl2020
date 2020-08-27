package com.zinc.buffer.egl.fbo

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * @author: Jiang Pengyong
 * @date: 2020/8/10 9:28 AM
 * @email: 56002982@qq.com
 * @des: FBO
 */
class FboBufferSurfaceView : GLSurfaceView {

    companion object {
        // 缩放比例
        private const val TOUCH_SCALE_FACTOR = 180.0f / 320
    }

    // 上次的触控位置Y坐标
    private var mPreviousY = 0f

    // 上次的触控位置X坐标
    private var mPreviousX = 0f

    private var mRender: FboBufferRender? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        this.setEGLContextClientVersion(3)
        mRender = FboBufferRender(context ?: return)
        setRenderer(mRender)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    /**
     * 触摸事件回调方法
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {
        val y = e.y
        val x = e.x
        when (e.action) {
            MotionEvent.ACTION_MOVE -> {
                val dy: Float = y - mPreviousY
                val dx: Float = x - mPreviousX

                mRender?.let {
                    // 绕y轴旋转的角度
                    it.yAngle = it.yAngle + dx * TOUCH_SCALE_FACTOR
                    // 绕x轴旋转的角度
                    it.xAngle = it.xAngle + dy * TOUCH_SCALE_FACTOR
                }

            }
        }
        mPreviousY = y
        mPreviousX = x
        return true
    }
}