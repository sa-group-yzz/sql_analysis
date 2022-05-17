package cases;

import cases.utils.CheckPoint;
import cases.utils.Helper;

import java.sql.*;

public class Case9 {
    public static void main(String[] args) throws SQLException {
        Connection con = Helper.createDB();
        Helper.initDB(con);
        Statement stm = con.createStatement();
        ResultSet rs = stm.executeQuery("SELECT id,name,price from cars where price >=10000 limit 1");
        rs.next();
        int a = 5;
        int b = args.length;
        int c=1, d=a+b;
        if(rs.getInt(2) > 1000000) {
            a = 1;
            c = a + b;
        } else {
            a = 10;
        }
        System.out.printf("%d,%d\n", c, d);
        CheckPoint.trigger(1, a + b, CheckPoint.EXPRESSION_ANALYSIS);
    }
}
