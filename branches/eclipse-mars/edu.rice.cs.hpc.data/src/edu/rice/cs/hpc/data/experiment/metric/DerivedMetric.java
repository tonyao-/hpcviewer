/**
 * 
 */
package edu.rice.cs.hpc.data.experiment.metric;

import edu.rice.cs.hpc.data.experiment.Experiment;
import edu.rice.cs.hpc.data.experiment.scope.*;

//math expression
import com.graphbuilder.math.*;

/**
 * @author la5
 *
 */
public class DerivedMetric extends BaseMetric {
	//===================================================================================
	// DATA
	//===================================================================================

	// formula expression
	private Expression expression;
	// the total aggregate value
	private double dRootValue = 0.0;
	// map function
	private ExtFuncMap fctMap;
	// map variable 
	private MetricVarMap varMap;

	private Experiment experiment;
	
	//===================================================================================
	// CONSTRUCTORS
	//===================================================================================
	

	/*****
	 * Create derived metric based on experiment data. We'll associate this metric with the root scope of CCT
	 * <p/>
	 * A metric should be independent to root scope. The root scope is only used to compute the percentage
	 * 
	 * @param experiment
	 * @param e
	 * @param sName
	 * @param sID
	 * @param index
	 * @param annotationType
	 * @param objType
	 */
	public DerivedMetric(Experiment experiment, Expression e, String sName, String sID, int index, AnnotationType annotationType, MetricType objType) {
		
		// no root scope information is provided, we'll associate this metric to CCT root scope 
		// the partner of this metric is itself (derived metric has no partner)
		super(sID, sName, true, null, annotationType, index, index, objType);
		
		this.expression = e;
		this.experiment = experiment;
		
		// set up the functions
		this.fctMap = new ExtFuncMap();
		
		RootScope root = (RootScope) experiment.getRootScope().getSubscope(0);
		
		BaseMetric []metrics = experiment.getMetrics(); 
		this.fctMap.init(metrics);

		// set up the variables
		this.varMap = new MetricVarMap(experiment);

		// Bug fix: always compute the aggregate value 
		this.dRootValue = getDoubleValue(root);
		if(this.dRootValue == 0.0)
			this.annotationType = AnnotationType.NONE ;
	}
	
	/****
	 * Set the new expression
	 * 
	 * @param expr : the new expression
	 */
	public void setExpression( Expression expr ) {
		this.expression = expr;
		
		// new formula has been set, refresh the root value used for computing percent
		RootScope root = (RootScope) experiment.getRootScope().getSubscope(0);
		dRootValue = getDoubleValue(root);
	}

	
	
	//===================================================================================
	// GET VALUE
	//===================================================================================
	/**
	 * Computing the value of the derived metric
	 * @param scope: the current scope
	 * @return the object Double if there is a value, null otherwise
	 */
	public double getDoubleValue(Scope scope) {
		this.varMap.setScope(scope);
		return expression.eval(this.varMap, this.fctMap);
	}
	
	/**
	 * Overloading method to compute the value of the derived metric of a scope
	 * Return a MetricValue
	 */
	@Override
	public MetricValue getValue(Scope scope) {
		double dVal;
		// if the scope is a root scope, then we return the aggregate value
		if(scope instanceof RootScope) {
			dVal = dRootValue;
		} else {
			// otherwise, we need to recompute the value again via the equation
			dVal = getDoubleValue(scope);
			
			// ugly test to check whether the value exist or not
			if(dVal == 0.0d)
				return MetricValue.NONE;	// the value is not available !
		}
		if(this.getAnnotationType() == AnnotationType.PERCENT){
			return new MetricValue(dVal, ((float) dVal/this.dRootValue));
		} else {
			return new MetricValue(dVal);
		}
	}

	/****
	 * return the current expression formula
	 * 
	 * @return
	 */
	public Expression getFormula() {
		return expression;
	}

	@Override
	public BaseMetric duplicate() {
		return new DerivedMetric(experiment, expression, displayName, shortName, index, annotationType, metricType);
	}
	
	/****
	 * update the experiment of this derived metric
	 * 
	 * @param experiment
	 */
	public void setExperiment(Experiment experiment)
	{
		this.experiment = experiment;
		// updating as well the variable mapping to metrics
		varMap.setExperiment(experiment);
	}
}
