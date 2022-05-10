package SQLTest.testcase.utils;

public class CheckPoint {

    public static int CONSTANT_ANALYSIS = 0x1;
    public static int DEFINITION_ANALYSIS = 0x10;
    public static int EXPRESSION_ANALYSIS = 0x100;
    public static int LIVENESS_ANALYSIS = 0x1000;

    public static void trigger(int i, int id, int flag) {
        System.out.printf("%d(%d):%d\n", i, flag, id);
    }
    public static void trigger(int i, Object id, int flag) {
        System.out.printf("%d(%d):%s\n", i, flag, id.toString());
    }
}
