package SQLTest.test;

import org.junit.Test;
import soot.*;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.DirectedGraph;
import sql.FetchSQLUsage;
import transform.DeleteUnreachableBranch;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;


public class DetectUnreachableBranchTest extends BaseTest {
    @Test
    public void testUnreachableIfBranchWithSQLUsage() {
        String className = "SQLTest.testcase.Case1";
        FetchSQLUsage fetchSQLUsage = new FetchSQLUsage(className);
        SootClass sootClass = Scene.v().getSootClass(className);
        SootMethod mainMethod = null;
        for (SootMethod method : sootClass.getMethods()) {
            if (method.getName().contains("main")) {
                mainMethod = method;
            }
        }

        assert mainMethod != null;
        Body body = mainMethod.retrieveActiveBody();
        DirectedGraph<Unit> cfg = new BriefUnitGraph(body);
        DeleteUnreachableBranch delete = new DeleteUnreachableBranch(fetchSQLUsage, className);
        delete.detectUnreachableBranchWithSQLUsage(cfg);

        Body result = delete.delete(getSootArgs());

        DirectedGraph<Unit> graph = new BriefUnitGraph(result);
        Unit head = graph.getHeads().get(0);
        Queue<Unit> q = new LinkedList<>();
        Set<Unit> visit = new HashSet<>();

        q.add(head);
        visit.add(head);

        while (!q.isEmpty()) {
            Unit unit = q.poll();
            System.out.println(unit);
            for (Unit succUnit : graph.getSuccsOf(unit)) {
                if (!visit.contains(succUnit)) {
                    visit.add(succUnit);
                    q.add(succUnit);
                }
            }
        }

    }


    private String[] getSootArgs() {
        return new String[]{
                "-process-dir", "target/test-classes",
                "-w",
                "-v",
                "-keep-line-number",
                "-keep-offset",
                "-allow-phantom-refs",
                "-p", "jb", "use-original-names:true"
        };
    }
}
