package SQLTest.testcase.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Helper {
    public static void initDB(Connection con) throws SQLException {
        con.createStatement().execute("CREATE TABLE cars(id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255), price INT);\n" +
                "INSERT INTO cars(name, price) VALUES('Audi', 52642);\n" +
                "INSERT INTO cars(name, price) VALUES('Mercedes', 57127);\n" +
                "INSERT INTO cars(name, price) VALUES('Skoda', 9000);\n" +
                "INSERT INTO cars(name, price) VALUES('Volvo', 29000);\n" +
                "INSERT INTO cars(name, price) VALUES('Bentley', 350000);\n" +
                "INSERT INTO cars(name, price) VALUES('Citroen', 21000);\n" +
                "INSERT INTO cars(name, price) VALUES('Hummer', 41400);\n" +
                "INSERT INTO cars(name, price) VALUES('Volkswagen', 21600);");

    }
    public static Connection createDB() throws SQLException {
        String url = "jdbc:h2:mem:app";
        Connection con = DriverManager.getConnection(url);
        return con;
    }
}
