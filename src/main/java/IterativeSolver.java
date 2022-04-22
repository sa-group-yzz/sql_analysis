import soot.Unit;
import soot.Value;
import soot.jimple.toolkits.typing.fast.QueuedSet;
import soot.toolkits.graph.DirectedGraph;

import java.util.*;

public class IterativeSolver {
    private final DirectedGraph<Unit> graph;
    private StaticAnalysis staticAnalysis;

    private Unit exit;
    private Map<Unit, List<Unit>> nextList = new HashMap<>();
    private Map<Unit, Set<Value>> after = new HashMap<>();
    private Map<Unit, Set<Value>> before = new HashMap<>();
    private List<Unit> workList = new ArrayList<>();

    public IterativeSolver(DirectedGraph<Unit> graph, StaticAnalysis staticAnalysis) {
        this.graph = graph;
        this.staticAnalysis = staticAnalysis;
        doAnalysis();
    }

    private void init() {
        for(Unit b : graph) {
            if(graph.getSuccsOf(b).size() == 0) {
                assert exit == null;
                exit = b;
            }
            after.put(b, this.newInitialFlow());
            before.put(b, this.newInitialFlow());
            List<Unit> next = new ArrayList<>(graph.getPredsOf(b));
            nextList.put(b, next);
        }
        initWorkList();
    }

    private boolean addRound = true;
    private void initWorkList() {
        ArrayList<Unit> next = new ArrayList<>();
        QueuedSet<Unit> us = new QueuedSet<>();
        us.addLast(exit);
        while (!us.isEmpty()) {
           Unit c =  us.removeFirst();
           next.add(c);
           graph.getPredsOf(c).forEach(p->{
               if(next.contains(p)) {
                   return ;
               }
               us.addLast(p);
           });
        }
        workList = next;
    }



    private void doAnalysis() {
        init();
        while (addRound) {
            addRound = false;
            for (Unit current : workList) {
                Set<Value> beforeValue = getFlowBefore(current);
                List<Unit> succs = graph.getSuccsOf(current);
                Set<Value> afterValue = newInitialFlow();
                if (succs.size() != 0) {
                    if (succs.size() == 1) {
                        copy(getFlowBefore(succs.get(0)), afterValue);
                    } else {
                        int i = 1;
                        List<Set<Value>> al = new ArrayList<>();
                        for (Unit b : succs) {
                            al.add(getFlowBefore(b));
                        }
                        for (; i < succs.size(); i++) {
                            Set<Value> na = new HashSet<>();
                            merge(al.get(i - 1), al.get(i), na);
                            al.set(i, na);
                        }
                        copy(al.get(i - 1), afterValue);
                    }
                }
                Set<Value> nb = new HashSet<>();
                flowThrough(afterValue, current, nb);
                after.put(current, afterValue);
                before.put(current, nb);
                if (!nb.equals(beforeValue)) {
                    addRound = true;
                }
            }
        }
    }


    public Set<Value> getFlowAfter(Unit s) {
        return after.get(s);
    }

    public Set<Value> getFlowBefore(Unit s) {
        return before.get(s);
    }

    protected void flowThrough(Set<Value> src, Unit unit, Set<Value> dest) {
        staticAnalysis.flowThrough(src, unit, dest);
    }

    protected Set<Value> newInitialFlow() {
        return staticAnalysis.newInitialFlow();
    }

    protected Set<Value> entryInitialFlow() {
        return staticAnalysis.entryInitialFlow();
    }

    protected void merge(Set<Value> a1, Set<Value> a2, Set<Value> dest) {
       staticAnalysis.merge(a1, a2, dest);
    }

    protected void copy(Set<Value> source, Set<Value> dest) {
        staticAnalysis.copy(source, dest);
    }
}
