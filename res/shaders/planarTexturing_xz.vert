uniform sampler3D volTexture;
uniform mat4 mTexMat;
uniform vec3 spacing;

void main()
{
	//gl_TexCoord[0] = gl_TextureMatrix[0]*gl_MultiTexCoord0;
	gl_TexCoord[1] = gl_MultiTexCoord1;
	gl_TexCoord[0] = mTexMat*gl_MultiTexCoord0;
	//gl_TexCoord[0] = gl_MultiTexCoord0;
	//gl_Position = ftransform();
	vec4 vertex = vec4(gl_Vertex.x*spacing.x, gl_Vertex.y*spacing.y, gl_Vertex.z*spacing.z, gl_Vertex.w);
	gl_Position = gl_ModelViewProjectionMatrix * vertex;
} 
