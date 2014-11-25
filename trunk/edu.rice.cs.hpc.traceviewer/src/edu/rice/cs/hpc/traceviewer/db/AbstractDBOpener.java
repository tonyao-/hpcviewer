package edu.rice.cs.hpc.traceviewer.db;

import java.io.IOException;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IWorkbenchWindow;
import edu.rice.cs.hpc.traceviewer.spaceTimeData.SpaceTimeDataController;
import edu.rice.cs.hpc.traceviewer.db.local.LocalDBOpener;
import edu.rice.cs.hpc.traceviewer.db.remote.RemoteDBOpener;

/**
 * An interface for the DBOpeners. Specifically, it is implemented by
 * {@link RemoteDBOpener} and {@link LocalDBOpener}. Its main purpose is to
 * create a {@link SpaceTimeDataController} from the connection to the database
 * (be it local or remote), but it also partially handles closing that connection.
 * 
 * @author Philip Taffet
 * 
 */
public abstract class AbstractDBOpener {

	

	/**
	 * This prepares the database for retrieving data and creates a
	 * SpaceTimeDataController from that data. The local implementation
	 * (LocalDBOpener) should return a SpaceTimeDataControllerLocal while the
	 * remote implementation (RemoteDBOpener) should return a
	 * SpaceTimeDataControllerRemote.
	 * 
	 * @param window
	 * @param args
	 *            The command line arguments used to start the application
	 * @param statusMgr
	 * @return
	 * @throws IOException 
	 */
	public abstract SpaceTimeDataController openDBAndCreateSTDC(IWorkbenchWindow window, String[] args,
			IStatusLineManager statusMgr) throws IOException;

	// Our current policy on closing: Except for back-to-back connections to the
	// same server, we should close the server when we are making a new
	// connection, local or remote.

	
	/*****
	 * closing the database.
	 * The caller is responsible to call this method to terminate the connection (in case of remote database)
	 * or closing local file (local database)
	 */
	public abstract void end();
}
