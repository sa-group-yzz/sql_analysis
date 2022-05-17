package SQLTest.testcase;

import java.sql.*;

public class Case1 {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://test:3306/test";
    static final String USER = "root";
    static final String PASS = "password";

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Connection conn;
        Statement stmt;
        Class.forName(JDBC_DRIVER);
        System.out.println("Connecting to database...");
        conn = DriverManager.getConnection(DB_URL, USER, PASS);
        System.out.println("Creating statement...");
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select id,name,price from cars where price < 300 limit 1");
        int id = rs.getInt(0);
        String name = rs.getString(1);
        int price = rs.getInt(2);
        if(price < 500){
            String new_name = rs.getString(1);
            String query = "INSERT ... " + new_name + id;
            stmt.execute(query);
        }else {
            System.out.println(name + " " + price);
        }
        rs.close();
        stmt.close();
        conn.close();
    }
}
