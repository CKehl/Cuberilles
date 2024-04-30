package de.ckehl;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.channels.*;
import java.nio.file.Path;
import java.nio.file.Paths;
//import java.lang.System;

public class LoadCT_Stefan {

	protected float [] _spacing;
	protected short [] _dimensions; 
	protected short[][][] _data = null;
	protected String _iniFilename, _datFilename;
	
	public LoadCT_Stefan()
	{
		_spacing = new float[3];
		_dimensions = new short[3];
	}
	
	public void finalize()
	{
		_data = null;
	}
	
	public void read()
	{
		BufferedReader _metareader;
		try
		{
			//System.out.printf("Inifile: %s\n", _iniFilename);
			//System.out.printf("Datfile: %s\n", _datFilename);
			_metareader = new BufferedReader(new FileReader(_iniFilename));
			_metareader.readLine();
			String xSpaceLine = _metareader.readLine();
			_spacing[0] = Float.parseFloat(xSpaceLine.split(" ")[2].split("=")[1]);
			String ySpaceLine = _metareader.readLine();
			_spacing[1] = Float.parseFloat(ySpaceLine.split(" ")[2].split("=")[1]);
			String zSpaceLine = _metareader.readLine();
			_spacing[2] = Float.parseFloat(zSpaceLine.split(" ")[2].split("=")[1]);
			//System.out.printf("Spacing: (%f %f %f)\n", _spacing[0],_spacing[1],_spacing[2]);
			_metareader.close();
			
			DataInputStream dis = new DataInputStream(new FileInputStream(_datFilename));
			
			byte[] buffer = new byte[2*3];

			int noOfShortsRead = 0;
			while(noOfShortsRead < (2*3))
			{
				noOfShortsRead+=dis.read(buffer);
			}	
			ByteBuffer initBuffer = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);
			_dimensions[0] = initBuffer.getShort(0*2);
			_dimensions[1] = initBuffer.getShort(1*2);
			_dimensions[2] = initBuffer.getShort(2*2);
			initBuffer=null; buffer = null;
			

			System.out.printf("Dimensions: %d,  %d, %d\n", _dimensions[0], _dimensions[1], _dimensions[2]);
			
			/*
			 * Allocate final 3D image matrix, and 2D image buffer
			 */
			_data = new short[_dimensions[0]][_dimensions[1]][_dimensions[2]];
			byte[] byteBuffers = new byte[_dimensions[0]*_dimensions[1]*2];
			
			System.out.println("Reading 3D image now ...");
			int currentSlice = 0;
			noOfShortsRead = 0;
			while(currentSlice < _dimensions[2])
			{
				noOfShortsRead = 0;
				while(noOfShortsRead < (_dimensions[0]*_dimensions[1]*2))
				{
					noOfShortsRead += dis.read(byteBuffers);
				}

				ByteBuffer sliceBuffer = ByteBuffer.wrap(byteBuffers).order(ByteOrder.LITTLE_ENDIAN);
				for(int i = 0; i < _dimensions[0]; i++)
				{
					for(int j = 0; j < _dimensions[1]; j++)
					{
						_data[i][j][currentSlice] = sliceBuffer.getShort((j*_dimensions[0]+i)*2);
					}
				}
				currentSlice++;
			}
			
			byteBuffers = null;
			dis.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public short [][][] getData()
	{
		return _data;
	}
	
	public short[] getDimensions()
	{
		return _dimensions;
	}
	
	public float[] getSpacing()
	{
		return _spacing;
	}
	
	/**
	 * Sets the ini filepath, automatically calculates the dat filepath
	 * @param inifile
	 */
	public void setFilename(String inifile)
	{
		_iniFilename = inifile;
		int dotpos = inifile.lastIndexOf(".");
		_datFilename = inifile.substring(0, dotpos+1)+"dat";
	}
}
