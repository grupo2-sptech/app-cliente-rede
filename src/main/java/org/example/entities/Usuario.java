package org.example.entities;

import org.example.dao.Implementation.DaoUsuarioImple;
import org.example.utilities.Utilitarios;

import java.io.Console;
import java.sql.SQLException;
import java.util.Scanner;

public class Usuario {
    private Integer id;
    private String nome;
    private String email;
    private String senha;
    private String login;
    private Integer idEmpresa;

    Console console = System.console();

    public Usuario() {
    }

    public Usuario(String nome, String email, String senha, String login, Integer idEmpresa) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.login = login;
        this.idEmpresa = idEmpresa;
    }

    public Usuario(String email, String senha, String login) {
        this.email = email;
        this.senha = senha;
        this.login = login;
    }

    public Usuario validarUsuario() throws SQLException {
        Usuario usuario = new Usuario();
        Scanner sc = new Scanner(System.in);
        Utilitarios utilitarios = new Utilitarios();
        utilitarios.centralizaTelaVertical(2);
        utilitarios.exibirMensagem();
        utilitarios.centralizaTelaHorizontal(22);
        System.out.print("Login: ");
        String login = sc.nextLine();
        utilitarios.centralizaTelaHorizontal(22);
        System.out.print("Senha: ");
        String senha;
        if (console != null) {
            char[] passwordArray = console.readPassword();
            senha = new String(passwordArray);
        } else {
            senha = sc.next();
        }

        DaoUsuarioImple daoUsuario = new DaoUsuarioImple();
        if (!login.equals(null) && !login.equals("") && !senha.equals(null) && !senha.equals("")) {
            usuario = daoUsuario.validarUsuarioSql(login, senha);
            if (usuario.getNome() == null) {
                return null;
            }
        }
        return usuario;
    }

    public Integer getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Integer idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", senha='" + senha + '\'' +
                ", login='" + login + '\'' +
                '}';
    }
}
