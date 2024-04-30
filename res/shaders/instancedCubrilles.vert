#version 330 compatibility
layout(location = 0) in vec3 position;
layout(location = 1) in float values;
layout(location = 2) in float visibility;
uniform float maxValue; 
uniform float lowCut, highCut;

out float valueTranslated;
out float visibilityTranslated;
out gl_PerVertex {
  vec4 gl_Position;
  float gl_ClipDistance[1];
};

void main()
{
	gl_Position = vec4(position.xyz, 1.0);
	float scaling = 65535.0/maxValue;
	valueTranslated = scaling * values;
	visibilityTranslated = visibility * min(((step(lowCut/maxValue, valueTranslated) * step(valueTranslated, highCut/maxValue))+0.0075),1.0);
}