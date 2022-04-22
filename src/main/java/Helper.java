import soot.*;
import soot.jimple.internal.JGotoStmt;
import soot.jimple.internal.JNopStmt;
import soot.options.Options;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.CompleteBlockGraph;
import soot.toolkits.graph.ExceptionalUnitGraph;

import java.io.*;
import java.util.*;

public class Helper {
    public static String destFolder = null;
    public static String outputFolder = null;

    public static List<String> getClasses() {
        File f = new File(destFolder);
        String [] paths = f.list();
        List<String> classes = new ArrayList<>();
        for (String p : paths) {
            if(!p.endsWith(".java")) {
                continue;
            }
            String[] subPaths = p.split("/");
            String className = subPaths[subPaths.length-1].replace(".java", "");
            classes.add(className);
        }
        return classes;
    }

    public static void initAugments(String [] args) {
        if(args.length < 1) {
            System.out.println("The dest folder is needed");
            System.exit(1);
        }
        destFolder = args[0];
        if(destFolder.endsWith("/")) {
            System.out.println("dest folder can not end with /");
            System.exit(1);
        }
        File df = new File(destFolder);
        if(!df.isDirectory()) {
            System.out.println("dest folder does not exist");
            System.exit(1);
        }

        outputFolder = destFolder + "/../output";
        File of = new File(outputFolder);
        if(!of.exists()) {
            if(!of.mkdir()) {
                System.out.println("create dir failed");
                System.exit(1);
            }
        } else if(!of.isDirectory()) {
            System.out.println("output is not dir");
            System.exit(1);
        }
    }

    public static void initEnv() {
        String javaHome = System.getenv("JAVA_HOME");
        if(javaHome == null || javaHome.equals("")) {
            System.out.println("Env[JAVA_HOME] is needed");
            System.exit(1);
        }
        Options.v().setPhaseOption("jop.cpf", "enabled:false");
        Options.v().set_soot_classpath(Helper.destFolder + ":" + javaHome + "/jre/lib/rt.jar");
        Options.v().set_output_format(Options.output_format_jimple);
    }
    static int magicNumber = 100000000;
    public static HashMap<Integer, Set<Integer>>getPrecdLine(BlockGraph graph) {
        HashMap<Integer, Set<Integer>> r = new HashMap<>();
        for(Block u: graph) {
            Integer cl = magicNumber;
            for(Unit uu : u) {
                if(uu instanceof JNopStmt || uu instanceof JGotoStmt) {
                    continue;
                }
                int tmp = uu.getJavaSourceStartLineNumber();
                if(tmp < cl) {
                    cl = tmp;
                }
            }
            if(cl == magicNumber) {
                continue;
            }
            for(Block up: u.getPreds()) {
                Stack<Unit> us = new Stack<>();
                for(Unit upu : up) {
                    us.push(upu);
                }
                int ul = -1;
                Unit chosenTmp;
                while (!us.empty()) {
                    Unit tmp = us.pop();
                    if(tmp.getJavaSourceStartLineNumber() < cl && tmp.getJavaSourceStartLineNumber() > ul) {
                        ul = tmp.getJavaSourceStartLineNumber();
                    }
                }
                if(ul == -1) {
                    continue;
                }
                if(cl.equals(ul)) {
                    continue;
                }
                if(!r.containsKey(cl)) {
                    r.put(cl, new HashSet<>());
                }
                r.get(cl).add(ul);
            }


        }
        return r;
    }

