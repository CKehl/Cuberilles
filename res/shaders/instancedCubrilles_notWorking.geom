#version 330 compatibility
layout(points) in;
layout(triangles, max_vertices=36) out;

in vec4 center[];
in float valueTranslated[];
out float _value;

void main()
{
	//Front
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x-0.5, center[0].y-0.5, center[0].z-0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x+0.5, center[0].y-0.5, center[0].z-0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x-0.5, center[0].y+0.5, center[0].z-0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	EndPrimitive();
	
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x-0.5, center[0].y+0.5, center[0].z-0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x+0.5, center[0].y-0.5, center[0].z-0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x+0.5, center[0].y+0.5, center[0].z-0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	EndPrimitive();
	
	// Left
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x-0.5, center[0].y-0.5, center[0].z+0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x-0.5, center[0].y-0.5, center[0].z-0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x-0.5, center[0].y+0.5, center[0].z+0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	EndPrimitive();
	
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x-0.5, center[0].y+0.5, center[0].z+0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x-0.5, center[0].y-0.5, center[0].z-0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x-0.5, center[0].y+0.5, center[0].z-0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	EndPrimitive();
	
	// Right
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x+0.5, center[0].y-0.5, center[0].z-0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x+0.5, center[0].y-0.5, center[0].z+0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x+0.5, center[0].y+0.5, center[0].z-0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	EndPrimitive();
	
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x+0.5, center[0].y+0.5, center[0].z-0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x+0.5, center[0].y-0.5, center[0].z+0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x+0.5, center[0].y+0.5, center[0].z+0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	EndPrimitive();
	
	// Back
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x+0.5, center[0].y-0.5, center[0].z+0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x-0.5, center[0].y-0.5, center[0].z+0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x+0.5, center[0].y+0.5, center[0].z+0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	EndPrimitive();
	
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x+0.5, center[0].y+0.5, center[0].z+0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x-0.5, center[0].y-0.5, center[0].z+0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x-0.5, center[0].y+0.5, center[0].z+0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	EndPrimitive();
	
	// Bottom
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x-0.5, center[0].y-0.5, center[0].z+0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x+0.5, center[0].y-0.5, center[0].z+0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x-0.5, center[0].y-0.5, center[0].z-0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	EndPrimitive();
	
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x-0.5, center[0].y-0.5, center[0].z-0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x+0.5, center[0].y-0.5, center[0].z+0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x+0.5, center[0].y-0.5, center[0].z-0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	EndPrimitive();
	
	// Top
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x-0.5, center[0].y+0.5, center[0].z-0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x+0.5, center[0].y+0.5, center[0].z-0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x-0.5, center[0].y+0.5, center[0].z+0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	EndPrimitive();
	
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x-0.5, center[0].y+0.5, center[0].z+0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x+0.5, center[0].y+0.5, center[0].z-0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(center[0].x+0.5, center[0].y+0.5, center[0].z+0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	EndPrimitive();
}