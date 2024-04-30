package de.ckehl;

import org.joml.Vector2i;

public interface HistogramSelectionInterface {
	public void center(int value, int occurence);
	public void markValues(Vector2i range);
	public void resetHistogramSelection();
}
