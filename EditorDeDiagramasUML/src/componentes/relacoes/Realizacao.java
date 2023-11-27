package componentes.relacoes;

import auxiliares.GerenciadorDeRecursos;
import interfacegrafica.AreaDeDiagramas;
import modelos.TipoDeRelacao;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.util.ArrayList;

/**
 * Classe que representa uma relação UML do tipo "Realização". Graficamente se trata de um
 * componente simples, possuindo uma seta "oca" em uma das extremidades e linhas tracejadas.
 */
public class Realizacao extends RelacaoSimples {

    public Realizacao(
        ArrayList<JPanel> linhasDaRelacao, AreaDeDiagramas areaDeDiagramas,
        Point primeiroPontoDaRelacao, Point ultimoPontoDaRelacao, TipoDeRelacao tipoDeRelacao
    ) {
        super(linhasDaRelacao, areaDeDiagramas, primeiroPontoDaRelacao, ultimoPontoDaRelacao, tipoDeRelacao);
    }

    @Override
    public void aplicarEstiloDasLinhas(ArrayList<JPanel> linhas) {
        float TAMANHO_TRACOS = 3f;
        float ESPACO_ENTRE_TRACOS = 4f;

        for (JPanel linha : linhas) {
            linha.setOpaque(false);

            linha.setBorder(new CompoundBorder(
                // Se a largura for TAMANHO_LINHAS_RELACAO entao a linha eh vertical
                linha.getWidth() == TAMANHO_LINHAS_RELACAO ?
                    // Garantindo que so um dos lados da linha tenha borda
                    BorderFactory.createEmptyBorder(-TAMANHO_LINHAS_RELACAO, 0, -TAMANHO_LINHAS_RELACAO, -TAMANHO_LINHAS_RELACAO) :
                    BorderFactory.createEmptyBorder(0, -TAMANHO_LINHAS_RELACAO, -TAMANHO_LINHAS_RELACAO, -TAMANHO_LINHAS_RELACAO),

                BorderFactory.createDashedBorder(
                    linha.getBackground(), TAMANHO_LINHAS_RELACAO,
                    TAMANHO_TRACOS, ESPACO_ENTRE_TRACOS, false
                )
            ));
        }
    }

    @Override
    public void desenharSeta(Graphics2D g2, int[] pontosXDaSeta, int[] pontosYDaSeta) {
        g2.setStroke(new BasicStroke(8));
        g2.setColor(relacaoEstaSelecionada() ? COR_SELECIONAR : COR_PADRAO);
        g2.drawPolygon(pontosXDaSeta, pontosYDaSeta, 3);

        g2.setColor(GerenciadorDeRecursos.getInstancia().getColor("white"));
        g2.fillPolygon(pontosXDaSeta, pontosYDaSeta, 3);
    }
}
