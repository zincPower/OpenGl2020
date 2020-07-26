#version 300 es
precision mediump float;
// 边长
uniform float uLength;

// 顶点位置
in vec3 vPosition;
// 环境光强度
in vec4 vAmbient;
// 散射光强度
in vec4 vDiffuse;
// 镜面光强度
in vec4 vSpecular;

// 片元色
out vec4 fragColor;

void main(){
    float span = uLength/8.0;

    //每一维在立方体内的行列数
    int i = int(floor(vPosition.x/span));
    int j = int(floor(vPosition.y/span));
    int k = int(floor(vPosition.z/span));

    vec3 color;
    //计算当点应位于白色块还是黑色块中
    int whichColor = int(mod(float(i+j+k), 2.0));
    if (whichColor == 1) { //奇数时为红色
        color = vec3(0.678, 0.231, 0.129);//红色
    }
    else { //偶数时为白色
        color = vec3(1.0, 1.0, 1.0);//白色
    }
    //最终颜色
    vec4 finalColor=vec4(color, 0);
    //给此片元颜色值
    fragColor=finalColor*vAmbient + finalColor*vDiffuse + finalColor*vSpecular;
}