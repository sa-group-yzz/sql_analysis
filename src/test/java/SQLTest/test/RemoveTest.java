package SQLTest.test;

import org.junit.Test;
import soot.G;
import transform.RemoveUnreachableBranch;
public class RemoveTest extends BaseTest {
    @Test
    public void testRemove() {
        System.out.println(RemoveUnreachableBranch.remove("SQLTest.testcase.Case1"));
    }
}
