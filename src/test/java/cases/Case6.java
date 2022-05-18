package cases;

import cases.utils.CheckPoint;
import cases.utils.Helper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Case6 {
    public static void main(String[] args) throws SQLException {
        Connection con = Helper.createDB();
        Helper.initDB(con);
        Statement stm = con.createStatement();
        ResultSet rs = stm.executeQuery("SELECT id,name,price from cars where price >=10000 limit 1");
        rs.next();
        int a = args[0].length();
        int b = args.length;
        int c=1, d=a+b;
        if(rs.getInt(2) < 100) {
            a = 10;
        } else {
            c = a + b;
        }
        System.out.printf("%d,%d\n", c, d);
        CheckPoint.trigger(1, a + b, CheckPoint.EXPRESSION_ANALYSIS);
    }
}
