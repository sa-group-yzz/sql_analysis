package analysis;

import analysis.utils.CheckPointAnalysis;
import analysis.utils.CheckPointDetail;
import analysis.utils.Helper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.cli.*;
import soot.*;
import soot.jimple.BinopExpr;
import soot.jimple.toolkits.typing.fast.QueuedSet;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import transform.RemoveUnreachableBranch;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class AnalysisTester {
    public static void main(String[] args) throws IOException, CsvValidationException {
        Options options = new Options();
        Option jar = new Option("t", "target", true, "target class path");
        jar.setRequired(true);
        options.addOption(jar);
        Option prefix = new Option("p", "prefix", true, "class prefix");
        prefix.setRequired(false);
        options.addOption(prefix);
        Option assertion = new Option("a", "assertion", true, "assertion path");
        assertion.setRequired(true);
        options.addOption(assertion);
        Option caseSpecific = new Option("c", "case", true, "specific case");
        caseSpecific.setRequired(true);
        options.addOption(caseSpecific);
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

        String targetPath = cmd.getOptionValue("target");
        String assertionPath = cmd.getOptionValue("assertion");
        String classPrefix = cmd.getOptionValue("prefix");
        String specificCase = cmd.getOptionValue("case");
        if (classPrefix == null || classPrefix.equals("")) {
            classPrefix = "cases";
        }

        List<String> testCases = Helper.getTestCases(classPrefix, targetPath);
        for (String caseName : testCases) {
            if(specificCase != null && !Objects.equals(specificCase, "") && !specificCase.equals(caseName)) {
                continue;
            }
            System.out.printf("handling %s ...\n", caseName);
            SootMethod method = Helper.getTestCaseSootMethod(targetPath, classPrefix, caseName);
            Body b = method.retrieveActiveBody();
            ExceptionalUnitGraph graph = new ExceptionalUnitGraph(b);

            String runType = "normal";
            classCaseNumber = 0;
            Helper.initEnv(targetPath);
            if(!runAnalysis(assertionPath, caseName, graph, runType)) {
                printErrorMsg(runType);
                System.exit(1);
            }
            RemoveUnreachableBranch.testPath = targetPath;
            b = RemoveUnreachableBranch.remove(Helper.getClassName(classPrefix, caseName));
            ExceptionalUnitGraph graph1 = new ExceptionalUnitGraph(b);
            runType = "sql";
            classCaseNumber = 0;
            if(!runAnalysis(assertionPath, caseName, graph1, runType)) {
                printErrorMsg(runType);
                System.exit(1);
            }
            System.out.println("pass");

            caseNumber += classCaseNumber;
        }
        System.out.printf("case number:%d\n", caseNumber);
    }
    static int caseNumber = 0;
    static int classCaseNumber = 0;

    private static void printErrorMsg(String runType) {
        System.out.printf("%s failed\n", runType);
        System.out.println("expected:");
        System.out.println(expectedResult);
        System.out.println("actual:");
        System.out.println(realResult);
    }


    private static boolean runAnalysis(String assertionPath, String caseName, ExceptionalUnitGraph graph, String runType) throws IOException, CsvValidationException {
        Map<Integer, Set<CheckPointDetail>> checkPointDetailMap = (new CheckPointAnalysis(graph)).ret;
        System.out.printf("run mode: %s\n", runType);
        currentCase = caseName;
        if(checkPointDetailMap.get(CheckPointDetail.LIVENESS_ANALYSIS) != null) {
            System.out.println("run liveness analysis");
            classCaseNumber++;
            LiveVarAnalysis liveVarAnalysis = new LiveVarAnalysis(graph);
            if (!runLiveness(assertionPath, caseName, runType, checkPointDetailMap, liveVarAnalysis)) return false;
        }
        if(checkPointDetailMap.get(CheckPointDetail.DEFINITION_ANALYSIS) != null) {
            classCaseNumber++;
            System.out.println("run reaching definition analysis");
            DefinitionAnalysis definitionAnalysis = new DefinitionAnalysis(graph);
            if(!runDefinition(assertionPath, caseName, runType, checkPointDetailMap, definitionAnalysis)) return false;
        }
        if(checkPointDetailMap.get(CheckPointDetail.CONSTANT_ANALYSIS) != null) {
            classCaseNumber++;
            System.out.println("run constant propagation analysis");
            ConstantPropagation constantPropagation = new ConstantPropagation(graph);
            if(!runConst(assertionPath, caseName, runType, checkPointDetailMap, constantPropagation)) return false;
        }
        if(checkPointDetailMap.get(CheckPointDetail.EXPRESSION_ANALYSIS) != null) {
            classCaseNumber++;
            System.out.println("run available expression analysis");
            AvailableExpression availableExpression = new AvailableExpression(graph);
            if(!runAvail(graph, assertionPath, caseName, runType, checkPointDetailMap, availableExpression)) return false;
        }
        return true;
    }

    private static boolean runAvail(ExceptionalUnitGraph graph, String assertionPath, String caseName, String runType, Map<Integer, Set<CheckPointDetail>> checkPointDetailMap, AvailableExpression availableExpression) throws CsvValidationException, IOException {
        String analysisType = "expression";
        currentAnalysis = analysisType;
        Map<String, List<String>> result = new HashMap<>();
        for (CheckPointDetail cd : checkPointDetailMap.get(CheckPointDetail.EXPRESSION_ANALYSIS)) {
            BinopExpr checkValue = null;
            Unit checkUnit = null;
            Value tmpValue = cd.getValue();
            QueuedSet<Unit> us = new QueuedSet<>();
            us.addLast(graph.getPredsOf(cd.getUnit()));
            while (checkValue == null) {
                Unit u = us.removeFirst();
                boolean findDef = false;
                for(ValueBox vb : u.getDefBoxes()) {
                    Value tv = vb.getValue();
                    if(tv.equivTo(tmpValue)) {
                        findDef = true;
                        break;
                    }
                }
                if(!findDef) {
                    us.addLast(graph.getPredsOf(u));
                    continue;
                }
                for(ValueBox vb : u.getUseBoxes()) {
                    Value tv = vb.getValue();
                    if(tv instanceof BinopExpr) {
                        checkValue = (BinopExpr) tv;
                        checkUnit = u;
                        break;
                    }
                }
            }
            ArraySparseSet<MyBinop> bv = availableExpression.getFlowBefore(checkUnit);
            List<String> expValueBoxes = new ArrayList<>();
            for(MyBinop entry : bv) {
                if(entry.equals(MyBinop.getInstance(checkValue))) {
                    expValueBoxes.add(String.format("%s", 1));
                    break;
                }
            }
            if(expValueBoxes.size() == 0)
                expValueBoxes.add(String.format("%s", 0));
            result.put(cd.getId(), expValueBoxes);
        }
        return runAssert(assertionPath, caseName, runType, result, analysisType);
    }

    private static boolean runConst(String assertionPath, String caseName, String runType, Map<Integer, Set<CheckPointDetail>> checkPointDetailMap, ConstantPropagation constantPropagation) throws CsvValidationException, IOException {
        String analysisType = "constant";
        currentAnalysis = analysisType;
        Map<String, List<String>> result = new HashMap<>();
        for (CheckPointDetail cd : checkPointDetailMap.get(CheckPointDetail.DEFINITION_ANALYSIS)) {
            CPFact bv = constantPropagation.getFlowBefore(cd.getUnit());
            Value checkValue = cd.getValue();
            List<String> constValueBoxes = new ArrayList<>();
            bv.entries().forEach(entry->{
                if(entry.getKey() == checkValue) {
                    constValueBoxes.add(String.format("%s", entry.getValue().toString()));

                }
            });
            result.put(cd.getId(), constValueBoxes);
        }
        return runAssert(assertionPath, caseName, runType, result, analysisType);
    }

    private static boolean runDefinition(String assertionPath, String caseName, String runType, Map<Integer, Set<CheckPointDetail>> checkPointDetailMap, DefinitionAnalysis defVarAnalysis) throws IOException, CsvValidationException {
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
    private static boolean runLiveness(String assertionPath, String caseName, String runType, Map<Integer, Set<CheckPointDetail>> checkPointDetailMap, LiveVarAnalysis liveVarAnalysis) throws IOException, CsvValidationException {
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
