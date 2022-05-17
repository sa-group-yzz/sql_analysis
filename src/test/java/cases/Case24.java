package cases;

import cases.utils.CheckPoint;
import cases.utils.Helper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Case24 {
    public static void main(String[] args) throws SQLException {
        Connection con = Helper.createDB();
        Helper.initDB(con);
        Statement stm = con.createStatement();
        ResultSet rs = stm.executeQuery("SELECT id,name,price,total from cars where price >=10 and total <= 100 limit 1");
        rs.next();
        int a = 5;
        int b = args.length;
        int c=1, d=a+b;
        if(rs.getInt(2) -2 > 10 || rs.getInt(3) <= 100) {
            a = 1;
            c = a + b;
        } else {
            a = 10;
        }
        System.out.printf("%d,%d\n", c, d);
        CheckPoint.trigger(1, a + b, CheckPoint.EXPRESSION_ANALYSIS);
    }
}
