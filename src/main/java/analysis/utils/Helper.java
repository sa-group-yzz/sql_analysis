package analysis.utils;

import soot.G;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.options.Options;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Helper {
    public static void initEnv(String targetPath) {
        G.reset();
        List<String> dir = new ArrayList<>();
        dir.add(targetPath);
        Options.v().set_process_dir(dir);
        Options.v().set_whole_program(true);
        Options.v().set_verbose(false);
        Options.v().set_keep_line_number(true);
        Options.v().set_keep_offset(true);
        Options.v().set_allow_phantom_refs(true);
        Scene.v().loadNecessaryClasses();
        Options.v().setPhaseOption("jb", "use-original-names:true");
    }

    private static Class getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            // handle the exception
        }
        return null;
    }

    public static String casePattern = "^Case\\d+\\.class$";
    public static List<String> getTestCases(String classPrefix, String targetPath)  {
        List<String> classNames = new ArrayList<String>();
        File dir = new File(targetPath + "/" + classPrefix);
        if( !dir.exists() || !dir.isDirectory()) {
            System.out.printf("%s is not exist or not a dir\n", targetPath);
            return classNames;
        }
        for(File tf : dir.listFiles()) {
            String fn = tf.getName();
            if(!Pattern.matches(casePattern, fn)) {
                continue;
            }
            fn = fn.replace(".class", "");
            classNames.add(fn);
        }
        Collections.sort(classNames);
        return classNames;
    }

    public static SootMethod getTestCaseSootMethod(String targetPath, String classPrefix, String caseName) {
        Helper.initEnv(targetPath);
        String className = getClassName(classPrefix, caseName);
        SootClass sootClass = Scene.v().getSootClass(className);
        SootMethod method = sootClass.getMethodByName("main");
        return method;
    }

    public static String getClassName(String classPrefix, String caseName) {
        String className = classPrefix + "." + caseName;
        return className;
    }
}
