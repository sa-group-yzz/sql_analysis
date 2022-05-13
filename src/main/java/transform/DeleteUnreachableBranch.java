package transform;

import soot.*;
import soot.jimple.IfStmt;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.TableSwitchStmt;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.DirectedGraph;
import sql.FetchSQLUsage;

import java.io.*;
import java.util.*;

public class DeleteUnreachableBranch {
    private FetchSQLUsage sqlUsage;
    private Set<Unit> unreachableBranch;

    public DeleteUnreachableBranch(FetchSQLUsage sqlUsage) {
        this.sqlUsage = sqlUsage;
    }


    public Body delete() {
//        DirectedGraph<Unit> cfg = new BriefUnitGraph(body);
//        detectUnreachableBranch(body, cfg);
        return null;
    }

    Set<Unit> detectUnreachableBranch(Body methodBody, DirectedGraph<Unit> graph) {
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
                unreachableBranch.add(unit);
            }
        }
        return unreachableBranch;
    }

    private void removeUnreachableBranch(DirectedGraph<Unit> cfg, File testFile) {
        Set<Unit> visited = new HashSet<>();
        Unit head = cfg.getHeads().get(0);
        Queue<Unit> q = new LinkedList<>();
        q.add(head);
        visited.add(head);
        while (!q.isEmpty()) {
            Unit unit = q.poll();
            System.out.println(unit + " -> " + cfg.getSuccsOf(unit));
            for (Unit succUnit : cfg.getSuccsOf(unit)) {
                if (!visited.contains(succUnit) && !unreachableBranch.contains(succUnit)) {
                    visited.add(succUnit);
                    q.add(succUnit);
                }
            }
        }

        visited.removeIf(unit -> unit.toString().contains("nop"));
    }
}
