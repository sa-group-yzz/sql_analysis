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


    public void detectUnreachableBranchWithSQLUsage(Body methodBody, DirectedGraph<Unit> graph) {
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
                System.out.println(methodBody.getMethod().getDeclaringClass().getName());
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

    public Body deleteUnreachableBranch(String[] sootArgs) {
        UnreachableBranchTransformer transformer = new UnreachableBranchTransformer(this.stmtToReplacement, this.deadStmts, this.className);
        Transform transform = new Transform("jtp.analysis", transformer);
        PackManager.v().getPack("jtp").add(transform);
        soot.Main.main(sootArgs);
        return transformer.getEliminatedBody();
    }

    public Set<Unit> detectUnreachableBranch(Body methodBody, DirectedGraph<Unit> graph) {
        ConstantPropagation cp = new ConstantPropagation(graph);
        cp.doAnalysis();

        Set<Unit> reachableBranch = new HashSet<>();
        Unit head = graph.getHeads().get(0);
        Queue<Unit> q = new LinkedList<>();
        Set<Unit> visit = new HashSet<>();
        q.add(head);
        visit.add(head);

        while (!q.isEmpty()) {
            Unit unit = q.poll();
            if (unit instanceof IfStmt) {
                reachableBranch.add(unit);
                IfStmt ifStmt = (IfStmt) unit;
                Value conditionValue = ifStmt.getCondition();
                Map<Local, MyValue> inMap = cp.getFlowBefore(unit);
                MyValue value = ComputeValue.compute(inMap, conditionValue);

                if (value.getType() != MyValue.Type.NAC && value.getType() != MyValue.Type.UNDEF) {
                    if (value.getValue() == 1) {
                        unit = ifStmt.getTarget();
                    } else {
                        unit = methodBody.getUnits().getSuccOf(ifStmt);
                    }
                }
            } else if (unit instanceof LookupSwitchStmt) {
                reachableBranch.add(unit);
                LookupSwitchStmt lookupSwitchStmt = (LookupSwitchStmt) unit;
                Value keyValue = lookupSwitchStmt.getKey();
                Map<Local, MyValue> inMap = cp.getFlowBefore(unit);
                MyValue value = ComputeValue.compute(inMap, keyValue);
                int index = -1;
                for (int i = 0; i < lookupSwitchStmt.getLookupValues().size(); i++) {
                    if (value.getValue() == lookupSwitchStmt.getLookupValues().get(i).value) {
                        index = i;
                        break;
                    }
                }

                if (index != -1) {
                    unit = lookupSwitchStmt.getTarget(index);
                } else {
                    unit = lookupSwitchStmt.getDefaultTarget();
                }
            } else if (unit instanceof TableSwitchStmt) {
                reachableBranch.add(unit);
                TableSwitchStmt tableSwitchStmt = (TableSwitchStmt) unit;
                Value keyValue = tableSwitchStmt.getKey();
                Map<Local, MyValue> inMap = cp.getFlowBefore(unit);
                MyValue value = ComputeValue.compute(inMap, keyValue);

                if (value.getValue() <= tableSwitchStmt.getHighIndex() && value.getValue() >= tableSwitchStmt.getLowIndex()) {
                    unit = tableSwitchStmt.getTarget(value.getValue() - tableSwitchStmt.getLowIndex());
                } else {
                    unit = tableSwitchStmt.getDefaultTarget();
                }
            }

            reachableBranch.add(unit);

            for (Unit succUnit : graph.getSuccsOf(unit)) {
                if (!visit.contains(succUnit)) {
                    visit.add(succUnit);
                    q.add(succUnit);
                }
            }
        }

        for (Unit unit : graph) {
            if (!reachableBranch.contains(unit) && !unit.toString().contains("nop")) {
                this.unreachableBranch.add(unit);
            }
        }
        return unreachableBranch;
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
