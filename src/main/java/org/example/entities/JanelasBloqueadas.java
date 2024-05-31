package org.example.entities;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.janelas.Janela;
import org.example.dao.DaoJanelasBloqueadas;
import org.example.dao.Implementation.DaoJanelasBloqueadasImple;
import org.example.utilities.Utilitarios;
import org.example.utilities.console.FucionalidadeConsole;

import java.util.ArrayList;
import java.util.List;

public class JanelasBloqueadas {

    private List<String> listaJanelasBloqueadas;
    private Integer categoria;
    private String nome;

    public JanelasBloqueadas(Integer categoria, String nome) {
        this.categoria = categoria;
        this.nome = nome;
    }


    Looca janelaGroup = new Looca();
    FucionalidadeConsole func = new FucionalidadeConsole();
    Utilitarios utilitarios = new Utilitarios();

    public JanelasBloqueadas() {
        listaJanelasBloqueadas = new ArrayList<>();
    }

    public void monitorarJanelas(List<JanelasBloqueadas> listaJanelasBloqueadas, Maquina maquina) throws InterruptedException {
        DaoJanelasBloqueadas daoJanelasBloqueadas = new DaoJanelasBloqueadasImple();
        for (Janela janela : janelaGroup.getGrupoDeJanelas().getJanelas()) {
            for (int i = 0; i < listaJanelasBloqueadas.size(); i++) {
                if (janela.getTitulo().contains(listaJanelasBloqueadas.get(i).getNome())) {
                    func.encerraProcesso(Math.toIntExact(janela.getPid()));
                    utilitarios.centralizaTelaVertical(1);
                    utilitarios.centralizaTelaHorizontal(8);
                    monitoraBloqueio(listaJanelasBloqueadas.get(i).getCategoria(), maquina);
                    daoJanelasBloqueadas.alertaBloqueio(maquina, listaJanelasBloqueadas.get(i).getNome());
                    System.out.println("Processo " + janela.getTitulo() + " foi encerrado por violar as políticas de segurança da empresa!");
                    Thread.sleep(3000);
                }
            }
        }
    }

    public void monitoraBloqueio(Integer categoria, Maquina maquina) {
        DaoJanelasBloqueadas daoJanelasBloqueadas = new DaoJanelasBloqueadasImple();
        daoJanelasBloqueadas.inserirDadosBloqueio(maquina, categoria);
    }

    public void addBloqueioNaLista(String nome) {
        listaJanelasBloqueadas.add(nome);
    }

    public List<String> exibirLista() {
        return listaJanelasBloqueadas;
    }

    public void setNome(List<String> nome) {
        this.listaJanelasBloqueadas = nome;
    }

    public Integer getCategoria() {
        return categoria;
    }

    public void setCategoria(Integer categoria) {
        this.categoria = categoria;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "JanelaBloqueada{" + "nome=" + listaJanelasBloqueadas + '}';
    }

}
