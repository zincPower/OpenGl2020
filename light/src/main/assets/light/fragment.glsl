#version 300 es
precision mediump float;

// 是否使用环境光
uniform bool uIsUseAmbient;
// 是否使用散射光
uniform bool uIsUseDiffuse;
// 是否使用镜面光
uniform bool uIsUseSpecular;

// 变换矩阵
uniform mat4 uMMatrixFrag;
// 光源
uniform vec3 uLightLocationFrag;
// 定向光方向
uniform vec3 uLightDirectionFrag;
// 相机📷
uniform vec3 uCameraFrag;
// 是否使用定位光
uniform bool uIsUsePositioningLightFrag;
// 粗糙度
uniform int roughnessFrag;
// 是否使用片元计算
uniform bool isCalculateByFrag;

// 顶点位置
in vec3 aPositionFrag;
// 法向量
in vec3 aNormalFrag;

// 接收从顶点着色器过来的顶点位置
in vec3 vPosition;
// 环境光
in vec4 vAmbient;
// 散射光
in vec4 vDiffuse;
// 镜面光
in vec4 vSpecular;

// 片元色
out vec4 fragColor;

/**
 * 计算片元的颜色
 */
void calColor(inout vec4 color){
    float span = 10.0;

    float x = vPosition.x;
    float y = vPosition.y;
    float z = vPosition.z;

    // 水平坐标
    vec2 vetical = normalize(vec2(x, z));
    // 水平法向量
    vec2 veticalNormal;
    if (z>0.0){
        veticalNormal = vec2(1.0, 0.0);
    } else {
        veticalNormal = vec2(-1.0, 0.0);
    }
    float vCos = dot(vetical, veticalNormal);
    // 水平夹角
    float vAngle = degrees(acos(vCos));
    int col = int(vAngle/span);

    // 垂直坐标
    vec3 horizontal = normalize(vec3(x, y, z));
    // 垂直法向量
    vec3 horizontalNormal;
    if (y>=0.0){
        horizontalNormal = vec3(x, 0.0, z);
    } else {
        horizontalNormal = vec3(-x, 0.0, -z);
    }
    float hCos = dot(horizontal, horizontalNormal);
    // 垂直夹角
    float hAngle = degrees(acos(hCos));
    int row = int(hAngle/span);

    int wColor =int(mod(float(col+row), 2.0));

    if (wColor == 1) {
        color = vec4(0.678, 0.231, 0.129, 1.0);//红色
    } else { //偶数时为白色
        color = vec4(1.0, 1.0, 1.0, 1.0);//白色
    }
}

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
    vec3 normalTarget = aPositionFrag + normal;
    vec3 newNormal = (uMMatrixFrag * vec4(normal, 1)).xyz - (uMMatrixFrag * vec4(aPositionFrag, 1)).xyz;

    // 对法向量规格化
    newNormal=normalize(newNormal);

    // 计算从表面点到光源位置的向量 vp
    vec3 vp= normalize(lightLocation-(uMMatrixFrag*vec4(aPositionFrag, 1)).xyz);

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
    vec3 normalTarget=aPositionFrag+normal;
    vec3 newNormal=(uMMatrixFrag*vec4(normalTarget, 1)).xyz-(uMMatrixFrag*vec4(aPositionFrag, 1)).xyz;
    // 对法向量规格化
    newNormal=normalize(newNormal);

    // 计算从表面点到摄像机的向量
    vec3 eye= normalize(uCameraFrag-(uMMatrixFrag*vec4(aPositionFrag, 1)).xyz);

    // 计算从表面点到光源位置的向量vp
    vec3 vp= normalize(lightLocation-(uMMatrixFrag*vec4(aPositionFrag, 1)).xyz);

    // 求视线与光线的半向量
    vec3 halfVector=normalize(vp+eye);
    // 粗糙度，越小越光滑
    float shininess=float(roughnessFrag);

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
    diffuseLight(normalize(aNormalFrag), diffuseTemp, uLightLocationFrag, lightDiffuse);
    vDiffuse = diffuseTemp;

    // 镜面光强度
    vec4 specularTemp = vec4(0.0, 0.0, 0.0, 0.0);
    specularLight(aNormalFrag, specularTemp, uLightLocationFrag, lightSpecular);
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
    vec3 normalTarget=aPositionFrag+normalize(aNormalFrag);
    vec3 newNormal=(uMMatrixFrag*vec4(normalTarget, 1)).xyz-(uMMatrixFrag*vec4(aPositionFrag, 1)).xyz;
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
    float shininess=float(roughnessFrag);
    //计算从表面点到摄像机的向量
    vec3 eye= normalize(uCameraFrag-(uMMatrixFrag*vec4(aPositionFrag, 1)).xyz);
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
    vec4 orgColor = vec4(0.0, 0.0, 0.0, 0.0);
    calColor(orgColor);

    vec4 finalColor = vec4(0.0, 0.0, 0.0, 0.0);

    vec4 ambientLightPower = vec4(0.15, 0.15, 0.15, 1.0);
    vec4 diffuseLightPower = vec4(0.7, 0.7, 0.7, 1.0);
    vec4 specularLightPower = vec4(0.7, 0.7, 0.7, 1.0);

    vec4 ambientResult, diffuseResult, specularResult;
    if (isCalculateByFrag){
        if (uIsUsePositioningLightFrag){
            pointLight(ambientResult, diffuseResult, specularResult, ambientLightPower, diffuseLightPower, specularLightPower);
        } else {
            directionalLight(ambientResult, diffuseResult, specularResult, uLightDirectionFrag, ambientLightPower, diffuseLightPower, specularLightPower);
        }
    } else {
        ambientResult = vAmbient;
        diffuseResult = vDiffuse;
        specularResult =vSpecular;
    }

    if (uIsUseAmbient){
        finalColor += orgColor * ambientResult;
    }

    if (uIsUseDiffuse){
        finalColor += orgColor * diffuseResult;
    }

    if (uIsUseSpecular){
        finalColor += orgColor * specularResult;
    }

    //根据镜面光最终强度计算片元的最终颜色值
    fragColor=finalColor;
}