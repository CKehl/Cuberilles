uniform sampler3D volTexture;
uniform float valueWindow;
uniform float valueLevel;
uniform float valueIncrease;
uniform float baseValue;
uniform float max_original;
uniform float lowCut, highCut;

vec4 medicalTexture(float value)
{
	float winWidth = 0.5*valueWindow;
	float winMid = valueLevel+0.5;
	float winMin = winMid-winWidth;
	float winMax = winMid+winWidth;
	float nval = (value-winMin)*(1.0/valueWindow) + valueIncrease;
	float cValue = clamp(nval,0.0,1.0);
	return vec4(cValue, cValue, cValue , 1.0);
}

void main()
{
	float max_offset = 65535.0/max_original;

	float value = 0.0;
	vec3 tC = vec3(0.0);
	float tVal = 0.0;
	for(float i=0.0; i<1.0; i+=0.05)
	{
		tC = vec3(gl_TexCoord[0].s, i, gl_TexCoord[0].p);
		tVal = texture3D(volTexture, tC).r;
		value+=(step(lowCut/max_original, tVal) * step(tVal, highCut/max_original))*tVal;
	}
	value/=20.0;
	value*=max_offset*2.0;
	
	if(baseValue>=0.0001)
	{
		float diffValue = (value-clamp(abs(baseValue-value),0.0,value))/value;
    	gl_FragColor = vec4(diffValue,diffValue,diffValue,1.0);
    }
    else
    {
		gl_FragColor = medicalTexture(value);
    }
}