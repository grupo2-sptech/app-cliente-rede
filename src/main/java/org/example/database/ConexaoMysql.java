package org.example.database;

import java.sql.*;

public class ConexaoMysql extends Conexao {


    private static final String URL = "jdbc:mysql://:3306/hardware_security";
    private static final String USUARIO = "aluno1";
    private static final String SENHA = "123";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConection() {
        conn = null;
            try {
                conn = DriverManager.getConnection(URL, USUARIO, SENHA);
            } catch (SQLException e) {
                throw new DatabaseExeption(e.getMessage());
        }
        return conn;
    }

    public static void closeStatementAndResultSet(Statement st, ResultSet rt, Connection conn) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                throw new DatabaseExeption(e.getMessage());
            }
        }
        if (rt != null) {
            try {
                rt.close();
            } catch (SQLException e) {
                throw new DatabaseExeption(e.getMessage());
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                // Tratar exceção, se necessário
                e.printStackTrace();
            }
        }
    }
}
