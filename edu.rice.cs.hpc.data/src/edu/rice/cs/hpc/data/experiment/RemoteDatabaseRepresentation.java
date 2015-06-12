package edu.rice.cs.hpc.data.experiment;

import java.io.InputStream;

import edu.rice.cs.hpc.data.experiment.xml.ExperimentFileXML;
import edu.rice.cs.hpc.data.util.IUserData;

public class RemoteDatabaseRepresentation implements IDatabaseRepresentation 
{
	final private InputStream expStream;
	final private IUserData<String, String> userData;
	final private String name;
	private ExperimentFileXML fileXML;
	
	public RemoteDatabaseRepresentation( 
			InputStream expStream, 
			IUserData<String, String> userData,
			String name)
	{
		this.expStream 	= expStream;
		this.userData  	= userData;
		this.name		= name;
	}
	
	@Override
	public ExperimentFileXML getXMLFile() {
		return fileXML;
	}

	@Override
	public void open(BaseExperiment experiment) throws Exception {
		
		if (fileXML == null) {
			fileXML = new ExperimentFileXML();
		}
		fileXML.parse(expStream, name, experiment, false, userData);
	}

	@Override
	public IDatabaseRepresentation duplicate() {
		RemoteDatabaseRepresentation dup = new RemoteDatabaseRepresentation(expStream, userData, name);
		dup.fileXML = new ExperimentFileXML();
		
		return dup;
	}
}
