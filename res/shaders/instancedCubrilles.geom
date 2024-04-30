#version 330 compatibility
layout(points) in;
layout(triangle_strip, max_vertices=36) out;

vec4 cVert;
vec4 clippedVertex;
in float valueTranslated[];
in float visibilityTranslated[];

in gl_PerVertex {
  vec4 gl_Position;
  float gl_ClipDistance[1];
} gl_in[];
out gl_PerVertex {
  vec4 gl_Position;
  float gl_ClipDistance[1];
};

out float _value;
out float _visibility;

uniform mat4 pvm;
uniform mat4 mMat;
uniform mat4 vMat;
uniform mat4 pMat;
uniform mat4 clipPlaneMatrix;
uniform vec3 spacing;
uniform vec3 scale;
uniform vec3 clipPlaneNormal;
uniform int clipPlaneSwitch;

void main()
{
	//Front
	cVert = vec4(spacing.x*(gl_in[0].gl_Position.x-scale.x), spacing.y*(gl_in[0].gl_Position.y-scale.y), spacing.z*(gl_in[0].gl_Position.z-scale.z), 1.0);
	gl_Position = pvm * cVert;
	_value = valueTranslated[0];
	_visibility = visibilityTranslated[0];
	if(clipPlaneSwitch>0)
	{
		clippedVertex = clipPlaneMatrix * cVert;
		gl_ClipDistance[0] = dot(clippedVertex, vec4(clipPlaneNormal,0));
	}
	EmitVertex();
	cVert = vec4(spacing.x*(gl_in[0].gl_Position.x+scale.x), spacing.y*(gl_in[0].gl_Position.y-scale.y), spacing.z*(gl_in[0].gl_Position.z-scale.z), 1.0);
	gl_Position = pvm * cVert;
	_value = valueTranslated[0];
	_visibility = visibilityTranslated[0];
	if(clipPlaneSwitch>0)
	{
		clippedVertex = clipPlaneMatrix * cVert;
		gl_ClipDistance[0] = dot(clippedVertex, vec4(clipPlaneNormal,0));
	}
	EmitVertex();
	cVert = vec4(spacing.x*(gl_in[0].gl_Position.x-scale.x), spacing.y*(gl_in[0].gl_Position.y+scale.y), spacing.z*(gl_in[0].gl_Position.z-scale.z), 1.0);
	gl_Position = pvm * cVert;
	_value = valueTranslated[0];
	_visibility = visibilityTranslated[0];
	if(clipPlaneSwitch>0)
	{
		clippedVertex = clipPlaneMatrix * cVert;
		gl_ClipDistance[0] = dot(clippedVertex, vec4(clipPlaneNormal,0));
	}
	EmitVertex();
	cVert = vec4(spacing.x*(gl_in[0].gl_Position.x+scale.x), spacing.y*(gl_in[0].gl_Position.y+scale.y), spacing.z*(gl_in[0].gl_Position.z-scale.z), 1.0);
	gl_Position = pvm * cVert;
	_value = valueTranslated[0];
	_visibility = visibilityTranslated[0];
	EmitVertex();
	EndPrimitive();
	
	// Left
	cVert = vec4(spacing.x*(gl_in[0].gl_Position.x-scale.x), spacing.y*(gl_in[0].gl_Position.y-scale.y), spacing.z*(gl_in[0].gl_Position.z+scale.z), 1.0);
	gl_Position = pvm * cVert;
	_value = valueTranslated[0];
	_visibility = visibilityTranslated[0];
	if(clipPlaneSwitch>0)
	{
		clippedVertex = clipPlaneMatrix * cVert;
		gl_ClipDistance[0] = dot(clippedVertex, vec4(clipPlaneNormal,0));
	}
	EmitVertex();
	cVert = vec4(spacing.x*(gl_in[0].gl_Position.x-scale.x), spacing.y*(gl_in[0].gl_Position.y-scale.y), spacing.z*(gl_in[0].gl_Position.z-scale.z), 1.0);
	gl_Position = pvm * cVert;
	_value = valueTranslated[0];
	_visibility = visibilityTranslated[0];
	if(clipPlaneSwitch>0)
	{
		clippedVertex = clipPlaneMatrix * cVert;
		gl_ClipDistance[0] = dot(clippedVertex, vec4(clipPlaneNormal,0));
	}
	EmitVertex();
	cVert = vec4(spacing.x*(gl_in[0].gl_Position.x-scale.x), spacing.y*(gl_in[0].gl_Position.y+scale.y), spacing.z*(gl_in[0].gl_Position.z+scale.z), 1.0);
	gl_Position = pvm * cVert;
	_value = valueTranslated[0];
	_visibility = visibilityTranslated[0];
	if(clipPlaneSwitch>0)
	{
		clippedVertex = clipPlaneMatrix * cVert;
		gl_ClipDistance[0] = dot(clippedVertex, vec4(clipPlaneNormal,0));
	}
	EmitVertex();
	cVert = vec4(spacing.x*(gl_in[0].gl_Position.x-scale.x), spacing.y*(gl_in[0].gl_Position.y+scale.y), spacing.z*(gl_in[0].gl_Position.z-scale.z), 1.0);
	gl_Position = pvm * cVert;
	_value = valueTranslated[0];
	_visibility = visibilityTranslated[0];
	if(clipPlaneSwitch>0)
	{
		clippedVertex = clipPlaneMatrix * cVert;
		gl_ClipDistance[0] = dot(clippedVertex, vec4(clipPlaneNormal,0));
	}
	EmitVertex();
	EndPrimitive();
	
	// Right
	cVert = vec4(spacing.x*(gl_in[0].gl_Position.x+scale.x), spacing.y*(gl_in[0].gl_Position.y-scale.y), spacing.z*(gl_in[0].gl_Position.z-scale.z), 1.0);
	gl_Position = pvm * cVert;
	_value = valueTranslated[0];
	_visibility = visibilityTranslated[0];
	if(clipPlaneSwitch>0)
	{
		clippedVertex = clipPlaneMatrix * cVert;
		gl_ClipDistance[0] = dot(clippedVertex, vec4(clipPlaneNormal,0));
	}
	EmitVertex();
	cVert = vec4(spacing.x*(gl_in[0].gl_Position.x+scale.x), spacing.y*(gl_in[0].gl_Position.y-scale.y), spacing.z*(gl_in[0].gl_Position.z+scale.z), 1.0);
	gl_Position = pvm * cVert;
	_value = valueTranslated[0];
	_visibility = visibilityTranslated[0];
	if(clipPlaneSwitch>0)
	{
		clippedVertex = clipPlaneMatrix * cVert;
		gl_ClipDistance[0] = dot(clippedVertex, vec4(clipPlaneNormal,0));
	}
	EmitVertex();
	cVert = vec4(spacing.x*(gl_in[0].gl_Position.x+scale.x), spacing.y*(gl_in[0].gl_Position.y+scale.y), spacing.z*(gl_in[0].gl_Position.z-scale.z), 1.0);
	gl_Position = pvm * cVert;
	_value = valueTranslated[0];
	_visibility = visibilityTranslated[0];
	if(clipPlaneSwitch>0)
	{
		clippedVertex = clipPlaneMatrix * cVert;
		gl_ClipDistance[0] = dot(clippedVertex, vec4(clipPlaneNormal,0));
	}
	EmitVertex();
	cVert = vec4(spacing.x*(gl_in[0].gl_Position.x+scale.x), spacing.y*(gl_in[0].gl_Position.y+scale.y), spacing.z*(gl_in[0].gl_Position.z+scale.z), 1.0);
	gl_Position = pvm * cVert;
	_value = valueTranslated[0];
	_visibility = visibilityTranslated[0];
	if(clipPlaneSwitch>0)
	{
		clippedVertex = clipPlaneMatrix * cVert;
		gl_ClipDistance[0] = dot(clippedVertex, vec4(clipPlaneNormal,0));
	}
	EmitVertex();
	EndPrimitive();
	
	// Back
	cVert = vec4(spacing.x*(gl_in[0].gl_Position.x+scale.x), spacing.y*(gl_in[0].gl_Position.y-scale.y), spacing.z*(gl_in[0].gl_Position.z+scale.z), 1.0);
	gl_Position = pvm * cVert;
	_value = valueTranslated[0];
	_visibility = visibilityTranslated[0];
	if(clipPlaneSwitch>0)
	{
		clippedVertex = clipPlaneMatrix * cVert;
		gl_ClipDistance[0] = dot(clippedVertex, vec4(clipPlaneNormal,0));
	}
	EmitVertex();
	cVert = vec4(spacing.x*(gl_in[0].gl_Position.x-scale.x), spacing.y*(gl_in[0].gl_Position.y-scale.y), spacing.z*(gl_in[0].gl_Position.z+scale.z), 1.0);
	gl_Position = pvm * cVert;
	_value = valueTranslated[0];
	_visibility = visibilityTranslated[0];
	if(clipPlaneSwitch>0)
	{
		clippedVertex = clipPlaneMatrix * cVert;
		gl_ClipDistance[0] = dot(clippedVertex, vec4(clipPlaneNormal,0));
	}
	EmitVertex();
	cVert = vec4(spacing.x*(gl_in[0].gl_Position.x+scale.x), spacing.y*(gl_in[0].gl_Position.y+scale.y), spacing.z*(gl_in[0].gl_Position.z+scale.z), 1.0);
	gl_Position = pvm * cVert;
	_value = valueTranslated[0];
	_visibility = visibilityTranslated[0];
	if(clipPlaneSwitch>0)
	{
		clippedVertex = clipPlaneMatrix * cVert;
		gl_ClipDistance[0] = dot(clippedVertex, vec4(clipPlaneNormal,0));
	}
	EmitVertex();
	cVert = vec4(spacing.x*(gl_in[0].gl_Position.x-scale.x), spacing.y*(gl_in[0].gl_Position.y+scale.y), spacing.z*(gl_in[0].gl_Position.z+scale.z), 1.0);
	gl_Position = pvm * cVert;
	_value = valueTranslated[0];
	_visibility = visibilityTranslated[0];
	if(clipPlaneSwitch>0)
	{
		clippedVertex = clipPlaneMatrix * cVert;
		gl_ClipDistance[0] = dot(clippedVertex, vec4(clipPlaneNormal,0));
	}
	EmitVertex();
	EndPrimitive();
	
	// Bottom
	cVert = vec4(spacing.x*(gl_in[0].gl_Position.x-scale.x), spacing.y*(gl_in[0].gl_Position.y-scale.y), spacing.z*(gl_in[0].gl_Position.z+scale.z), 1.0);
	gl_Position = pvm * cVert;
	_value = valueTranslated[0];
	_visibility = visibilityTranslated[0];
	if(clipPlaneSwitch>0)
	{
		clippedVertex = clipPlaneMatrix * cVert;
		gl_ClipDistance[0] = dot(clippedVertex, vec4(clipPlaneNormal,0));
	}
	EmitVertex();
	cVert = vec4(spacing.x*(gl_in[0].gl_Position.x+scale.x), spacing.y*(gl_in[0].gl_Position.y-scale.y), spacing.z*(gl_in[0].gl_Position.z+scale.z), 1.0);
	gl_Position = pvm * cVert;
	_value = valueTranslated[0];
	_visibility = visibilityTranslated[0];
	if(clipPlaneSwitch>0)
	{
		clippedVertex = clipPlaneMatrix * cVert;
		gl_ClipDistance[0] = dot(clippedVertex, vec4(clipPlaneNormal,0));
	}
	EmitVertex();
	cVert = vec4(spacing.x*(gl_in[0].gl_Position.x-scale.x), spacing.y*(gl_in[0].gl_Position.y-scale.y), spacing.z*(gl_in[0].gl_Position.z-scale.z), 1.0);
	gl_Position = pvm * cVert;
	_value = valueTranslated[0];
	_visibility = visibilityTranslated[0];
	if(clipPlaneSwitch>0)
	{
		clippedVertex = clipPlaneMatrix * cVert;
		gl_ClipDistance[0] = dot(clippedVertex, vec4(clipPlaneNormal,0));
	}
	EmitVertex();
	cVert = vec4(spacing.x*(gl_in[0].gl_Position.x+scale.x), spacing.y*(gl_in[0].gl_Position.y-scale.y), spacing.z*(gl_in[0].gl_Position.z-scale.z), 1.0);
	gl_Position = pvm * cVert;
	_value = valueTranslated[0];
	_visibility = visibilityTranslated[0];
	if(clipPlaneSwitch>0)
	{
		clippedVertex = clipPlaneMatrix * cVert;
		gl_ClipDistance[0] = dot(clippedVertex, vec4(clipPlaneNormal,0));
	}
	EmitVertex();
	EndPrimitive();
	
	// Top
	cVert = vec4(spacing.x*(gl_in[0].gl_Position.x-scale.x), spacing.y*(gl_in[0].gl_Position.y+scale.y), spacing.z*(gl_in[0].gl_Position.z-scale.z), 1.0);
	gl_Position = pvm * cVert;
	_value = valueTranslated[0];
	_visibility = visibilityTranslated[0];
	if(clipPlaneSwitch>0)
	{
		clippedVertex = clipPlaneMatrix * cVert;
		gl_ClipDistance[0] = dot(clippedVertex, vec4(clipPlaneNormal,0));
	}
	EmitVertex();
	cVert = vec4(spacing.x*(gl_in[0].gl_Position.x+scale.x), spacing.y*(gl_in[0].gl_Position.y+scale.y), spacing.z*(gl_in[0].gl_Position.z-scale.z), 1.0);
	gl_Position = pvm * cVert;
	_value = valueTranslated[0];
	_visibility = visibilityTranslated[0];
	if(clipPlaneSwitch>0)
	{
		clippedVertex = clipPlaneMatrix * cVert;
		gl_ClipDistance[0] = dot(clippedVertex, vec4(clipPlaneNormal,0));
	}
	EmitVertex();
	cVert = vec4(spacing.x*(gl_in[0].gl_Position.x-scale.x), spacing.y*(gl_in[0].gl_Position.y+scale.y), spacing.z*(gl_in[0].gl_Position.z+scale.z), 1.0);
	gl_Position = pvm * cVert;
	_value = valueTranslated[0];
	_visibility = visibilityTranslated[0];
	if(clipPlaneSwitch>0)
	{
		clippedVertex = clipPlaneMatrix * cVert;
		gl_ClipDistance[0] = dot(clippedVertex, vec4(clipPlaneNormal,0));
	}
	EmitVertex();
	cVert = vec4(spacing.x*(gl_in[0].gl_Position.x+scale.x), spacing.y*(gl_in[0].gl_Position.y+scale.y), spacing.z*(gl_in[0].gl_Position.z+scale.z), 1.0);
	gl_Position = pvm * cVert;
	_value = valueTranslated[0];
	_visibility = visibilityTranslated[0];
	if(clipPlaneSwitch>0)
	{
		clippedVertex = clipPlaneMatrix * cVert;
		gl_ClipDistance[0] = dot(clippedVertex, vec4(clipPlaneNormal,0));
	}
	EmitVertex();
	EndPrimitive();
}