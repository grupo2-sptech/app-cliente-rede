package org.example.dao.Implementation;

import org.example.database.ConexaoMysql;
import org.example.database.ConexaoSQLServer;
import org.example.entities.Maquina;
import org.example.entities.Usuario;
import org.example.utilities.console.FucionalidadeConsole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DaoMaquinaImple implements org.example.dao.DaoMaquina {


    private Connection connSql = null;
    private Connection connMysql = null;
    private PreparedStatement st = null;
    private ResultSet rs = null;

    public Maquina validarMaquinaMysql(String idProcessador) {

        if (connMysql == null) {
            connMysql = ConexaoMysql.getConection();
        }

        Maquina maquina = new Maquina();
        try {
            st = connMysql.prepareStatement("SELECT * FROM maquina WHERE processador_id = ?");
            st.setString(1, idProcessador);
            rs = st.executeQuery();
            if (rs.next()) {
                maquina.setId(rs.getInt("id_maquina"));
                maquina.setIdPorcessador(rs.getString("processador_id"));
                maquina.setSistemaOperacional(rs.getString("sistema_operacional"));
                maquina.setMemorialTotal(rs.getDouble("memoria_total_maquina"));
                maquina.setArquitetura(rs.getInt("arquitetura"));
                maquina.setIdSetor(rs.getInt("fk_setor"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao validar maquina: " + e.getMessage());
        }
        return maquina;
    }

    public Maquina validarMaquinaSqlServer(Maquina maquina, Usuario usuario) throws SQLException {

        if (connSql == null) {
            connSql = ConexaoSQLServer.getConection();
        }

        Maquina maquinaReturn = new Maquina();
        try {
            st = connSql.prepareStatement("SELECT * FROM maquina WHERE processador_id = ? AND fk_empresa = ? AND num_mac = ?;");
            st.setString(1, maquina.getIdPorcessador());
            st.setInt(2, usuario.getIdEmpresa());
            st.setString(3, maquina.getMac());
            rs = st.executeQuery();
            if (rs.next()) {
                maquinaReturn.setId(rs.getInt("id_maquina"));
                maquinaReturn.setIdPorcessador(rs.getString("processador_id"));
                maquinaReturn.setMac(rs.getString("num_mac"));
                maquinaReturn.setNome(rs.getString("nome_maquina"));
                maquinaReturn.setSistemaOperacional(rs.getString("sistema_operacional"));
                maquinaReturn.setMemorialTotal(rs.getDouble("memoria_total_maquina"));
                maquinaReturn.setArquitetura(rs.getInt("arquitetura"));
                maquinaReturn.setIdSetor(rs.getInt("fk_setor"));
                maquinaReturn.setIdEmpresa(rs.getInt("fk_empresa"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao validar maquina: " + e.getMessage());
        }
        return maquinaReturn;
    }

    public void cadastrarMaquinaMysql(Integer id_cadastro, Maquina maquina) throws SQLException {

        if (connMysql == null) {
            connMysql = ConexaoMysql.getConection();
        }
        try {
            st = connMysql.prepareStatement("""
                    UPDATE maquina SET processador_id = ?, sistema_operacional = ?, memoria_total_maquina = ?, arquitetura = ? WHERE id_maquina = ?;
                    """);
            st.setString(1, maquina.getIdPorcessador());
            st.setString(2, maquina.getSistemaOperacional());
            st.setDouble(3, maquina.getMemorialTotal());
            st.setInt(4, maquina.getArquitetura());
            st.setInt(5, id_cadastro);
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar maquina: " + e.getMessage());
        }
    }

    public void cadastrarMaquinaSqlServer(Integer id_cadastro, Maquina maquina) throws SQLException {

        if (connSql == null) {
            connSql = ConexaoSQLServer.getConection();
        }
        try {
            st = connSql.prepareStatement("""
                    UPDATE maquina SET processador_id = ?, num_mac = ?, sistema_operacional = ?, memoria_total_maquina = ?, arquitetura = ? WHERE id_maquina = ?;
                    """);
            st.setString(1, maquina.getIdPorcessador());
            st.setString(2, maquina.getMac());
            st.setString(3, maquina.getSistemaOperacional());
            st.setDouble(4, maquina.getMemorialTotal());
            st.setInt(5, maquina.getArquitetura());
            st.setInt(6, id_cadastro);
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar maquina: " + e.getMessage());

        }
    }

    public Integer buscarSetorMaquinaMysql(Integer idMaquina) {
        if (connMysql == null) {
            connMysql = ConexaoMysql.getConection();
        }
        if (connMysql == null) {
            FucionalidadeConsole fucionalidadeConsole = new FucionalidadeConsole();
            fucionalidadeConsole.limparConsole();
        } else {
            try {
                st = connMysql.prepareStatement("SELECT fk_setor FROM maquina  WHERE id_maquina = ?;");
                st.setInt(1, idMaquina);
                rs = st.executeQuery();
                if (rs.next()) {
                    return rs.getInt("fk_setor");
                }
            } catch (SQLException e) {
                System.out.println("Erro ao capturar dados do setor: " + e.getMessage());
            }
        }
        return null;
    }

    public Integer buscarSetorMaquinaSqlServer(Integer idMaquina) throws SQLException {

        if (connSql == null) {
            connSql = ConexaoSQLServer.getConection();
        }

        try {
            st = connSql.prepareStatement("SELECT fk_setor FROM maquina WHERE id_maquina = ?;");
            st.setInt(1, idMaquina);
            rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt("fk_setor");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao capturar dados do setor: " + e.getMessage());
        }
        return null;
    }
}