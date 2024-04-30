package de.ckehl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector4f;

import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLException;
import com.jogamp.opencl.CLKernel;
import com.jogamp.opencl.CLPlatform;
import com.jogamp.opencl.gl.CLGLBuffer;
import com.jogamp.opencl.gl.CLGLContext;
import com.jogamp.opencl.llb.CL;
import com.jogamp.opengl.GL3;
import com.jogamp.opencl.CLResource;
import com.jogamp.opencl.CLMemory.Mem.*;

public class DistanceSortKernel extends KernelControlJOCL {
	protected CLGLBuffer<FloatBuffer> _centreBuffer = null;
	protected CLGLBuffer<IntBuffer> _indexBuffer = null;
	protected CLBuffer<FloatBuffer> _distanceBuffer = null;
	protected CLBuffer<IntBuffer> _inputIndexBuffer = null;
	protected IntBuffer mBuffer;
	protected Vector4f _eye_world = null;
	protected int _arraySize;
	protected int workgroupSize;
	
	public DistanceSortKernel()
	{
		super();
	}
	
	public DistanceSortKernel(String kernelPath)
	{
		super(kernelPath);
		addFunction("computeDistance");
		addFunction("sort");
	}
	
	/*
	public DistanceSortKernel(String kernelPath, List<String> functionNames)
	{
		super(kernelPath, functionNames);
	}
	*/
	
	public void setEyeCoordinate(Vector4f eye)
	{
		_eye_world = new Vector4f(eye);
	}
	
	@Override
	public void dispose(CLCommandQueue queue)
	{
		super.dispose(queue);
	}
	
	public void init(CLCommandQueue queue, int arraySize, int vboCentres, int vboIndices, int inputIndices[])
	{
		if(_isInitialized==false)
		{
			super.init(queue);
		}
		CLGLContext context = (CLGLContext)queue.getContext();
		
		_arraySize = arraySize;
		_centreBuffer = (CLGLBuffer<FloatBuffer>)context.createFromGLBuffer(vboCentres, _arraySize*3*VersionHelper.FloatBYTES(), CLGLBuffer.Mem.READ_ONLY);
		System.out.println("clsize: "+_centreBuffer.getCLSize());
		System.out.println("cl buffer type: " + _centreBuffer.getGLObjectType());
		System.out.println("shared with gl buffer: " + _centreBuffer.getGLObjectID());
		_indexBuffer = (CLGLBuffer<IntBuffer>)context.createFromGLBuffer(vboIndices, _arraySize*VersionHelper.IntBYTES(), CLGLBuffer.Mem.READ_WRITE);
		System.out.println("clsize: "+_indexBuffer.getCLSize());
		System.out.println("cl buffer type: " + _indexBuffer.getGLObjectType());
		System.out.println("shared with gl buffer: " + _indexBuffer.getGLObjectID());
		_distanceBuffer = context.createFloatBuffer(_arraySize*VersionHelper.FloatBYTES(), CLBuffer.Mem.READ_WRITE);
		/*
		_inputIndexBuffer = context.createIntBuffer(_arraySize*VersionHelper.IntBYTES(), CLGLBuffer.Mem.READ_WRITE);
		mBuffer = (IntBuffer)(_inputIndexBuffer.getBuffer().rewind());
		mBuffer.put(inputIndices);
		mBuffer.rewind();
		queue.putWriteBuffer(_inputIndexBuffer, true);
		queue.finish();
		*/
		
		/*----------------- TO DO -------------
		 * WORK OUT HOW TO ALLOCATE THE LOCAL 
		 * AUXILLIARY BUFFER
		 */
		
		int POT = Integer.highestOneBit(_arraySize)<<1;
		System.out.println("Highest next power-of-two: "+Integer.toString(POT));
		int k = 256, r = POT % k;
		while(r>0)
		{
			k/=2;
			r = POT % k;
		}
		workgroupSize = k;
		System.out.printf("workgroup size: %d\n", workgroupSize);
		int data_t_size = VersionHelper.FloatBYTES()+VersionHelper.IntBYTES();
		
		_kernels[0] = _program.createCLKernel(_functionNames.get(0)).putArg(_centreBuffer).putArg(_distanceBuffer).putArg(_arraySize).rewind();
		//_kernels[1] = _program.createCLKernel(_functionNames.get(1)).putArg(_distanceBuffer).putArg(_indexBuffer).putArg(_inputIndexBuffer).putArg(_arraySize).putNullArg(workgroupSize*data_t_size).rewind(); //.putArgSize(workgroupSize*data_t_size)
		_kernels[1] = _program.createCLKernel(_functionNames.get(1)).putArg(_distanceBuffer).putArg(_indexBuffer).putArg(_arraySize).putNullArg(workgroupSize*data_t_size).rewind();
		//CL cl = CLPlatform.getLowLevelCLInterface();
		//cl.clSetKernelArg(_kernels[1].getID(), 4, workgroupSize*data_t_size, null);
	}
	
	public void compute(CLCommandQueue queue)
	{
		
		if(_eye_world == null)
			return;
		
		if((_kernels!=null) && (_kernels[0]!=null))
		{
		_kernels[0].setArg(3, _eye_world.x);
		_kernels[0].setArg(4, _eye_world.y);
		_kernels[0].setArg(5, _eye_world.z);
		}
		
		int POT = Integer.highestOneBit(_arraySize)<<1;
		try
		{
			if(queue==null)
				throw new CLException("CommandQueue is null.");
			if(queue.isReleased())
				throw new CLException("CommandQueue is released.");
			
			queue.putAcquireGLObject(_centreBuffer).put1DRangeKernel(_kernels[0], 0, POT, workgroupSize).putReleaseGLObject(_centreBuffer);
			queue.finish();
			queue.putAcquireGLObject(_indexBuffer).put1DRangeKernel(_kernels[1], 0, POT, workgroupSize).putReleaseGLObject(_indexBuffer);
			queue.finish();
		}
		catch(CLException e)
		{
			e.printStackTrace();
		}
	}
}
