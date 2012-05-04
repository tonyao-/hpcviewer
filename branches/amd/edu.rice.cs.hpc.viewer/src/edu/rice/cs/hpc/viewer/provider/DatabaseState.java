package edu.rice.cs.hpc.viewer.provider;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchWindow;
import edu.rice.cs.hpc.viewer.window.ViewerWindow;
import edu.rice.cs.hpc.viewer.window.ViewerWindowManager;

public class DatabaseState extends AbstractSourceProvider {

	public final static String DATABASE_ACTIVE_STATE = "edu.rice.cs.hpc.viewer.provider.data.active";
	public final static String DATABASE_MERGE_STATE = "edu.rice.cs.hpc.viewer.provider.data.merge";
	public final static String ENABLED = "ENABLED";
	public final static String DISABLED = "DISABLED";
	
	private int num_opened_database = 0;
	
	public void dispose() {
	}

	public Map getCurrentState() 
	{
		Map<String, Object> map = new HashMap<String, Object>(1);
		String value = num_opened_database>0 ? ENABLED : DISABLED;
		map.put(DATABASE_ACTIVE_STATE, value);
		
		value = num_opened_database>1 ? ENABLED : DISABLED;
		map.put(DATABASE_MERGE_STATE, value);
		
		return map;
	}

	public String[] getProvidedSourceNames() 
	{
		return new String[] { DATABASE_ACTIVE_STATE, DATABASE_MERGE_STATE };
	}


	public void toogleEnabled(IWorkbenchWindow window) 
	{
		ViewerWindow vw = ViewerWindowManager.getViewerWindow(window);
		
		num_opened_database = vw.getOpenDatabases();
		String value = num_opened_database>0 ? ENABLED : DISABLED;
		fireSourceChanged(ISources.WORKBENCH, DATABASE_ACTIVE_STATE, value);
		
		value = num_opened_database>1 ? ENABLED : DISABLED;
		fireSourceChanged(ISources.WORKBENCH, DATABASE_MERGE_STATE, value);
	}
	
}