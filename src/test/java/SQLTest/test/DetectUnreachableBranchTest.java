package SQLTest.test;

import org.junit.Test;
import soot.*;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.DirectedGraph;
import sql.FetchSQLUsage;
import transform.DeleteUnreachableBranch;


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
        delete.detectUnreachableBranchWithSQLUsage(body, cfg);

        Body result = delete.deleteUnreachableBranch(getSootArgs());
        System.out.println(result);
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
