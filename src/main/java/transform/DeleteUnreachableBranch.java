package transform;

import soot.*;
import soot.jimple.*;
import soot.toolkits.graph.DirectedGraph;
import sql.FetchSQLUsage;

import java.util.*;

public class DeleteUnreachableBranch {
    private FetchSQLUsage sqlUsage;
    private Set<Unit> unreachableBranch;
    private String className;
    private Map<Stmt, GotoStmt> stmtToReplacement;
    private List<IfStmt> deadStmts;

    public DeleteUnreachableBranch(FetchSQLUsage sqlUsage, String className) {
        this.sqlUsage = sqlUsage;
        this.unreachableBranch = new HashSet<>();
        this.stmtToReplacement = new HashMap<>();
        this.deadStmts = new ArrayList<>();
        this.className = className;
    }

    public DeleteUnreachableBranch() {
        this.unreachableBranch = new HashSet<>();
    }


    public void detectUnreachableBranchWithSQLUsage(DirectedGraph<Unit> graph) {
        ConstantPropagation cp = new ConstantPropagation(graph, this.sqlUsage.getUnitColumnHashMap());
        cp.doAnalysis();
        Unit head = graph.getHeads().get(0);
        Queue<Unit> q = new LinkedList<>();
        Set<Unit> visit = new HashSet<>();
        q.add(head);
        visit.add(head);

        while (!q.isEmpty()) {
            Unit unit = q.poll();
            if (unit instanceof IfStmt) {
                IfStmt ifStmt = (IfStmt) unit;
                Value conditionValue = ifStmt.getCondition();
                Map<Local, MyValue> inMap = cp.getFlowBefore(unit);
                MyValue value = ComputeValue.computeWithSQL(inMap, conditionValue, this.sqlUsage.getUnitColumnHashMap().get(unit));

                if (value.getType() != MyValue.Type.NAC && value.getType() != MyValue.Type.UNDEF) {
                    if (value.getValue() == 1) {
                        this.stmtToReplacement.put(ifStmt, Jimple.v().newGotoStmt(ifStmt.getTargetBox()));
                    } else {
                        this.deadStmts.add(ifStmt);
                    }
                }
            }


            for (Unit succUnit : graph.getSuccsOf(unit)) {
                if (!visit.contains(succUnit)) {
                    visit.add(succUnit);
                    q.add(succUnit);
                }
            }
        }
    }

    public Body delete(String[] sootArgs) {
        UnreachableBranchTransformer transformer = new UnreachableBranchTransformer(this.stmtToReplacement, this.deadStmts, this.className);
        Transform transform = new Transform("jtp.analysis", transformer);
        PackManager.v().getPack("jtp").add(transform);
        soot.Main.main(sootArgs);
        return transformer.getEliminatedBody();
    }


    public FetchSQLUsage getSqlUsage() {
        return sqlUsage;
    }

    public void setSqlUsage(FetchSQLUsage sqlUsage) {
        this.sqlUsage = sqlUsage;
    }

    public Set<Unit> getUnreachableBranch() {
        return unreachableBranch;
    }


}
