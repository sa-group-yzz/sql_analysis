package sql;

import top.viewv.abstraction.Silica;

import java.util.HashSet;
import java.util.Set;

public class FetchSQLUsage {
    private Set<String> classfilter = new HashSet<>();
    private SQLDetector detector = new SQLDetector();
    private Set<Silica> silica;

    public FetchSQLUsage(String className) {
        classfilter.add(className);
        silica = detector.detect(classfilter);
    }
}
