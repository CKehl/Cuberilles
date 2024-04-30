uniform sampler1D lookup;

void main()
{
	float value = gl_Color.r;
	vec4 lutColor = texture1D(lookup, value);
	if(lutColor.a < 0.01)
		discard;
	gl_FragColor = lutColor;
}