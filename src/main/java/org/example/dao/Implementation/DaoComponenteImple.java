package org.example.dao.Implementation;

import org.example.database.ConexaoMysql;
import org.example.database.ConexaoSQLServer;
import org.example.entities.Componente;
import org.example.entities.Maquina;
import org.example.utilities.console.FucionalidadeConsole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DaoComponenteImple implements org.example.dao.DaoComponente {

    private Connection connSql = null;
    private Connection connMysql = null;
    private PreparedStatement st = null;
    private ResultSet rs = null;

    public Integer cadastrarComponenteMysql(Componente componente, Integer idMaquina) {

        if (connMysql == null) {
            connMysql = ConexaoMysql.getConection();
        }

        Integer idInserido = 0;
        try {
            st = connMysql.prepareStatement("""
                            INSERT INTO componente (tipo_componente, tamanho_total_gb, tamanho_disponivel_gb, modelo, frequencia, fabricante, fk_maquina) VALUES (?,?,?,?,?,?,?);
                    """, st.RETURN_GENERATED_KEYS);

            st.setString(1, componente.getTipo() == null ? "" : componente.getTipo());
            st.setDouble(2, componente.getTamanho_total_gb() == null ? 0.0 : componente.getTamanho_total_gb());
            st.setDouble(3, componente.getTamanho_livre_gb() == null ? 0.0 : componente.getTamanho_livre_gb());
            st.setString(4, componente.getModelo() == null ? "" : componente.getModelo());
            st.setLong(5, componente.getFrequencia() == null ? 0 : componente.getFrequencia());
            st.setString(6, componente.getFabricante() == null ? "" : componente.getFabricante());
            st.setInt(7, idMaquina);
            st.executeUpdate();

            try (ResultSet generatedKeys = st.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    idInserido = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Falha ao obter o ID inserido.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar componente: " + e.getMessage());
        }
        return idInserido;
    }

    public Integer cadastrarComponenteSqlServer(Componente componente, Integer idMaquina) {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        Integer idInserido = 0;
        try {
            conn = ConexaoSQLServer.getConection();

            st = conn.prepareStatement("""
                            INSERT INTO componente (tipo_componente, tamanho_total_gb, tamanho_disponivel_gb, modelo, frequencia, fabricante, fk_maquina) VALUES (?,?,?,?,?,?,?);
                    """, st.RETURN_GENERATED_KEYS);

            st.setString(1, componente.getTipo() == null ? "" : componente.getTipo());
            st.setDouble(2, componente.getTamanho_total_gb() == null ? 0.0 : componente.getTamanho_total_gb());
            st.setDouble(3, componente.getTamanho_livre_gb() == null ? 0.0 : componente.getTamanho_livre_gb());
            st.setString(4, componente.getModelo() == null ? "" : componente.getModelo());
            st.setLong(5, componente.getFrequencia() == null ? 0 : componente.getFrequencia());
            st.setString(6, componente.getFabricante() == null ? "" : componente.getFabricante());
            st.setInt(7, idMaquina);
            st.executeUpdate();

            try (ResultSet generatedKeys = st.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    idInserido = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Falha ao obter o ID inserido.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar componente: " + e.getMessage());
        }
        return idInserido;
    }


    public List<Componente> buscarComponenteMysql(Maquina maquina) {

        List<Componente> componentes = new ArrayList<>();

        if (connMysql == null) {
            connMysql = ConexaoMysql.getConection();
        }

        try {
            st = connMysql.prepareStatement("SELECT componente.* FROM componente join maquina on id_maquina = fk_maquina WHERE fk_maquina = ?;");
            st.setInt(1, maquina.getId());
            rs = st.executeQuery();
            while (rs.next()) {
                Componente componente = new Componente();
                componente.setIdComponente(rs.getInt("id_componente"));
                componente.setTipo(rs.getString("tipo_componente"));
                componente.setTamanho_total_gb(rs.getDouble("tamanho_total_gb"));
                componente.setTamanho_livre_gb(rs.getDouble("tamanho_disponivel_gb"));
                componente.setModelo(rs.getString("modelo"));
                componente.setFrequencia(rs.getLong("frequencia"));
                componente.setFabricante(rs.getString("fabricante"));
                componentes.add(componente);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar componente: " + e.getMessage());
        }
        return componentes;
    }

    public List<Componente> buscarComponenteSqlServer(Maquina maquina) {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        List<Componente> componentes = new ArrayList<>();

        try {
            conn = ConexaoSQLServer.getConection();
            if (conn == null) {
                FucionalidadeConsole fucionalidadeConsole = new FucionalidadeConsole();
                fucionalidadeConsole.limparConsole();
            } else {
                st = conn.prepareStatement("SELECT componente.* FROM componente join maquina on id_maquina = fk_maquina WHERE fk_maquina = ?;");
                st.setInt(1, maquina.getId());
                rs = st.executeQuery();
                while (rs.next()) {
                    Componente componente = new Componente();
                    componente.setIdComponente(rs.getInt("id_componente"));
                    componente.setTipo(rs.getString("tipo_componente"));
                    componente.setTamanho_total_gb(rs.getDouble("tamanho_total_gb"));
                    componente.setTamanho_livre_gb(rs.getDouble("tamanho_disponivel_gb"));
                    componente.setModelo(rs.getString("modelo"));
                    componente.setFrequencia(rs.getLong("frequencia"));
                    componente.setFabricante(rs.getString("fabricante"));
                    componentes.add(componente);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar componente: " + e.getMessage());
        }
        return componentes;
    }
}
