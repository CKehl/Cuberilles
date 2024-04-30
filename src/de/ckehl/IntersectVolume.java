package de.ckehl;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.RayAabIntersection;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;

public class IntersectVolume extends Intersections {
	int _dimx, _dimy, _dimz;
	float _spacex, _spacey, _spacez;
	Vector3f _start;
	Ray _r;
	List<Vector3i> _intersectionCubes = null;
	Matrix4f _ModelMatrix=null, _ViewMatrix=null;
	
	public IntersectVolume()
	{
		super();
		_r = new Ray();
		_start = new Vector3f();
		_intersectionCubes = new ArrayList<>();
	}
	
	public void setDimensions(int x, int y, int z)
	{
		_dimx = x;
		_dimy = y;
		_dimz = z;
	}
	
	public void setSpacing(float x, float y, float z)
	{
		_spacex = x;
		_spacey = y;
		_spacez = z;
	}
	
	public void setCubeTranslation(float translateX, float translateY, float translateZ)
	{
		_start.set(translateX, translateY, translateZ);
	}
	
	public void setRay(Ray intersectionRay)
	{
		_r = intersectionRay;
		//_intersectionCubes.clear();
	}
	
	public void setModelMatrix(Matrix4f mat)
	{
		_ModelMatrix = mat;
	}
	
	public void setViewMatrix(Matrix4f mat)
	{
		_ViewMatrix = mat;
	}
	
	public List<Vector3i> computeIntersections()
	{
		_intersectionCubes.clear();
		
		RayAabIntersection intersectionCalculation = new RayAabIntersection();
		Ray r_use;
		System.out.println(_r.toString());
		if((_ModelMatrix!=null) && (_ViewMatrix!=null))
		{
			Matrix4f transform = new Matrix4f(), mInv = new Matrix4f(), vInv = new Matrix4f();
			_ModelMatrix.invert(mInv);
			_ViewMatrix.invert(vInv);
			//transform.mul(_ViewMatrix, _ModelMatrix).invert();
			mInv.mul(vInv, transform);
			Vector4f rayOrigin_in = new Vector4f(_r._orig.x, _r._orig.y, _r._orig.z, 1.0f), rayOrigin_out = new Vector4f();
			Vector3f rayDir_in = new Vector3f(_r._dir.x, _r._dir.y, _r._dir.z), rayDir_out = new Vector3f();
			
			transform.transform(rayOrigin_in, rayOrigin_out);
			transform.transformDirection(rayDir_in, rayDir_out);
			//rayOrigin_out = transform.transform(rayOrigin_in);
			//rayDir_out = transform.transformDirection(rayDir_in);
			rayDir_out.normalize();
			r_use = new Ray(new Vector3f(rayOrigin_out.x, rayOrigin_out.y, rayOrigin_out.z), new Vector3f(rayDir_out.x, rayDir_out.y, rayDir_out.z));
			intersectionCalculation.set(r_use._orig.x, r_use._orig.y, r_use._orig.z, r_use._dir.x, r_use._dir.y, r_use._dir.z);
			
			System.out.println("use m-v matrix compute");
		}
		else
		{
			r_use = new Ray(new Vector3f(_r._orig.x, _r._orig.y, _r._orig.z), new Vector3f(_r._dir.x, _r._dir.y, _r._dir.z));
			intersectionCalculation.set(r_use._orig.x, r_use._orig.y, r_use._orig.z, r_use._dir.x, r_use._dir.y, r_use._dir.z);
			
			System.out.println("use original ray");
		}
		System.out.println(r_use.toString());
		for(int i = 0; i < _dimx; i++)
		{
			for(int j = 0; j < _dimy; j++)
			{
				for(int k = 0; k < _dimz; k++)
				{
					Vector3f vmin = new Vector3f();
					vmin.x = _start.x + (((float)i)*_spacex) - (0.5f*_spacex);
					vmin.y = _start.y + (((float)j)*_spacey) - (0.5f*_spacey);
					vmin.z = _start.z + (((float)k)*_spacez) - (0.5f*_spacez);
					Vector3f vmax = new Vector3f();
					vmax.x = _start.x + (((float)i)*_spacex) + (0.5f*_spacex);
					vmax.y = _start.y + (((float)j)*_spacey) + (0.5f*_spacey);
					vmax.z = _start.z + (((float)k)*_spacez) + (0.5f*_spacez);
					//Box3 aabb = new Box3(vmin, vmax);
					//if((i==(_dimx/2)) && (j==(_dimy/2)) && (k==(_dimz/2)))
					//	System.out.println(aabb.toString());
					if(intersectionCalculation.test(vmin.x, vmin.y, vmin.z, vmax.x, vmax.y, vmax.z)==true)
					{
						_intersectionCubes.add(new Vector3i(i,j,k));
					}
				}
			}
		}
		return _intersectionCubes;
	}
}
