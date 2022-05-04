package sql;

import top.viewv.abstraction.Silica;
import top.viewv.abstraction.Use;
import top.viewv.function.Analyzer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FetchSQLUsage {
    private Set<String> classfilter = new HashSet<>();
    private SQLDetector detector = new SQLDetector();
    private Set<Silica> silicas;

    private HashMap<Silica,Set<Use>> silicaUseSetHashMap = new HashMap<>();

    private HashMap<String,String> silicaQueryHashMap = new HashMap<>();

    public FetchSQLUsage(List<String> className) {
        classfilter.addAll(className);
        silicas = detector.detect(classfilter);
        for (Silica silica : silicas) {
            Set<Use> uses = Analyzer.getUseSet(silica);
            silicaUseSetHashMap.put(silica,uses);
        }
    }

    public Set<Silica> getAllSilica() {
        return silicas;
    }

    public Set<String> getClassFilter() {
        return classfilter;
    }
}
