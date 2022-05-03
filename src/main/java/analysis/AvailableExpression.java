package analysis;

import soot.Local;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.BinopExpr;
import soot.jimple.toolkits.typing.fast.QueuedSet;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;
public class AvailableExpression extends ForwardFlowAnalysis<Unit, ArraySparseSet<MyBinop>> {
    private FlowSet<MyBinop> allExprs = new ArraySparseSet<>();
    public AvailableExpression(DirectedGraph<Unit> graph) {
        super(graph);
        initGraph(graph);
        doAnalysis();
    }

    private void initGraph(DirectedGraph<Unit> graph) {
        for(Unit unit : graph) {
            for(ValueBox vb : unit.getUseBoxes()) {
                Value v = vb.getValue();
                if(v instanceof BinopExpr) {
                    allExprs.add(MyBinop.getInstance((BinopExpr) v));
                }
            }
        }
    }

    @Override
    protected void flowThrough(ArraySparseSet<MyBinop> binopExprs, Unit unit, ArraySparseSet<MyBinop> dest) {
        dest.clear();
        binopExprs.copy(dest);
        for(ValueBox vb : unit.getDefBoxes()) {
            Value v = vb.getValue();
            if(!(v instanceof Local)) {
                continue;
            }
            QueuedSet<MyBinop> bs = new QueuedSet<>();
            for(MyBinop bo : dest) {
                if(bo.getBinopExpr().getOp1().equals(v) || bo.getBinopExpr().getOp1().equals(v)) {
                    bs.addLast(bo);
                }
            }
            while (!bs.isEmpty()) {
                MyBinop bo = bs.removeFirst();
                dest.remove(bo);
            }
        }
        for(ValueBox vb : unit.getUseBoxes()) {
            Value v = vb.getValue();
            if(!(v instanceof BinopExpr)) {
                continue;
            }
            dest.add(MyBinop.getInstance((BinopExpr) v));
        }
    }

    @Override
    protected ArraySparseSet<MyBinop> newInitialFlow() {
        return new ArraySparseSet<>();
    }
    @Override
    protected ArraySparseSet<MyBinop> entryInitialFlow() {
        ArraySparseSet<MyBinop>as =  new ArraySparseSet<>();
        for(MyBinop bo : allExprs) {
            as.add(bo);
        }
        return as;
    }

    @Override
    protected void merge(ArraySparseSet<MyBinop> a1, ArraySparseSet<MyBinop> a2, ArraySparseSet<MyBinop> dest) {
        dest.clear();
        a1.intersection(a2, dest);

    }

    @Override
    protected void copy(ArraySparseSet<MyBinop> binopExprs, ArraySparseSet<MyBinop> a1) {
        a1.clear();
        binopExprs.copy(a1);
    }
}
