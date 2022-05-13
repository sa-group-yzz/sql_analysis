package transform;

import soot.Local;
import soot.Unit;
import soot.jimple.AssignStmt;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.ForwardFlowAnalysis;

import java.util.HashMap;
import java.util.Map;

public class ConstantPropagation extends ForwardFlowAnalysis<Unit, Map<Local, MyValue>> {

    public ConstantPropagation(DirectedGraph<Unit> graph) {
        super(graph);
    }

    @Override
    protected void flowThrough(Map<Local, MyValue> in, Unit unit, Map<Local, MyValue> out) {
        copy(in, out);

        if (unit instanceof AssignStmt) {
            AssignStmt stmt = (AssignStmt) unit;

            Local leftLocal = (Local) stmt.getLeftOp();
            out.put(leftLocal, ComputeValue.compute(in, stmt.getRightOp()));
        }
    }

    @Override
    protected Map<Local, MyValue> newInitialFlow() {
        return new HashMap<>();
    }

    @Override
    protected void merge(Map<Local, MyValue> in1, Map<Local, MyValue> in2, Map<Local, MyValue> out) {
        Map<Local, MyValue> result = new HashMap<>();
        for (Local l : in1.keySet()) {
            MyValue tempValue;
            if (in2.containsKey(l)) {
                tempValue = MyValue.meetValue(in1.get(l), in2.get(l));
            } else {
                tempValue = MyValue.meetValue(in1.get(l), MyValue.getUNDEF());
            }

            result.put(l, tempValue);
        }

        copy(result, out);
    }

    @Override
    protected void copy(Map<Local, MyValue> source, Map<Local, MyValue> dest) {
        dest.clear();
        dest.putAll(source);
    }

    @Override
    protected void doAnalysis() {
        super.doAnalysis();
    }
}
