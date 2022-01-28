package com.zinc.buffer.multirender

import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.*

//存储系统矩阵状态的类
object MatrixState {
    private val mProjMatrix = FloatArray(16) //4x4矩阵 投影用
    private val mVMatrix = FloatArray(16) //摄像机位置朝向9参数矩阵

    //获取具体物体的变换矩阵
    var mMatrix: FloatArray = FloatArray(16)

    var lightLocation = floatArrayOf(0f, 0f, 0f) //定位光光源位置

    var cameraFB: FloatBuffer? = null

    var lightPositionFB: FloatBuffer? = null
    var mStack = Stack<FloatArray>() //保护变换矩阵的栈

    // 获取不变换初始矩阵
    @JvmStatic
    fun setInitStack() {
        mMatrix = FloatArray(16)
        Matrix.setRotateM(mMatrix, 0, 0f, 1f, 0f, 0f)
    }

    // 保护变换矩阵
    @JvmStatic
    fun pushMatrix() {
        mStack.push(mMatrix.clone())
    }

    // 恢复变换矩阵
    @JvmStatic
    fun popMatrix() {
        mMatrix = mStack.pop()
    }

    // 设置沿 xyz 轴移动
    @JvmStatic
    fun translate(x: Float, y: Float, z: Float) {
        Matrix.translateM(mMatrix, 0, x, y, z)
    }

    // 设置绕 xyz 轴移动
    @JvmStatic
    fun rotate(angle: Float, x: Float, y: Float, z: Float) {
        Matrix.rotateM(mMatrix, 0, angle, x, y, z)
    }

    // 设置摄像机
    @JvmStatic
    fun setCamera(
        cx: Float,  //摄像机位置x
        cy: Float,  //摄像机位置y
        cz: Float,  //摄像机位置z
        tx: Float,  //摄像机目标点x
        ty: Float,  //摄像机目标点y
        tz: Float,  //摄像机目标点z
        upx: Float,  //摄像机UP向量X分量
        upy: Float,  //摄像机UP向量Y分量
        upz: Float //摄像机UP向量Z分量
    ) {
        Matrix.setLookAtM(
            mVMatrix,
            0,
            cx,
            cy,
            cz,
            tx,
            ty,
            tz,
            upx,
            upy,
            upz
        )
        val cameraLocation = FloatArray(3) //摄像机位置
        cameraLocation[0] = cx
        cameraLocation[1] = cy
        cameraLocation[2] = cz
        val llbb = ByteBuffer.allocateDirect(3 * 4)
        llbb.order(ByteOrder.nativeOrder()) //设置字节顺序
        cameraFB = llbb.asFloatBuffer()
        cameraFB?.put(cameraLocation)
        cameraFB?.position(0)
    }

    // 设置透视投影参数
    @JvmStatic
    fun setProjectFrustum(
        left: Float,  // near 面的 left
        right: Float,  // near 面的 right
        bottom: Float,  // near 面的 bottom
        top: Float,  // near 面的 top
        near: Float,  // near 面距离
        far: Float // far 面距离
    ) {
        Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far)
    }

    // 设置正交投影参数
    @JvmStatic
    fun setProjectOrtho(
        left: Float,  // near 面的 left
        right: Float,  // near 面的 right
        bottom: Float,  // near 面的 bottom
        top: Float,  // near 面的 top
        near: Float,  // near 面距离
        far: Float // far 面距离
    ) {
        Matrix.orthoM(mProjMatrix, 0, left, right, bottom, top, near, far)
    }

    // 获取具体物体的总变换矩阵
    @JvmStatic
    val finalMatrix: FloatArray
        get() {
            val mMVPMatrix = FloatArray(16)
            Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMatrix, 0)
            Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0)
            return mMVPMatrix
        }

    //设置灯光位置的方法
    @JvmStatic
    fun setLightLocation(x: Float, y: Float, z: Float) {
        lightLocation[0] = x
        lightLocation[1] = y
        lightLocation[2] = z
        val llbb = ByteBuffer.allocateDirect(3 * 4)
        llbb.order(ByteOrder.nativeOrder()) //设置字节顺序
        lightPositionFB = llbb.asFloatBuffer()
        lightPositionFB?.put(lightLocation)
        lightPositionFB?.position(0)
    }
}