uniform sampler1D gaussCurves;
uniform float maxValue;

float getGaussValue(float x, float mu, float sigma)
{
	return (exp(-((x-mu)*(x-mu))/(2*sigma*sigma)));
	///sqrt(2.0*3.1415926535897932384626433832795*sigma*sigma)
}

void main()
{
	float _value = gl_Color.r;

	float gaussMultiplier = 1.0;
	int numGaussCurves = int(round(log2(texture1D(gaussCurves, 0).r)));
	for(int i=0; i<numGaussCurves; i++)
	{
		float lookupPos = float(i+1)+0.2;
		vec4 params = texture1D(gaussCurves, lookupPos);
		
		//gaussMultiplier=max(gaussMultiplier, getGaussValue(maxValue*_value, params.y, params.z));
		//gaussMultiplier=min(gaussMultiplier, getGaussValue(maxValue*_value, params.y, params.z));
		gaussMultiplier=gaussMultiplier+getGaussValue(_value/65535.0, params.y, params.z);
	}
	float indicator=step(0.75, float(numGaussCurves));
	gaussMultiplier-=indicator;
	gaussMultiplier/=float(max(numGaussCurves,1));
	float visibility=mix(1.0, gaussMultiplier, indicator);

	if(visibility < 0.005)
		discard;
	gl_FragColor = vec4(_value,_value,_value,visibility);
}