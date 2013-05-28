package edu.rice.cs.hpc.traceviewer.operation;


import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Menu;

/******
 * 
 * Base abstract class for undo/redo action menu
 *
 */
public abstract class OperationHistoryAction extends Action
	implements IAction, IOperationHistoryListener, IMenuCreator 
{
	private Menu menu;
	
	public OperationHistoryAction(ImageDescriptor img) {
		super(null, Action.AS_DROP_DOWN_MENU);
		setImageDescriptor(img);
		setMenuCreator(this);
		TraceOperation.getOperationHistory().addOperationHistoryListener(this);
	}
	
	@Override
	public void dispose() {
		if (menu != null) {
			menu.dispose();
			menu = null;
		}
	}


	@Override
	public Menu getMenu(Menu parent) {
		return null;
	}
	
	protected Menu getMenu()
	{
		return menu;
	}
	
	@Override
	public void run() {
		execute();
	}
	
	protected void addActionToMenu(Menu parent, Action action) {
		ActionContributionItem item = new ActionContributionItem(action);
		item.fill(parent, -1);
	}
	
	@Override
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.operations.IOperationHistoryListener#historyNotification(org.eclipse.core.commands.operations.OperationHistoryEvent)
	 */
	public void historyNotification(final OperationHistoryEvent event) 
	{
		final IUndoableOperation operation = event.getOperation();
		
		if (operation.hasContext(TraceOperation.undoableContext)) {
			switch(event.getEventType()) {
			case OperationHistoryEvent.DONE:
			case OperationHistoryEvent.UNDONE:
			case OperationHistoryEvent.REDONE:
				setStatus();
				break;
			}
		}
	}
	
	abstract protected IUndoableOperation[] getHistory();
	abstract protected void execute();
	abstract protected void execute(IUndoableOperation operation) ;
	abstract protected void setStatus();
}
