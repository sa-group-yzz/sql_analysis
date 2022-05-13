package SQLTest.testcase;

public class LookupSwitchCase {
    public int main() {
        int x = 3, y = 1;
        switch (x) {
            case 1:
                y = 100;
                break;
            case 3:
                y = 200;
            case 5:
                y = 300;
            default:
                y = 400;
        }
        return y;
    }
}
