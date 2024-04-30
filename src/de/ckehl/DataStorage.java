package de.ckehl;

import org.joml.Vector3f;

public class DataStorage implements DataHolderInterface {

	protected short[][][] _data = null;
	protected float[] _spacing = null;
	protected short[] _dimensions = null;
	protected int num_elements = 0;
	protected Vector3f[][][] _normals = null;
	protected byte _normalTexData[] = null;
	protected boolean _hasData = false;
	protected boolean _hasNormals = false;
	protected boolean _hasNormalTexture = false;
	
	public DataStorage()
	{

	}
	
	public DataStorage(short dimensions[], float spacing[], short data[][][])
	{
		_dimensions = new short[3];
		_spacing = new float[3];
		System.arraycopy(dimensions, 0, _dimensions, 0, 3);
		System.arraycopy(spacing, 0, _spacing, 0, 3);
		_data = new short[_dimensions[0]][_dimensions[1]][_dimensions[2]];
		for(int i=0; i<_dimensions[0]; i++)
		{
			for(int j=0; j<_dimensions[1]; j++)
			{
				for(int k=0; k<_dimensions[2]; k++)
				{
					_data[i][j][k] = data[i][j][k];
				}
			}
		}
		_hasData = true;
	}
	
	@Override
	public void setData(short[][][] data) {
		// TODO Auto-generated method stub
		if(_dimensions!=null)
		{
			_data = new short[_dimensions[0]][_dimensions[1]][_dimensions[2]];
			for(int i=0; i<_dimensions[0]; i++)
			{
				for(int j=0; j<_dimensions[1]; j++)
				{
					for(int k=0; k<_dimensions[2]; k++)
					{
						_data[i][j][k] = data[i][j][k];
					}
				}
			}
			_hasData = true;
		}
	}

	@Override
	public short[][][] getData() {
		// TODO Auto-generated method stub
		return _data;
	}

	@Override
	public short getDataValue(int x, int y, int z) {
		// TODO Auto-generated method stub
		return _data[x][y][z];
	}

	@Override
	public void setDimensions(short[] dims) {
		// TODO Auto-generated method stub
		_dimensions = new short[3];
		if(dims.length >= 3)
		{
			//System.arraycopy(dims, 0, _dimensions, 0, 3);
			_dimensions[0] = dims[0];
			_dimensions[1] = dims[1];
			_dimensions[2] = dims[2];
		}
	}

	@Override
	public int[] getDimensionsInt() {
		// TODO Auto-generated method stub
		int result [] = {(int)_dimensions[0], (int)_dimensions[1], (int)_dimensions[2]};
		return result;
	}
	
	@Override
	public short[] getDimensions() {
		// TODO Auto-generated method stub
		return _dimensions;
	}

	@Override
	public void setSpacing(float[] spacing) {
		// TODO Auto-generated method stub
		_spacing = new float[3];
		if(spacing.length >= 3)
		{
			//System.arraycopy(spacing, 0, _spacing, 0, 3);
			_spacing[0] = spacing[0];
			_spacing[1] = spacing[1];
			_spacing[2] = spacing[2];
		}
	}

	@Override
	public float[] getSpacing() {
		// TODO Auto-generated method stub
		return _spacing;
	}

	@Override
	public boolean hasData() {
		// TODO Auto-generated method stub
		return _hasData;
	}

	@Override
	public boolean hasNormals() {
		// TODO Auto-generated method stub
		return _hasNormals;
	}

	@Override
	public void computeNormals() {
		// TODO Auto-generated method stub
		if(_data == null)
		{
			System.out.println("no data available.");
			return;
		}
		_normals = new Vector3f[_dimensions[0]][_dimensions[1]][_dimensions[2]];
		for(int i = 0; i< _dimensions[0]; i++)
		{
			for(int j = 0; j< _dimensions[1]; j++)
			{
				for(int k = 0; k< _dimensions[2]; k++)
				{
					//System.out.printf("%d %d %d\n", i,j,k);
					float x,y,z;
					x = (float)(_data[Math.min(i+1, _dimensions[0]-1)][j][k] - _data[Math.max(i-1, 0)][j][k])*_spacing[0];
					y = (float)(_data[i][Math.min(j+1, _dimensions[1]-1)][k] - _data[i][Math.max(j-1, 0)][k])*_spacing[1];
					z = (float)(_data[i][j][Math.min(k+1, _dimensions[2]-1)] - _data[i][j][Math.max(k-1, 0)])*_spacing[2];
					_normals[i][j][k] = new Vector3f(x,y,z).normalize();
				}
			}
		}
		_hasNormals = true;
	}

	@Override
	public Vector3f[][][] getNormals() {
		// TODO Auto-generated method stub
		return _normals;
	}

	@Override
	public Vector3f getNormal(int blockX, int blockY, int blockZ) {
		// TODO Auto-generated method stub
		return _normals[blockX][blockY][blockZ];
	}

	@Override
	public int getNumberElements() {
		// TODO Auto-generated method stub
		return _dimensions[0]*_dimensions[1]*_dimensions[2];
	}

	@Override
	public void computeNormalTexture() {
		if(_data == null)
		{
			System.out.println("no data available.");
			return;
		}
		_normalTexData = new byte[_dimensions[0]*_dimensions[1]*_dimensions[2]*3];
		Vector3f tn = new Vector3f();
		for(int i = 0; i< _dimensions[0]; i++)
		{
			for(int j = 0; j< _dimensions[1]; j++)
			{
				for(int k = 0; k< _dimensions[2]; k++)
				{
					int idx = (i*_dimensions[1]*_dimensions[2]*3)+(j*_dimensions[2]*3)+(k*3);
					//System.out.printf("%d %d %d\n", i,j,k);
					//float x,y,z;
					tn.x = (float)(_data[Math.min(i+1, _dimensions[0]-1)][j][k] - _data[Math.max(i-1, 0)][j][k])*_spacing[0];
					tn.y = (float)(_data[i][Math.min(j+1, _dimensions[1]-1)][k] - _data[i][Math.max(j-1, 0)][k])*_spacing[1];
					tn.z = (float)(_data[i][j][Math.min(k+1, _dimensions[2]-1)] - _data[i][j][Math.max(k-1, 0)])*_spacing[2];
					tn.normalize();
					_normalTexData[idx+0] = (byte)((int)(((tn.x/2.0)+0.5f)*255));
					_normalTexData[idx+1] = (byte)((int)(((tn.y/2.0)+0.5f)*255));
					_normalTexData[idx+2] = (byte)((int)(((tn.z/2.0)+0.5f)*255));
				}
			}
		}
		_hasNormalTexture = true;
	}

	@Override
	public boolean hasNormalTexture() {
		// TODO Auto-generated method stub
		return _hasNormalTexture;
	}

	@Override
	public byte[] getNormalTextureData() {
		// TODO Auto-generated method stub
		return _normalTexData;
	}

	@Override
	public int getTextureAddress(int blockX, int blockY, int blockZ) {
		return (blockX*_dimensions[1]*_dimensions[2]*3)+(blockY*_dimensions[2]*3)+(blockZ*3);
	}

}
