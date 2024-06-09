package org.example.dao;

import org.example.entities.Maquina;
import org.example.entities.Usuario;

import java.sql.SQLException;

public interface DaoMaquina {

    void cadastrarMaquinaMysql(Integer id_cadastro, Maquina maquina) throws SQLException;

    Integer buscarSetorMaquinaMysql(Integer idMaquina);

    Maquina validarMaquinaMysql(String idProcessador);

    void cadastrarMaquinaSqlServer(Integer id_cadastro, Maquina maquina) throws SQLException;

    Integer buscarSetorMaquinaSqlServer(Integer idMaquina) throws SQLException;

    Maquina validarMaquinaSqlServer(Maquina maquina, Usuario usuario) throws SQLException;

}