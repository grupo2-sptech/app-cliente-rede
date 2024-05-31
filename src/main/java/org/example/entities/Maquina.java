package org.example.entities;

import com.github.britooo.looca.api.core.Looca;
import org.example.dao.*;
import org.example.dao.Implementation.*;
import org.example.entities.component.Registro;
import org.example.utilities.Slack;
import org.example.utilities.Utilitarios;
import org.example.utilities.console.FucionalidadeConsole;

import java.sql.SQLException;
import java.util.*;

public class Maquina {
    private Integer id;
    private String idPorcessador;
    private String mac;
    private String nome;
    private String modelo;
    private Double memorialTotal;
    private String sistemaOperacional;
    private Integer arquitetura;
    private Integer idSetor;
    private Integer idEmpresa;


    private List<Componente> componentes;

    final Looca locca = new Looca();
    final Utilitarios utilitarios = new Utilitarios();
    final Scanner sc = new Scanner(System.in);
    Registro registro = new Registro();
    Looca looca = new Looca();
    DaoAlerta daoAlerta = new DaoAlertaimple();
    DaoUsuario daoUsuario = new DaoUsuarioImple();
    Timer timer = new Timer();

    public Double getMemorialTotal() {
        return memorialTotal;
    }

    public void setMemorialTotal(Double memorialTotal) {
        this.memorialTotal = memorialTotal;
    }

    public Maquina(Integer id, String idPorcessador, String mac, String nome, String modelo, Double memorialTotal, String sistemaOperacional, Integer arquitetura) {
        this.id = id;
        this.idPorcessador = idPorcessador;
        this.mac = mac;
        this.nome = nome;
        this.modelo = modelo;
        this.memorialTotal = memorialTotal;
        this.sistemaOperacional = sistemaOperacional;
        this.arquitetura = arquitetura;
        componentes = new ArrayList<>();
    }

    public Maquina() {
        componentes = new ArrayList<>();
    }

    public void monitoramento(Maquina maquina, Usuario usuario) throws SQLException, InterruptedException {
        DaoComponente daoComponente = new DaoComponenteImple();
        DaoRegistro daoRegistro = new DaoRegistroImple();
        DaoJanelasBloqueadas daoJanelasBloqueadas = new DaoJanelasBloqueadasImple();
        FucionalidadeConsole fucionalidadeConsole = new FucionalidadeConsole();
        JanelasBloqueadas janelasBloqueadas = new JanelasBloqueadas();
        Rede rede = new Rede();
        DaoRede daoRede = new DaoRedeImple();



        setComponentes(daoComponente.buscarComponenteSqlServer(maquina));

        Slack slack;
        slack = daoUsuario.getTokenSlack(usuario);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Double procentagerUsoRam = daoAlerta.buscarMediaUsoRam(maquina) / registro.converterGB(looca.getMemoria().getTotal()) * 100;
                if (procentagerUsoRam > 80) {
                    daoAlerta.inserirAlertaRam(procentagerUsoRam, maquina);
                    slack.mensagemSlack("Atenção!\nA " + maquina.getNome() + "teve um uso médio de RAM acima de 80% por 5 minutos.");
                    slack.mensagemSlack("Média de Uso: %.2f".formatted(procentagerUsoRam));
                }
            }
        }, 5 * 60 * 1000, 5 * 60 * 1000);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Double procentagerUsoCpu = daoAlerta.buscarMediaUsoCpu(maquina) * 2;
                if (procentagerUsoCpu > 70) {
                    daoAlerta.inserirAlertaCpu(procentagerUsoCpu, maquina);
                    slack.mensagemSlack("Atenção!\nA " + maquina.getNome() + "teve um uso médio de CPU acima de 70% por 2 minutos.");
                    slack.mensagemSlack("Média de Uso: %.2f".formatted(procentagerUsoCpu));
                }
            }
        }, 2 * 60 * 1000, 2 * 60 * 1000);

        for (int i = 0; i < looca.getRede().getGrupoDeInterfaces().getInterfaces().size(); i++) {
            if (looca.getRede().getGrupoDeInterfaces().getInterfaces().get(i).getPacotesRecebidos() != 0) {
                rede.setTotalPacoteRecebidos(rede.getTotalPacoteRecebidos() + looca.getRede().getGrupoDeInterfaces().getInterfaces().get(i).getPacotesRecebidos());
                rede.setTotalPacoteEnviados(rede.getTotalPacoteEnviados() + looca.getRede().getGrupoDeInterfaces().getInterfaces().get(i).getPacotesEnviados());
                rede.addInterfaceRede(looca.getRede().getGrupoDeInterfaces().getInterfaces().get(i).getNome());
                rede.addIpv4(looca.getRede().getGrupoDeInterfaces().getInterfaces().get(i).getEnderecoIpv4().get(0));
            }
        }

        daoRede.salvarIp(daoRede.salvarRede(rede, maquina), rede);

        Thread registroThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    daoRegistro.inserirRegistroTempoReal(maquina);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        
                    }
                }
            }
        });

        Thread monitoramentoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    FucionalidadeConsole fucionalidadeConsole = new FucionalidadeConsole();
                    fucionalidadeConsole.limparConsole();
                    Utilitarios utilitarios = new Utilitarios();
                    utilitarios.mensagemInformativa();
                    try {
                        janelasBloqueadas.monitorarJanelas(daoJanelasBloqueadas.buscarJanelasBloqueadasSqlServer(daoJanelasBloqueadas.buscarCadsAtivosNoSetorSql(maquina.getIdSetor(), usuario.getIdEmpresa())), maquina);
                    } catch (InterruptedException e) {
                        System.out.println("Erro ao monitorar as janelas " + e);
                    }
                    System.out.println(rede);
                }
            }
        });

        registroThread.start();
        monitoramentoThread.start();

