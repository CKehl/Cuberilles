uniform sampler3D volTexture;
uniform mat4 mTexMat;


void main()
{
	gl_TexCoord[0] = gl_TextureMatrix[0]*gl_MultiTexCoord0;
	//gl_TexCoord[0] = mTexMat*gl_MultiTexCoord0;
	//gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_Position = ftransform();
} 
