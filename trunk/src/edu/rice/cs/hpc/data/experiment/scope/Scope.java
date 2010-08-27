//////////////////////////////////////////////////////////////////////////
//									//
//	Scope.java							//
//									//
//	experiment.scope.Scope -- a scope in an experiment		//
//	Last edited: October 10, 2001 at 4:03 pm			//
//									//
//	(c) Copyright 2001 Rice University. All rights reserved.	//
//									//
//////////////////////////////////////////////////////////////////////////




package edu.rice.cs.hpc.data.experiment.scope;


//import java.util.ArrayList;

import edu.rice.cs.hpc.data.experiment.Experiment;
import edu.rice.cs.hpc.data.experiment.metric.AggregateMetric;
import edu.rice.cs.hpc.data.experiment.metric.BaseMetric;
//import edu.rice.cs.hpc.data.experiment.metric.Metric;
import edu.rice.cs.hpc.data.experiment.metric.MetricValue;
import edu.rice.cs.hpc.data.experiment.scope.filters.MetricValuePropagationFilter;
import edu.rice.cs.hpc.data.experiment.scope.visitors.IScopeVisitor;
import edu.rice.cs.hpc.data.experiment.source.SourceFile;
//import edu.rice.cs.hpc.data.util.*;
//import edu.rice.cs.hpc.data.experiment.metric.DerivedMetric; // laks: add derived metric feature

//import sun.tools.tree.ThisExpression;


 
//////////////////////////////////////////////////////////////////////////
//	CLASS SCOPE							//
//////////////////////////////////////////////////////////////////////////

/**
 *
 * A scope in an HPCView experiment.
 *
 * FIXME: do we want to merge the functionality of Scope and Scope.Node?
 * it's kind of irritating to have the two things be distinct and having
 * objects which point at each other makes me a little uneasy.
 */


