package org.example.dao.Implementation;

import com.github.britooo.looca.api.core.Looca;
import org.example.database.ConexaoSQLServer;
import org.example.entities.Maquina;
import org.example.entities.Rede;

import java.sql.*;

public class DaoRedeImple implements org.example.dao.DaoRede {

    Looca looca = new Looca();

    public Integer salvarRede(Rede rede, Maquina maquina) {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        Integer idRede = 0;
        try {
            conn = ConexaoSQLServer.getConection();
            st = conn.prepareStatement("insert into rede (hostname, pacotes_enviados, pacotes_recebidos, fk_maquina) values (?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            st.setString(1, looca.getRede().getParametros().getHostName());
            st.setLong(2, rede.getTotalPacoteEnviados());
            st.setLong(3, rede.getTotalPacoteRecebidos());
            st.setInt(4, maquina.getId());
            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                rs = st.getGeneratedKeys();
                if (rs.next()) {
                    idRede = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao salvar rede: " + e.getMessage());
        } finally {
            // ConexaoMysql.closeStatementAndResultSet(st, rs, conn);
        }
        return idRede;
    }

    public void salvarIp(Integer idRede, Rede rede) throws SQLException {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        conn = ConexaoSQLServer.getConection();

        for (String redeVez : rede.getIpv4()) {
            st = conn.prepareStatement("insert into ipv4 (ip, fk_rede) values (?, ?);");
            st.setString(1, redeVez);
            st.setInt(2, idRede);
            st.executeUpdate();
        }
    }
}
