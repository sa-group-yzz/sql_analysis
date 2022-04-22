import soot.Unit;
import soot.Value;

import java.util.HashSet;
import java.util.Set;

public interface StaticAnalysis {
    public void flowThrough(Set<Value> src, Unit unit, Set<Value> dest);
    public Set<Value> newInitialFlow();
    public Set<Value> entryInitialFlow();

    public void merge(Set<Value> a1, Set<Value> a2, Set<Value> dest);

    public void copy(Set<Value> source, Set<Value> dest);
}
