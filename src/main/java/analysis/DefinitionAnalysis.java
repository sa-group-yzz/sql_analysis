package analysis;

import com.sun.applet2.AppletParameters;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.ForwardFlowAnalysis;

import java.util.*;

public class DefinitionAnalysis extends ForwardFlowAnalysis<Unit, BitSet> {
    private int defNumber = 0;

    public Map<ValueBox, Integer> getVbLineMap() {
        return vbLineMap;
    }

    private Map<ValueBox, Integer> vbLineMap = new HashMap<>();

    public Map<ValueBox, Integer> getvIndexMap() {
        return vIndexMap;
    }

    public Map<Integer, ValueBox> getIndexVMap() {
        return indexVMap;
    }

    public Map<Value, List<ValueBox>> getSameValueBoxes() {
        return sameValueBoxes;
    }

    private Map<ValueBox, Integer> vIndexMap = new HashMap<>();
    private Map<Integer, ValueBox> indexVMap = new HashMap<>();
    private Map<Value, List<ValueBox>> sameValueBoxes = new HashMap<>();

    public DefinitionAnalysis(DirectedGraph<Unit> graph) {
        super(graph);
        initGraph(graph);
        doAnalysis();
    }

    public void printIndexValueBoxes() {
        indexVMap.forEach((i, v)-> {
            System.out.printf("%d:%s ", i, v.toString());
        });
        System.out.println();
    }

    private void initGraph(DirectedGraph<Unit> graph) {
        for(Unit u: graph) {
                for(ValueBox vb : u.getDefBoxes()) {
                    this.vbLineMap.put(vb, u.getJavaSourceStartLineNumber());
                    Value v = vb.getValue();
                    vIndexMap.put(vb, defNumber);
                    indexVMap.put(defNumber, vb);
                    defNumber++;
                    if(!sameValueBoxes.containsKey(v)) {
                        sameValueBoxes.put(v, new ArrayList<>());
                    }
                    sameValueBoxes.get(v).add(vb);
                }
            }
    }

    @Override
    protected void flowThrough(BitSet bitSet, Unit u, BitSet dest) {
        copy(bitSet, dest);
        for(ValueBox vb: u.getDefBoxes()) {
            Value v = vb.getValue();
            List<ValueBox> svBoxes = sameValueBoxes.get(v);
            for(ValueBox ovb: svBoxes) {
                dest.set(vIndexMap.get(ovb), false);
            }
            dest.set(vIndexMap.get(vb), true);
        }
    }

    @Override
    protected BitSet newInitialFlow() {
        return new BitSet(defNumber);
    }

    @Override
    protected BitSet entryInitialFlow() {
        return new BitSet(defNumber);
    }

    @Override
    protected void merge(BitSet a1, BitSet a2, BitSet dest) {
        for(int i = 0; i < defNumber; i++) {
            dest.set(i, a1.get(i) || a2.get(i));
        }
    }

    @Override
    protected void copy(BitSet bitSet, BitSet dest) {
        for(int i = 0; i < defNumber; i++) {
            dest.set(i, bitSet.get(i));
        }
    }
}
