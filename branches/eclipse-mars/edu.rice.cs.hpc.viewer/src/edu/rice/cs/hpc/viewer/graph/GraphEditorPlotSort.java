package edu.rice.cs.hpc.viewer.graph;

import java.io.IOException;

import edu.rice.cs.hpc.data.experiment.metric.MetricRaw;
import edu.rice.cs.hpc.data.experiment.scope.Scope;

public class GraphEditorPlotSort extends GraphEditor {

    public static final String ID = "edu.rice.cs.hpc.viewer.graph.GraphEditorPlotSort";


	@Override
	protected double[] getValuesX(Scope scope, MetricRaw metric) {

		double x_values[] = threadData.getProcessIDsDouble(metric.getID());
		double sequence_x[] = new double[x_values.length];
		for (int i=0; i<x_values.length; i++) {
			sequence_x[i] = (double) i;
		}
		return sequence_x;
	}



	@Override
	protected double[] getValuesY(Scope scope, MetricRaw metric) throws IOException {

		double y_values[] = null;
		{
			y_values = threadData.getMetrics( metric, scope.getCCTIndex());
			
			java.util.Arrays.sort(y_values);
		}			
		return y_values;
	}



	@Override
	protected String getXAxisTitle() {
		return "Rank in Sorted Order";
	}

	

}
