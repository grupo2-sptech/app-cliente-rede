package org.example.utilities.console;


import org.example.utilities.Utilitarios;

import java.io.*;

public class FucionalidadeConsole {

    static void matarProcessos() {
        Utilitarios utilitarios = new Utilitarios();
        try {
            while (true) {
                String os = System.getProperty("os.name");
                if (os.contains("Windows")) {
                    // Se for Windows, mantenha o código existente
                    limparConsole();
                    System.out.println("""
                                  
                                               Monitoramento ativo!
                                               
                            Este computador é monitorado em tempo real, incluindo o hardware, para
                            assegurar conformidade com as políticas da empresa.
                            Todas as atividades serão verificadas e, se necessário, medidas serão
                            tomadas automaticamente pelo sistema.
                                                           
                            """);
                    if (isProcessRunning("whatsApp.exe")) {
                        Thread.sleep(3000);
                        limparConsole();
                        utilitarios.centralizaTelaVertical(5);
                        utilitarios.centralizaTelaHorizontal(15);
                        System.out.println("Programa indevido localizado");
                        utilitarios.centralizaTelaHorizontal(15);
                        System.out.println("Encerrando programa indevido!");
                        Thread.sleep(3000);
                        limparConsole();
                        utilitarios.barraLoad(1);
                        new ProcessBuilder("cmd", "/c", "taskkill", "/f", "/im", "whatsApp.exe").inheritIO().start().waitFor();
                        Thread.sleep(3000);
                        limparConsole();
                        utilitarios.centralizaTelaVertical(2);
                        utilitarios.centralizaTelaHorizontal(7);
                        System.out.println("Programa indevido foi encerrado com sucesso!");
                    }
                } else {
                    // Se for Linux, execute o comando para matar o processo
                    try {
                        Process process = Runtime.getRuntime().exec("ps -e");
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.contains("whatsApp")) {
                                String[] parts = line.trim().split("\\s+");
                                String pid = parts[0];
                                Process killProcess = Runtime.getRuntime().exec("kill -9 " + pid);
                                killProcess.waitFor();
                            }
                        }
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Thread.sleep(5000);
            }
        } catch (final Exception exception) {
            System.out.println("Erro ao Limpar o console!");
        }
    }

    public static void limparConsole() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033\143");
            }
        } catch (final Exception exception) {
            System.out.println("Erro ao Limpar o console!");
        }
    }

    public static boolean isProcessRunning(String processName) throws IOException {
        Process process = new ProcessBuilder("tasklist", "/fi", "imagename eq " + processName).start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(processName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void encerraProcesso(Integer pid) {
        try {
            Runtime.getRuntime().exec("taskkill /F /PID " + pid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

