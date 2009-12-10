/**
 * 
 */
package edu.rice.cs.hpc.viewer.actions;

import org.eclipse.swt.graphics.FontData;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;

import edu.rice.cs.hpc.viewer.util.PreferenceConstants;
import edu.rice.cs.hpc.viewer.experiment.ExperimentManager;
import edu.rice.cs.hpc.viewer.framework.Activator;
import edu.rice.cs.hpc.viewer.scope.ScopeActions;
import edu.rice.cs.hpc.viewer.util.Utilities;


/**
 * @author laksono
 *
 */
public class Preferences implements IWorkbenchWindowActionDelegate {
	IWorkbenchWindow objWindow;
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub
		this.objWindow = window;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {

		//Show the preference for hpcviewer
		PreferenceDialog objDialog = PreferencesUtil.createPreferenceDialogOn(this.objWindow.getShell(), 
				"edu.rice.cs.hpc.viewer.util.PreferencePage", null, null);
		if(objDialog != null) {
			int iRet = objDialog.open();
			if(iRet == Window.OK) {
				// user click OK
				ScopedPreferenceStore objPref = (ScopedPreferenceStore)Activator.getDefault().getPreferenceStore();
				// get the threshold
				double fThreshold = objPref.getDouble(PreferenceConstants.P_THRESHOLD);
				ScopeActions.fTHRESHOLD = fThreshold;
				// get the font for metrics columns
				FontData []objFontsMetric = PreferenceConverter.getFontDataArray(objPref, PreferenceConstants.P_FONT_METRIC);
				FontData []objFontsGeneric = PreferenceConverter.getFontDataArray(objPref, PreferenceConstants.P_FONT_GENERIC);
				Utilities.setFontMetric(this.objWindow, objFontsMetric, objFontsGeneric);

				ExperimentManager.sLastPath = objPref.getString(PreferenceConstants.P_PATH);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

}
