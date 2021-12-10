#version 300 es

// 总变换矩阵
uniform mat4 uMVPMatrix;
// 顶点位置
in vec3 aPosition;
// 顶点纹理坐标
in vec2 aTexCoor;

// 传递给片元着色器的变量
out vec2 vTextureCoord;

void main(){
    gl_Position = uMVPMatrix * vec4(aPosition, 1);
    vTextureCoord = aTexCoor;
}