#version 330 compatibility
layout(points) in;
layout(points, max_vertices=1) out;
in float valueTranslated[];
out float _value;

void main()
{
	gl_Position = gl_in[0].gl_Position;
	_value = valueTranslated[0];
	EmitVertex();
	EndPrimitive();
}