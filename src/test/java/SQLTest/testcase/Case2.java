package SQLTest.testcase;

import java.sql.*;

public class Case2 {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://test:3306/test";
    static final String USER = "root";
    static final String PASS = "password";

    //match
    public void testRowRetrieval() throws ClassNotFoundException, SQLException {
        Connection conn;
        Statement stmt;
        Class.forName(JDBC_DRIVER);
        System.out.println("Connecting to database...");
        conn = DriverManager.getConnection(DB_URL, USER, PASS);
        System.out.println("Creating statement...");
        stmt = conn.createStatement();
        //stmt.execute("create table mac(id text,name text,grade text, primary key(name, id))");
        ResultSet rs = stmt.executeQuery("select id,pk,g from mac where id > 100");
        int idx = rs.getInt(0);
        String pk = rs.getString(1);
        if(idx < 10 && pk.equals("pk")){
            String new_name = rs.getString(1);
            String query = "INSERT ... " + new_name + pk;
            stmt.execute(query);
        }
        rs.close();
        stmt.close();
        conn.close();
    }
}
