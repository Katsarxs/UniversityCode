package util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConnection {
    private static String url, user, password;

    static {
        try {
            Properties properties = new Properties();
            InputStream inputStream = DBConnection.class.getClassLoader().getResourceAsStream("db.properties");
            properties.load(inputStream);

            url = properties.getProperty("db.url");
            user = properties.getProperty("db.user");
            password = properties.getProperty("db.password");
        } catch (Exception e) {
            System.out.println("Σφάλμα : " + e.getMessage());
        }
    }

    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, user, password);
    }
}