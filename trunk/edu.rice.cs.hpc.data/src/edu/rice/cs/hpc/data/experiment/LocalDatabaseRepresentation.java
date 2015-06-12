package edu.rice.cs.hpc.data.experiment;

import java.io.File;

import edu.rice.cs.hpc.data.experiment.xml.ExperimentFileXML;
import edu.rice.cs.hpc.data.util.IUserData;

public class LocalDatabaseRepresentation implements IDatabaseRepresentation 
{
	final private File fileExperiment;
	final private IUserData<String, String> userData; 
	final private boolean need_metric;
	private ExperimentFileXML fileXML;

	public LocalDatabaseRepresentation(File fileExperiment, 
			IUserData<String, String> userData, 
			boolean need_metric)
	{
		this.fileExperiment = fileExperiment;
		this.userData		= userData;
		this.need_metric	= need_metric;
	}
	
	@Override
	public ExperimentFileXML getXMLFile() {
		return fileXML;
	}

	@Override
	public void open(BaseExperiment experiment) throws Exception
	{		
		if (fileXML == null) {
			fileXML = new ExperimentFileXML();
		}
		fileXML.parse(fileExperiment, experiment, need_metric, userData);	
	}

	@Override
	public IDatabaseRepresentation duplicate() {
		LocalDatabaseRepresentation dup = new LocalDatabaseRepresentation(fileExperiment, userData, need_metric);
		dup.fileXML = new ExperimentFileXML();
		
		return dup;
	}
}
