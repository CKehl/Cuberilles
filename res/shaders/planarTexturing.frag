uniform sampler3D volTexture;
uniform float valueWindow;
uniform float valueLevel;
float max_original;

vec3 rgb2hsv(vec3 c)
{
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

vec4 medicalTexture(vec3)
{
	max_original = 4095.0;
	//vec2 clamping_original;
	vec2 clamping_adapted;
	float max_offset = max_original/65535.0;
	//clamping_original = vec2(0.0,max_offset);

	vec4 colour = vec4(texture3D(volTexture, gl_TexCoord[0].stp));
	vec3 hsv_in = rgb2hsv(colour.rgb);
	// uses original
	//vec3 hsv_out = vec3(hsv_in.rg,(1.0-max_offset)+clamp(hsv_in.b,clamping_original.x,clamping_original.y));
	//vec3 hsv_out = vec3(hsv_in.rg,(1.0-max_offset)+clamp(hsv_in.b,clamping_original.x,clamping_original.y));
	float value = (hsv_in.b/max_offset);
	float winWidth = 0.5*valueWindow;
	float winMid = valueLevel+0.5;
	clamping_adapted = vec2(max(0.0,winMid-winWidth),min(1.0,winMid+winWidth));
	vec3 hsv_out = vec3(hsv_in.rg,clamp(value,clamping_adapted.x,clamping_adapted.y));
	return vec4(hsv2rgb(hsv_out),colour.a);
}

void main()
{
	//vec4 colour = vec4(gl_TexCoord[0].stp, 1.0);
	vec4 colour = medicalTexture(gl_TexCoord[0].stp);
	gl_FragColor = colour;
}
