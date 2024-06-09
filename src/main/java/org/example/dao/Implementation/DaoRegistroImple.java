package org.example.dao.Implementation;

import com.github.britooo.looca.api.core.Looca;
import org.example.database.ConexaoMysql;
import org.example.database.ConexaoSQLServer;
import org.example.entities.Maquina;
import org.example.entities.Usuario;
import org.example.entities.component.Registro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DaoRegistroImple implements org.example.dao.DaoRegistro {

    private Connection connMysl = null;
    private Connection connSql = null;
    private PreparedStatement stRamMysql = null;
    private PreparedStatement stCpuMysql = null;
    private PreparedStatement stRamSqlServer = null;
    private PreparedStatement stCpuSqlServer = null;

    Looca looca = new Looca();
    Registro registro = new Registro();

    public DaoRegistroImple() {
        try {
            connMysl = ConexaoMysql.getConection();
            connSql = ConexaoSQLServer.getConection();

            stRamMysql = connMysl.prepareStatement("INSERT INTO historico_hardware (ram_ocupada, data_hora, fk_maquina) VALUES (?, now(), ?);");
            stCpuMysql = connMysl.prepareStatement("INSERT INTO historico_hardware (cpu_ocupada, data_hora, fk_maquina) VALUES (?, now(), ?);");

            stRamSqlServer = connSql.prepareStatement("INSERT INTO historico_hardware (ram_ocupada, data_hora, fk_maquina) VALUES (?, GETDATE(), ?);");
            stCpuSqlServer = connSql.prepareStatement("INSERT INTO historico_hardware (cpu_ocupada, data_hora, fk_maquina) VALUES (?, GETDATE(), ?);");
        } catch (SQLException e) {
            System.out.println("Erro ao preparar statements: " + e.getMessage());
        }
    }

    public void inserirRegistroTempoReal(Maquina maquina) {
        Double usoCpu = Math.round(looca.getProcessador().getUso() * 100.0) / 100.0;
        Double usoRam = registro.converterGB(looca.getMemoria().getEmUso());

        try {
            connMysl.setAutoCommit(false);
            connSql.setAutoCommit(false);

            stRamMysql.setDouble(1, usoRam);
            stRamMysql.setInt(2, maquina.getId());
            stRamMysql.addBatch();

            stCpuMysql.setDouble(1, usoCpu);
            stCpuMysql.setInt(2, maquina.getId());
            stCpuMysql.addBatch();

            stRamSqlServer.setDouble(1, usoRam);
            stRamSqlServer.setInt(2, maquina.getId());
            stRamSqlServer.addBatch();

            stCpuSqlServer.setDouble(1, usoCpu);
            stCpuSqlServer.setInt(2, maquina.getId());
            stCpuSqlServer.addBatch();

            stRamMysql.executeBatch();
            stCpuMysql.executeBatch();
            stRamSqlServer.executeBatch();
            stCpuSqlServer.executeBatch();

            connMysl.commit();
            connSql.commit();

        } catch (SQLException e) {
            System.out.println("Erro ao inserir registros: " + e.getMessage());
            try {
                if (connMysl != null) connMysl.rollback();
                if (connSql != null) connSql.rollback();
            } catch (SQLException ex) {
                System.out.println("Erro ao reverter transações: " + ex.getMessage());
            }
        } finally {
            try {
                if (connMysl != null) connMysl.setAutoCommit(true);
                if (connSql != null) connSql.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Erro ao redefinir auto-commit: " + e.getMessage());
            }
        }
    }

    public void registroEntrada(Usuario usuario, Maquina maquina) {
        try (Connection conn = ConexaoSQLServer.getConection()) {
            PreparedStatement st = conn.prepareStatement(
                    "INSERT INTO uso_maquina (fk_funcionario, fk_maquina, hora_data_entrada) VALUES (?, ?, GETDATE());"
            );
            st.setInt(1, usuario.getId());
            st.setInt(2, maquina.getId());
            st.execute();
        } catch (SQLException e) {
            System.out.println("Erro ao registrar entrada: " + e.getMessage());
        }
    }
}
