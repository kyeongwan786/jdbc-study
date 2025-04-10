package org.example.study.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtils {
    private static final String CONNECTION_URL = "jdbc:mariadb://localhost:33067/";
    private static final String CONNECTION_USERNAME = "root";
    private static final String CONNECTION_PASSWORD = "test1234";


    public static Connection getConnection() throws SQLException, ClassNotFoundException { //
        Class.forName("org.mariadb.jdbc.Driver");
        return DriverManager.getConnection(CONNECTION_URL,
                CONNECTION_USERNAME,
                CONNECTION_PASSWORD);
    }

    private DatabaseUtils() {
        super();
    }
}
