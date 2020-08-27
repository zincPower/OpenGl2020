package com.zinc.buffer.egl.fbo

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import com.zinc.base.control.StretchMode
import com.zinc.base.utils.Load3DMaxObjUtils
import com.zinc.base.utils.MatrixState
import com.zinc.base.utils.TextureUtils
import com.zinc.base.utils.buffer.FboUtils
import com.zinc.buffer.R
import com.zinc.buffer.model.fbo.TextureModel
import com.zinc.buffer.model.vbo.VboModel
import java.lang.ref.WeakReference
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author: Jiang Pengyong
 * @date: 2020/8/10 9:27 AM
 * @email: 56002982@qq.com
 * @des: FBO 渲染
 */
class FboBufferRender(context: Context) : GLSurfaceView.Renderer {

    private var mFboModel: VboModel? = null
    private var mRectTexture: TextureModel? = null

    private var mTeaPotTextureId = -1

    private var mFrameBufferId = -1
    private var mRenderBufferId = -1
    private var mTextureId = -1

    private var mTextureWidth: Int = 1080
    private var mTextureHeight: Int = 1080

    private val mContext: WeakReference<Context> = WeakReference(context)

    private var mSurfaceViewRatio = 0f

    private var mScreenWidth = 0
    private var mScreenHeight = 0

    var yAngle: Float = 0f
    var xAngle: Float = 0f
    var zAngle: Float = 0f

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // 设置屏幕背景色RGBA
        GLES30.glClearColor(0f, 0f, 0f, 1.0f);
        // 打开深度检测
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)
        // 打开背面剪裁
        GLES30.glEnable(GLES30.GL_CULL_FACE)
        MatrixState.initStack()
        MatrixState.setLightLocation(40f, 100f, 20f)
        mContext.get()?.let {
            val maxObjInfo = Load3DMaxObjUtils.load("teapot.obj", it.resources, textureFlip = true)
            mFboModel = VboModel(it, maxObjInfo)
        }

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        mScreenWidth = width
        mScreenHeight = height
        // 计算GLSurfaceView的宽高比
        mSurfaceViewRatio = width.toFloat() / height.toFloat()
        initFBO(mTextureWidth, mTextureHeight)

        mContext.get()?.let {
            // 加载纹理
            mTeaPotTextureId = TextureUtils.obtainTexture(it, R.drawable.teapot, StretchMode.REPEAT)
            mRectTexture = TextureModel(it, mSurfaceViewRatio)
        }
    }

    /**
     * 绘制帧
     */
    override fun onDrawFrame(gl: GL10?) {
        generateTextureImage()
        drawTextureModel()
    }

    /**
     * 初始化 FBO
     */
    private fun initFBO(width: Int, height: Int) {
        // 生成纹理
        mTextureId = FboUtils.createTexture(width, height)

        // 生成并绑定 FrameBufferId
        mFrameBufferId = FboUtils.createFrameBuffer()
        FboUtils.bindFrameBuffer(mFrameBufferId)

        // 生成并绑定 RenderBufferId
        mRenderBufferId = FboUtils.createRenderBuffer()
        FboUtils.bindRenderBuffer(mRenderBufferId, width, height)

        // 将纹理绑定进 FrameBuffer 作为颜色附件
        FboUtils.bindTextureToFrameBuffer(mTextureId, mFrameBufferId)

        // 将 RenderBuffer 绑定进 FrameBuffer 作为深度附件
        FboUtils.bindRenderBufferToFrameBuffer(mRenderBufferId, mFrameBufferId)
    }

    /**
     * 创建纹理rect
     */
    private fun generateTextureImage() {
        // 设置视窗大小及位置
        GLES30.glViewport(0, 0, mTextureWidth, mTextureHeight)
        // 绑定帧缓冲id
        FboUtils.bindFrameBuffer(mFrameBufferId)
        // 清除深度缓冲与颜色缓冲
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT or GLES30.GL_COLOR_BUFFER_BIT)

        //设置透视投影
        MatrixState.setProjectFrustum(
            -mSurfaceViewRatio, mSurfaceViewRatio,
            -1f, 1f,
            2f, 100f
        )
        MatrixState.setCamera(
            0f, 0f, 0f,
            0f, 0f, -1f,
            0f, 1.0f, 0.0f
        )
        MatrixState.pushMatrix()
        MatrixState.translate(0f, -16f, -80f)
        MatrixState.rotate(yAngle, 0f, 1f, 0f)
        MatrixState.rotate(xAngle, 1f, 0f, 0f)
        mFboModel?.draw(mTeaPotTextureId)
        MatrixState.popMatrix()
    }

    private fun drawTextureModel() {
        //设置视窗大小及位置
        GLES30.glViewport(0, 0, mScreenWidth, mScreenHeight)
        FboUtils.unbindFrameBuffer()

        //清除深度缓冲与颜色缓冲
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT or GLES30.GL_COLOR_BUFFER_BIT)
        //设置正交投影
        MatrixState.setProjectOrtho(
            -mSurfaceViewRatio, mSurfaceViewRatio,
            -1f, 1f,
            2f, 100f
        )
        //调用此方法产生摄像机9参数位置矩阵
        MatrixState.setCamera(
            0f, 0f, 3f,
            0f, 0f, 0f,
            0f, 1.0f, 0.0f
        )
        MatrixState.pushMatrix()
        mRectTexture?.draw(mTextureId)
        MatrixState.popMatrix()
    }

}