public abstract class Scope extends Node
{
	/** The current maximum number of ID for all scopes	 */
static protected int idMax = 0;

/** The experiment owning this scope. */
protected Experiment experiment;

/** The source file containing this scope. */
protected SourceFile sourceFile;

/** the scope identifier */
protected int flat_node_index;

protected int cct_node_index;

/** The first line number of this scope. */
protected int firstLineNumber;

/** The last line number of this scope. */
protected int lastLineNumber;

/** The metric values associated with this scope. */
protected MetricValue[] metrics;
protected MetricValue[] combinedMetrics;

/** source citation */
protected String srcCitation;

/** special marker used for halting during debugging. */
protected boolean stop;

/**
 * This public variable indicates if the node contains information about the source code file.
 * If the boolean is true, then the filename can be retrieved from its scope
 * @author laksono
 */
public boolean hasSourceCodeFile;

/**
 * FIXME: this variable is only used for the creation of callers view to count
 * 			the number of instances. To be removed in the future
 */
public int iCounter = 0;
// --------------------------

static public final int SOURCE_CODE_UNKNOWN = 0;
static public final int SOURCE_CODE_AVAILABLE = 1;
static public final int SOURCE_CODE_NOT_AVAILABLE= 2;
public int iSourceCodeAvailability = Scope.SOURCE_CODE_UNKNOWN;

//////////////////////////////////////////////////////////////////////////
//	PUBLIC CONSTANTS						//
//////////////////////////////////////////////////////////////////////////


/** The value used to indicate "no line number". */
public static final int NO_LINE_NUMBER = -169; // any negative number other than -1


//////////////////////////////////////////////////////////////////////////
//	INITIALIZATION														//
//////////////////////////////////////////////////////////////////////////




/*************************************************************************
 *	Creates a Scope object with associated source line range.
 ************************************************************************/
	
public Scope(Experiment experiment, SourceFile file, int first, int last, int cct_id, int flat_id)
{
	super(cct_id);
	
	// creation arguments
	this.experiment = experiment;
	this.sourceFile = file;
	this.firstLineNumber = first;
	this.lastLineNumber = last;

	this.stop = false;
	this.srcCitation = null;
	this.flat_node_index = flat_id;
	this.cct_node_index = cct_id;
	this.hasSourceCodeFile = false;
}




/*************************************************************************
 *	Creates a Scope object with associated source file.
 ************************************************************************/
	
public Scope(Experiment experiment, SourceFile file, int scopeID)
{
	this(experiment, file, Scope.NO_LINE_NUMBER, Scope.NO_LINE_NUMBER, scopeID, scopeID);
}


public int getFlatIndex() {
	return this.flat_node_index;
}

public int getCCTIndex() {
	return (int) this.cct_node_index;
}

//////////////////////////////////////////////////////////////////////////
// DUPLICATION														//
//////////////////////////////////////////////////////////////////////////



/*************************************************************************
 *	Creates a Scope object with no associated source file.
 ************************************************************************/
	
public abstract Scope duplicate();



//////////////////////////////////////////////////////////////////////////
//	SCOPE DISPLAY														//
//////////////////////////////////////////////////////////////////////////




/*************************************************************************
 *	Returns the user visible name for this scope.
 *
 *	Subclasses should override this to implement useful names.
 *
 ************************************************************************/
	
public abstract String getName();



/*************************************************************************
 *	Returns the short user visible name for this scope.
 *
 *	This name is only used in tree views where the scope's name appears
 *	in context with its containing scope's name.
 *
 *	Subclasses may override this to implement better short names.
 *
 ************************************************************************/
	
public String getShortName()
{
	return this.getName();
}




/*************************************************************************
 *	Returns the tool tip for this scope.
 ************************************************************************/
	
public String getToolTip()
{
	return this.getSourceCitation();
}




/*************************************************************************
 *	Converts the scope to a <code>String</code>.
 *
 *	<p>
 *	This method is for the convenience of <code>ScopeTreeModel</code>,
 *	which passes <code>Scope</code> objects to the default tree cell
 *	renderer.
 *
 ************************************************************************/
	
public String toString()
{
	return this.getName();
}


public int hashCode() {
	return this.flat_node_index;
}



/*************************************************************************
 *	Returns a display string describing the scope's source code location.
 ************************************************************************/
	
protected String getSourceCitation()
{
	if (this.srcCitation == null)  {
		
		srcCitation = this.getSourceCitation(sourceFile, firstLineNumber, lastLineNumber);
	}

	return srcCitation;
}


private String getSourceCitation(SourceFile sourceFile, int line1, int line2)
{

		// some scopes such as load module, doesn't have a source code file (they are binaries !!)
		// this hack will return the name of the scope instead of the citation file
		if (sourceFile == null) {
			return this.getName();
		}
		return sourceFile.getName() + ": " + this.getLineOnlyCitation(line1, line2);

}




/*************************************************************************
 *	Returns a display string describing the scope's line number range.
 ************************************************************************/
	
protected String getLineNumberCitation()
{
	return this.getLineNumberCitation(firstLineNumber, lastLineNumber);
}


private String getLineNumberCitation(int line1, int line2)
{
	String cite;

	// we must display one-based line numbers
	int first1 = 1 + line1;
	int last1  = 1 + line2;

	if(line1 == Scope.NO_LINE_NUMBER) {
		cite = "";	// TEMPORARY: is this the right thing to do?
	} else if(line1 == line2)
		cite = "line" + " " + first1;
	else
		cite = "lines" + " " + first1 + "-" + last1;

	return cite;
}


private String getLineOnlyCitation(int line1, int line2) {
	String cite;

	// we must display one-based line numbers
	int first1 = 1 + line1;
	int last1  = 1 + line2;

	if(line1 == Scope.NO_LINE_NUMBER) {
		cite = "";	// TEMPORARY: is this the right thing to do?
	} else if(line1 == line2)
		cite = String.valueOf(first1);
	else
		cite = first1 + "-" + last1;

	return cite;
}

//////////////////////////////////////////////////////////////////////////
//	ACCESS TO SCOPE														//
//////////////////////////////////////////////////////////////////////////




/*************************************************************************
 *	Returns the source file of this scope.
 *
 *	<p>
 *	<em>TEMPORARY: This assumes that each scope "has" (i.e. intersects)
 *	at most one source file -- not true for discontiguous scopes.</em>
 *
 ************************************************************************/
	
public SourceFile getSourceFile()
{
	return this.sourceFile;
}


/*************************************************************************
 *	Returns the first line number of this scope in its source file.
 *
 *	<p>
 *	<em>TEMPORARY: This assumes that each scope "has" (i.e. intersects)
 *	at most one source file -- not true for discontiguous scopes.</em>
 *
 ************************************************************************/
	
public int getFirstLineNumber()
{
	return this.firstLineNumber;
}




/*************************************************************************
 *	Returns the last line number of this scope in its source file.
 *
 *	<p>
 *	<em>TEMPORARY: This assumes that each scope "has" (i.e. intersects)
 *	at most one source file -- not true for discontiguous scopes.</em>
 *
 ************************************************************************/
	
public int getLastLineNumber()
{
	return this.lastLineNumber;
}




//////////////////////////////////////////////////////////////////////////
//	SCOPE HIERARCHY														//
//////////////////////////////////////////////////////////////////////////




/*************************************************************************
 *	Returns the parent scope of this scope.
 ************************************************************************/
	
public Scope getParentScope()
{
	return (Scope) this.getParent();
}


/*************************************************************************
 *	Sets the parent scope of this scope.
 ************************************************************************/
	
public void setParentScope(Scope parentScope)
{
	this.setParent(parentScope);
}




/*************************************************************************
 *	Returns the number of subscopes within this scope.
 ************************************************************************/
	
public int getSubscopeCount()
{
	return this.getChildCount();
}




/*************************************************************************
 *	Returns the subscope at a given index.
 ************************************************************************/
	
public Scope getSubscope(int index)
{
	Scope child = (Scope) this.getChildAt(index);
	return child;
}


/*************************************************************************
 *	Adds a subscope to the scope.
 ************************************************************************/
	
public void addSubscope(Scope subscope)
{
	this.add(subscope);
}



//////////////////////////////////////////////////////////////////////////
//	ACCESS TO METRICS													//
//////////////////////////////////////////////////////////////////////////

public boolean hasMetrics() 
{
	return (metrics != null);
}

public boolean hasNonzeroMetrics() {
	if (this.hasMetrics())
		for (int i = 0; i< this.metrics.length; i++) {
			MetricValue m = this.getMetricValue(i);
			if (m != MetricValue.NONE && m.getValue() != 0.0) return true;
		}
	return false;
}

//////////////////////////////////////////////////////////////////////////
//   DEBUGGING HOOK 													//
//////////////////////////////////////////////////////////////////////////

public boolean stopHere()
{
	return stop;
}

//////////////////////////////////////////////////////////////////////////
// EXPERIMENT DATABASE 													//
//////////////////////////////////////////////////////////////////////////
public Experiment getExperiment() {
	return experiment;
}

public void setExperiment(Experiment exp) {
	this.experiment = exp;
}


//===================================================================
//						METRICS
//===================================================================

/*************************************************************************
 *	Returns the value of a given metric at this scope.
 ************************************************************************/
	
public MetricValue getMetricValue(BaseMetric metric)
{
	int index = metric.getIndex();
	MetricValue value;

	if(this.metrics != null && index < this.metrics.length)
	{
		value = this.metrics[index];

		// compute percentage if necessary
		Scope root = this.experiment.getRootScope();
		if((this != root) && (! value.isPercentAvailable()))
		{
			MetricValue total = root.getMetricValue(metric);
			if(total.isAvailable())
				value.setPercentValue(value.getValue()/total.getValue());
		} 

	}
	else
		value = MetricValue.NONE;

	return value;
}


/***
  overload the method to take-in the index ---FMZ
***/

public MetricValue getMetricValue(int index)
{
	MetricValue value;
        if(this.metrics != null && index < this.metrics.length)
           {
                value = this.metrics[index];
           }
        else
                value = MetricValue.NONE;

        return value;
}


/*************************************************************************
 *	Sets the value of a given metric at this scope.
 ************************************************************************/
public void setMetricValue(int index, MetricValue value)
{
	ensureMetricStorage();
	this.metrics[index] = value;
}

/*************************************************************************
 *	Add the metric cost from a source with a certain filter for all metrics
 ************************************************************************/
public void accumulateMetrics(Scope source, MetricValuePropagationFilter filter, int nMetrics) {
	for (int i = 0; i< nMetrics; i++) {
		this.accumulateMetric(source, i, i, filter);
	}
}

/*************************************************************************
 *	Add the metric cost from a source with a certain filter for a certain metric
 ************************************************************************/
public void accumulateMetric(Scope source, int src_i, int targ_i, MetricValuePropagationFilter filter) {
	if (filter.doPropagation(source, this, src_i, targ_i)) {
		MetricValue m = source.getMetricValue(src_i);
		if (m != MetricValue.NONE && m.getValue() != 0.0) {
			this.accumulateMetricValue(targ_i, m.getValue());
		}
	}
}

/*************************************************************************
 * Laks: accumulate a metric value (used to compute aggregate value)
 * @param index
 * @param value
 ************************************************************************/
public void accumulateMetricValue(int index, double value)
{
	ensureMetricStorage();
	if (index >= this.metrics.length) 
		return;

	MetricValue m = this.metrics[index];
	if (m == MetricValue.NONE) {
		this.metrics[index] = new MetricValue(value);
	} else {
		// TODO Could do non-additive accumulations here?
		m.setValue(m.getValue() + value);
	}

}

/**************************************************************************
 * combining metric from source. use this function to combine metric between
 * 	different views
 * @param source
 * @param filter
 **************************************************************************/
public void combine(Scope source, MetricValuePropagationFilter filter) {
	int nMetrics = this.experiment.getMetricCount();
	for (int i=0; i<nMetrics; i++) {
		BaseMetric metric = this.experiment.getMetric(i);
		if (metric instanceof AggregateMetric) {
			//--------------------------------------------------------------------
			// aggregate metric need special treatment when combining two metrics
			//--------------------------------------------------------------------
			AggregateMetric aggMetric = (AggregateMetric) metric;
			if (filter.doPropagation(source, this, i, i)) {
				aggMetric.combine(source, this);
			}
		} else {
			this.accumulateMetric(source, i, i, filter);
		}
	}
}


/**********************************************************************************
 * Safely combining metrics from another scope. 
 * This method checks if the number of metrics is the same as the number of metrics
 * 	in the experiment. If not, it generates additional metrics
 * this method is used for dynamic metrics creation such as when computing metrics
 * 	in caller view (if a new metric is added)
 * @param source
 * @param filter
 **********************************************************************************/
public void safeCombine(Scope source, MetricValuePropagationFilter filter) {
	ensureMetricStorage();
	this.combine(source, filter);
}

/*************************************************************************
 *	Makes sure that the scope object has storage for its metric values.
 ************************************************************************/
	
protected void ensureMetricStorage()
{
	if(this.metrics == null)
		this.metrics = this.makeMetricValueArray();
	// Expand if metrics not as big as experiment's (latest) metricCount
	if(this.metrics.length < this.experiment.getMetricCount()) {
		MetricValue[] newMetrics = this.makeMetricValueArray();
		for(int i=0; i<this.metrics.length; i++)
			newMetrics[i] = metrics[i];
		this.metrics = newMetrics;
	}
}




/*************************************************************************
 *	Gives the scope object storage for its metric values.
 ************************************************************************/
	
protected MetricValue[] makeMetricValueArray()
{
	int count = this.experiment.getMetricCount();
	MetricValue[] array = new MetricValue[count];
	for(int k = 0; k < count; k++)
		array[k] = MetricValue.NONE;
	return array;
}



/*************************************************************************
 * Copies defensively the metric array into a target scope
 * Used to implement duplicate() in subclasses of Scope  
 ************************************************************************/

public void copyMetrics(Scope targetScope) {
	if (this.metrics != null) {
		targetScope.ensureMetricStorage();
		for (int k=0; k<this.metrics.length && k<targetScope.metrics.length; k++) {
			MetricValue mine = null;
			MetricValue crtMetric = this.metrics[k];
			if ( crtMetric.isAvailable() && crtMetric.getValue() != 0.0) { // there is something to copy
				mine = new MetricValue();
				mine.setValue(crtMetric.getValue());

				if (crtMetric.isPercentAvailable()) {
					mine.setPercentValue(crtMetric.getPercentValue());
				} 
			} else {
				mine = MetricValue.NONE;
			}
			targetScope.metrics[k] = mine;
		}
	}
}



//////////////////////////////////////////////////////////////////////////
//support for visitors													//
//////////////////////////////////////////////////////////////////////////

public void dfsVisitScopeTree(IScopeVisitor sv) {
	accept(sv, ScopeVisitType.PreVisit);
	int nKids = getSubscopeCount();
	for (int i=0; i< nKids; i++) {
		Scope childScope = getSubscope(i);
		childScope.dfsVisitScopeTree(sv);
	}
	accept(sv, ScopeVisitType.PostVisit);
}

public void accept(IScopeVisitor visitor, ScopeVisitType vt) {
	visitor.visit(this, vt);
}

	
}
