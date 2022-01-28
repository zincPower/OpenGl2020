package com.jiangpengyong.egl_object.egl

import android.graphics.SurfaceTexture
import android.opengl.*
import android.view.Surface
import com.jiangpengyong.egl_object.log.Logger

/**
 * @author: jiang peng yong
 * @date: 2021/12/10 4:32 下午
 * @email: 56002982@qq.com
 * @desc: egl 环境辅助
 */
class EglHelper {

    private var mEglDisplay: EGLDisplay? = null
    private var mEglConfig: EGLConfig? = null
    private var mEglSurface: EGLSurface? = null
    private var mEglContext: EGLContext? = null

    private var mRed = 8
    private var mGreen = 8
    private var mBlue = 8
    private var mAlpha = 8
    private var mDepth = 0
    private val mStencilSize = 0
    private var mRenderType = EGL14.EGL_OPENGL_ES2_BIT

    /**
     * 初始化 EGL
     */
    fun init(): Boolean {
        // 1. 连接窗口
        mEglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        if (mEglDisplay == EGL14.EGL_NO_DISPLAY) {
            Logger.e("Unable to connect window.")
            return false
        } else {
            Logger.i("Connect to window success.")
        }

        // 2. 初始化 EGL，同时会获取版本号（主版本、次版本）
        val version = IntArray(2)
        if (!EGL14.eglInitialize(mEglDisplay, version, 0, version, 1)) {
            val errorMsg = when (EGL14.eglGetError()) {
                EGL14.EGL_BAD_DISPLAY -> {
                    "EGL_BAD_DISPLAY"
                }
                EGL14.EGL_NOT_INITIALIZED -> {
                    "EGL_NOT_INITIALIZED"
                }
                else -> {
                    "UN_KNOW"
                }
            }
            Logger.e("Initialize EGL failure.[msg: $errorMsg]")
            return false
        } else {
            Logger.i("Initialize EGL success.version:[${version[0]}.${version[1]}]")
        }

        // 3. 获取表面配置
        val attributes = intArrayOf(
            EGL14.EGL_RED_SIZE, mRed,                // 颜色缓冲区r分量位数（单位：bits）- argb
            EGL14.EGL_GREEN_SIZE, mGreen,            // 颜色缓冲区g分量位数（单位：bits）- argb
            EGL14.EGL_BLUE_SIZE, mBlue,              // 颜色缓冲区b分量位数（单位：bits）- argb
            EGL14.EGL_ALPHA_SIZE, mAlpha,            // 颜色缓冲区a分量位数（单位：bits）- argb
            EGL14.EGL_DEPTH_SIZE, mDepth,            // 深度缓冲区位数
            EGL14.EGL_STENCIL_SIZE, mStencilSize,    // 模板缓冲区位数
            EGL14.EGL_RENDERABLE_TYPE, mRenderType,  // 指定渲染api版本, EGL14.EGL_OPENGL_ES2_BIT
            EGL14.EGL_NONE                          // EGL10.EGL_NONE 为结尾符
        )

        // 4. 从系统中获取对应属性的配置
        val numConfig = IntArray(1)
        val configs: Array<EGLConfig?> = arrayOfNulls(1)
        val chooseResult = EGL14.eglChooseConfig(
            mEglDisplay,                // 窗口
            attributes,                 // 配置属性
            0,           // 配置属性存储偏移量
            configs,                    // 获取的配置属性
            0,             // 获取的配置属性偏移量
            1,               // 获取配置属性量
            numConfig,                  // 想要获取的配置数量
            0           // 想要获取的配置数量的偏移量
        )
        if (!chooseResult) {
            Logger.e("EglChooseConfig obtain failed.")
            val errorMsg = when (EGL14.eglGetError()) {
                EGL14.EGL_NOT_INITIALIZED -> {
                    "EGL_NOT_INITIALIZED"
                }
                EGL14.EGL_BAD_PARAMETER -> {
                    "EGL_BAD_PARAMETER"
                }
                else -> {
                    "UN_KNOW"
                }
            }
            Logger.e("EglChooseConfig obtain failed.[Msg: $errorMsg]")
            return false
        } else {
            Logger.i("EglChooseConfig obtain success.")
        }

        mEglConfig = configs[0]

        // 6. 创建Context
        val contextAttr = intArrayOf(
            EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,      // 使用版本为2
            EGL14.EGL_NONE
        )
        mEglContext = EGL14.eglCreateContext(
            mEglDisplay,
            mEglConfig,
            EGL14.EGL_NO_CONTEXT,                       // 不共享
            contextAttr,
            0
        )
        if (mEglContext == EGL14.EGL_NO_CONTEXT) {
            val error = EGL14.eglGetError()
            if (error == EGL14.EGL_BAD_CONFIG) {
                Logger.e("Context create failure because of the bad config.")
            } else {
                Logger.e("Context create failure.")
            }
            return false
        } else {
            Logger.i("EglContext create success.")
        }
        return true
    }

