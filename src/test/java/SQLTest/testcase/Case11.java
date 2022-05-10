package SQLTest.testcase;

import SQLTest.testcase.utils.CheckPoint;
import SQLTest.testcase.utils.Helper;

import java.sql.*;

public class Case11 {
    public static void main(String[] args) throws SQLException {
        Connection con = Helper.createDB();
        Helper.initDB(con);
        Statement stm = con.createStatement();
        ResultSet rs = stm.executeQuery("SELECT id,name,price, total from cars where price >=10 and total <= 100  limit 1");
        rs.next();
        int a;
        int b = args.length;
        CheckPoint.trigger(1, null, CheckPoint.LIVENESS_ANALYSIS);
        if(rs.getInt(3) >= 10 && rs.getInt(4) <= 100) {
            a = 1;
        } else {
            a = b + 1;
        }
        CheckPoint.trigger(1, a, CheckPoint.CONSTANT_ANALYSIS | CheckPoint.DEFINITION_ANALYSIS);
    }
}
