package SQLTest.testcase;

public class TableSwitchBranchCase {
    public int main() {
        int x = 3, y = 1;
        switch (x) {
            case 1:
                y = 100;
                break;
            case 2:
                y = 200;
            case 3:
                y = 300;
            default:
                y = 400;
        }
        return y;
    }
}