//        while (true) {
//            daoRegistro.inserirRegistroTempoReal(maquina);
//            fucionalidadeConsole.limparConsole();
//            Utilitarios utilitarios = new Utilitarios();
//            utilitarios.mensagemInformativa();
//            janelasBloqueadas.monitorarJanelas(daoJanelasBloqueadas.buscarJanelasBloqueadasSqlServer(daoJanelasBloqueadas.buscarCadsAtivosNoSetorSql(maquina.getIdSetor(), usuario.getIdEmpresa())), maquina);
////            Thread.sleep(1000);
//        }
    }

    public void cadastrarMaquina(Maquina maquina, Usuario usuario) throws SQLException {

        DaoMaquina daoMaquina = new DaoMaquinaImple();
        DaoComponente daoComponente = new DaoComponenteImple();
        utilitarios.centralizaTelaHorizontal(8);
        utilitarios.mensagemCadastroMaquina();
        utilitarios.centralizaTelaHorizontal(8);
        System.out.print("Insira o código aqui: ");
        Integer idCadastro = sc.nextInt();
        if (daoMaquina.buscarSetorMaquinaSqlServer(idCadastro) == null) {
            utilitarios.codigoIncorreto();
        } else {
            maquina.setId(idCadastro);
            Componente componenteRam = new Componente(
                    "Memória Ram",
                    registro.converterGB(looca.getMemoria().getTotal()),
                    null,
                    null,
                    null,
                    null);
            maquina.addComponente(componenteRam);

            Componente componenteCpu = new Componente(
                    "Processador",
                    null,
                    null,
                    looca.getProcessador().getFabricante(),
                    looca.getProcessador().getNome(),
                    looca.getProcessador().getFrequencia());

            maquina.addComponente(componenteCpu);

            Map<String, Componente> componentesDisco = new HashMap<>();

            for (int i = 0; i < looca.getGrupoDeDiscos().getDiscos().size(); i++) {
                Componente componenteDisco = new Componente(
                        "Disco " + (i + 1),
                        registro.converterGB(looca.getGrupoDeDiscos().getVolumes().get(i).getTotal()),
                        registro.converterGB(looca.getGrupoDeDiscos().getVolumes().get(i).getDisponivel()),
                        null,
                        looca.getGrupoDeDiscos().getDiscos().get(i).getModelo(),
                        null
                );

                Integer idComponenteDisco;
                idComponenteDisco = daoComponente.cadastrarComponenteSqlServer(componenteDisco, idCadastro);
                daoComponente.cadastrarComponenteMysql(componenteDisco, idCadastro);
                componenteDisco.setIdComponente(idComponenteDisco);
                componentesDisco.put("componenteDisco" + (i + 1), componenteDisco);
                maquina.addComponente(componenteDisco);
            }

            daoMaquina.cadastrarMaquinaSqlServer(idCadastro, maquina);
            daoMaquina.cadastrarMaquinaMysql(idCadastro, maquina);

            maquina = daoMaquina.validarMaquinaSqlServer(maquina, usuario);

            maquina.setIdSetor(maquina.getIdSetor());
            maquina.setId(maquina.getId());

            componenteRam.setIdComponente(daoComponente.cadastrarComponenteSqlServer(componenteRam, idCadastro));
            componenteCpu.setIdComponente(daoComponente.cadastrarComponenteSqlServer(componenteCpu, idCadastro));
            componenteRam.setIdComponente(daoComponente.cadastrarComponenteMysql(componenteRam, idCadastro));
            componenteCpu.setIdComponente(daoComponente.cadastrarComponenteMysql(componenteCpu, idCadastro));
        }
    }


    public List<Componente> listarComponentes() {
        return componentes;
    }

    public void setComponentes(List<Componente> componentes) {
        this.componentes = componentes;
    }

    public Integer getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Integer idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public void addComponente(Componente componente) {
        componentes.add(componente);
    }

    public void removeComponente(Componente componente) {
        componentes.remove(componente);
    }

    public void listarCaracteristicas() {

    }

    public Integer getIdSetor() {
        return idSetor;
    }

    public void setIdSetor(Integer idSetor) {
        this.idSetor = idSetor;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdPorcessador() {
        return idPorcessador;
    }

    public void setIdPorcessador(String idPorcessador) {
        this.idPorcessador = idPorcessador;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getSistemaOperacional() {
        return sistemaOperacional;
    }

    public void setSistemaOperacional(String sistemaOperacional) {
        this.sistemaOperacional = sistemaOperacional;
    }

    public Integer getArquitetura() {
        return arquitetura;
    }

    public void setArquitetura(Integer arquitetura) {
        this.arquitetura = arquitetura;
    }

    @Override
    public String toString() {
        return
                """
                        Maquina : {
                        id : %d
                        idPorcessador : %s
                        nome : %s
                        modelo : %s
                        sistemaOperacional : %s
                        memoryTotal : %.2f
                        arquitetura : %d
                        componentes : %s
                        }
                                                """.formatted(id, idPorcessador, nome, modelo, sistemaOperacional, memorialTotal, arquitetura, componentes);
    }
}
// Path: src/main/java/org/example/entidade/Componente.java