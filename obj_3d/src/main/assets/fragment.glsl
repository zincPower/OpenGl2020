#version 300 es
precision mediump float;

uniform sampler2D sTexture;

in vec4 ambient;
in vec4 diffuse;
in vec4 specular;
in vec2 vTextureCoord;

out vec4 fragColor;

void main(){
    vec4 finalColor=texture(sTexture, vTextureCoord);
    fragColor = finalColor*ambient+finalColor*specular+finalColor*diffuse;
}   