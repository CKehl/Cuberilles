#version 330 compatibility
in float _value;
in float _visibility;
in vec3 _normal;
uniform sampler1D lookup;
vec3 normColour;
uniform int numGaussCurves;
uniform vec3 gaussParameter;
in float gl_ClipDistance[1];

float getGaussValue(float x, float mu, float sigma)
{
	return (exp(-((x-mu)*(x-mu))/(2*sigma*sigma)));
}

void main()
{
	float ranger=0.15;
	//gl_FragColor = vec4(_value, _value, _value, 1.0);

	float gaussMultiplier = 0.0;
	gaussMultiplier= min(getGaussValue(_value*4095.0, gaussParameter.y, gaussParameter.z), 1.0);
	float indicator=step(0.5, float(numGaussCurves));
	float visibility=mix(_visibility, gaussMultiplier, indicator);

	//V1 - colours
	//vec3 cnX = vec3(252.0/255.0, 141.0/255.0, 89.0/255.0);
	//vec3 cnY = vec3(255.0/255.0, 255.0/255.0, 191.0/255.0);
	//vec3 cnZ = vec3(145.0/255.0, 191.0/255.0, 219.0/255.0);

	//V2 colours
	//vec3 cnX = vec3(239.0/255.0, 138.0/255.0, 98.0/255.0);
	//vec3 cnY = vec3(240.0/255.0, 240.0/255.0, 240.0/255.0);
	//vec3 cnZ = vec3(103.0/255.0, 169.0/255.0, 207.0/255.0);
	
	//V3 colours
	//vec3 tnX = vec3(233.0/255.0, 78.0/255.0, 255.0/255.0);
	//vec3 bnX = vec3(158.0/255.0, 0.0/255.0, 180.0/255.0);
	//vec3 tnY = vec3(81.0/255.0, 77.0/255.0, 255.0/255.0);
	//vec3 bnY = vec3(4.0/255.0, 0.0/255.0, 177.0/255.0);
	//vec3 tnZ = vec3(77.0/255.0, 255.0/255.0, 255.0/255.0);
	//vec3 bnZ = vec3(0.0/255.0, 179.0/255.0, 179.0/255.0);
	
	//V4 colours
	vec3 tnX = vec3(200.0/255.0, 0.0/255.0, 255.0/255.0);
	vec3 cnX = vec3(140.0/255.0, 0.0/255.0, 179.0/255.0);
	vec3 bnX = vec3(80.0/255.0, 0.0/255.0, 102.0/255.0);
	vec3 tnY = vec3(0.0/255.0, 0.0/255.0, 255.0/255.0);
	vec3 cnY = vec3(0.0/255.0, 0.0/255.0, 179.0/255.0);
	vec3 bnY = vec3(0.0/255.0, 0.0/255.0, 102.0/255.0);
	vec3 tnZ = vec3(0.0/255.0, 255.0/255.0, 255.0/255.0);
	vec3 cnZ = vec3(0.0/255.0, 166.0/255.0, 166.0/255.0);
	vec3 bnZ = vec3(0.0/255.0, 75.0/255.0, 75.0/255.0);
	
	vec4 lutColor = texture1D(lookup, _value);
	if((lutColor.a < 0.005) || (gl_ClipDistance[0]<0))
		discard;
	lutColor.a *= visibility;
	// too bright
	//vec3 ncX = vec3( (abs(_normal.x) * (((sign(_normal.x)+1.0)/2.0)*vec3(cnX.x,cnX.y+ranger,cnX.z) + ((sign(_normal.x)-1.0)/-2.0)*vec3(cnX.x+ranger,cnX.y,cnX.z))) + ((1.0-abs(_normal.x))*vec3(0.3,0.3,0.3)));
	//vec3 ncY = vec3( (abs(_normal.y) * (((sign(_normal.y)+1.0)/2.0)*vec3(cnY.x,cnY.y,cnY.z+ranger) + ((sign(_normal.y)-1.0)/-2.0)*vec3(cnY.x+ranger,cnY.y,cnY.z))) + ((1.0-abs(_normal.y))*vec3(0.3,0.3,0.3)));
	//vec3 ncZ = vec3( (abs(_normal.z) * (((sign(_normal.z)+1.0)/2.0)*vec3(cnZ.x,cnZ.y,cnZ.z+ranger) + ((sign(_normal.z)-1.0)/-2.0)*vec3(cnZ.x,cnZ.y+ranger,cnZ.z))) + ((1.0-abs(_normal.z))*vec3(0.3,0.3,0.3)));
	//normColour = (abs(_normal.x)*ncX)+(abs(_normal.y)*ncY)+(abs(_normal.z)*ncZ);
	
	//too dull
	//vec3 ncX = vec3( ((sign(_normal.x)+1.0)/2.0)*vec3(cnX.x,cnX.y+ranger,cnX.z) + ((sign(_normal.x)-1.0)/-2.0)*vec3(cnX.x+ranger,cnX.y,cnX.z) );
	//vec3 ncY = vec3( ((sign(_normal.y)+1.0)/2.0)*vec3(cnY.x,cnY.y,cnY.z+ranger) + ((sign(_normal.y)-1.0)/-2.0)*vec3(cnY.x+ranger,cnY.y,cnY.z) );
	//vec3 ncZ = vec3( ((sign(_normal.z)+1.0)/2.0)*vec3(cnZ.x,cnZ.y,cnZ.z+ranger) + ((sign(_normal.z)-1.0)/-2.0)*vec3(cnZ.x,cnZ.y+ranger,cnZ.z) );
	//normColour = (0.3*ncX)+(0.3*ncY)+(0.3*ncZ);
	//normColour = ((abs(_normal.x)*ncX)+(abs(_normal.y)*ncY)+(abs(_normal.z)*ncZ))/3.0;
	
	//V1 mixing
	//vec3 ncX = vec3( ((sign(_normal.x)+1.0)/2.0)*vec3(cnX.x-ranger,cnX.y,cnX.z) + ((sign(_normal.x)-1.0)/-2.0)*vec3(cnX.x+ranger,cnX.y,cnX.z) );
	//vec3 ncY = vec3( ((sign(_normal.y)+1.0)/2.0)*vec3(cnY.x-ranger/2.0,cnY.y-ranger/2.0,cnY.z) + ((sign(_normal.y)-1.0)/-2.0)*vec3(cnY.x+ranger/2.0,cnY.y+ranger/2.0,cnY.z) );
	//vec3 ncZ = vec3( ((sign(_normal.z)+1.0)/2.0)*vec3(cnZ.x,cnZ.y,cnZ.z-ranger) + ((sign(_normal.z)-1.0)/-2.0)*vec3(cnZ.x,cnZ.y,cnZ.z+ranger) );

	//V2 mixing
	//vec3 ncX = vec3( ((sign(_normal.x)+1.0)/2.0)*vec3(cnX.x-ranger,cnX.y,cnX.z) + ((sign(_normal.x)-1.0)/-2.0)*vec3(cnX.x+ranger,cnX.y,cnX.z) );
	//vec3 ncY = vec3( ((sign(_normal.y)+1.0)/2.0)*vec3(cnY.x-ranger/3.0,cnY.y-ranger/3.0,cnY.z-ranger/3.0) + ((sign(_normal.y)-1.0)/-2.0)*vec3(cnY.x+ranger/3.0,cnY.y+ranger/3.0,cnY.z+ranger/3.0) );
	//vec3 ncZ = vec3( ((sign(_normal.z)+1.0)/2.0)*vec3(cnZ.x,cnZ.y,cnZ.z-ranger) + ((sign(_normal.z)-1.0)/-2.0)*vec3(cnZ.x,cnZ.y,cnZ.z+ranger) );	

	//V3 mixing
	vec3 ncX = vec3( ((sign(_normal.x)+1.0)/2.0)*tnX + ((sign(_normal.x)-1.0)/-2.0)*bnX );
	vec3 ncY = vec3( ((sign(_normal.y)+1.0)/2.0)*tnY + ((sign(_normal.y)-1.0)/-2.0)*bnY );
	vec3 ncZ = vec3( ((sign(_normal.z)+1.0)/2.0)*tnZ + ((sign(_normal.z)-1.0)/-2.0)*bnZ );
	
	//normColour = normalize((abs(_normal.x)*ncX)+(abs(_normal.y)*ncY)+(abs(_normal.z)*ncZ));
	normColour = (abs(_normal.x)*ncX)+(abs(_normal.y)*ncY)+(abs(_normal.z)*ncZ);
	//normColour = (0.3*ncX)+(0.3*ncY)+(0.3*ncZ);
	
	
	//gl_FragColor = vec4(_normal, lutColor.a);
	gl_FragColor = vec4(normColour, lutColor.a);
}