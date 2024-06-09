package org.example.dao;

import org.example.entities.JanelasBloqueadas;
import org.example.entities.Maquina;

import java.util.List;

public interface DaoJanelasBloqueadas {

    List<String> buscarJanelasBloqueadasMysql(List<Integer> idCard);

    List<JanelasBloqueadas> buscarJanelasBloqueadasSqlServer(List<Integer> idCard);

    List<Integer>buscarCadsAtivosNoSetorSql(Integer idSetor, Integer idEmpresa);

    void inserirDadosBloqueio(Maquina maquina, Integer categoria);

    void alertaBloqueio(Maquina maquina, String nomeProcesso);
}
