#version 300 es
precision mediump float;
// 纹理内容数据
uniform sampler2D sTexture;
// 接收从顶点着色器过来的参数
in vec2 vTextureCoord;
in vec4 vambient;
in vec4 vdiffuse;
in vec4 vspecular;

uniform int uDrawType;

out vec4 fragColor;

void main(){
    vec4 finalColor;
    if (uDrawType == 4){
        finalColor  = texture(sTexture, vTextureCoord);
    } else {
        finalColor= vec4(1.0, 1.0, 1.0, 1.0);
    }

    fragColor = finalColor*vambient+finalColor*vspecular+finalColor*vdiffuse;
}