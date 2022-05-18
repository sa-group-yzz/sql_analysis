package SQLTest.test;

import org.junit.BeforeClass;
import soot.G;
import soot.Scene;
import soot.options.Options;

import java.util.ArrayList;
import java.util.List;

public class BaseTest {
    @BeforeClass
    public static void setUpBeforeClass() {
        G.reset();
        List<String> dir = new ArrayList<>();
        dir.add("target/test-classes");
        Options.v().set_process_dir(dir);
        Options.v().set_whole_program(true);
        Options.v().set_verbose(false);
        Options.v().set_keep_line_number(true);
        Options.v().set_keep_offset(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().setPhaseOption("jb","use-original-names:true");

        Scene.v().loadNecessaryClasses();
    }
}
