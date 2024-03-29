//////////////////////////////////////////////////////////////////////////
//																		//
//	Metric.java															//
//																		//
//	experiment.Metric -- a metric and its data in an experiment			//
//	Last edited: January 15, 2002 at 12:37 am							//
//																		//
//	(c) Copyright 2002 Rice University. All rights reserved.			//
//																		//
//////////////////////////////////////////////////////////////////////////




package edu.rice.cs.hpc.data.experiment.metric;


import edu.rice.cs.hpc.data.experiment.metric.MetricType;
import edu.rice.cs.hpc.data.experiment.scope.Scope;



//////////////////////////////////////////////////////////////////////////
//	CLASS METRIC														//
//////////////////////////////////////////////////////////////////////////

 /**
 *
 * A metric and its data in an HPCView experiment.
 *
 */


public class Metric extends BaseMetric
{

public final static int NO_PARTNER_INDEX = -1;


//////////////////////////////////////////////////////////////////////////
//	INITIALIZATION														//
//////////////////////////////////////////////////////////////////////////




/*************************************************************************
 *	Creates a Metric.
 ************************************************************************/
	
/**
 * Construct a metric using a "String" sample period
 * @param shortName
 * @param nativeName
 * @param displayName
 * @param displayed
 * @param format
 * @param annotationType
 * @param sampleperiod
 * @param metricType
 * @param partnerIndex
 */
public Metric(String shortName, String nativeName, String displayName, boolean displayed, 
              String format, AnnotationType annotationType, String samplePeriod, 
              int index, MetricType metricType, int partnerIndex)
{
	super(shortName, displayName, displayed, format, annotationType, index, partnerIndex, metricType);
	// creation arguments
	this.nativeName  = nativeName;
    this.sampleperiod  = this.convertSamplePeriode(samplePeriod);
    this.metricType     = metricType;
}



/*************************************************************************
 *	Returns the value of this metric at a given scope.
 ************************************************************************/	
public MetricValue getValue(Scope s)
{
	return s.getMetricValue(this);
}



//@Override
public BaseMetric duplicate() {
	return new Metric(shortName, nativeName, displayName, displayed, null, annotationType, 
			String.valueOf(sampleperiod), index, metricType, partner_index);
}

}








