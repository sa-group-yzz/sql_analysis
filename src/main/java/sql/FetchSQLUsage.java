package sql;

import com.alibaba.druid.stat.TableStat;
import soot.Unit;
import top.viewv.SQLAnalysis;
import top.viewv.abstraction.Silica;
import top.viewv.abstraction.Use;
import top.viewv.function.Analyzer;

import java.util.*;

public class FetchSQLUsage {
    private Set<String> classfilter = new HashSet<>();
    private SQLDetector detector = new SQLDetector();

    private Set<Silica> silicas;

    private Set<String> querys = new HashSet<>();

    private Set<TableStat.Condition> conditionSet = new HashSet<>();

    private HashMap<Silica,Set<Use>> silicaUseSetHashMap = new HashMap<>();

    private HashMap<Silica,Set<String>> silicaQueryHashMap = new HashMap<>();

    private HashMap<Unit, Set<String>> unitColumnHashMap = new HashMap<>();


    public FetchSQLUsage(String className) {
        classfilter.addAll(Collections.singletonList(className));
        silicas = detector.detect(classfilter);
        for (Silica silica : silicas) {
            Set<Use> uses = Analyzer.getUseSet(silica);
            silicaUseSetHashMap.put(silica,uses);
            Set<String> querys = silica.getStringQueries();
            this.querys = querys;
            silicaQueryHashMap.put(silica,querys);

            for (String query : querys) {
                SQLAnalysis sqlAnalysis = new SQLAnalysis(query);
                List<TableStat.Condition> conditions = sqlAnalysis.getConditions();
                this.conditionSet.addAll(conditions);
            }

            for (Use use : uses) {
                Unit unit = use.getCodepoint().getStatement();
                Set<String> columns = use.getSelectedColumn();
                unitColumnHashMap.put(unit,columns);
            }
        }
    }

    public HashMap<Unit, Set<String>> getUnitColumnHashMap() {
        return unitColumnHashMap;
    }

    public Set<TableStat.Condition> getConditions() {
        return this.conditionSet;
    }

    public Set<Silica> getSilicas() {
        return silicas;
    }

    public Set<String> getQuerys() {
        return querys;
    }

    public Set<Silica> getAllSilica() {
        return silicas;
    }

    public Set<String> getClassFilter() {
        return classfilter;
    }
}
