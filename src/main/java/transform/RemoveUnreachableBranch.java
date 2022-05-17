package transform;

import soot.*;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.DirectedGraph;
import sql.FetchSQLUsage;

public class RemoveUnreachableBranch {
    public static Body remove(String className) {
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

        return delete.deleteUnreachableBranch(getSootArgs());
    }


    private static String[] getSootArgs() {
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
