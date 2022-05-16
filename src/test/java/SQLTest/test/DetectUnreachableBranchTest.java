package SQLTest.test;

import org.junit.Test;
import soot.*;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.DirectedGraph;
import sql.FetchSQLUsage;
import transform.DeleteUnreachableBranch;

import java.io.*;
import java.util.Set;

public class DetectUnreachableBranchTest extends BaseTest {
    @Test
    public void testUnreachableIfBranch() {
        SootClass sootClass = Scene.v().getSootClass("SQLTest.testcase.Case1");
        SootMethod mainMethod = null;
        for (SootMethod method : sootClass.getMethods()) {
            if (method.getName().contains("main")) {
                mainMethod = method;
            }
        }

        assert mainMethod != null;
        Body body = mainMethod.retrieveActiveBody();
        DirectedGraph<Unit> cfg = new BriefUnitGraph(body);
        DeleteUnreachableBranch delete = new DeleteUnreachableBranch();

        for (Unit unit : delete.detectUnreachableBranch(body, cfg)) {
            System.out.println(unit);
        }

        System.out.println("-----");
        DetectUnreachableBranchTest.printSource(new File("src/test/java/SQLTest/testcase/IfBranchCase.java"), cfg, delete.removeUnreachableBranch(cfg));
    }

    @Test
    public void testUnreachableIfBranchWithSQLUsage(){
        FetchSQLUsage fetchSQLUsage = new FetchSQLUsage("SQLTest.testcase.Case1");
        SootClass sootClass = Scene.v().getSootClass("SQLTest.testcase.Case1");
        SootMethod mainMethod = null;
        for (SootMethod method : sootClass.getMethods()) {
            if (method.getName().contains("main")) {
                mainMethod = method;
            }
        }

        assert mainMethod != null;
        Body body = mainMethod.retrieveActiveBody();
        DirectedGraph<Unit> cfg = new BriefUnitGraph(body);
        DeleteUnreachableBranch delete = new DeleteUnreachableBranch(fetchSQLUsage);
        delete.detectUnreachableBranchWithSQLUsage(body, cfg);
    }

    @Test
    public void testUnreachableLookupSwitchCase() {
        SootClass sootClass = Scene.v().getSootClass("SQLTest.testcase.LookupSwitchCase");
        SootMethod mainMethod = null;
        for (SootMethod method : sootClass.getMethods()) {
            if (method.getName().contains("main")) {
                mainMethod = method;
            }
        }

        assert mainMethod != null;
        Body body = mainMethod.retrieveActiveBody();
        DirectedGraph<Unit> cfg = new BriefUnitGraph(body);
        DeleteUnreachableBranch delete = new DeleteUnreachableBranch();

        for (Unit unit : delete.detectUnreachableBranch(body, cfg)) {
            System.out.println(unit);
        }
    }

    @Test
    public void testUnreachableTableSwitchBranchCase() {
        SootClass sootClass = Scene.v().getSootClass("SQLTest.testcase.TableSwitchCase");
        SootMethod mainMethod = null;
        for (SootMethod method : sootClass.getMethods()) {
            if (method.getName().contains("main")) {
                mainMethod = method;
            }
        }

        assert mainMethod != null;
        Body body = mainMethod.retrieveActiveBody();
        DirectedGraph<Unit> cfg = new BriefUnitGraph(body);
        DeleteUnreachableBranch delete = new DeleteUnreachableBranch();

        for (Unit unit : delete.detectUnreachableBranch(body, cfg)) {
            System.out.println(unit);
        }
    }

    private static void printSource(File testFile, DirectedGraph<Unit> cfg, Set<Unit> visited) {
        int startLine = cfg.getHeads().get(0).getJavaSourceStartLineNumber();
        int endLine = cfg.getTails().get(0).getJavaSourceStartLineNumber();

        StringBuilder sb = new StringBuilder();

        try {
            FileInputStream inputStream = new FileInputStream(testFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            int count = 1;
            while ((line = bufferedReader.readLine()) != null) {
                if (count >= startLine && count <= endLine) {
                    boolean flag = false;
                    for (Unit unit : visited) {
                        if (unit.getJavaSourceStartLineNumber() == count) {
                            flag = true;
                            break;
                        }
                    }

                    if (flag) {
                        sb.append(line).append("\n");
                    }
                } else {
                    sb.append("\n");
                }
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(sb);
    }
}
