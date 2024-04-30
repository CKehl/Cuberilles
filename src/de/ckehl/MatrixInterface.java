package de.ckehl;

public interface MatrixInterface {

	public static final int ROTATE_ORDER_XYZ = 0;
	public static final int ROTATE_ORDER_ZYX = 1;
	public static final int ROTATE_ORDER_YXZ = 2;
	
	public void setRotateOrderXYZ();
	public void setRotateOrderZYX();
	public void setRotateOrderYXZ();
	
	public int getRotateOrder();
}
