package org.example.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DB {

    private static Connection conn = null;

    public static Connection getConection() {
        if (conn == null) {
            try {
                conn = DriverManager.getConnection("jdbc:mysql://:3306/hardware_security", "root", "jhow");
            } catch (SQLException e) {
                throw new DbExeption(e.getMessage());
            }

        }
        return conn;
    }

    public static void cloneConection() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new DbExeption(e.getMessage());
            }
        }
    }


    private static Properties loadProperties() {
        try (FileInputStream fs = new FileInputStream("db.propities")) {
            Properties properties = new Properties();
            properties.load(fs);
            return properties;
        } catch (IOException e) {
            throw new DbExeption(e.getMessage());
        }
    }

    public static void closeStatementAndResultSet(Statement st, ResultSet rt) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                throw new DbExeption(e.getMessage());
            }
        }
        if (rt != null) {
            try {
                rt.close();
            } catch (SQLException e) {
                throw new DbExeption(e.getMessage());
            }
        }

    }
}
