package analysis.utils;

import soot.options.Options;

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
}
