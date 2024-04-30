package de.ckehl;

public interface SliceSelectionInterface {
	public void showHistogramXY(int z);
	public void showHistogramXZ(int y);
	public void showHistogramYZ(int x);
	public void graphHistogramSelection(int[] subHistogram);
}
