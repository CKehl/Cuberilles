#version 330 compatibility
layout(location = 0) in vec3 position;
layout(location = 1) in float values;
layout(location = 2) in float visibility;
layout(location = 3) in vec3 normal;
uniform float maxValue; 

out float valueTranslated;
out float visibilityTranslated;
out gl_PerVertex {
  vec4 gl_Position;
  float gl_ClipDistance[1];
};
out vec3 gNormal;

void main()
{
	gl_Position = vec4(position.xyz, 1.0);
	gNormal = normal;
	float scaling = 65535.0/maxValue;
	//float scaling = 65535.0/4095.0;
	valueTranslated = scaling * values;
	visibilityTranslated = visibility;
}