#version 330 compatibility
layout(location = 0) in vec3 position;
layout(location = 1) in float values;
layout(location = 2) in float visibility;
layout(location = 3) in vec3 normal;
uniform float maxValue;
uniform vec3 LightPosition;
uniform float lightImpact;
uniform mat4 mMat;
uniform mat4 vMat;
uniform mat4 pMat;

out float valueTranslated;
out float visibilityTranslated;
out gl_PerVertex {
  vec4 gl_Position;
  float gl_ClipDistance[1];
};
out float lightIntensity;
out vec3 gNormal;

void main()
{
	gl_Position = vec4(position.xyz, 1.0);
	gNormal = (vMat*mMat*vec4(normal,0.0)).xyz;
	
	vec3 globalCoordPos = (mMat * vec4(position.xyz, 1.0)).xyz;
	vec3 localCoordPos = (vMat * mMat * vec4(position.xyz, 1.0)).xyz;
	vec3 lightpos = (vMat * vec4(LightPosition.xyz,1.0)).xyz;
	
	// Lighting part
	vec4 diffuse;
	vec3 lightVector = lightpos - localCoordPos;
	float dist = length(lightVector);
	//float cosTheta = clamp(dot(normalize(gNormal),normalize(lightVector)), 0, 1);
	float cosTheta = clamp(dot(normalize(gNormal)*lightImpact,normalize(lightVector)), 0, 1);
	//lightIntensity = cosTheta/(dist*dist);
	lightIntensity = cosTheta;
	
	float scaling = 65535.0/maxValue;
	//float scaling = 65535.0/4095.0;
	valueTranslated = scaling * values;
	visibilityTranslated = visibility;
}

