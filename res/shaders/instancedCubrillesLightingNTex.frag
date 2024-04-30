#version 330 compatibility
in float _value;
in float _visibility;
in vec3 _normal;
in float _lightIntensity;
uniform sampler1D lookup;
uniform int numGaussCurves;
uniform vec3 gaussParameter;
in float gl_ClipDistance[1];

float getGaussValue(float x, float mu, float sigma)
{
	return (exp(-((x-mu)*(x-mu))/(2*sigma*sigma)));
}

void main()
{
	//gl_FragColor = vec4(_value, _value, _value, 1.0);

	float gaussMultiplier = 0.0;
	gaussMultiplier= min(getGaussValue(_value*4095.0, gaussParameter.y, gaussParameter.z), 1.0);
	float indicator=step(0.5, float(numGaussCurves));
	float visibility=mix(_visibility, gaussMultiplier, indicator);

	vec4 lutColor = texture1D(lookup, _value);
	if((lutColor.a < 0.005) || (gl_ClipDistance[0]<0))
		discard;
	lutColor.a = lutColor.a * visibility;
	gl_FragColor = vec4(lutColor.xyz*_lightIntensity, lutColor.a);
}