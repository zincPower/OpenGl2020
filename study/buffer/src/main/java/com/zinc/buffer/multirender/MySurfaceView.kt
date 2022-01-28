package com.zinc.buffer.multirender

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.view.MotionEvent
import com.zinc.buffer.R
import com.zinc.buffer.multirender.LoadUtil.loadFromFile
import com.zinc.buffer.multirender.MatrixState.popMatrix
import com.zinc.buffer.multirender.MatrixState.pushMatrix
import com.zinc.buffer.multirender.MatrixState.rotate
import com.zinc.buffer.multirender.MatrixState.setCamera
import com.zinc.buffer.multirender.MatrixState.setInitStack
import com.zinc.buffer.multirender.MatrixState.setLightLocation
import com.zinc.buffer.multirender.MatrixState.setProjectFrustum
import com.zinc.buffer.multirender.MatrixState.setProjectOrtho
import com.zinc.buffer.multirender.MatrixState.translate
import java.io.IOException
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

@SuppressLint("NewApi")
class MySurfaceView(context: Context?) : GLSurfaceView(context) {
    private val TOUCH_SCALE_FACTOR = 180.0f / 320 //角度缩放比例

    //场景渲染器
    private val mRenderer: SceneRenderer

    //上次的触控位置Y坐标
    private var mPreviousY = 0f

    //上次的触控位置X坐标
    private var mPreviousX = 0f
    var ratio = 0f
    var SCREEN_WIDTH = 0
    var SCREEN_HEIGHT = 0

    //触摸事件回调方法
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {
        val y = e.y
        val x = e.x
        when (e.action) {
            MotionEvent.ACTION_MOVE -> {
                val dy = y - mPreviousY //计算触控点Y位移
                val dx = x - mPreviousX //计算触控点X位移
                mRenderer.yAngle += dx * TOUCH_SCALE_FACTOR //设置沿y轴旋转角度
                mRenderer.xAngle += dy * TOUCH_SCALE_FACTOR //设置沿x轴旋转角度
                requestRender() //重绘画面
            }
        }
        mPreviousY = y //记录触控笔位置
        mPreviousX = x //记录触控笔位置
        return true
    }

