#version 300 es
// 总变换矩阵
uniform mat4 uMVPMatrix;
// 变换矩阵
uniform mat4 uMMatrix;
// 光源位置
uniform vec3 uLightLocation;
// 摄像机位置
uniform vec3 uCamera;

// 顶点位置
in vec3 aPosition;
// 顶点法向量
in vec3 aNormal;
// 顶点纹理坐标
in vec2 aTexCoor;


out vec4 ambient;
out vec4 diffuse;
out vec4 specular;
out vec2 vTextureCoord;

/**
 * 定向光
 * @param normal 法向量
 * @param vAmbient 环境光的最终强度
 * @param vDiffuse 散射光的最终强度
 * @param vSpecular 镜面光的最终强度
 * @param lightDirection 定向光的方向
 * @param lightAmbient 环境光的强度
 * @param lightDiffuse 散射光的强度
 * @param lightSpecular 镜面光的强度
 */
void directionalLight(
in vec3 normal,
inout vec4 vAmbient,
inout vec4 vDiffuse,
inout vec4 vSpecular,
in vec3 lightDirection,
in vec4 lightAmbient,
in vec4 lightDiffuse,
in vec4 lightSpecular
){
    //--------------------------------------环境光start----------------------------------------
    vAmbient=lightAmbient;
    //--------------------------------------环境光end----------------------------------------

    // 计算变换后的法向量
    vec3 normalTarget=aPosition+normalize(normal);
    vec3 newNormal=(uMMatrix*vec4(normalTarget, 1)).xyz-(uMMatrix*vec4(aPosition, 1)).xyz;
    //对法向量规格化
    newNormal=normalize(newNormal);
    //规格化定向光方向向量
    vec3 vp= normalize(lightDirection);

    //--------------------------------------散射光start----------------------------------------
    float nDotViewPosition=max(0.0, dot(newNormal, vp));//求法向量与vp的点积与0的最大值
    vDiffuse=lightDiffuse*nDotViewPosition;//计算散射光的最终强度
    //--------------------------------------散射光end----------------------------------------

    //--------------------------------------镜面光start----------------------------------------
    //粗糙度，越小越光滑
    float shininess=float(50);
    //计算从表面点到摄像机的向量
    vec3 eye= normalize(uCamera-(uMMatrix*vec4(aPosition, 1)).xyz);
    //求视线与光线的半向量
    vec3 halfVector=normalize(vp+eye);
    //法线与半向量的点积
    float nDotViewHalfVector=dot(newNormal, halfVector);
    //镜面反射光强度因子
    float powerFactor=max(0.0, pow(nDotViewHalfVector, shininess));

    //计算镜面光的最终强度
    vSpecular=lightSpecular*powerFactor;
    //--------------------------------------镜面光end----------------------------------------
}

void main(){
    gl_Position = uMVPMatrix * vec4(aPosition, 1);

    vec4 ambientLightPower = vec4(0.15, 0.15, 0.15, 1.0);
    vec4 diffuseLightPower = vec4(0.7, 0.7, 0.7, 1.0);
    vec4 specularLightPower = vec4(0.7, 0.7, 0.7, 1.0);

    vec4 ambientTemp, diffuseTemp, specularTemp;
    directionalLight(aNormal, ambientTemp, diffuseTemp, specularTemp, uLightLocation, ambientLightPower, diffuseLightPower, specularLightPower);

    ambient=ambientTemp;
    diffuse=diffuseTemp;
    specular=specularTemp;
    vTextureCoord = aTexCoor;
}                      