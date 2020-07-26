package com.zinc.base.utils

import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * 矩阵类
 *
 * 可以设置的内容：
 * 1、栈管理[mStack]，栈深可以看 [MATRIX_STACK_DEEP]
 * -- 进栈[pushMatrix]
 * -- 出栈[popMatrix]
 * -- 栈顶[stackTop]
 * 2、平移[translate]
 * 3、旋转[rotate]
 * 4、相机位置[setCamera]
 * 5、透视投影[setProjectFrustum]
 * 6、正交投影[setProjectOrtho]
 * 7、获取最后矩阵[getFinalMatrix]
 * 8、设置灯光位置[setLightLocation]
 */
object MatrixState {
    private const val MATRIX_STACK_DEEP = 10
    private const val MATRIX_LENGTH = 16

    // 4x4矩阵 投影用
    private val mProjMatrix = FloatArray(MATRIX_LENGTH)

    // 摄像机位置朝向9参数矩阵
    private val mVMatrix = FloatArray(MATRIX_LENGTH)

    // 获取具体物体的变换矩阵
    var mMatrix: FloatArray? = null

    // 定位光光源位置
    var lightLocation = floatArrayOf(0f, 0f, 0f)
    var lightPositionFB: FloatBuffer? = null
    var cameraFB: FloatBuffer? = null

    // 保护变换矩阵的栈
    private var mStack = Array(MATRIX_STACK_DEEP) { FloatArray(MATRIX_LENGTH) }

    // 栈顶
    private var stackTop = -1

    /**
     * 初始化栈
     */
    fun initStack() {
        mMatrix = FloatArray(16)
        Matrix.setRotateM(mMatrix, 0, 0f, 1f, 0f, 0f)
    }

    /**
     * 进栈，保护变换矩阵
     */
    fun pushMatrix() {
        mMatrix?.let {
            stackTop++
            for (i in 0..15) {
                mStack[stackTop][i] = it[i]
            }
        }
    }

    /**
     * 出栈，恢复变换矩阵
     */
    fun popMatrix() {
        mMatrix?.let {
            for (i in 0..15) {
                it[i] = mStack[stackTop][i]
            }
            stackTop--
        }
    }

    /**
     * 设置沿xyz轴移动
     */
    fun translate(x: Float, y: Float, z: Float) {
        Matrix.translateM(mMatrix, 0, x, y, z)
    }

    /**
     * 设置绕xyz轴旋转
     */
    fun rotate(angle: Float, x: Float, y: Float, z: Float) {
        Matrix.rotateM(mMatrix, 0, angle, x, y, z)
    }

    // 摄像机位置
    private var llbb: ByteBuffer = ByteBuffer.allocateDirect(3 * 4)
    private var cameraLocation = FloatArray(3)

    /**
     * 设置摄像机
     *
     * @param cx  摄像机位置x
     * @param cy  摄像机位置y
     * @param cz  摄像机位置z
     * @param tx  摄像机目标点x
     * @param ty  摄像机目标点y
     * @param tz  摄像机目标点z
     * @param upx 摄像机UP向量X分量
     * @param upy 摄像机UP向量Y分量
     * @param upz 摄像机UP向量Z分量
     */
    fun setCamera(
        cx: Float, cy: Float, cz: Float,
        tx: Float, ty: Float, tz: Float,
        upx: Float, upy: Float, upz: Float
    ) {
        Matrix.setLookAtM(
            mVMatrix,
            0,
            cx, cy, cz,
            tx, ty, tz,
            upx, upy, upz
        )

        cameraLocation[0] = cx
        cameraLocation[1] = cy
        cameraLocation[2] = cz

        // 清除摄像机位置缓冲
        llbb.clear()
        // 设置字节顺序
        llbb.order(ByteOrder.nativeOrder())
        // 转换为float型缓冲
        cameraFB = llbb.asFloatBuffer()
        // 将摄像机位置放入缓冲
        cameraFB?.put(cameraLocation)
        // 设置缓冲的起始位置
        cameraFB?.position(0)
    }

    /**
     * 设置透视投影参数
     *
     * @param left near面的left
     * @param right near面的right
     * @param bottom near面的bottom
     * @param top near面的top
     * @param near 相机到near面距离
     * @param far 相机到far面距离
     */
    fun setProjectFrustum(
        left: Float, right: Float,
        bottom: Float, top: Float,
        near: Float, far: Float
    ) {
        Matrix.frustumM(
            mProjMatrix,
            0,
            left, right,
            bottom, top,
            near, far
        )
    }

    /**
     * 设置正交投影参数
     *
     * @param right near面的left
     * @param left near面的right
     * @param bottom near面的bottom
     * @param top near面的top
     * @param near 相机到near面距离
     * @param far 相机到far面距离
     */
    fun setProjectOrtho(
        left: Float, right: Float,
        bottom: Float, top: Float,
        near: Float, far: Float
    ) {
        Matrix.orthoM(
            mProjMatrix,
            0,
            left, right,
            bottom, top,
            near, far
        )
    }


    private var mMVPMatrix = FloatArray(16)

    /**
     * 获取具体物体的总变换矩阵
     *
     * 将 mProjMatrix * mVMatrix * mMatrix 放置  mMVPMatrix
     */
    fun getFinalMatrix(): FloatArray {
        // 将 mVMatrix * mMatrix 存至 mMVPMatrix
        Matrix.multiplyMM(
            mMVPMatrix, 0,
            mVMatrix, 0,
            mMatrix, 0
        )
        // 将 mProjMatrix * mMVPMatrix 存至 mMVPMatrix
        Matrix.multiplyMM(
            mMVPMatrix, 0,
            mProjMatrix, 0,
            mMVPMatrix, 0
        )
        return mMVPMatrix
    }

    var llbbL = ByteBuffer.allocateDirect(3 * 4)

    /**
     * 设置灯光位置的方法
     */
    fun setLightLocation(x: Float, y: Float, z: Float) {
        llbbL.clear()
        lightLocation[0] = x
        lightLocation[1] = y
        lightLocation[2] = z
        llbbL.order(ByteOrder.nativeOrder())
        lightPositionFB = llbbL.asFloatBuffer()
        lightPositionFB?.put(lightLocation)
        lightPositionFB?.position(0)
    }
}