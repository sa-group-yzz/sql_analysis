import polyglot.ast.For;
import soot.*;
import soot.toolkits.graph.CompleteBlockGraph;
import soot.toolkits.graph.ExceptionalUnitGraph;

import java.util.HashMap;
import java.util.Set;

public class TestPreced {
    public static void main(String[] args) {
        Helper.initAugments(args);
        Helper.initEnv();
        SootClass sootClass = Scene.v().loadClassAndSupport("test2");
        sootClass.setApplicationClass();
        Scene.v().loadNecessaryClasses();
        SootMethod method = sootClass.getMethodByName("main");
        Body body = method.retrieveActiveBody();
        CompleteBlockGraph graph = new CompleteBlockGraph(body);
        HashMap<Integer, Set<Integer>>s =  Helper.getPrecdLine(graph);
        System.out.println(s);
    }
}
