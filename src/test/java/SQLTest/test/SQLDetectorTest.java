package SQLTest.test;

import com.alibaba.druid.stat.TableStat;
import org.junit.Test;
import soot.jimple.IfStmt;
import top.viewv.SQLAnalysis;
import top.viewv.SQLDetector;
import top.viewv.abstraction.Silica;
import top.viewv.abstraction.Use;
import top.viewv.function.Analyzer;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class SQLDetectorTest extends BaseTest {
    @Test
    public void test() {
        SQLDetector detector = new SQLDetector();
        Set<Silica> relevantSilicas = detector.detect(new HashSet<>(Arrays.asList("SQLTest.testcase.Case1")));
        for (Silica silica : relevantSilicas) {
            Set<String> queries = silica.getStringQueries();
            HashMap<String, TableStat.Condition> conditionHashMap = new HashMap<>();
            for (String query : queries) {
                SQLAnalysis analysis = new SQLAnalysis(query);
                for (TableStat.Condition condition : analysis.getConditions()) {
                    conditionHashMap.put(condition.getColumn().getName(), condition);
                }
            }
            System.out.println(silica.getStringQueries());
            Set<Use> useSet = Analyzer.getUseSet(silica);
            for (Use use : useSet) {
                if (use.getSelectedColumn() != null) {
                    if (use.getCodepoint().getStatement() instanceof IfStmt) {
                        List<String> columes = new ArrayList<>(use.getSelectedColumn());
                        assertEquals(1, columes.size());
                        assertEquals("price", columes.get(0));
                        break;
                    }
                }
            }
        }
    }
}
