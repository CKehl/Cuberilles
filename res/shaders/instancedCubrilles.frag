#version 330 compatibility
in float _value;
in float _visibility;
uniform sampler1D lookup;
uniform int numGaussCurves;
uniform vec3 gaussParameter;
in float gl_ClipDistance[1];

float getGaussValue(float x, float mu, float sigma)
{
	//float sigmaScaled = sigma/4095.0;
	return (exp(-((x-mu)*(x-mu))/(2*sigma*sigma)));
	// /sqrt(2.0*3.1415926535897932384626433832795*sigmaScaled*sigmaScaled);
}

void main()
{
	//gl_FragColor = vec4(_value, _value, _value, 1.0);
	
	float gaussMultiplier = 0.0;
	gaussMultiplier= min(getGaussValue(_value*4095.0, gaussParameter.y, gaussParameter.z), 1.0);
	//gaussMultiplier=gaussMultiplier+getGaussValue(_value*4095.0, gaussParameter.y, gaussParameter.z);
	float indicator=step(0.5, float(numGaussCurves));
	//gaussMultiplier/=float(max(numGaussCurves,1));
	float visibility=mix(_visibility, gaussMultiplier, indicator);
	
	vec4 lutColor = texture1D(lookup, _value);
	if((lutColor.a < 0.005) || (gl_ClipDistance[0]<0))
		discard;
	lutColor = vec4(lutColor.rgb*max(0.4,visibility), visibility*lutColor.a);
	//lutColor = vec4(lutColor.rgb*max(1.0,visibility), visibility*lutColor.a);
	gl_FragColor = lutColor;
}