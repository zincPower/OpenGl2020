package com.zinc.base.egl

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import com.zinc.base.IModel
import com.zinc.base.utils.MatrixState
import java.lang.ref.WeakReference
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author: Jiang Pengyong
 * @date: 2020/7/21 11:21 PM
 * @email: 56002982@qq.com
 * @des:
 */
abstract class BaseSceneRender<DATA : IModel>(context: Context) : GLSurfaceView.Renderer {

    protected var mData: DATA? = null

    protected val mContext: WeakReference<Context> = WeakReference(context)

    protected var mSurfaceViewRatio = 0f

    protected var mScreenWidth = 0
    protected var mScreenHeight = 0

    var yAngle: Float = 0f
    var xAngle: Float = 0f
    var zAngle: Float = 0f

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // 设置屏幕背景色RGBA
        GLES30.glClearColor(0f, 0f, 0f, 1.0f);
        // 创建球对象
        mContext.get()?.let {
            initData(it)
        }
        // 打开深度检测
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)
        // 打开背面剪裁
        GLES30.glEnable(GLES30.GL_CULL_FACE)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        // 设置视窗大小及位置
        GLES30.glViewport(0, 0, width, height)

        mScreenWidth = width
        mScreenHeight = height

        // 计算GLSurfaceView的宽高比
        mSurfaceViewRatio = width.toFloat() / height.toFloat()

        if (isUseProjectFrustum()) {
            // 调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(
                -mSurfaceViewRatio, mSurfaceViewRatio,
                -1f, 1f,
                getNear(), getFar()
            )
        } else {
            // 正交投影
            MatrixState.setProjectOrtho(
                -mSurfaceViewRatio, mSurfaceViewRatio,
                -1f, 1f,
                getNear(), getFar()
            )
        }

        // 调用此方法产生摄像机9参数位置矩阵
        MatrixState.setCamera(
            0f, 0f, getSeeZ(),
            0f, 0f, 0f,
            0f, 1.0f, 0.0f
        )

        // 初始化变换矩阵
        MatrixState.initStack()

        // 初始化光源
        MatrixState.setLightLocation(10f, 0f, -10f)
    }

    /**
     * 绘制帧
     */
    override fun onDrawFrame(gl: GL10?) {
        // 清除深度缓冲与颜色缓冲
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT or GLES30.GL_COLOR_BUFFER_BIT)
        MatrixState.pushMatrix()

        preDraw()
        mData?.let {
            drawData(it)
        }
        postDraw()

        MatrixState.popMatrix()
    }

    /**
     * 是否使用透视投影
     */
    open fun isUseProjectFrustum() = true

    /**
     * 绘画之前
     */
    open fun preDraw() {}

    /**
     * 绘画之后
     */
    open fun postDraw() {}

    /**
     * 初始化数据
     * @param context 上下文
     */
    abstract fun initData(context: Context)

    /**
     * 画数据
     * @param data 数据模型
     */
    abstract fun drawData(data: DATA)

    /**
     * 获取视景体的近面距离
     */
    open fun getNear(): Float = 20f

    /**
     * 获取视景体的远面距离
     */
    open fun getFar(): Float = 100f

    /**
     * 获取观察点的z轴距离
     */
    open fun getSeeZ(): Float = 30f
}