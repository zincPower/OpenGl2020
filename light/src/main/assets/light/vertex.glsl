#version 300 es
// 总变换矩阵
uniform mat4 uMVPMatrix;
// 变换矩阵
uniform mat4 uMMatrix;
// 光源
uniform vec3 uLightLocation;
// 定向光方向
uniform vec3 uLightDirection;
// 相机📷
uniform vec3 uCamera;
// 是否使用定位光
uniform bool uIsUsePositioningLight;
// 粗糙度
uniform int roughness;

// 顶点位置
in vec3 aPosition;
// 法向量
in vec3 aNormal;

//用于传递给片元着色器的顶点位置
out vec3 vPosition;

// 环境光
out vec4 vAmbient;

// 散射光
out vec4 vDiffuse;

// 镜面光
out vec4 vSpecular;

// 顶点位置
out vec3 aPositionFrag;
// 法向量
out vec3 aNormalFrag;

/**
 * 散射光光照计算的方法
 * @param normal 法向量(单位处理)
 * @param diffuse 散射光计算结果
 * @param lightLocation 光源位置
 * @param lightDiffuse 散射光照强度
 */
void diffuseLight (
in vec3 normal,
inout vec4 diffuse,
in vec3 lightLocation,
in vec4 lightDiffuse
){
    // 计算变换后的法向量
    vec3 normalTarget = aPosition + normal;
    vec3 newNormal = (uMMatrix * vec4(normal, 1)).xyz - (uMMatrix * vec4(aPosition, 1)).xyz;

    // 对法向量规格化
    newNormal=normalize(newNormal);

    // 计算从表面点到光源位置的向量 vp
    vec3 vp= normalize(lightLocation-(uMMatrix*vec4(aPosition, 1)).xyz);

    // 法向量与 vp 向量的点积与 0 的最大值
    float nDotViewPosition=max(0.0, dot(newNormal, vp));

    // 计算散射光的最终强度
    diffuse = lightDiffuse * nDotViewPosition;
}

/**
 * 镜面光光照计算方法
 * @param normal 法向量(单位处理)
 * @param specular 镜面光最终强度
 * @param lightLocation 光源位置
 * @param lightSpecular 镜面光强度
 */
void specularLight(
in vec3 normal,
inout vec4 specular,
in vec3 lightLocation,
in vec4 lightSpecular
){
    // 计算变换后的法向量
    vec3 normalTarget=aPosition+normal;
    vec3 newNormal=(uMMatrix*vec4(normalTarget, 1)).xyz-(uMMatrix*vec4(aPosition, 1)).xyz;
    // 对法向量规格化
    newNormal=normalize(newNormal);

    // 计算从表面点到摄像机的向量
    vec3 eye= normalize(uCamera-(uMMatrix*vec4(aPosition, 1)).xyz);

    // 计算从表面点到光源位置的向量vp
    vec3 vp= normalize(lightLocation-(uMMatrix*vec4(aPosition, 1)).xyz);

    // 求视线与光线的半向量
    vec3 halfVector=normalize(vp+eye);
    // 粗糙度，越小越光滑
    float shininess=float(roughness);

    // 法线与半向量的点积
    float nDotViewHalfVector=dot(newNormal, halfVector);
    // 镜面反射光强度因子
    float powerFactor=max(0.0, pow(nDotViewHalfVector, shininess));
    // 最终的镜面光强度
    specular=lightSpecular*powerFactor;
}

/**
 * 定位光
 * @param vAmbient 环境光的最终强度
 * @param vDiffuse 散射光的最终强度
 * @param vSpecular 镜面光的最终强度
 * @param lightAmbient 环境光的强度
 * @param lightDiffuse 散射光的强度
 * @param lightSpecular 镜面光的强度
 */
void pointLight(
inout vec4 vAmbient,
inout vec4 vDiffuse,
inout vec4 vSpecular,
in vec4 lightAmbient,
in vec4 lightDiffuse,
in vec4 lightSpecular
){
    // 环境光强度
    vAmbient =lightAmbient;

    // 散射光强度
    vec4 diffuseTemp=vec4(0.0, 0.0, 0.0, 0.0);
    diffuseLight(normalize(aNormal), diffuseTemp, uLightLocation, lightDiffuse);
    vDiffuse = diffuseTemp;

    // 镜面光强度
    vec4 specularTemp = vec4(0.0, 0.0, 0.0, 0.0);
    specularLight(aNormal, specularTemp, uLightLocation, lightSpecular);
    vSpecular = specularTemp;
}

/**
 * 定向光
 * @param vAmbient 环境光的最终强度
 * @param vDiffuse 散射光的最终强度
 * @param vSpecular 镜面光的最终强度
 * @param lightDirection 定向光的方向
 * @param lightAmbient 环境光的强度
 * @param lightDiffuse 散射光的强度
 * @param lightSpecular 镜面光的强度
 */
void directionalLight(
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
    vec3 normalTarget=aPosition+normalize(aNormal);
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
    float shininess=float(roughness);
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
    // 根据总变换矩阵计算此次绘制此顶点位置
    gl_Position = uMVPMatrix * vec4(aPosition, 1);
    // 将顶点的位置传给片元着色器
    vPosition = aPosition;

    aPositionFrag = aPosition;
    aNormalFrag = aNormal;

    vec4 ambientLightPower = vec4(0.15, 0.15, 0.15, 1.0);
    vec4 diffuseLightPower = vec4(0.7, 0.7, 0.7, 1.0);
    vec4 specularLightPower = vec4(0.7, 0.7, 0.7, 1.0);

    if (uIsUsePositioningLight){
        pointLight(vAmbient, vDiffuse, vSpecular, ambientLightPower, diffuseLightPower, specularLightPower);
    } else {
        directionalLight(vAmbient, vDiffuse, vSpecular, uLightDirection, ambientLightPower, diffuseLightPower, specularLightPower);
    }

}