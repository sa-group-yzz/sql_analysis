package analysis;

import analysis.utils.CheckPointAnalysis;
import analysis.utils.CheckPointDetail;
import analysis.utils.Helper;
import com.google.common.reflect.ClassPath;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.cli.*;
import soot.*;
import soot.toolkits.graph.ExceptionalUnitGraph;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class AnalysisTester {
    public static void main(String[] args) throws IOException, CsvValidationException {
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
        if (classPrefix == null || classPrefix.equals("")) {
            classPrefix = "cases";
        }

        List<String> testCases = Helper.getTestCases(jarPath);
        for (String caseName : testCases) {
            System.out.printf("handling %s ...\n", caseName);
            SootMethod method = Helper.getTestCaseSootMethod(jarPath, classPrefix, caseName);
            Body b = method.retrieveActiveBody();
            ExceptionalUnitGraph graph = new ExceptionalUnitGraph(b);

            String runType = "normal";
            if(!runAnalysis(assertionPath, caseName, graph, runType)) {
                printErrorMsg(runType);
                System.exit(1);
            }
            System.out.println("pass");
        }
    }

    private static void printErrorMsg(String runType) {
        System.out.printf("%s failed\n", runType);
        System.out.println("expected:");
        System.out.println(expectedResult);
        System.out.println("actual:");
        System.out.println(realResult);
    }


    private static boolean runAnalysis(String assertionPath, String caseName, ExceptionalUnitGraph graph, String runType) throws IOException, CsvValidationException {
        Map<Integer, List<CheckPointDetail>> checkPointDetailMap = (new CheckPointAnalysis(graph)).ret;

        currentCase = caseName;
        if(checkPointDetailMap.get(CheckPointDetail.LIVENESS_ANALYSIS) != null) {
            System.out.println("run liveness analysis");
            LiveVarAnalysis liveVarAnalysis = new LiveVarAnalysis(graph);
            if (!runLiveness(assertionPath, caseName, runType, checkPointDetailMap, liveVarAnalysis)) return false;
        }
        if(checkPointDetailMap.get(CheckPointDetail.DEFINITION_ANALYSIS) != null) {
            System.out.println("run reaching definition analysis");
            DefinitionAnalysis definitionAnalysis = new DefinitionAnalysis(graph);
            if(!runDefinition(assertionPath, caseName, runType, checkPointDetailMap, definitionAnalysis)) return false;
        }
        return true;
    }
    private static boolean runDefinition(String assertionPath, String caseName, String runType, Map<Integer, List<CheckPointDetail>> checkPointDetailMap, DefinitionAnalysis defVarAnalysis) throws IOException, CsvValidationException {
        String analysisType = "definition";
        currentAnalysis = analysisType;
        Map<String, List<String>> result = new HashMap<>();
        for (CheckPointDetail cd : checkPointDetailMap.get(CheckPointDetail.DEFINITION_ANALYSIS)) {
            BitSet bv = defVarAnalysis.getFlowAfter(cd.getUnit());
            List<String> reachValueBoxes = new ArrayList<>();
            for(ValueBox vb : defVarAnalysis.getSameValueBoxes().get(cd.getValue())) {
                if(bv.get(defVarAnalysis.getvIndexMap().get(vb))) {
                    reachValueBoxes.add(String.format("%d", defVarAnalysis.getVbLineMap().get(vb)));
                }
            }
            result.put(cd.getId(), reachValueBoxes);
        }
        return runAssert(assertionPath, caseName, runType, result, analysisType);
    }
    private static boolean runLiveness(String assertionPath, String caseName, String runType, Map<Integer, List<CheckPointDetail>> checkPointDetailMap, LiveVarAnalysis liveVarAnalysis) throws IOException, CsvValidationException {
        Map<String, List<String>> result = new HashMap<>();
        for (CheckPointDetail cd : checkPointDetailMap.get(CheckPointDetail.LIVENESS_ANALYSIS)) {
            Set<Value> bv = liveVarAnalysis.getFlowAfter(cd.getUnit());
            List<String> cl = result.computeIfAbsent(cd.getId(), k -> new ArrayList<>());
            for (Value v : bv)
                cl.add(v.toString());
        }
        String analysisType = "liveness";
        currentAnalysis = analysisType;
        return runAssert(assertionPath, caseName, runType, result, analysisType);
    }

    static Map<String, List<String>> realResult = null;
    static Map<String, List<String>> expectedResult = null;
    static String currentAnalysis = "";
    static String currentCase = "";

    private static boolean runAssert(String assertionPath, String caseName, String runType, Map<String, List<String>> result, String analysisType) throws IOException, CsvValidationException {
        // read assert
        for (String k : result.keySet()) {
            Collections.sort(result.get(k));
        }
        String normalAssertPath = Paths.get(assertionPath, runType, analysisType, caseName.toLowerCase() +
                ".csv").toString();
        CSVReader csvReader = new CSVReader(new FileReader(normalAssertPath));
        String[] nextRecord;
        Map<String, List<String>> asserResult = new HashMap<>();

        while ((nextRecord = csvReader.readNext()) != null) {
            List<String> cl = asserResult.computeIfAbsent(nextRecord[0].trim(), k -> new ArrayList<>());
            cl.add(nextRecord[1].trim());
        }
        for (String k : asserResult.keySet()) {
            Collections.sort(asserResult.get(k));
        }
        expectedResult = asserResult;
        realResult = result;
        return asserResult.equals(result);
    }
}
