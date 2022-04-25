package analysis.utils;

import soot.G;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.options.Options;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Helper {
    public static void initEnv(String jarPath) {
        String javaHome = System.getenv("JAVA_HOME");
        if(javaHome == null || javaHome.equals("")) {
            System.out.println("Env[JAVA_HOME] is needed");
            System.exit(1);
        }
        Options.v().setPhaseOption("jop.cpf", "enabled:false");
        Options.v().setPhaseOption("jb", "use-original-names:true");
        Options.v().set_soot_classpath(jarPath + ":" + javaHome + "/jre/lib/rt.jar");
        Options.v().set_output_format(Options.output_format_jimple);
    }

    public static String casePattern = "^cases\\.Case\\d+\\.class$";
    public static List<String> getTestCases(String jarPath) throws IOException {
        List<String> classNames = new ArrayList<String>();
        ZipInputStream zip = new ZipInputStream(new FileInputStream(jarPath));
        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
            if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                // This ZipEntry represents a class. Now, what class does it represent?
                String className = entry.getName().replace('/', '.'); // including ".class"
                if(!Pattern.matches(casePattern, className)) {
                    continue;
                }
                className = className.replace("cases.", "");
                classNames.add(className.substring(0, className.length() - ".class".length()));
            }
        }
        return classNames;
    }

    public static SootMethod getTestCaseSootMethod(String jarPath, String classPrefix, String caseName) {
        G.reset();
        Options.v().set_keep_line_number(true);
        Helper.initEnv(jarPath);
        String className = classPrefix + "." + caseName;
        SootClass sootClass = Scene.v().loadClassAndSupport(className);
        sootClass.setApplicationClass();
        Scene.v().loadNecessaryClasses();
        SootMethod method = sootClass.getMethodByName("main");
        return method;
    }
}
