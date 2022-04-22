package analysis;

import analysis.utils.Helper;
import org.apache.commons.cli.*;
import soot.*;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.CompleteBlockGraph;
import soot.toolkits.graph.ExceptionalUnitGraph;

import java.util.Set;

public class AnalysisTester {
    public static void main(String[] args) {
        Options options = new Options();
        Option jar = new Option("j", "jar", true, "jar path");
        jar.setRequired(true);
        options.addOption(jar);
        Option prefix = new Option("p", "prefix", true, "class prefix");
        prefix.setRequired(false);
        options.addOption(prefix);
        Option assertion = new Option("a", "assertion", true, "assertion path");
        assertion.setRequired(true);
        options.addOption(assertion);
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }

        String jarPath = cmd.getOptionValue("jar");
        String assertionPath = cmd.getOptionValue("assertion");
        String classPrefix = cmd.getOptionValue("prefix");
        if(classPrefix == null || classPrefix.equals("")) {
            classPrefix = "cases";
        }

        System.out.println(jarPath);
        Helper.initEnv(jarPath);

        String className = classPrefix + ".Case1";

        SootClass sootClass = Scene.v().loadClassAndSupport(className);
        sootClass.setApplicationClass();
        Scene.v().loadNecessaryClasses();
        SootMethod method = sootClass.getMethodByName("main");

        Body b = method.retrieveActiveBody();
        ExceptionalUnitGraph graph = new ExceptionalUnitGraph(b);



        LiveVarAnalysis liveVarAnalysis = new LiveVarAnalysis(graph);

        for(Unit u : graph) {
            Set<Value> bv = liveVarAnalysis.getFlowBefore(u);
            Set<Value>av = liveVarAnalysis.getFlowAfter(u);
            System.out.println("---------------------------");
            System.out.println(u);
            System.out.println(av);
            System.out.println(bv);
            System.out.println("---------------------------");

        }

    }
}
