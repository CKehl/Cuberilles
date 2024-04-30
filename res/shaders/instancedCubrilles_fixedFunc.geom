#version 330 compatibility
layout(points) in;
layout(triangle_strip, max_vertices=36) out;

in float valueTranslated[];
out float _value;

uniform mat4 pvm;
uniform mat4 mMat;
uniform mat4 vMat;
uniform mat4 pMat;

void main()
{
	//Front
	gl_Position = gl_ModelViewProjectionMatrix * vec4(gl_in[0].gl_Position.x-0.5, gl_in[0].gl_Position.y-0.5, gl_in[0].gl_Position.z-0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(gl_in[0].gl_Position.x+0.5, gl_in[0].gl_Position.y-0.5, gl_in[0].gl_Position.z-0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(gl_in[0].gl_Position.x-0.5, gl_in[0].gl_Position.y+0.5, gl_in[0].gl_Position.z-0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();

	gl_Position = gl_ModelViewProjectionMatrix * vec4(gl_in[0].gl_Position.x+0.5, gl_in[0].gl_Position.y+0.5, gl_in[0].gl_Position.z-0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	EndPrimitive();
	
	// Left
	gl_Position = gl_ModelViewProjectionMatrix * vec4(gl_in[0].gl_Position.x-0.5, gl_in[0].gl_Position.y-0.5, gl_in[0].gl_Position.z+0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(gl_in[0].gl_Position.x-0.5, gl_in[0].gl_Position.y-0.5, gl_in[0].gl_Position.z-0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(gl_in[0].gl_Position.x-0.5, gl_in[0].gl_Position.y+0.5, gl_in[0].gl_Position.z+0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();

	gl_Position = gl_ModelViewProjectionMatrix * vec4(gl_in[0].gl_Position.x-0.5, gl_in[0].gl_Position.y+0.5, gl_in[0].gl_Position.z-0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	EndPrimitive();
	
	// Right
	gl_Position = gl_ModelViewProjectionMatrix * vec4(gl_in[0].gl_Position.x+0.5, gl_in[0].gl_Position.y-0.5, gl_in[0].gl_Position.z-0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(gl_in[0].gl_Position.x+0.5, gl_in[0].gl_Position.y-0.5, gl_in[0].gl_Position.z+0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(gl_in[0].gl_Position.x+0.5, gl_in[0].gl_Position.y+0.5, gl_in[0].gl_Position.z-0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();

	gl_Position = gl_ModelViewProjectionMatrix * vec4(gl_in[0].gl_Position.x+0.5, gl_in[0].gl_Position.y+0.5, gl_in[0].gl_Position.z+0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	EndPrimitive();
	
	// Back
	gl_Position = gl_ModelViewProjectionMatrix * vec4(gl_in[0].gl_Position.x+0.5, gl_in[0].gl_Position.y-0.5, gl_in[0].gl_Position.z+0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(gl_in[0].gl_Position.x-0.5, gl_in[0].gl_Position.y-0.5, gl_in[0].gl_Position.z+0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(gl_in[0].gl_Position.x+0.5, gl_in[0].gl_Position.y+0.5, gl_in[0].gl_Position.z+0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();

	gl_Position = gl_ModelViewProjectionMatrix * vec4(gl_in[0].gl_Position.x-0.5, gl_in[0].gl_Position.y+0.5, gl_in[0].gl_Position.z+0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	EndPrimitive();
	
	// Bottom
	gl_Position = gl_ModelViewProjectionMatrix * vec4(gl_in[0].gl_Position.x-0.5, gl_in[0].gl_Position.y-0.5, gl_in[0].gl_Position.z+0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(gl_in[0].gl_Position.x+0.5, gl_in[0].gl_Position.y-0.5, gl_in[0].gl_Position.z+0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(gl_in[0].gl_Position.x-0.5, gl_in[0].gl_Position.y-0.5, gl_in[0].gl_Position.z-0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();

	gl_Position = gl_ModelViewProjectionMatrix * vec4(gl_in[0].gl_Position.x+0.5, gl_in[0].gl_Position.y-0.5, gl_in[0].gl_Position.z-0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	EndPrimitive();
	
	// Top
	gl_Position = gl_ModelViewProjectionMatrix * vec4(gl_in[0].gl_Position.x-0.5, gl_in[0].gl_Position.y+0.5, gl_in[0].gl_Position.z-0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(gl_in[0].gl_Position.x+0.5, gl_in[0].gl_Position.y+0.5, gl_in[0].gl_Position.z-0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	gl_Position = gl_ModelViewProjectionMatrix * vec4(gl_in[0].gl_Position.x-0.5, gl_in[0].gl_Position.y+0.5, gl_in[0].gl_Position.z+0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();

	gl_Position = gl_ModelViewProjectionMatrix * vec4(gl_in[0].gl_Position.x+0.5, gl_in[0].gl_Position.y+0.5, gl_in[0].gl_Position.z+0.5, 1.0);
	_value = valueTranslated[0];
	EmitVertex();
	EndPrimitive();
}