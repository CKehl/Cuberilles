package de.ckehl;

import org.joml.Vector3f;

public enum PlaneViewpoint {
	
	/*
	FRONT(0, new Vector3f(0,0,1), "FRONT", Texture3DPane.XY_PANE, "XY_PANE"),
	BACK(1, new Vector3f(0,0,-1), "BACK", Texture3DPane.MINUS_XY_PANE, "MINUS_XY_PANE"),
	LEFT(2, new Vector3f(-1,0,0), "LEFT", Texture3DPane.MINUS_YZ_PANE, "MINUS_YZ_PANE"),
	RIGHT(3, new Vector3f(1,0,0), "RIGHT", Texture3DPane.YZ_PANE, "YZ_PANE"),
	TOP(4, new Vector3f(0,1,0), "TOP", Texture3DPane.XZ_PANE, "XZ_PANE"),
	BOTTOM(5, new Vector3f(0,-1,0), "BOTTOM", Texture3DPane.MINUS_XZ_PANE, "MINUS_XZ_PANE");
	*/
	FRONT(0, new Vector3f(-1,1,1), "FRONT", Texture3DPane.XY_PANE, "XY_PANE"),
	BACK(1, new Vector3f(1,-1,-1), "BACK", Texture3DPane.MINUS_XY_PANE, "MINUS_XY_PANE"),
	LEFT(2, new Vector3f(-1,1,-1), "LEFT", Texture3DPane.MINUS_YZ_PANE, "MINUS_YZ_PANE"),
	RIGHT(3, new Vector3f(1,-1,1), "RIGHT", Texture3DPane.YZ_PANE, "YZ_PANE"),
	TOP(4, new Vector3f(1,1,0), "TOP", Texture3DPane.XZ_PANE, "XZ_PANE"),
	BOTTOM(5, new Vector3f(-1,-1,0), "BOTTOM", Texture3DPane.MINUS_XZ_PANE, "MINUS_XZ_PANE");
	
	private final int _index;
	private final Vector3f _center;
	private final String _name;
	private final short _paneNumber;
	private final String _paneName;
	
	PlaneViewpoint(int index, Vector3f center, String name, Short paneNumber, String paneName)
	{
		this._index = index;
		this._center = center;
		this._name = name;
		this._paneNumber = paneNumber;
		this._paneName = paneName;
	}
	
	public Vector3f center() { return _center; }
	public Short paneNumber() { return _paneNumber; }
	public Integer index() { return _index; }
	
	/**
	 * Gives (squared) distance to request vector,
	 * for closest-by comparison reasons
	 * @param v - input vector
	 * @return squared distance (float)
	 */
	public float distance(Vector3f v)
	{
		return _center.distanceSquared(v);
	}
	
	public void dispose()
	{

	}
	
	public boolean equals(PlaneViewpoint p)
	{
		return ((_index == p.index()) && (_paneNumber==p.paneNumber().shortValue()));
	}
	
	public String toString()
	{
		return _name+" ("+_paneName+")";
	}
	
	/**
	 * Tests all plane viewpoints and returns the closest
	 * @param input - request position (Vector3f)
	 * @return plane viewpoint with closest center (PlaneViewpoint)
	 */
	public static PlaneViewpoint determinePlaneViewpoint(Vector3f input)
	{
		PlaneViewpoint result = FRONT;
		float min_distance = FRONT.distance(input);
		float angle_alpha = FRONT.center().angle(input);
		
		if(BACK.distance(input)<min_distance)
		//if(BACK.center().angle(input)<angle_alpha)
		{
			result = BACK;
			min_distance = BACK.distance(input);
			angle_alpha = BACK.center().angle(input);
		}
		
		if(LEFT.distance(input)<min_distance)
		//if(LEFT.center().angle(input)<angle_alpha)
		{
			result = LEFT;
			min_distance = LEFT.distance(input);
			angle_alpha = LEFT.center().angle(input);
		}
		
		if(RIGHT.distance(input)<min_distance)
		//if(RIGHT.center().angle(input)<angle_alpha)
		{
			result = RIGHT;
			min_distance = RIGHT.distance(input);
			angle_alpha = RIGHT.center().angle(input);
		}
		
		if(TOP.distance(input)<min_distance)
		//if(TOP.center().angle(input)<angle_alpha)
		{
			result = TOP;
			min_distance = TOP.distance(input);
			angle_alpha = TOP.center().angle(input);
		}
		
		if(BOTTOM.distance(input)<min_distance)
		//if(BOTTOM.center().angle(input)<angle_alpha)
		{
			result = BOTTOM;
			min_distance = BOTTOM.distance(input);
			angle_alpha = BOTTOM.center().angle(input);
		}
		
		return result;
	}
}
