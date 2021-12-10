#version 300 es
// 总变换矩阵
uniform mat4 uMVPMatrix;

// 顶点位置
in vec3 aPosition;
// 顶点纹理坐标
in vec2 aTexCoor;

// 用于传递给片元着色器的变量
out vec2 vTextureCoord;

void main(){
    // 根据总变换矩阵计算此次绘制此顶点位置
    gl_Position = uMVPMatrix * vec4(aPosition, 1);
    // 将接收的纹理坐标传递给片元着色器
    vTextureCoord = aTexCoor;
}