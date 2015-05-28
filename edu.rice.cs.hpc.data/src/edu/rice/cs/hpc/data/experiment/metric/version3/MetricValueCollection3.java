package edu.rice.cs.hpc.data.experiment.metric.version3;

import java.io.IOException;

import edu.rice.cs.hpc.data.experiment.metric.IMetricValueCollection;
import edu.rice.cs.hpc.data.experiment.metric.MetricValue;

public class MetricValueCollection3 implements IMetricValueCollection 
{
	final private DataSummary data;
	final private int cct_index;
	final private float []root_values;
	
	public MetricValueCollection3(DataSummary data, float []root_values, int cct_index) throws IOException
	{
		this.data 		 = data;
		this.cct_index 	 = cct_index;
		this.root_values = root_values;
	}
	
	@Override
	public MetricValue getValue(int index) {
		try {
			float val = data.getMetric(cct_index, index);
			if (val != DataSummary.DEFAULT_METRIC)
			{
				float percent = 100 * (val/root_values[index]);
				return new MetricValue(val, percent);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return MetricValue.NONE;
	}

	@Override
	public float getAnnotation(int index) {
		MetricValue mv = getValue(index);
		return MetricValue.getAnnotationValue(mv);
	}

	@Override
	public void setValue(int index, MetricValue value) {
		System.err.println("setValue unsupported");
	}

	@Override
	public void setAnnotation(int index, float ann) {
		System.err.println("setAnnotation unsupported");
	}

	@Override
	public boolean isValueAvailable(int index) {
		return getValue(index) == MetricValue.NONE;
	}

	@Override
	public boolean isAnnotationAvailable(int index) {
		MetricValue mv = getValue(index);
		return MetricValue.isAnnotationAvailable(mv);
	}

	@Override
	public int size() {
		return root_values.length;
	}

	@Override
	public void dispose() {

	}

}
