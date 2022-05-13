package SQLTest.test;

import org.junit.Test;
import soot.*;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.DirectedGraph;
import transform.DeleteUnreachableBranch;

public class DetectUnreachableBranchTest extends BaseTest {
    @Test
    public void testUnreachableIfBranch() {
        SootClass sootClass = Scene.v().getSootClass("SQLTest.testcase.IfBranchCase");
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

        for(Unit unit : delete.detectUnreachableBranch(body, cfg)) {
            System.out.println(unit);
        }
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

        for(Unit unit : delete.detectUnreachableBranch(body, cfg)) {
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

        for(Unit unit : delete.detectUnreachableBranch(body, cfg)) {
            System.out.println(unit);
        }
    }
}
