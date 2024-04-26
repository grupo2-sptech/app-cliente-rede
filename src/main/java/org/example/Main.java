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

        System.out.println(looca.getRede().getParametros());
//        while (true) {
//            System.out.print("Pacotes enviados: ");
//            System.out.println(looca.getRede().getGrupoDeInterfaces().getInterfaces().get(1).getPacotesEnviados());
//            System.out.print("Pacotes recebidos: ");
//            System.out.println(looca.getRede().getGrupoDeInterfaces().getInterfaces().get(1).getPacotesRecebidos());
//            System.out.println("----------------------------------");
//            Thread.sleep(1000);
//        }

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
            char[] passwordArray = console.readPassword();
            String senha = new String(passwordArray);

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
                }

            } else {
                System.out.println("Usuario inválido");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}