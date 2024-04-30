package de.ckehl;

import org.joml.Vector2i;

public interface BaseLevelAdaptorInterface {
	public void centerValue(int value, int occurence);
	public void updateValueBounds(int min, int max);
	public void markValues(Vector2i range);
	public void resetHistogramSelection();
}
