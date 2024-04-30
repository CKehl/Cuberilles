#version 330 compatibility
layout(location = 0) in vec3 position;
layout(location = 1) in float values;
layout(location = 2) in float visibility;
uniform float maxValue;
uniform sampler3D normalTexture;
uniform vec3 dimensions;
uniform vec3 spacing; 

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
	vec3 texIndex = vec3(position.x/dimensions.x, position.y/dimensions.y, position.z/dimensions.z);
	gNormal = (texture3D(normalTexture, texIndex).rgb-vec3(0.5))*2.0;
	float scaling = 65535.0/maxValue;
	valueTranslated = scaling * values;
	visibilityTranslated = visibility;
}