package analysis;

import soot.Local;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.BackwardFlowAnalysis;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class LiveVarAnalysis extends BackwardFlowAnalysis<Unit, Set<Value>> {

    public LiveVarAnalysis(DirectedGraph<Unit> graph) {
        super(graph);
        doAnalysis();
    }

    @Override
    protected void flowThrough(Set<Value> srcValue, Unit unit, Set<Value> destValue) {
        destValue.clear();
        destValue.addAll(srcValue);
        for(ValueBox box: unit.getDefBoxes()) {
            Value v = box.getValue();
            if(v instanceof Local) {
                destValue.remove(v);
            }
        }
        for(ValueBox box: unit.getUseBoxes()) {
            Value v = box.getValue();
            if(v instanceof Local) {
                destValue.add(v);
            }
        }
    }

    @Override
    protected Set<Value> newInitialFlow() {
        return new HashSet<>();
    }

    @Override
    protected Set<Value> entryInitialFlow() {
        return new HashSet<>();
    }

    @Override
    protected void merge(Set<Value> exprs, Set<Value> a1, Set<Value> a2) {
        a2.clear();
        a2.addAll(a1);
        a2.addAll(exprs);
    }

    @Override
    protected void copy(Set<Value> exprs, Set<Value> a1) {
        a1.clear();
        a1.addAll(exprs);
    }
}