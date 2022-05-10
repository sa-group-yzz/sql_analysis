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
    public void testCase1() {
        String className = "SQLTest.testcase.Case1";
        SQLTestResult result = getResult(className);
        Set<Use> useSet = result.getUseSet();
        HashMap<String, TableStat.Condition> conditionHashMap = result.getCondition();
        assertEquals(conditionHashMap.size(), 1);
        for (Use use : useSet) {
            if (use.getSelectedColumn() != null) {
                if (use.getCodepoint().getStatement() instanceof IfStmt) {
                    List<String> columns = new ArrayList<>(use.getSelectedColumn());
                    assertEquals(1, columns.size());
                    assertEquals("price", columns.get(0));
                }
            }
        }
    }

    @Test
    public void testCase2() {
        String className = "SQLTest.testcase.Case2";
        SQLTestResult result = getResult(className);
        Set<Use> useSet = result.getUseSet();
        HashMap<String, TableStat.Condition> conditionHashMap = result.getCondition();
        assertEquals(conditionHashMap.size(), 1);
        for (Use use : useSet) {
            if (use.getSelectedColumn() != null) {
                if (use.getCodepoint().getStatement() instanceof IfStmt) {
                    List<String> columns = new ArrayList<>(use.getSelectedColumn());
                    assertEquals(1, columns.size());
                    String column = columns.get(0);
                    if (!(column.equals("pk") || column.equals("id"))) {
                        throw new RuntimeException("column is not pk or id");
                    }
                }
            }
        }
    }

    @Test
    public void testCase3() {
        String className = "SQLTest.testcase.Case3";
        SQLTestResult result = getResult(className);
        Set<Use> useSet = result.getUseSet();
        HashMap<String, TableStat.Condition> conditionHashMap = result.getCondition();
        assertEquals(conditionHashMap.size(), 1);
        for (Use use : useSet) {
            if (use.getSelectedColumn() != null) {
                if (use.getCodepoint().getStatement() instanceof IfStmt) {
                    List<String> columns = new ArrayList<>(use.getSelectedColumn());
                    assertEquals(1, columns.size());
                    assertEquals("price", columns.get(0));
                }
            }
        }
    }

    @Test
    public void testCase4() {
        String className = "SQLTest.testcase.Case4";
        SQLTestResult result = getResult(className);
        Set<Use> useSet = result.getUseSet();
        HashMap<String, TableStat.Condition> conditionHashMap = result.getCondition();
        assertEquals(conditionHashMap.size(), 1);
        for (Use use : useSet) {
            if (use.getSelectedColumn() != null) {
                if (use.getCodepoint().getStatement() instanceof IfStmt) {
                    List<String> columns = new ArrayList<>(use.getSelectedColumn());
                    assertEquals(1, columns.size());
                    assertEquals("price", columns.get(0));
                    break;
                }
            }
        }
    }

    @Test
    public void testCase5() {
        String className = "SQLTest.testcase.Case5";
        SQLTestResult result = getResult(className);
        Set<Use> useSet = result.getUseSet();
        HashMap<String, TableStat.Condition> conditionHashMap = result.getCondition();
        assertEquals(conditionHashMap.size(), 1);
        for (Use use : useSet) {
            if (use.getSelectedColumn() != null) {
                if (use.getCodepoint().getStatement() instanceof IfStmt) {
                    List<String> columns = new ArrayList<>(use.getSelectedColumn());
                    assertEquals(1, columns.size());
                    assertEquals("price", columns.get(0));
                    break;
                }
            }
        }
    }

    @Test
    public void testCase6() {
        String className = "SQLTest.testcase.Case6";
        SQLTestResult result = getResult(className);
        Set<Use> useSet = result.getUseSet();
        HashMap<String, TableStat.Condition> conditionHashMap = result.getCondition();
        assertEquals(conditionHashMap.size(), 1);
        for (Use use : useSet) {
            if (use.getSelectedColumn() != null) {
                if (use.getCodepoint().getStatement() instanceof IfStmt) {
                    List<String> columns = new ArrayList<>(use.getSelectedColumn());
                    assertEquals(1, columns.size());
                    assertEquals("price", columns.get(0));
                }
            }
        }
    }

    @Test
    public void testCase7() {
        String className = "SQLTest.testcase.Case7";
        SQLTestResult result = getResult(className);
        Set<Use> useSet = result.getUseSet();
        HashMap<String, TableStat.Condition> conditionHashMap = result.getCondition();
        assertEquals(conditionHashMap.size(), 0);
        for (Use use : useSet) {
            if (use.getSelectedColumn() != null) {
                if (use.getCodepoint().getStatement() instanceof IfStmt) {
                    List<String> columns = new ArrayList<>(use.getSelectedColumn());
                    assertEquals(1, columns.size());
                    assertEquals("price", columns.get(0));
                }
            }
        }
    }

    @Test
    public void testCase8() {
        String className = "SQLTest.testcase.Case8";
        SQLTestResult result = getResult(className);
        Set<Use> useSet = result.getUseSet();
        HashMap<String, TableStat.Condition> conditionHashMap = result.getCondition();
        assertEquals(conditionHashMap.size(), 0);
        for (Use use : useSet) {
            if (use.getSelectedColumn() != null) {
                if (use.getCodepoint().getStatement() instanceof IfStmt) {
                    List<String> columns = new ArrayList<>(use.getSelectedColumn());
                    assertEquals(1, columns.size());
                    assertEquals("price", columns.get(0));
                }
            }
        }
    }

    @Test
    public void testCase9() {
        String className = "SQLTest.testcase.Case9";
        SQLTestResult result = getResult(className);
        Set<Use> useSet = result.getUseSet();
        HashMap<String, TableStat.Condition> conditionHashMap = result.getCondition();
        assertEquals(conditionHashMap.size(), 1);
        for (Use use : useSet) {
            if (use.getSelectedColumn() != null) {
                if (use.getCodepoint().getStatement() instanceof IfStmt) {
                    List<String> columns = new ArrayList<>(use.getSelectedColumn());
                    assertEquals(1, columns.size());
                    assertEquals("price", columns.get(0));
                }
            }
        }
    }

    @Test
    public void testCase10() {
        String className = "SQLTest.testcase.Case10";
        SQLTestResult result = getResult(className);
        Set<Use> useSet = result.getUseSet();
        HashMap<String, TableStat.Condition> conditionHashMap = result.getCondition();
        assertEquals(conditionHashMap.size(), 1);
        for (Use use : useSet) {
            if (use.getSelectedColumn() != null) {
                if (use.getCodepoint().getStatement() instanceof IfStmt) {
                    List<String> columns = new ArrayList<>(use.getSelectedColumn());
                    assertEquals(1, columns.size());
                    assertEquals("price", columns.get(0));
                }
            }
        }
    }

    @Test
    public void testCase11() {
        String className = "SQLTest.testcase.Case11";
        SQLTestResult result = getResult(className);
        Set<Use> useSet = result.getUseSet();
        HashMap<String, TableStat.Condition> conditionHashMap = result.getCondition();
        assertEquals(conditionHashMap.size(), 2);
        for (Use use : useSet) {
            if (use.getSelectedColumn() != null) {
                if (use.getCodepoint().getStatement() instanceof IfStmt) {
                    List<String> columns = new ArrayList<>(use.getSelectedColumn());
                    assertEquals(1, columns.size());
                    String column = columns.get(0);
                    if (!(column.equals("price") || column.equals("total"))) {
                        throw new RuntimeException("unexpected column: " + column);
                    }
                }
            }
        }
    }

    @Test
    public void testCase12() {
        String className = "SQLTest.testcase.Case12";
        SQLTestResult result = getResult(className);
        Set<Use> useSet = result.getUseSet();
        HashMap<String, TableStat.Condition> conditionHashMap = result.getCondition();
        assertEquals(conditionHashMap.size(), 2);
        for (Use use : useSet) {
            if (use.getSelectedColumn() != null) {
                if (use.getCodepoint().getStatement() instanceof IfStmt) {
                    List<String> columns = new ArrayList<>(use.getSelectedColumn());
                    assertEquals(1, columns.size());
                    String column = columns.get(0);
                    if (!(column.equals("price") || column.equals("total"))) {
                        throw new RuntimeException("unexpected column: " + column);
                    }
                }
            }
        }
    }

    @Test
    public void testCase13() {
        String className = "SQLTest.testcase.Case13";
        SQLTestResult result = getResult(className);
        Set<Use> useSet = result.getUseSet();
        HashMap<String, TableStat.Condition> conditionHashMap = result.getCondition();
        assertEquals(conditionHashMap.size(), 2);
        for (Use use : useSet) {
            if (use.getSelectedColumn() != null) {
                if (use.getCodepoint().getStatement() instanceof IfStmt) {
                    List<String> columns = new ArrayList<>(use.getSelectedColumn());
                    assertEquals(1, columns.size());
                    String column = columns.get(0);
                    if (!(column.equals("price") || column.equals("total"))) {
                        throw new RuntimeException("unexpected column: " + column);
                    }
                }
            }
        }
    }

    @Test
    public void testCase14() {
        String className = "SQLTest.testcase.Case14";
        SQLTestResult result = getResult(className);
        Set<Use> useSet = result.getUseSet();
        HashMap<String, TableStat.Condition> conditionHashMap = result.getCondition();
        assertEquals(conditionHashMap.size(), 2);
        for (Use use : useSet) {
            if (use.getSelectedColumn() != null) {
                if (use.getCodepoint().getStatement() instanceof IfStmt) {
                    List<String> columns = new ArrayList<>(use.getSelectedColumn());
                    assertEquals(1, columns.size());
                    String column = columns.get(0);
                    if (!(column.equals("price") || column.equals("total"))) {
                        throw new RuntimeException("unexpected column: " + column);
                    }
                }
            }
        }
    }

    private SQLTestResult getResult(String className) {
        SQLDetector detector = new SQLDetector();
        Set<Silica> relevantSilicas = detector.detect(new HashSet<>(Collections.singletonList(className)));
        Set<Use> useSetResult = new HashSet<>();
        HashMap<String, TableStat.Condition> conditionHashMap = new HashMap<>();
        for (Silica silica : relevantSilicas) {
            Set<String> queries = silica.getStringQueries();
            for (String query : queries) {
                SQLAnalysis analysis = new SQLAnalysis(query);
                for (TableStat.Condition condition : analysis.getConditions()) {
                    conditionHashMap.put(condition.getColumn().getName(), condition);
                }
            }
            Set<Use> useSet = Analyzer.getUseSet(silica);
            useSetResult.addAll(useSet);
        }
        return new SQLTestResult(useSetResult,conditionHashMap);
    }
}
