package edu.rice.cs.hpc.viewer.graph;

import java.io.IOException;
import java.text.DecimalFormat;

import org.swtchart.IAxisSet;
import org.swtchart.IAxisTick;

import edu.rice.cs.hpc.data.experiment.metric.MetricRaw;
import edu.rice.cs.hpc.data.experiment.scope.Scope;
import edu.rice.cs.hpc.viewer.metric.IThreadDataCollection;
import edu.rice.cs.hpc.viewer.metric.ThreadLevelDataFile;
import edu.rice.cs.hpc.viewer.metric.ThreadLevelDataManager;

public class GraphEditorPlot extends GraphEditor {

    public static final String ID = "edu.rice.cs.hpc.viewer.graph.GraphEditorPlot";
    
	@Override
	protected double[] getValuesX(Scope scope, MetricRaw metric) 
	throws NumberFormatException {

		double []x_values = threadData.getProcessIDsDouble( metric.getID() );				
		return x_values;
	}

	@Override
	protected double[] getValuesY(Scope scope, MetricRaw metric) throws IOException {
		{
			double []y_values = threadData.getMetrics( metric, scope.getCCTIndex());
			return y_values;
		}
	}


	@Override
	protected String getXAxisTitle() {
		IAxisSet axisSet = this.getChart().getAxisSet();
		IAxisTick xTick = axisSet.getXAxis(0).getTick();

		xTick.setFormat(new DecimalFormat("##########"));

		IThreadDataCollection  threadCol = threadData.getThreadCollection();
		if (threadCol.getParallelismLevel()>1) 
		{
			xTick.setFormat(new DecimalFormat("######00.00##"));
		}

		return threadCol.getRankTitle();
	}

}