    private inner class SceneRenderer : Renderer {
        // 绕 Y 轴旋转的角度
        var yAngle = 0f

        // 绕 X 轴旋转的角度
        var xAngle = 0f

        // 从指定的 obj 文件中加载对象
        var lovo: LoadedObjectVertexNormalTexture? = null

        // 用于存放产生纹理 id 的数组
        var textureIds = IntArray(4)

        // 帧缓冲 id
        var frameBufferId = 0

        // 国画小品的纹理 id
        var textureIdGHXP = 0

        // 矩形绘制对象
        var tr: TextureRect? = null

        // 渲染深度缓冲 id
        var renderDepthBufferId = 0

        @SuppressLint("NewApi")
        fun initFBO(): Boolean {
            val attachments = intArrayOf(
                GLES30.GL_COLOR_ATTACHMENT0,
                GLES30.GL_COLOR_ATTACHMENT1,
                GLES30.GL_COLOR_ATTACHMENT2,
                GLES30.GL_COLOR_ATTACHMENT3
            )
            val tia = IntArray(1) //用于存放产生的帧缓冲id的数组

            //帧缓冲========start==========
            GLES30.glGenFramebuffers(1, tia, 0) //产生一个帧缓冲id
            frameBufferId = tia[0] //将帧缓冲id记录到成员变量中
            //绑定帧缓冲id
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferId)
            //帧缓冲========end==========

            //渲染缓冲=========start============
            GLES30.glGenRenderbuffers(1, tia, 0) //产生一个渲染缓冲id
            renderDepthBufferId = tia[0] //将渲染缓冲id记录到成员变量中
            //绑定指定id的渲染缓冲
            GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, renderDepthBufferId)
            //为渲染缓冲初始化存储
            GLES30.glRenderbufferStorage(
                GLES30.GL_RENDERBUFFER,
                GLES30.GL_DEPTH_COMPONENT16, GEN_TEX_WIDTH, GEN_TEX_HEIGHT
            )
            //设置自定义帧缓冲的深度缓冲附件
            GLES30.glFramebufferRenderbuffer(
                GLES30.GL_FRAMEBUFFER,
                GLES30.GL_DEPTH_ATTACHMENT,  //深度缓冲附件
                GLES30.GL_RENDERBUFFER,  //渲染缓冲
                renderDepthBufferId //渲染深度缓冲id
            )
            //渲染缓冲=========end============
            GLES30.glGenTextures( //产生4个纹理id
                textureIds.size,  //产生的纹理id的数量
                textureIds,  //纹理id的数组
                0 //偏移量
            )
            for (i in attachments.indices) {
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[i]) //绑定纹理id
                GLES30.glTexImage2D( //设置颜色附件纹理图的格式
                    GLES30.GL_TEXTURE_2D,
                    0,  //层次
                    GLES30.GL_RGBA,  //内部格式
                    GEN_TEX_WIDTH,  //宽度
                    GEN_TEX_HEIGHT,  //高度
                    0,  //边界宽度
                    GLES30.GL_RGBA,  //格式
                    GLES30.GL_UNSIGNED_BYTE,  //每个像素数据格式
                    null
                )
                GLES30.glTexParameterf(
                    GLES30.GL_TEXTURE_2D,  //设置MIN采样方式
                    GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST.toFloat()
                )
                GLES30.glTexParameterf(
                    GLES30.GL_TEXTURE_2D,  //设置MAG采样方式
                    GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR.toFloat()
                )
                GLES30.glTexParameterf(
                    GLES30.GL_TEXTURE_2D,  //设置S轴拉伸方式
                    GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE.toFloat()
                )
                GLES30.glTexParameterf(
                    GLES30.GL_TEXTURE_2D,  //设置T轴拉伸方式
                    GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE.toFloat()
                )
                GLES30.glFramebufferTexture2D( //将指定纹理绑定到帧缓冲
                    GLES30.GL_DRAW_FRAMEBUFFER,
                    attachments[i],  //颜色附件
                    GLES30.GL_TEXTURE_2D,
                    textureIds[i],  //纹理id
                    0 //层次
                )
            }
            GLES30.glDrawBuffers(attachments.size, attachments, 0)
            return GLES30.GL_FRAMEBUFFER_COMPLETE == GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER)
        }

        //通过绘制产生纹理
        @SuppressLint("NewApi")
        fun generateTextImage() {
            //设置屏幕背景色RGBA
            GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
            //设置视窗大小及位置
            GLES30.glViewport(0, 0, GEN_TEX_WIDTH, GEN_TEX_HEIGHT)
            //绑定帧缓冲id
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferId) //frameBufferId
            //清除深度缓冲与颜色缓冲
            GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT or GLES30.GL_COLOR_BUFFER_BIT)
            //设置透视投影
            setProjectFrustum(-ratio, ratio, -1f, 1f, 2f, 100f)
            //调用此方法产生摄像机9参数位置矩阵
            setCamera(0f, 0f, 3f, 0f, 0f, -1f, 0f, 1.0f, 0.0f)
            pushMatrix() //保护现场
            translate(0f, -15f, -70f) //坐标系推远
            //绕Y轴、X轴旋转
            rotate(yAngle, 0f, 1f, 0f)
            rotate(xAngle, 1f, 0f, 0f)

            //若加载的物体部位空则绘制物体
            lovo?.drawSelf(textureIdGHXP)
            popMatrix() //恢复现场
        }

        //绘制生成的矩形纹理
        @SuppressLint("NewApi")
        fun drawShadowTexture() {
            //设置屏幕背景色RGBA
            GLES30.glClearColor(0.5f, 0.5f, 0.5f, 1.0f)
            //设置视窗大小及位置
            GLES30.glViewport(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT)
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0) //绑定帧缓冲id
            //清除深度缓冲与颜色缓冲
            GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT or GLES30.GL_COLOR_BUFFER_BIT)
            //设置正交投影
            setProjectOrtho(-ratio, ratio, -1f, 1f, 2f, 100f)
            //调用此方法产生摄像机9参数位置矩阵
            setCamera(0f, 0f, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
            pushMatrix()
            translate(-ratio / 2, 0.5f, 0f)
            tr?.drawSelf(textureIds[0]) //绘制纹理矩形
            popMatrix()
            pushMatrix()
            translate(ratio / 2, 0.5f, 1f)
            tr?.drawSelf(textureIds[1]) //绘制纹理矩形
            popMatrix()
            pushMatrix()
            translate(-ratio / 2, -0.5f, 0f)
            tr?.drawSelf(textureIds[2]) //绘制纹理矩形
            popMatrix()
            pushMatrix()
            translate(ratio / 2, -0.5f, 1f)
            tr?.drawSelf(textureIds[3]) //绘制纹理矩形
            popMatrix()
        }

        override fun onDrawFrame(gl: GL10) {
            generateTextImage() //通过绘制产生矩形纹理
            drawShadowTexture() //绘制矩形纹理
        }

        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
            SCREEN_WIDTH = width
            SCREEN_HEIGHT = height
            ratio = width.toFloat() / height //计算GLSurfaceView的宽高比
            initFBO()
            textureIdGHXP = initTexture(R.drawable.ghxp) //加载国画小品纹理图
            tr = TextureRect(this@MySurfaceView.context, ratio) //创建矩形绘制对象
        }

        override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST)
            //打开背面剪裁
            GLES30.glEnable(GLES30.GL_CULL_FACE)
            //初始化变换矩阵
            setInitStack()
            //初始化光源位置
            setLightLocation(40f, 100f, 20f)
            //加载要绘制的物体
            lovo = loadFromFile(
                "ch_t.obj", this@MySurfaceView.resources,
                this@MySurfaceView
            )
        }
    }

    //textureId
    fun initTexture(drawableId: Int): Int {
        //生成纹理ID
        val textures = IntArray(1)
        GLES30.glGenTextures(
            1,  //产生的纹理id的数量
            textures,  //纹理id的数组
            0 //偏移量
        )
        val textureId = textures[0]
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId)
        GLES30.glTexParameterf(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_MIN_FILTER,
            GLES30.GL_NEAREST.toFloat()
        )
        GLES30.glTexParameterf(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_MAG_FILTER,
            GLES30.GL_LINEAR.toFloat()
        )
        GLES30.glTexParameterf(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_WRAP_S,
            GLES30.GL_REPEAT.toFloat()
        )
        GLES30.glTexParameterf(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_WRAP_T,
            GLES30.GL_REPEAT.toFloat()
        )

        //通过输入流加载图片===============begin===================
        val `is` = this.resources.openRawResource(drawableId)
        val bitmapTmp: Bitmap = try {
            BitmapFactory.decodeStream(`is`)
        } finally {
            try {
                `is`.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        //通过输入流加载图片===============end=====================
        GLUtils.texImage2D(
            GLES30.GL_TEXTURE_2D,  //纹理类型
            0,  //层次
            GLUtils.getInternalFormat(bitmapTmp),  //内部格式
            bitmapTmp,  //纹理图像
            GLUtils.getType(bitmapTmp),  //纹理类型
            0 //纹理边框尺寸
        )
        bitmapTmp.recycle() //纹理加载成功后释放图片
        return textureId //返回纹理id
    }

    companion object {
        const val GEN_TEX_WIDTH = 1024
        const val GEN_TEX_HEIGHT = 512
    }

    init {
        setEGLContextClientVersion(3) //设置使用OpenGL ES 3.0
        mRenderer = SceneRenderer() //创建场景渲染器
        setRenderer(mRenderer) //设置渲染器
        renderMode = RENDERMODE_CONTINUOUSLY //设置渲染模式为主动渲染
    }
}