    fun attachWindow(surface: Surface): Boolean {
        // 5. 创建Surface
        mEglSurface = createWindowSurface(surface)

        if (mEglSurface == null) {
            Logger.e("EglSurface create failure.")
            return false
        } else {
            Logger.i("EglSurface create success.")
        }

        // 7. 绑定EglContext和Surface到显示设备中
        val curRes = EGL14.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)
        if (!curRes) {
            Logger.e("EGLMakeCurrent failed.")
            return false
        } else {
            Logger.i("EglMakeCurrent success.")
        }

        Logger.i("EGL create success.")

        return true
    }

    fun attachPBuffer(width: Int, height: Int): Boolean {
        // 5. 创建Surface
        mEglSurface = createOffscreenSurface(width, height)

        if (mEglSurface == null) {
            Logger.e("EglSurface create failure.")
            return false
        } else {
            Logger.i("EglSurface create success.")
        }

        // 7. 绑定EglContext和Surface到显示设备中
        val curRes = EGL14.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)
        if (!curRes) {
            Logger.e("EGLMakeCurrent failed.")
            return false
        } else {
            Logger.i("EglMakeCurrent success.")
        }

        Logger.i("EGL create success.")

        return true
    }

    fun detachWindow(): Boolean {
        if (mEglSurface == null) {
            Logger.e("Egl surface is null.")
            return false
        }

        if (mEglDisplay == null) {
            Logger.e("Egl display is null.")
            return false
        }

        EGL14.eglMakeCurrent(
            mEglDisplay,
            EGL14.EGL_NO_SURFACE,
            EGL14.EGL_NO_SURFACE,
            EGL14.EGL_NO_CONTEXT
        )
        EGL14.eglDestroySurface(mEglDisplay, mEglSurface)
        return true
    }

    /**
     * 释放
     */
    fun destroy() {
        mEglDisplay?.let { eglDisplay ->
            EGL14.eglMakeCurrent(
                eglDisplay,
                EGL14.EGL_NO_SURFACE,
                EGL14.EGL_NO_SURFACE,
                EGL14.EGL_NO_CONTEXT
            )
            mEglSurface?.let { eglSurface ->
                EGL14.eglDestroySurface(eglDisplay, eglSurface)
            }
            mEglContext?.let { eglContext ->
                EGL14.eglDestroyContext(eglDisplay, eglContext)
            }
            EGL14.eglTerminate(eglDisplay)
        }
    }

    /**
     * 更换缓冲
     */
    fun swapBuffers(): Boolean {
        if (mEglDisplay == null) {
            Logger.i("Egl display is null.Please call init function first.")
            return false
        }

        if (mEglSurface == null) {
            Logger.i("Egl surface is null.Please call init function first.")
            return false
        }

        return EGL14.eglSwapBuffers(mEglDisplay, mEglSurface)
    }

    /**
     * 创建可显示的渲染缓存
     * @param surface 渲染窗口的surface
     */
    fun createWindowSurface(surface: Any): EGLSurface? {
        if (surface !is Surface && surface !is SurfaceTexture) {
            Logger.e("Surface is invalid.")
            return null
        }

        val surfaceAttr = intArrayOf(EGL14.EGL_NONE)

        val eglSurface = EGL14.eglCreateWindowSurface(
            mEglDisplay,
            mEglConfig,
            surface,
            surfaceAttr,
            0
        )

        if (eglSurface == null) {
            Logger.e("Egl surface is null.[WindowSurface]")
            return null
        }

        return eglSurface
    }

    /**
     * 创建离屏渲染缓存
     * @param width 缓存窗口宽
     * @param height 缓存窗口高
     */
    private fun createOffscreenSurface(width: Int, height: Int): EGLSurface? {
        val surfaceAttr = intArrayOf(
            EGL14.EGL_WIDTH, width,
            EGL14.EGL_HEIGHT, height,
            EGL14.EGL_NONE
        )

        val eglSurface = EGL14.eglCreatePbufferSurface(mEglDisplay, mEglConfig, surfaceAttr, 0)

        if (eglSurface == null) {
            Logger.e("Egl surface is null.[Pbuffer]")
            return null
        }

        return eglSurface
    }


    // =========================
    fun bindSurface(eglSurface: EGLSurface): Boolean {
        // 7. 绑定EglContext和Surface到显示设备中
        val curRes = EGL14.eglMakeCurrent(mEglDisplay, eglSurface, eglSurface, mEglContext)
        if (!curRes) {
            Logger.e("EGLMakeCurrent failed.")
            return false
        } else {
            Logger.i("EglMakeCurrent success.")
        }

        Logger.i("EGL create success.")

        return true
    }

    /**
     * 更换缓冲
     */
    fun swapBuffers(eglSurface: EGLSurface): Boolean {
        if (mEglDisplay == null) {
            Logger.i("Egl display is null.Please call init function first.")
            return false
        }

        return EGL14.eglSwapBuffers(mEglDisplay, eglSurface)
    }

}