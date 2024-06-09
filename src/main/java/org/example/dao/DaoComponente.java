package org.example.dao;

import org.example.entities.Componente;
import org.example.entities.Maquina;

import java.util.List;

public interface DaoComponente {

    Integer cadastrarComponenteMysql(Componente componente, Integer idMaquina);

    List<Componente> buscarComponenteMysql(Maquina maquina);

    Integer cadastrarComponenteSqlServer(Componente componente, Integer idMaquina);

    List<Componente> buscarComponenteSqlServer(Maquina maquina);

}
