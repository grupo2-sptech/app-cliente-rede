package org.example.dao;

import org.example.entities.Maquina;
import org.example.entities.Rede;

import java.sql.SQLException;

public interface DaoRede {
    public Integer salvarRede(Rede rede, Maquina maquina);

    public void salvarIp(Integer idRede, Rede rede) throws SQLException;
}
