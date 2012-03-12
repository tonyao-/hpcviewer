//////////////////////////////////////////////////////////////////////////
//																		//
//	ExperimentMerger.java												//
//																		//
//	ExperimentMerger -- class to merge two Experiments					//
//	Created: May 7, 2007 												//
//																		//
//	(c) Copyright 2007-2012 Rice University. All rights reserved.		//
//																		//
//////////////////////////////////////////////////////////////////////////
package edu.rice.cs.hpc.data.experiment.merge;

import java.io.File;
import java.util.*;

import edu.rice.cs.hpc.data.experiment.Experiment;
import edu.rice.cs.hpc.data.experiment.ExperimentConfiguration;
import edu.rice.cs.hpc.data.experiment.metric.*;
import edu.rice.cs.hpc.data.experiment.scope.*;
import edu.rice.cs.hpc.data.experiment.scope.visitors.*;

/****
 * Merging experiments
 *
 * Steps:
 * 
 * 1. create the new experiment 
 * 2. add metrics 		-->	add into metric list 
 * 3. add raw metrics 	-->	add into a list
 * 4. add trace data 	-->	add into a list
 * 5. merge the experiments
 */
public class ExperimentMerger {
	
	static public Experiment merge(Experiment exp1, Experiment exp2) {
		
		// -----------------------------------------------
		// step 1: create new base Experiment
		// -----------------------------------------------
		Experiment merged = exp1.duplicate();
		
		final ExperimentConfiguration configuration = new ExperimentConfiguration();
		configuration.setName( exp1.getName() + " &  " + exp2.getName() );
		configuration.searchPaths = exp1.getConfiguration().searchPaths;
		
		merged.setConfiguration( configuration );


		// Add tree1, walk tree2 & add; just CCT/Flat
		RootScope rootScope = new RootScope(merged, "Merged Experiment","Invisible Outer Root Scope", RootScopeType.Invisible);
		merged.setRootScope(rootScope);
				
		// -----------------------------------------------
		// step 2: append metricList
		// -----------------------------------------------
		List<BaseMetric> metrics = buildMetricList(merged, exp1.getMetrics(), exp2.getMetrics());
		merged.setMetrics(metrics);

		// -----------------------------------------------
		// step 3: mark the new experiment file
		// -----------------------------------------------
		File file1 = exp1.getXMLExperimentFile();
		final File fileMerged  = new File( file1.getParent()+ "/merged.xml"); 
		merged.setXMLExperimentFile( fileMerged );
		System.out.println("EM create merged file: " + fileMerged.getAbsolutePath());

		// -----------------------------------------------
		// step 4: create cct root
		// -----------------------------------------------		

		// -----------------------------------------------
		// step 5: merge the two experiments
		// -----------------------------------------------

		mergeScopeTrees(exp1,new DuplicateScopeTreesVisitor(rootScope));		
		
		RootScope root1 = (RootScope) merged.getRootScopeChildren()[0];		
		RootScope root2 = (RootScope) exp2.getRootScopeChildren()[0];		
		
		final int metricCount = exp1.getMetricCount();
		final TreeSimilarity similar = new TreeSimilarity(metricCount, root1, root2);
		
		return merged;
	}
	
	
	private static Vector<BaseMetric> buildMetricList(Experiment exp, BaseMetric[] m1, BaseMetric[] m2) {
		final Vector<BaseMetric> metricList = new Vector<BaseMetric>();
		
		for (int i=0; i<m1.length; i++) {
			metricList.add(m1[i]);
		}
		
		final int m1_last_index = m1[m1.length-1].getIndex();
		
		for (int i=0; i<m2.length; i++) {
			final BaseMetric m = m2[i].duplicate();
			
			// recompute the index of the metric from the second experiment
			m.setIndex( m1_last_index + m.getIndex() );
			
			metricList.add(m);
		}
		
		return metricList;
	}


	
	private static void mergeScopeTrees(Experiment exp2, 
			BaseDuplicateScopeTreesVisitor visitor) {

		RootScope root2 = (RootScope) exp2.getRootScopeChildren()[0];		

		root2.dfsVisitScopeTree(visitor);
	}	
}

