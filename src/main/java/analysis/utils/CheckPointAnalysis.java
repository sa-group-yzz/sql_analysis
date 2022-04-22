package analysis.utils;

import soot.Local;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.baf.StaticInvokeInst;
import soot.jimple.Constant;
import soot.jimple.InvokeExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.toolkits.typing.fast.QueuedSet;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.BackwardFlowAnalysis;

import java.util.*;

public class CheckPointAnalysis  {
    public Map<Integer, List<CheckPointDetail>> ret = new HashMap<>();

    public CheckPointAnalysis(DirectedGraph<Unit> graph) {
        Iterator<Unit> ui = graph.getTails().iterator();
        QueuedSet<Unit> qs = new QueuedSet<>();
        while (ui.hasNext()) {
            Unit u = ui.next();
            qs.addLast(u);
        }
        InvokeExpr currentExpr = null;
        CheckPointDetail currentCheckPointDetail = null;
        Set<Value> currentBitValues = null;
        int currentBit = 0;
        while (!qs.isEmpty()) {
           Unit u =  qs.removeFirst();
           for(Unit pu : graph.getPredsOf(u)) {
               qs.addLast(pu);
           }

           if(u instanceof JInvokeStmt) {
               JInvokeStmt jInvokeStmt = (JInvokeStmt)u;
               InvokeExpr invokeExpr = jInvokeStmt.getInvokeExpr();
               if(!invokeExpr.toString().contains("staticinvoke <cases.utils.CheckPoint: void trigger")) {
                   continue;
               }
               currentExpr = invokeExpr;
               currentCheckPointDetail = new CheckPointDetail(u, invokeExpr.getArg(0).toString(), invokeExpr.getArg(1));
               currentBitValues = new HashSet<>();
               currentBitValues.add(invokeExpr.getArg(2));
               currentBit = 0;
               continue;
           }
           if(currentExpr == null) {
               continue;
           }
           if(u instanceof JAssignStmt) {
               boolean related = false;
               for (ValueBox vb : u.getDefBoxes()) {
                   Value av = vb.getValue();
                   related = currentBitValues.remove(av);
               }
               if(!related) {
                   continue;
               }
               JAssignStmt jas = (JAssignStmt) u;
               if(jas.rightBox.getValue() instanceof StaticFieldRef) {
                   String checkPointType = jas.rightBox.getValue().toString();
                   switch (checkPointType) {
                       case "<cases.utils.CheckPoint: int DEFINITION_ANALYSIS>":
                           currentBit = currentBit | CheckPointDetail.DEFINITION_ANALYSIS;
                           break;
                       case "<cases.utils.CheckPoint: int CONSTANT_ANALYSIS>":
                           currentBit = currentBit | CheckPointDetail.CONSTANT_ANALYSIS;
                           break;
                       case "<cases.utils.CheckPoint: int LIVENESS_ANALYSIS>":
                           currentBit = currentBit | CheckPointDetail.LIVENESS_ANALYSIS;
                           break;
                       case "<cases.utils.CheckPoint: int EXPRESSION_ANALYSIS>":
                           currentBit = currentBit | CheckPointDetail.EXPRESSION_ANALYSIS;
                           break;
                       default:
                           throw new RuntimeException(String.format("unsupported type: %s", checkPointType));
                   }
                   if(currentBitValues.isEmpty()) {
                       currentCheckPointDetail.setAnalysisBit(currentBit);
                       currentCheckPointDetail.set2Map(ret);

                       currentExpr = null;
                       currentCheckPointDetail = null;
                       currentBitValues = null;
                       currentBit = 0;
                   }
                   continue;
               }
               for (ValueBox vb : u.getUseBoxes()) {
                   Value av = vb.getValue();
                   if(av instanceof Local) {
                       currentBitValues.add(av);
                   }
               }

           }

        }
    }


}
