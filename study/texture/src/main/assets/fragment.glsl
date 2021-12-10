#version 300 es
precision mediump float;

// 接收从顶点着色器过来的参数
in vec2 vTextureCoord;

// 片元
out vec4 fragColor;

// 纹理内容数据
uniform sampler2D sTexture;

void main(){
    // 给此片元从纹理中采样出颜色值
    fragColor = texture(sTexture, vTextureCoord);
}