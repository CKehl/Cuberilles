package de.ckehl;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class LoadRoxarText {
	protected float [] _spacing;
	protected short [] _dimensions; 
	protected short[][][] _data = null;
	protected String _iniFilename, _textFilename;
	
	public LoadRoxarText()
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
		BufferedReader _metareader, _datareader;
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
			
			//text read
			_datareader = new BufferedReader(new FileReader(_textFilename));
			String line = _datareader.readLine();
			String sExplodes[] = line.split(" ");

			_dimensions[0] = (short)Integer.parseInt(sExplodes[0]);
			_dimensions[1] = (short)Integer.parseInt(sExplodes[1]);
			_dimensions[2] = (short)Integer.parseInt(sExplodes[2]);
			float mVal = Float.parseFloat(sExplodes[3]);

			System.out.printf("Dimensions: %d, %d, %d\n", _dimensions[0], _dimensions[1], _dimensions[2]);
			
			/*
			 * Allocate final 3D image matrix, and 2D image buffer
			 */
			_data = new short[_dimensions[0]][_dimensions[1]][_dimensions[2]];
			
			System.out.println("Reading 3D image now ...");
			for(int i = 0; i < _dimensions[0]; i++)
			{
				for(int j = 0; j < _dimensions[1]; j++)
				{
					for(int k = 0; k < _dimensions[2]; k++)
					{
						line = _datareader.readLine().trim();
						_data[i][j][k] = (short)(Math.round((Float.parseFloat(line)/mVal)*4095f));
					}
				}
			}
			_datareader.close();
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
		_textFilename = inifile.substring(0, dotpos+1)+"txt";
	}
}
