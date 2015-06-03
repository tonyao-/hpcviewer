package edu.rice.cs.hpc.data.experiment.metric.version3;

import java.io.IOException;

import edu.rice.cs.hpc.data.experiment.BaseExperimentWithMetrics;
import edu.rice.cs.hpc.data.experiment.metric.BaseMetric;
import edu.rice.cs.hpc.data.experiment.metric.DerivedMetric;
import edu.rice.cs.hpc.data.experiment.metric.IMetricValueCollection;
import edu.rice.cs.hpc.data.experiment.metric.MetricValue;
import edu.rice.cs.hpc.data.experiment.scope.RootScope;
import edu.rice.cs.hpc.data.experiment.scope.Scope;


/******************************************************************
 * 
 * The implementation of {@link IMetricValueCollection} for 
 * database version 3 (the compact version).
 * 
 * This class is designed to read metric values when needed.
 * If the scope is never asked for metric value, no access to
 * the database file will occur.
 * 
 * The current version is a draft implementation, it is not well
 * optimized yet. 
 *
 ******************************************************************/
public class MetricValueCollection3 implements IMetricValueCollection 
{
	final private DataSummary data;
	final private RootScope root;
	private MetricValue []values;
	
	public MetricValueCollection3(DataSummary data, RootScope root, Scope scope) throws IOException
	{
		this.data 	 = data;
		this.root	 = root;
	}
	
	@Override
	public MetricValue getValue(Scope scope, int index) 
	{
		if (values == null)
		{
			// create and initialize the first metric values instance
			BaseExperimentWithMetrics exp = (BaseExperimentWithMetrics) root.getExperiment();
			int metric_size = exp.getMetricCount();
			
			// initialize
			try {
				values = data.getMetrics(scope.getCCTIndex(), exp);
				if (values != null && values.length>0)
				{
					// compute the percent annotation
					for(int i=0; i<metric_size; i++)
					{
						if (values[i] != MetricValue.NONE)
						{
							MetricValue mv = root.getMetricValue(i);
							if (mv != MetricValue.NONE)
							{
								float percent = 100 * (values[i].getValue()/mv.getValue());
								MetricValue.setAnnotationValue(values[i], percent);
							}
						}
					}
					return values[index];
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else 
		{
			// metric values already exist
			if (index < values.length) {
				return values[index];
			} else {
				// metric values already exist, but the index is bigger than the standard values
				// this must be a derived metric
				BaseExperimentWithMetrics exp = (BaseExperimentWithMetrics) root.getExperiment();
				BaseMetric metric = exp.getMetric(index);
				if (metric instanceof DerivedMetric)
				{
					return ((DerivedMetric)metric).getValue(scope);
				}
			}
		}
		return MetricValue.NONE;
	}

	@Override
	public float getAnnotation(int index) {
		MetricValue mv = values[index];
		return MetricValue.getAnnotationValue(mv);
	}

	@Override
	public void setValue(int index, MetricValue value) {
		if (values != null) {
			// If the index is out of array bound, it means we want to add a new derived metric.
			// We will compute the derived value on the fly instead of storing it.
			if (index < values.length) {
				values[index]  = value;
			}
		}
	}

	@Override
	public void setAnnotation(int index, float ann) {
		MetricValue value = values[index];
		MetricValue.setAnnotationValue(value, ann);
	}


	@Override
	public int size() {
		BaseExperimentWithMetrics exp = (BaseExperimentWithMetrics) root.getExperiment();
		return exp.getMetricCount();
	}

	@Override
	public void dispose() {

	}

}
