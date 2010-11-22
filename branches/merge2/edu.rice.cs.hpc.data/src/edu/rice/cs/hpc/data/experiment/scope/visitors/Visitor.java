package edu.rice.cs.hpc.data.experiment.scope.visitors;

import java.util.HashMap;

import edu.rice.cs.hpc.data.experiment.scope.AlienScope;
import edu.rice.cs.hpc.data.experiment.scope.CallSiteScope;
import edu.rice.cs.hpc.data.experiment.scope.FileScope;
import edu.rice.cs.hpc.data.experiment.scope.GroupScope;
import edu.rice.cs.hpc.data.experiment.scope.LineScope;
import edu.rice.cs.hpc.data.experiment.scope.LoadModuleScope;
import edu.rice.cs.hpc.data.experiment.scope.LoopScope;
import edu.rice.cs.hpc.data.experiment.scope.ProcedureScope;
import edu.rice.cs.hpc.data.experiment.scope.RootScope;
import edu.rice.cs.hpc.data.experiment.scope.Scope;
import edu.rice.cs.hpc.data.experiment.scope.ScopeVisitType;
import edu.rice.cs.hpc.data.experiment.scope.StatementRangeScope;

public class Visitor implements IScopeVisitor {
	protected HashMap<Integer, Scope> map;

	public Visitor(HashMap<Integer,Scope> _map) {
		map = _map;
	}

	//----------------------------------------------------
	// visitor pattern instantiations for each Scope type
	//----------------------------------------------------

	public void visit(Scope scope, ScopeVisitType vt) {  }
	public void visit(RootScope scope, ScopeVisitType vt) { }
	public void visit(LoadModuleScope scope, ScopeVisitType vt) { }
	public void visit(FileScope scope, ScopeVisitType vt) { }
	public void visit(ProcedureScope scope, ScopeVisitType vt) { }
	public void visit(AlienScope scope, ScopeVisitType vt) { }
	public void visit(LoopScope scope, ScopeVisitType vt) { }
	public void visit(LineScope scope, ScopeVisitType vt) { 
		if (vt == ScopeVisitType.PreVisit) {
			int cpid = scope.getCpid();
			//int cpid = Integer.valueOf(scope.getShortName());
			if (cpid > 0) {
				this.map.put(cpid, scope);
			}
		}
	}
	
	public void visit(StatementRangeScope scope, ScopeVisitType vt) { }
	
	public void visit(CallSiteScope scope, ScopeVisitType vt) { 
		/*if (vt == ScopeVisitType.PreVisit) {
			int cpid = scope.getCpid();
			if (cpid > 0) {
				this.map.put(cpid, scope);
				System.out.println("adding cs scope cpid " + cpid);
			}
		}*/
	}
	
	public void visit(GroupScope scope, ScopeVisitType vt) { }

	public HashMap<Integer,Scope> getMap(){
		return map;
	}
}
