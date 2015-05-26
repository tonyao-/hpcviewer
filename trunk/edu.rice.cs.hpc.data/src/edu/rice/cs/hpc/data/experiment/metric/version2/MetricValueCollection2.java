package edu.rice.cs.hpc.data.experiment.metric.version2;

import edu.rice.cs.hpc.data.experiment.metric.IMetricValueCollection;
import edu.rice.cs.hpc.data.experiment.metric.MetricValue;

/*********************************************************************
 * 
 * The implementation of {@link IMetricValueCollection} for database
 * version 2 format (uncompact format)
 *
 *********************************************************************/
public class MetricValueCollection2 implements IMetricValueCollection 
{
	final private MetricValue []values;

	public MetricValueCollection2(int size) {
		values = new MetricValue[size];
	}
	
	@Override
	public MetricValue getValue(int index) {
		if (values != null) {
			final MetricValue mv = values[index];
			if (mv != null) {
				return mv;
			}
		}
		return MetricValue.NONE;
	}

	@Override
	public float getAnnotation(int index) {
		if (values[index] != null) {
			return MetricValue.getAnnotationValue(values[index]);
		}
		return 0;
	}

	@Override
	public void setValue(int index, MetricValue value) {
		values[index] = value;
	}

	@Override
	public void setAnnotation(int index, float annotation) {
		MetricValue.setAnnotationValue(values[index], annotation);
	}

	@Override
	public boolean isValueAvailable(int index) {
		return MetricValue.isAvailable(values[index]);
	}

	@Override
	public boolean isAnnotationAvailable(int index) {
		return MetricValue.isAnnotationAvailable(values[index]);
	}

	@Override
	public int size() {
		if (values != null)
			return values.length;
		else
			return 0;
	}

	@Override
	public void dispose() {
	}
}
