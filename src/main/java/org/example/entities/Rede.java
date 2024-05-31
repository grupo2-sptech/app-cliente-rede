package org.example.entities;

import java.util.ArrayList;
import java.util.List;

public class Rede {
    private Long totalPacoteRecebidos = (long) 0;
    private Long totalPacoteEnviados = (long) 0;
    private List<String> ipv4 = new ArrayList<>();
    private List<String> interfaceRede = new ArrayList<>();


    public Rede(Long totalPacoteRecebidos, Long totalPacoteEnviados) {
        this.totalPacoteRecebidos = totalPacoteRecebidos;
        this.totalPacoteEnviados = totalPacoteEnviados;
    }

    public Rede() {
    }

    public void addIpv4(String ipv4) {
        this.ipv4.add(ipv4);
    }

    public void addInterfaceRede(String interfaceRede) {
        this.interfaceRede.add(interfaceRede);
    }

    public List<String> getIpv4() {
        return ipv4;
    }

    public List<String> getInterfaceRede() {
        return interfaceRede;
    }

    public Long getTotalPacoteRecebidos() {
        return totalPacoteRecebidos;
    }

    public void setTotalPacoteRecebidos(Long totalPacoteRecebidos) {
        this.totalPacoteRecebidos = totalPacoteRecebidos;
    }

    public Long getTotalPacoteEnviados() {
        return totalPacoteEnviados;
    }

    private String formatList(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "Nenhum";
        }
        return String.join(", ", list);
    }

    @Override
    public String toString() {
        return String.format(
                "        Rede:\n" +
                        "           Total de Pacotes Recebidos: %d\n" +
                        "           Total de Pacotes Enviados: %d\n" +
                        "           IPv4: %s\n" +
                        "           Interface de Rede: %s",
                totalPacoteRecebidos,
                totalPacoteEnviados,
                formatList(ipv4),
                formatList(interfaceRede)
        );
    }

    public void setTotalPacoteEnviados(Long totalPacoteEnviados) {
        this.totalPacoteEnviados = totalPacoteEnviados;
    }
}
