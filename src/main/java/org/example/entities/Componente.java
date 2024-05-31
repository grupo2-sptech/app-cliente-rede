package org.example.entities;

import org.example.entities.component.Registro;

public class Componente {
    private Integer idComponente;


    private String tipo;
    private Double tamanho_total_gb;
    private Double tamanho_livre_gb;
    private String fabricante;
    private String modelo;
    private Long frequencia;
    private Registro registro;


    public Componente(String tipo, Double tamanho_total_gb, Double tamanho_livre_gb, String fabricante, String modelo, Long frequencia) {
        this.tipo = tipo;
        this.tamanho_total_gb = tamanho_total_gb;
        this.tamanho_livre_gb = tamanho_livre_gb;
        this.fabricante = fabricante;
        this.modelo = modelo;
        this.frequencia = frequencia;
    }

    public Componente(Integer idComponente, String tipo, Double tamanho_total_gb, Double tamanho_livre_gb, String fabricante, String modelo, Long frequencia) {
        this.idComponente = idComponente;
        this.tipo = tipo;
        this.tamanho_total_gb = tamanho_total_gb;
        this.tamanho_livre_gb = tamanho_livre_gb;
        this.fabricante = fabricante;
        this.modelo = modelo;
        this.frequencia = frequencia;
    }


    public Componente() {
    }

    public Integer getIdComponente() {
        return idComponente;
    }

    public void setIdComponente(Integer idComponente) {
        this.idComponente = idComponente;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getTamanho_total_gb() {
        return tamanho_total_gb;
    }

    public void setTamanho_total_gb(Double tamanho_total_gb) {
        this.tamanho_total_gb = tamanho_total_gb;
    }

    public Double getTamanho_livre_gb() {
        return tamanho_livre_gb;
    }

    public void setTamanho_livre_gb(Double tamanho_livre_gb) {
        this.tamanho_livre_gb = tamanho_livre_gb;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Long getFrequencia() {
        return frequencia;
    }

    public void setFrequencia(Long frequencia) {
        this.frequencia = frequencia;
    }

    @Override
    public String toString() {
        return """
                Componente{
                    tipo='%s',
                    tamanho_total_gb=%.2f,
                    tamanho_livre_gb=%.2f,
                    fabricante='%s',
                    modelo='%s',
                    frequencia=%d
                }
                """.formatted(tipo, tamanho_total_gb, tamanho_livre_gb, fabricante, modelo, frequencia);
    }
}
