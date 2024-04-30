package de.ckehl;

import java.text.DecimalFormat;

import org.joml.Vector3f;

class Ray
{
	public Vector3f _orig, _dir, _invdir;
	public int sign[] = {0, 0, 0};
	
	public Ray()
	{
		_orig = new Vector3f();
		_dir = new Vector3f();
		_invdir = new Vector3f();
	}
	
	public Ray(Vector3f orig, Vector3f dir)
	{
		_orig = orig; _dir = dir;
		_invdir = new Vector3f();
		_invdir.x = 1.0f / dir.x; _invdir.y = 1.0f / dir.y; _invdir.z = 1.0f / dir.z;
		
		if(_invdir.x < 0f)
			sign[0] = 1;
		else
			sign[0] = 0;
		
		if(_invdir.y < 0f)
			sign[1] = 1;
		else
			sign[1] = 0;
		
		if(_invdir.z < 0f)
			sign[2] = 1;
		else
			sign[2] = 0;
	}
	
	public String toString()
	{
		return "Ray: {o:("+_orig.toString(new DecimalFormat( "#,###,###,##0.00" ))+"), d:("+_dir.toString(new DecimalFormat( "#,###,###,##0.00" ))+")}";
	}
}

class Box3
{
	public Vector3f bounds[] = new Vector3f[2];
	
	public Box3()
	{
		
	}
	
	public Box3(Vector3f vmin, Vector3f vmax)
	{
		bounds[0] = vmin;
		bounds[1] = vmax;
	}
	
	public String toString()
	{
		return "Box: {min:("+bounds[0].toString(new DecimalFormat( "#,###,###,##0.00" ))+"), max:("+bounds[1].toString(new DecimalFormat( "#,###,###,##0.00" ))+")}";
	}
};

class Sphere
{
	public Vector3f c;
	public float r;
	
	public Sphere()
	{
		
	}
	
	public Sphere(Vector3f center, float radius)
	{
		c = center;
		r = radius;
	}
	
	public String toString()
	{
		return "Sphere: {c:("+c.toString(new DecimalFormat( "#,###,###,##0.000" ))+"), r: "+Float.toString(r)+"}";
	}
}


public class Intersections {
	
	public static boolean intersectBoxRay(Box3 b, Ray r) 
	{ 
	    float tmin, tmax, tymin, tymax, tzmin, tzmax; 
	 
	    tmin = (b.bounds[r.sign[0]].x - r._orig.x) * r._invdir.x; 
	    tmax = (b.bounds[1-r.sign[0]].x - r._orig.x) * r._invdir.x; 
	    tymin = (b.bounds[r.sign[1]].y - r._orig.y) * r._invdir.y; 
	    tymax = (b.bounds[1-r.sign[1]].y - r._orig.y) * r._invdir.y; 
	 
	    if ((tmin > tymax) || (tymin > tmax)) 
	        return false; 
	    if (tymin > tmin) 
	        tmin = tymin; 
	    if (tymax < tmax) 
	        tmax = tymax; 
	 
	    tzmin = (b.bounds[r.sign[2]].z - r._orig.z) * r._invdir.z; 
	    tzmax = (b.bounds[1-r.sign[2]].z - r._orig.z) * r._invdir.z; 
	 
	    if ((tmin > tzmax) || (tzmin > tmax)) 
	        return false; 
	    if (tzmin > tmin) 
	        tmin = tzmin; 
	    if (tzmax < tmax) 
	        tmax = tzmax; 
	 
	    return true; 
	}
	
	/*
	public static boolean intersectSphereRay(Sphere s, Ray r)
	{
		float t0, t1;
		Vector3f L = new Vector3f();
		s.c.sub(r._orig, L);
		float tca = L.dot(r._dir);
		System.out.println("tca: "+Float.toString(tca));
		if (tca < 0) return false;
		float d2 = L.dot(L) - (tca * tca);
		System.out.println("d_square: "+Float.toString(d2)+", r_square: "+Float.toString((s.r*s.r)));
		if (d2 > (s.r*s.r)) return false;
		float thc = (float)Math.sqrt((s.r*s.r) - d2);
		System.out.println("thc: "+Float.toString(thc));
		t0 = tca - thc;
		t1 = tca + thc;

		if (t0 > t1){
			float temp = t0;
			t0 = t1;
			t1 = temp;
		}

		if (t0 < 0) {
			t0 = t1; // if t0 is negative, let's use t1 instead
			if (t0 < 0) return false; // both t0 and t1 are negative
		}
		
		float t = t0;

		return true;
	}
	*/
	
	public static boolean intersectSphereRay(Sphere s, Ray r)
	{
		float t0, t1;
		Vector3f L = new Vector3f();
		s.c.sub(r._orig, L);
		Vector3f pc = new Vector3f();
		L.mul(L.dot(r._dir)/L.length(),pc);
		pc.add(r._orig);
		Vector3f pc_c = new Vector3f();
		s.c.sub(pc, pc_c);
		if(pc_c.length()>s.r)
			return false;
		Vector3f c_pc = new Vector3f();
		pc.sub(s.c,c_pc);
		float d2 = (s.r * s.r) - c_pc.lengthSquared();
		if(L.lengthSquared() > (s.r*s.r))
		{
			t0 = (float)Math.sqrt(pc.sub(r._orig).lengthSquared()-d2);
		}
		else
		{
			t0 = (float)Math.sqrt(pc.sub(r._orig).lengthSquared()+d2);
		}
		float t=t0;
		return true;
	}
}
