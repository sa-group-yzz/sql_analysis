import soot.Local;
import soot.Unit;
import soot.Value;
import soot.ValueBox;

import java.util.HashSet;
import java.util.Set;

public class LiveVariableAnalysis implements StaticAnalysis{
    @Override
    public void flowThrough(Set<Value> src, Unit unit, Set<Value> dest) {
        dest.clear();
        dest.addAll(src);
        for(ValueBox box: unit.getDefBoxes()) {
            Value v = box.getValue();
            if(v instanceof Local) {
                dest.remove(v);
            }
        }
        for(ValueBox box: unit.getUseBoxes()) {
            Value v = box.getValue();
            if(v instanceof Local) {
                dest.add(v);
            }
        }
    }

    @Override
    public Set<Value> newInitialFlow() {
        return new HashSet<>();
    }

    @Override
    public Set<Value> entryInitialFlow() {
        return new HashSet<>();
    }

    @Override
    public void merge(Set<Value> a1, Set<Value> a2, Set<Value> dest) {
        dest.clear();
        dest.addAll(a1);
        dest.addAll(a2);
    }

    @Override
    public void copy(Set<Value> source, Set<Value> dest) {
        dest.clear();
        dest.addAll(source);
    }
}
