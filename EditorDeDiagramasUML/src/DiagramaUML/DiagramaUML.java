package DiagramaUML;

import ComponentesUML.ComponenteUML;
import InterfaceGrafica.AreaDeDiagramas;
import RelacoesUML.RelacaoUML;

import java.util.ArrayList;

public class DiagramaUML {

    private final AreaDeDiagramas areaDeDiagramas;

    private final ArrayList<ComponenteUML> listaComponentesUML = new ArrayList<>();

    private final ArrayList<RelacaoUML> listaRelacaoes = new ArrayList<>(); // colocar relacoes !!!!!!!!!!!!!!!

    private boolean diagramaSalvo = true;


    public DiagramaUML(AreaDeDiagramas areaDeDiagramas) {
        this.areaDeDiagramas = areaDeDiagramas;
    }

    public void addComponente(ComponenteUML novoComponente) {
        listaComponentesUML.add(novoComponente);
        areaDeDiagramas.addComponenteAoQuadro(novoComponente, true);
    }

    public void addRelacao(RelacaoUML novaRelacao) {
        listaRelacaoes.add(novaRelacao);
        areaDeDiagramas.addRelacionametoAoQuadro(novaRelacao.getListaPaineisRelacao());
    }

    public void removerComponente(ComponenteUML componente) {
        listaComponentesUML.remove(componente);
        areaDeDiagramas.removerComponenteDoQuadro(componente);
    }

    public void removerRelacao(RelacaoUML relacaoUML) {
        listaRelacaoes.remove(relacaoUML);
        areaDeDiagramas.removerRelacaoDoQuadro(relacaoUML);
    }

    public void setDiagramaSalvo(boolean diagramaSalvo) {
        this.diagramaSalvo = diagramaSalvo;
    }


    public AreaDeDiagramas getAreaDeDiagramas() {
        return areaDeDiagramas;
    }

    public ArrayList<ComponenteUML> getListaComponentesUML() {
        return listaComponentesUML;
    }


    public ArrayList<RelacaoUML> getListaRelacaoes() {
        return listaRelacaoes;
    }

    public boolean isDiagramaSalvo() {
        return diagramaSalvo;
    }
}

