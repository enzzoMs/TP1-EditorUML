package componentes.modelos.estruturas;

import auxiliares.GerenciadorDeRecursos;

public enum Visibilidade {
    PUBLICO("+", GerenciadorDeRecursos.getInstancia().getString("publico")),
    PRIVADO("-", GerenciadorDeRecursos.getInstancia().getString("privado")),
    PROTEGIDO("#", GerenciadorDeRecursos.getInstancia().getString("protegido"));

    private final String simbolo;
    private final String nome;

    Visibilidade(String simbolo, String nome) {
        this.simbolo = simbolo;
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public static Visibilidade getVisibilidadePorNome(String nome) {
        for (Visibilidade visibilidade : Visibilidade.values()) {
            if (visibilidade.nome.equalsIgnoreCase(nome)) {
                return visibilidade;
            }
        }

        return null;
    }
}