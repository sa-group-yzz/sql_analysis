package analysis;

import org.apache.commons.cli.*;

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
        System.out.println(assertionPath);
        System.out.println(classPrefix);

    }
}
