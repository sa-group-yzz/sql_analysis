import soot.*;
import soot.toolkits.graph.ExceptionalUnitGraph;

import java.util.*;

public class LiveVariableAnalyzer {

    public static void main(String[] args) {
        Helper.initAugments(args);
        List<String> classes = Helper.getClasses();
        for(String className : classes) {
            System.out.printf("handling %s ...\n", className);
            soot.G.reset();
            Helper.initEnv();
            Body body = Helper.getBodyFromSoot(className);
            HashMap<Integer, List<String>> lData = Helper.getClassLivenessData(body);
            Helper.output(className, lData, body);
        }

    }
}
