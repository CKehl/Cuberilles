package de.ckehl;

import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.common.util.IOUtil;
import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLContext;
import com.jogamp.opencl.CLKernel;
import com.jogamp.opencl.CLProgram;
import com.jogamp.opencl.gl.CLGLContext;

public class KernelControlJOCL {
	protected String _kernelPath;
	protected List<String> _functionNames = null;
	protected CLProgram _program = null;
	protected CLKernel[] _kernels = null; 
	protected boolean _isInitialized = false;
	
	public KernelControlJOCL()
	{
		_kernelPath = "";
		_functionNames = new ArrayList<>();
	}
	
	public KernelControlJOCL(String kernelPath)
	{
		_kernelPath = kernelPath;
		_functionNames = new ArrayList<>();
	}
	
	public KernelControlJOCL(String kernelPath, List<String> functionNames)
	{
		_kernelPath = kernelPath;
		_functionNames = new ArrayList<>(functionNames);
		_kernels = new CLKernel[functionNames.size()];
	}
	
	public void dispose(CLCommandQueue queue)
	{
		for(CLKernel k : _kernels)
		{
			k.release();
		}
		_kernels = null;
		_program.release();
		_program = null;
		_isInitialized = false;
	}
	
	public String toString()
	{
		return "KernelControlJOCL";
	}
	
	public void setKernelPath(String kernelPath)
	{
		_kernelPath = kernelPath;
	}
	
	public void addFunction(String functionName)
	{
		_functionNames.add(functionName);
	}
	
	public boolean isInitialized()
	{
		return _isInitialized;
	}
	
	public void init(CLCommandQueue queue)
	{
		CLGLContext context = (CLGLContext)queue.getContext();
		if(_kernels==null)
			_kernels = new CLKernel[_functionNames.size()];
			
		try
		{
			URLConnection conn = IOUtil.getResource(_kernelPath, this.getClass().getClassLoader());
			_program = context.createProgram(conn.getInputStream());
			_program.build();
			System.out.println(_program.getBuildStatus());
			System.out.println(_program.getBuildLog());
			if(!_program.isExecutable())
				throw new RuntimeException("OpenCL kernel program build error.");
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return;
		}
		catch(RuntimeException e)
		{
			e.printStackTrace();
			return;
		}
		

		_isInitialized = true;
		System.out.println("OpenCL initialized.");
	}
}
