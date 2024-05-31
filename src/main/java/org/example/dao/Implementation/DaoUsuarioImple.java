package org.example.dao.Implementation;

import org.example.database.ConexaoMysql;
import org.example.database.ConexaoSQLServer;
import org.example.database.DatabaseExeption;
import org.example.entities.Usuario;
import org.example.utilities.Slack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class DaoUsuarioImple implements org.example.dao.DaoUsuario {

    private Connection conn = null;
    private PreparedStatement st = null;
    private ResultSet rs = null;

    public Usuario validarUsuarioMysql(String login, String senha) throws DatabaseExeption {
        Usuario usuario = new Usuario();
        conn = ConexaoMysql.getConection();
        try {
            st = conn.prepareStatement("""
                            SELECT * FROM funcionario WHERE email_funcionario = ? AND senha_acesso = ? OR login_acesso = ? AND senha_acesso = ?;                    
                    """);
            st.setString(1, login);
            st.setString(2, senha);
            st.setString(3, login);
            st.setString(4, senha);
            rs = st.executeQuery();
            if (rs.next()) {
                usuario.setNome(rs.getString("nome_funcionario"));
                usuario.setEmail(rs.getString("email_funcionario"));
                usuario.setIdEmpresa(rs.getInt("fk_empresa"));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao Validar Usuario MYSQL: " + e.getMessage());
        }
        return usuario;
    }


    public Usuario validarUsuarioSql(String login, String senha) throws DatabaseExeption, SQLException {
        Usuario usuario = new Usuario();

        conn = ConexaoSQLServer.getConection();
        if (conn == null) {
            System.exit(0);
        } else {
            try {
                st = conn.prepareStatement("""
                                SELECT * FROM funcionario WHERE email_funcionario = ? AND senha_acesso = ? OR login_acesso = ? AND senha_acesso = ?;                    
                        """);
                st.setString(1, login);
                st.setString(2, senha);
                st.setString(3, login);
                st.setString(4, senha);
                rs = st.executeQuery();
                if (rs.next()) {
                    usuario.setId(rs.getInt("id_funcionario"));
                    usuario.setNome(rs.getString("nome_funcionario"));
                    usuario.setIdEmpresa(rs.getInt("fk_empresa"));
                }
            } catch (SQLException e) {
                System.out.println("Erro ao validar usuario SQL SERVER: " + e.getMessage());
            }
        }
        return usuario;
    }


    public Slack getTokenSlack(Usuario usuario) throws DatabaseExeption, SQLException {

        Slack slack = new Slack();

        conn = ConexaoSQLServer.getConection();
        if (conn == null) {
            System.exit(0);
        } else {
            try {
                st = conn.prepareStatement("""
                               SELECT e.token_slack, e.canal_slack, e.web_url_slack
                                   FROM empresa as e where id_empresa = ?;                
                        """);
                st.setInt(1, usuario.getIdEmpresa());
                rs = st.executeQuery();
                if (rs.next()) {
                    slack.setToken(rs.getString("token_slack"));
                    slack.setCanal(rs.getString("canal_slack"));
                    slack.setWebUrl(rs.getString("web_url_slack"));
                }
            } catch (SQLException e) {
                System.out.println("Erro ao obter token slack: " + e.getMessage());
            }
        }
        return slack;
    }

}
