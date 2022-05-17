package SQLTest.test;

import analysis.AvailableExpression;
import analysis.ConstantPropagation;
import analysis.DefinitionAnalysis;
import analysis.LiveVarAnalysis;
import analysis.utils.CheckPointAnalysis;
import analysis.utils.CheckPointDetail;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.junit.Test;
import soot.*;
import soot.toolkits.graph.ExceptionalUnitGraph;
import transform.RemoveUnreachableBranch;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
public class RemoveTest extends BaseTest {
    @Test
    public void testRemove() throws CsvValidationException, IOException {
        String className = "cases.Case1";
        SootClass sootClass = Scene.v().getSootClass(className);
        SootMethod mainMethod = null;
        for (SootMethod method : sootClass.getMethods()) {
            if (method.getName().contains("main")) {
                mainMethod = method;
            }
        }

        String assertionPath = "/Users/zhouzhichao/sql_analysis_demo/assertions";

        assert mainMethod != null;
        Body body = mainMethod.retrieveActiveBody();
        ExceptionalUnitGraph graph = new ExceptionalUnitGraph(body);
        assert  runAnalysis(assertionPath, "case1", graph, "normal");
        System.out.println("success normal");


        body = RemoveUnreachableBranch.remove(className);
        ExceptionalUnitGraph graph1 = new ExceptionalUnitGraph(body);
        assert runAnalysis(assertionPath, "case1", graph1, "sql");
        System.out.println("success sql");
    }

    private static String currentCase;
    private static int classCaseNumber;

    private static String currentAnalysis;

    private static boolean runAnalysis(String assertionPath, String caseName, ExceptionalUnitGraph graph, String runType) throws IOException, CsvValidationException {
        Map<Integer, Set<CheckPointDetail>> checkPointDetailMap = (new CheckPointAnalysis(graph)).ret;

        currentCase = caseName;
        if(checkPointDetailMap.get(CheckPointDetail.LIVENESS_ANALYSIS) != null) {
            System.out.println("run liveness analysis");
            classCaseNumber++;
            LiveVarAnalysis liveVarAnalysis = new LiveVarAnalysis(graph);
            if (!runLiveness(assertionPath, caseName, runType, checkPointDetailMap, liveVarAnalysis)) return false;
        }
        return true;
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
        return asserResult.equals(result);
    }

}
