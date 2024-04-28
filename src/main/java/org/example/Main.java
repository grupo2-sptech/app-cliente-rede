package org.example;


import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.janelas.Janela;
import org.example.db.DB;


import java.io.Console;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import static org.example.FucionalidadeConsole.limparConsole;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Utilitarios utils = new Utilitarios();
        Scanner sc = new Scanner(System.in);
        Looca looca = new Looca();
        Locale.setDefault(Locale.US);
        limparConsole();
        Integer idMaquina = 0;
        utils.centralizaTelaHorizontal(22);


        Connection conn = null;
        Statement st = null;
        ResultSet rt = null;
        Boolean maquinaCadastrada = false;
        List<String> processosBloqueados = new ArrayList<>();
        Console console = System.console();


        limparConsole();
        utils.exibirLogo();
        utils.exibirMenu();
        utils.exibirMensagem();

        conn = DB.getConection();

        // Lógica principal do programa
        try {
            utils.centralizaTelaHorizontal(22);
            System.out.println("Login:");
            utils.centralizaTelaHorizontal(22);
            String email = sc.next();
            System.out.println();
            utils.centralizaTelaHorizontal(22);
            System.out.println("Senha:");
            utils.centralizaTelaHorizontal(22);
            String senha;
            if (console != null) {
                char[] passwordArray = console.readPassword();
                senha = new String(passwordArray);
            } else {
                senha = sc.next();
            }

            String query = """
                    SELECT funcionario_id, nome_funcionario, setor.setor_id from
                    funcionario
                        JOIN setor ON setor.setor_id = funcionario.fk_setor
                        JOIN processos_bloqueados_no_setor as pb ON pb.fk_setor = setor.setor_id
                        JOIN processos_janelas as pj ON pj.processo_id = pb.fk_processo
                        WHERE email_funcionario  = '%s' AND senha_acesso = '%s' OR
                        login_acesso = '%s' AND senha_acesso = '%s';
                    """.formatted(email, senha, email, senha);

            st = conn.createStatement();
            rt = st.executeQuery(query);

            if (rt.next()) {
                utils.centralizaTelaHorizontal(22);
                limparConsole();
                utils.barraLoad(1);
                System.out.println("""



                                         __________________________________________
                                         |            ACESSO VALIDO !             |
                                         |________________________________________|
                        """);
                Thread.sleep(2000);
                Integer setorId = rt.getInt("setor_id");
                String querylista = """
                            SELECT  pj.titulo_processo from processos_bloqueados_no_setor as pb join setor on pb.fk_setor = setor.setor_id
                            JOIN processos_janelas as pj ON pj.processo_id = pb.fk_processo
                            WHERE setor_id = %d;
                        """.formatted(setorId);
                st = conn.createStatement();
                rt = st.executeQuery(querylista);
                while (rt.next()) {
                    processosBloqueados.add(rt.getString("titulo_processo"));
                }

                String sqlVerificarId = "SELECT processador_id, maquina_id FROM maquina WHERE processador_id = '%s'".formatted(looca.getProcessador().getId());
                rt = st.executeQuery(sqlVerificarId);
                if (rt.next()) {
                    idMaquina = rt.getInt("maquina_id");
                    maquinaCadastrada = true;
                } else {

                    do {
                        System.out.println();
                        utils.centralizaTelaHorizontal(22);
                        System.out.println("Essa maquina ainda não foi cadastrada");
                        utils.centralizaTelaHorizontal(22);
                        System.out.print("Insira o código de cadastro: ");
                        idMaquina = sc.nextInt();

                        if (idMaquina <= 0) {
                            utils.centralizaTelaHorizontal(22);
                            System.out.println("Digite um numero valido");
                        }
                    } while (idMaquina <= 0);

                    String sqlMaquina = """
                            update maquina set sistema_operacional = '%s', arquitetura = '%s', processador_id = '%s', fk_empresa = %d, fk_setor = %d where maquina_id = %d;
                            """
                            .formatted(looca.getSistema().getSistemaOperacional(),
                                    looca.getSistema().getArquitetura(),
                                    looca.getProcessador().getId(),
                                    100,
                                    setorId,
                                    idMaquina);

                    maquinaCadastrada = true;
                    st = conn.createStatement();
                    Integer execute = st.executeUpdate(sqlMaquina);
                    limparConsole();
                    utils.barraLoad(1);
                    limparConsole();
                    System.out.println("""



                                             __________________________________________
                                             |    MÁQUINA CADASTRADA COM SUCESSO!     |
                                             |________________________________________|
                            """);
                    Thread.sleep(2000);
                    if (execute == 0) {
                        utils.centralizaTelaHorizontal(22);
                        System.out.println("Código inválido!");
                    } else {
                        String sqlProcessador = """
                                INSERT INTO componente (modelo, tipo_componente, frequencia, fabricante, fk_maquina)
                                VALUES ('%s', '%s', '%s', '%s', %d);
                                """.formatted(looca.getProcessador().getNome(),
                                "Processador",
                                String.valueOf(looca.getProcessador().getFrequencia()),
                                looca.getProcessador().getFabricante(),
                                idMaquina);

                        st = conn.createStatement();
                        st.executeUpdate(sqlProcessador);

                        String sqlMemoriaRam = """
                                INSERT INTO componente (tamanho_total_gb, tipo_componente, fk_maquina)
                                VALUES (%.2f, '%s', %d)
                                """.formatted(Math.round((double) looca.getMemoria().getTotal() / Math.pow(1024, 3) * 100.0) / 100.0,
                                "Memória Ram",
                                idMaquina);

                        st = conn.createStatement();
                        st.executeUpdate(sqlMemoriaRam);

                        String sqlDisco = """
                                INSERT INTO componente (tipo_componente, modelo, tamanho_total_gb, tamanho_disponivel_gb, fk_maquina)
                                VALUES ('%s', '%s', %.2f, %.2f, %d)
                                """.formatted("Disco",
                                looca.getGrupoDeDiscos().getDiscos().get(0).getModelo(),
                                Math.round((double) looca.getGrupoDeDiscos().getVolumes().get(0).getTotal() / Math.pow(1024, 3) * 100.0) / 100.0,
                                Math.round((double) looca.getGrupoDeDiscos().getVolumes().get(0).getDisponivel() / Math.pow(1024, 3) * 100.0) / 100.0,
                                idMaquina);

                        st = conn.createStatement();
                        st.executeUpdate(sqlDisco);

                        String sqlHistorico = """
                                insert into historico_hardware (cpu_ocupada, ram_ocupada, fk_maquina, data_hora)
                                values(%.2f, %.2f, %d, now());
                                """.formatted(Math.round(looca.getProcessador().getUso() * 100.0) / 100.0,
                                Math.round((double) looca.getMemoria().getEmUso() / Math.pow(1024, 3) * 100.0) / 100.0,
                                idMaquina);

                        st = conn.createStatement();
                        st.executeUpdate(sqlHistorico);


                        Long totalPacoteRecebidos = (long) 0;
                        Long totalPacoteEnviados = (long) 0;
                        Long totalBytesEnviados = (long) 0;
                        Long totalBytesRecebidos = (long) 0;
                        List<String> ipv4 = new ArrayList<>();
                        List<String> interfaceRede = new ArrayList<>();

                        for (int i = 0; i < looca.getRede().getGrupoDeInterfaces().getInterfaces().size(); i++) {
                            if (looca.getRede().getGrupoDeInterfaces().getInterfaces().get(i).getPacotesRecebidos() != 0) {
                                totalPacoteRecebidos += looca.getRede().getGrupoDeInterfaces().getInterfaces().get(i).getPacotesRecebidos();
                                totalPacoteEnviados += looca.getRede().getGrupoDeInterfaces().getInterfaces().get(i).getPacotesEnviados();
                                totalBytesEnviados += looca.getRede().getGrupoDeInterfaces().getInterfaces().get(i).getBytesEnviados();
                                totalBytesRecebidos += looca.getRede().getGrupoDeInterfaces().getInterfaces().get(i).getBytesRecebidos();
                                interfaceRede.add(looca.getRede().getGrupoDeInterfaces().getInterfaces().get(i).getNome());
                                ipv4.add(looca.getRede().getGrupoDeInterfaces().getInterfaces().get(i).getEnderecoIpv4().get(0));
                            }
                        }

                        String sqlRede = """
                                 insert into rede (id_rede, hostname, pacotes_enviados, pacotes_recebidos, fk_maquina) values (null,'%s',%d ,%d ,%d);
                                """.formatted(looca.getRede().getParametros().getHostName(), totalPacoteEnviados, totalPacoteRecebidos, idMaquina);

                        st = conn.createStatement();
                        st.executeUpdate(sqlRede, Statement.RETURN_GENERATED_KEYS);
                        Integer idRede = 0;
                        ResultSet generatedKeys = st.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            idRede = generatedKeys.getInt(1);
                        }

                        for (int i = 0; i < ipv4.size(); i++) {
                            String sqlIpv4 = """
                                    insert into ipv4 (id_ipv4, ipv4, fk_rede) values (null, '%s', %d);
                                           """.formatted(ipv4.get(i), idRede);
                            st = conn.createStatement();
                            st.executeUpdate(sqlIpv4);
                        }
                    }
                }

                Integer idIp = 0;
                Integer idRede = 0;

                String queryRede = """
                            SELECT id_ipv4, id_rede
                            FROM rede
                            JOIN ipv4 ON id_rede = fk_rede
                            WHERE fk_maquina = %d;
                        """.formatted(idMaquina);

                st = conn.createStatement();
                rt = st.executeQuery(queryRede);

                List<Integer> contador = new ArrayList<>();

                for (int i = 0; i < looca.getRede().getGrupoDeInterfaces().getInterfaces().size(); i++) {
                    if (looca.getRede().getGrupoDeInterfaces().getInterfaces().get(i).getPacotesRecebidos() != 0) {
                        contador.add(i);
                    }
                }

                Integer c = 0;
                try (Statement stUpdate = conn.createStatement()) {
                    while (rt.next()) {
                        idIp = rt.getInt("id_ipv4");
                        idRede = rt.getInt("id_rede");
                        String ipv4Value = looca.getRede().getGrupoDeInterfaces().getInterfaces().get(contador.get(c)).getEnderecoIpv4().get(0);
                        // Crie a consulta de atualização
                        String queryIpUpdate = """
                                    UPDATE ipv4
                                    SET ipv4 = '%s'
                                    WHERE id_ipv4 = %d;
                                """.formatted(ipv4Value, idIp);
                        stUpdate.executeUpdate(queryIpUpdate);
                        c++;
                    }
                }

                Looca janelaGroup = new Looca();
                FucionalidadeConsole func = new FucionalidadeConsole();


                while (maquinaCadastrada) {


                    for (Janela janela : janelaGroup.getGrupoDeJanelas().getJanelas()) {
                        for (int i = 0; i < processosBloqueados.size(); i++) {
                            if (janela.getTitulo().contains(processosBloqueados.get(i))) {
                                func.encerraProcesso(Math.toIntExact(janela.getPid()));
                                utils.centralizaTelaVertical(1);
                                utils.centralizaTelaHorizontal(8);
                                System.out.println("Processo " + janela.getTitulo() + " foi encerrado por violar as políticas de segurança da empresa!");
                                Thread.sleep(3000);
                            }
                        }
                    }

                    String sqlHistorico = """
                            insert into historico_hardware (cpu_ocupada, ram_ocupada, fk_maquina, data_hora)
                            values(%.2f, %.2f, %d, now());
                            """.formatted(Math.round(looca.getProcessador().getUso() * 100.0) / 100.0,
                            Math.round((double) looca.getMemoria().getEmUso() / Math.pow(1024, 3) * 100.0) / 100.0,
                            idMaquina);


                    st = conn.createStatement();
                    st.executeUpdate(sqlHistorico);
                    Thread.sleep(1000);
                    String processos = "";

                    limparConsole();
                    utils.mensagemInformativa();

                    for (int i = 0; i < processosBloqueados.size(); i++) {
                        if (i == processosBloqueados.size() - 1) {
                            processos += processosBloqueados.get(i);
                        } else {
                            processos += processosBloqueados.get(i) + ", ";
                        }

                    }
                    utils.centralizaTelaHorizontal(8);
                    System.out.println("Processos bloqueados: " + processos);

                    Long totalPacoteRecebidos = (long) 0;
                    Long totalPacoteEnviados = (long) 0;
                    Long totalBytesEnviados = (long) 0;
                    Long totalBytesRecebidos = (long) 0;
                    List<String> ipv4 = new ArrayList<>();
                    List<String> interfaceRede = new ArrayList<>();


                    utils.centralizaTelaVertical(2);
                    System.out.println("----------------------------------");
                    System.out.println();
                    System.out.println("PARÂMETROS DA MÁQUINA:");
                    System.out.println(looca.getRede().getParametros());
                    System.out.println("----------------------------------");
                    System.out.println();

                    for (int i = 0; i < looca.getRede().getGrupoDeInterfaces().getInterfaces().size(); i++) {
                        if (looca.getRede().getGrupoDeInterfaces().getInterfaces().get(i).getPacotesRecebidos() != 0) {
                            totalPacoteRecebidos += looca.getRede().getGrupoDeInterfaces().getInterfaces().get(i).getPacotesRecebidos();
                            totalPacoteEnviados += looca.getRede().getGrupoDeInterfaces().getInterfaces().get(i).getPacotesEnviados();
                            totalBytesEnviados += looca.getRede().getGrupoDeInterfaces().getInterfaces().get(i).getBytesEnviados();
                            totalBytesRecebidos += looca.getRede().getGrupoDeInterfaces().getInterfaces().get(i).getBytesRecebidos();
                            interfaceRede.add(looca.getRede().getGrupoDeInterfaces().getInterfaces().get(i).getNome());
                            ipv4.add(looca.getRede().getGrupoDeInterfaces().getInterfaces().get(i).getEnderecoIpv4().get(0));
                        }
                    }

                    String sqlPacortes = """
                            update rede set pacotes_enviados = %d, pacotes_recebidos = %d where fk_maquina = %d;
                                   """.formatted(totalPacoteEnviados, totalPacoteRecebidos, idMaquina);
                    st = conn.createStatement();
                    st.executeUpdate(sqlPacortes);

                    System.out.println("INTERFACES DE REDE:");
                    for (int i = 0; i < interfaceRede.size(); i++) {

                        System.out.println((i + 1) + "° - " + interfaceRede.get(i));
                    }
                    System.out.println();
                    System.out.println("IPV4 DA MAQUINA:");
                    for (int i = 0; i < interfaceRede.size(); i++) {
                        System.out.println((i + 1) + "° - " + ipv4.get(i));
                    }
                    System.out.println();

                    System.out.println("----------------------------------");
                    System.out.println();
                    System.out.println("TRAFEGO DE DADOS NA REDE:");
                    System.out.print("Pacotes enviados: ");
                    System.out.println(totalPacoteEnviados);
                    System.out.print("Pacotes recebidos: ");
                    System.out.println(totalPacoteRecebidos);
                    System.out.print("Byts enviados: ");
                    System.out.println(totalBytesEnviados);
                    System.out.print("Byts Recebidos: ");
                    System.out.println(totalBytesRecebidos);
                    System.out.println();
                    System.out.println("----------------------------------");
                    Thread.sleep(1000);
                }
            } else {
                System.out.println("Usuario inválido");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}