    public static HashMap<Integer, List<String>> getClassLivenessData(Body body) {
        ExceptionalUnitGraph graph = new ExceptionalUnitGraph(body);
        IterativeSolver liveVarAnalysis = new IterativeSolver(graph, new LiveVariableAnalysis());
        HashMap<Integer, Set<String>> lineToVar = new HashMap<>();
        for (Unit u : graph) {
            if(u.branches()) {
                continue;
            }
            if(u.toString().contains("nop")) {
                continue;
            }
            Set<Value> bv = liveVarAnalysis.getFlowBefore(u);
            Integer line = u.getJavaSourceStartLineNumber();
            lineToVar.computeIfAbsent(line, k -> new HashSet<>());
            for(Value v : bv) {
                if(v.toString().startsWith("temp$")) {
                    continue;
                }
                lineToVar.get(line).add(v.toString());
            }
        }
        HashMap<Integer, List<String>>lvListMap =  new HashMap<>();
        lineToVar.forEach((l, vs)->{
            ArrayList<String> vl = new ArrayList<>(vs);
            Collections.sort(vl);
            lvListMap.put(l, vl);
        });
        return lvListMap;
    }

    public static Body getBodyFromSoot(String className) {
        SootClass sootClass = Scene.v().loadClassAndSupport(className);
        sootClass.setApplicationClass();
        Scene.v().loadNecessaryClasses();
        SootMethod method = sootClass.getMethodByName("main");
        Body body = method.retrieveActiveBody();
        return body;
    }

    public static void output(String className, HashMap<Integer, List<String>> lData,Body body) {
        String classPath = destFolder + "/" + className + ".java";
        HashMap<Integer, Set<Integer>> precdLines = Helper.getPrecdLine(new CompleteBlockGraph(body));
        File sourceFileHandle = new File(classPath);
        if(!sourceFileHandle.exists()) {
            System.out.println(classPath + " does not exists");
            System.exit(1);
        }
        List<String> sourceList = new ArrayList<>();
        try {
                File outFile = new File(outputFolder + "/" + className + ".txt");
                if(outFile.exists()) {
                    outFile.delete();
                } else {
                    outFile.createNewFile();
                }
                BufferedReader reader= new BufferedReader(new FileReader(sourceFileHandle));
                String temp;
                while((temp=reader.readLine())!=null) {
                    sourceList.add(temp.trim());
                }
            } catch (IOException e) {
                System.out.println("handle file failed: " + e.toString());
                System.exit(1);
        }
        int endLine = -1;
        for(Integer e : lData.keySet()) {
            if(e > endLine) {
                endLine = e;
            }
        }
        int i = 1;
        int beginLine = -1;
        HashMap<Integer, String> outLines = new HashMap<>();
        Set<Integer> handled = new HashSet<>();
        List<Integer> record = new ArrayList<>();

        for(String s : sourceList) {
           if(beginLine != -1 && i <= endLine) {
               outLines.put(i, s);
               handled.add(i);
               record.add(i);
           }
           i++;
           if(s.contains("static void main")) {
               beginLine = i;
           } else if(s.contains("static int main")) {
               beginLine = i;
           }
        }
        int finalBeginLine = beginLine;
        lData.forEach((li, vs)->{
            if(li < finalBeginLine) {
                return;
            }
            Set<Integer> locs;
            if(precdLines.containsKey(li)) {
                locs = new HashSet<>(precdLines.get(li));
            } else {
                int findLoc = li - 1;
                while (outLines.get(findLoc).equals("")) {
                    findLoc--;
                }
                locs = new HashSet<>();
                locs.add(findLoc);
            }
            for(Integer fl : locs) {
                outLines.put(fl, outLines.get(fl) + " [" + String.join(",", vs) + "]");
                handled.remove(fl);
            }
        });
        for(Integer e : handled) {
            outLines.put(e, outLines.get(e) + " []");
        }
        List<String>outlineList = new ArrayList<>();
        for(Integer e: record) {
            outlineList.add(outLines.get(e));
        }
        try {
            PrintWriter writer = new PrintWriter(outputFolder + "/" + className + ".txt", "UTF-8");
            outlineList.forEach(writer::println);
            writer.close();
        } catch (Exception e) {
            System.out.println("handle file failed: " + e.toString());
            System.exit(1);
        }

    }
}
