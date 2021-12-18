package com.jiangpengyong.egl_object.model

import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * @author: jiang peng yong
 * @date: 2021/12/10 4:37 下午
 * @email: 56002982@qq.com
 * @desc: 矩阵类
 * 可以设置的内容：
 * 1、栈管理[mMatrixStack]，栈深可以看 [MATRIX_STACK_DEEP]
 * -- 进栈[pushMatrix]
 * -- 出栈[popMatrix]
 * -- 栈顶[stackTop]
 * 2、设置平移[translate]
 * 3、设置旋转[rotate]
 * 4、设置相机位置[setCamera]
 * 5、设置透视投影[setProjectFrustum]
 * 6、设置正交投影[setProjectOrtho]
 * 7、设置灯光位置[setLightLocation]
 * 8、获取：
 * -- 相机位置[cameraLocationBuffer]
 * -- 物体位置[modelMatrix]
 * -- 光源位置[lightLocationBuffer]
 * 9、获取最后矩阵[getFinalMatrix]
 */
class MatrixState {
    companion object {
        // 矩阵栈深
        private const val MATRIX_STACK_DEEP = 5

        private const val MATRIX_LENGTH = 16
    }

    // 投影矩阵
    private val mProjectMatrix = FloatArray(MATRIX_LENGTH)

    // 摄像机位置朝向矩阵
    private val mCameraMatrix = FloatArray(MATRIX_LENGTH)

    // 获取具体物体的变换矩阵
    var modelMatrix = FloatArray(MATRIX_LENGTH)
        private set

    // ======================================== 光源 start ==========================================
    // 光源位置
    private var lightLocation = floatArrayOf(0f, 0f, 0f)

    // 光源 buffer
    var lightLocationBuffer = ByteBuffer.allocateDirect(3 * 4).apply {
        order(ByteOrder.nativeOrder())
    }.asFloatBuffer()
        private set
    // ======================================== 光源 end ============================================

    // ======================================== 相机 start ==========================================
    // 相机位置
    private var cameraLocation = floatArrayOf(0f, 0f, 0f)

    // 摄像机位置
    private var cameraLocationBuffer = ByteBuffer.allocateDirect(3 * 4).apply {
        order(ByteOrder.nativeOrder())
    }.asFloatBuffer()
        private set
    // ======================================== 相机 end ============================================

    // 保护变换矩阵的栈
    private var mMatrixStack = Array(MATRIX_STACK_DEEP) {
        FloatArray(MATRIX_LENGTH)
    }

    // 栈顶
    var stackTop = -1
        private set

    // 最终矩阵
    private var mMVPMatrix = FloatArray(MATRIX_LENGTH)

    init {
        Matrix.setRotateM(modelMatrix, 0, 0f, 1f, 0f, 0f)
    }

    /**
     * 进栈，保护变换矩阵
     */
    fun pushMatrix() {
        stackTop++
        for (i in 0 until MATRIX_LENGTH) {
            mMatrixStack[stackTop][i] = modelMatrix[i]
        }
    }

    /**
     * 出栈，恢复变换矩阵
     */
    fun popMatrix() {
        for (i in 0 until MATRIX_LENGTH) {
            modelMatrix[i] = mMatrixStack[stackTop][i]
        }
        stackTop--
    }

    /**
     * 重置 ModelMatrix
     */
    fun resetModelMatrix() {
        Matrix.setRotateM(modelMatrix, 0, 0f, 1f, 0f, 0f)
    }

    /**
     * 设置沿xyz轴移动
     */
    fun translate(x: Float, y: Float, z: Float) {
        Matrix.translateM(modelMatrix, 0, x, y, z)
    }

    /**
     * 设置绕xyz轴旋转
     */
    fun rotate(angle: Float, x: Float, y: Float, z: Float) {
        Matrix.rotateM(modelMatrix, 0, angle, x, y, z)
    }

    /**
     * 缩放
     */
    fun scale(x: Float, y: Float, z: Float) {
        Matrix.scaleM(modelMatrix, 0, x, y, z)
    }

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
            mCameraMatrix,
            0,
            cx, cy, cz,
            tx, ty, tz,
            upx, upy, upz
        )

        cameraLocation[0] = cx
        cameraLocation[1] = cy
        cameraLocation[2] = cz

        cameraLocationBuffer.clear()
        cameraLocationBuffer.put(cameraLocation)
        cameraLocationBuffer.position(0)
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
            mProjectMatrix,
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
            mProjectMatrix,
            0,
            left, right,
            bottom, top,
            near, far
        )
    }

    /**
     * 获取具体物体的总变换矩阵
     *
     * 将 mProjMatrix * mVMatrix * mMatrix 放置  mMVPMatrix
     */
    fun getFinalMatrix(): FloatArray {
        Matrix.multiplyMM(
            mMVPMatrix,
            0,
            mProjectMatrix,
            0,
            mCameraMatrix,
            0
        )
        Matrix.multiplyMM(
            mMVPMatrix,
            0,
            mMVPMatrix,
            0,
            modelMatrix,
            0
        )
        return mMVPMatrix
    }

    /**
     * 设置灯光位置的方法
     * @param x 灯源点x
     * @param y 灯源点y
     * @param z 灯源点z
     */
    fun setLightLocation(x: Float, y: Float, z: Float) {
        lightLocationBuffer.clear()
        lightLocation[0] = x
        lightLocation[1] = y
        lightLocation[2] = z
        lightLocationBuffer.put(lightLocation)
        lightLocationBuffer.position(0)
    }
}