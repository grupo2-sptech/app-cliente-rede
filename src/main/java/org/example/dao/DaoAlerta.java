package org.example.dao;

import org.example.entities.Maquina;

public interface DaoAlerta {
    Double buscarMediaUsoRam(Maquina maquina);

    void inserirAlertaRam(Double usoRam, Maquina maquina);

    Double buscarMediaUsoCpu(Maquina maquina);

    void inserirAlertaCpu(Double usoCpu, Maquina maquina);


}
