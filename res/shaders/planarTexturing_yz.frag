uniform sampler3D volTexture;
uniform sampler2D markTextureYZ;
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
	//y = 1/valueWindow * x + valueIncrease
	float nval = (value-winMin)*(1.0/valueWindow) + valueIncrease;
	//vec2 clamping_adapted = vec2(max(0.0,winMid-winWidth),min(1.0,winMid+winWidth));
	//float cValue = clamp(value,clamping_adapted.x,clamping_adapted.y);
	float cValue = clamp(nval,0.0,1.0);
	return vec4(cValue, cValue, cValue , 1.0);
}

void main()
{
	float max_offset = max_original/(65535.0);
	vec4 colour = vec4(texture3D(volTexture, gl_TexCoord[0].stp));
	float markerVal = texture2D(markTextureYZ, gl_TexCoord[1].tp).r;
	float value = (colour.r/max_offset);
	value = (step(lowCut/max_original, value) * step(value, highCut/max_original))*value*2.0;
	
	if(baseValue>=0.0001)
	{
		float diffValue = (value-clamp(abs(baseValue-value),0.0,value))/value;
    	gl_FragColor = vec4(diffValue,diffValue,diffValue,1.0);
    	//gl_FragColor = vec4(baseValue,0.0,0.0,1.0);
    }
    else
    {
		vec4 colour = (1.0-markerVal)*medicalTexture(value) + markerVal*vec4(0.68,0,0,1);
		gl_FragColor = colour;
	}
}
