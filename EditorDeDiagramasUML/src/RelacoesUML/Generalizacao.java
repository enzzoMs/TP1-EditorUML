package RelacoesUML;

import auxiliares.GerenciadorDeRecursos;
import componentes.relacoes.OrientacaoDeRelacao;
import componentes.relacoes.RelacaoUML;
import componentes.relacoes.TipoDeRelacao;
import interfacegrafica.AreaDeDiagramas;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * TODO
 */
public class Generalizacao extends RelacaoUML {
    private String sentidoDaSeta;
    private JPanel painelSeta;
    int[] pontosXDaSeta = { 0, 0, 0 };
    int[] pontosYDaSeta = { 0, 0, 0 };

    public Generalizacao(
        ArrayList<JPanel> linhasDaRelacao, AreaDeDiagramas areaDeDiagramas,
        Point primeiroPontoDaRelacao, Point ultimoPontoDaRelacao, TipoDeRelacao tipoDeRelacao
    ) {
        super(linhasDaRelacao, areaDeDiagramas, primeiroPontoDaRelacao, ultimoPontoDaRelacao, tipoDeRelacao);
        aplicarEstiloDaRelacao();
        updateLocalizacaoPontoDeExtensao();
    }

    @Override
    public void aplicarEstiloDaRelacao() {
        if (painelSeta == null) {
            painelSeta = new JPanel() {
                public void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;

                    g2.setStroke(new BasicStroke(8));
                    g2.setColor(relacaoEstaSelecionada() ? COR_SELECIONAR : COR_PADRAO);
                    g2.drawPolygon(pontosXDaSeta, pontosYDaSeta, 3);

                    g2.setColor(GerenciadorDeRecursos.getInstancia().getColor("white"));
                    g2.fillPolygon(pontosXDaSeta, pontosYDaSeta, 3);
                }
            };

            painelSeta.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (relacaoEstaHabilitada()) {
                        selecionarRelacao(true);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (relacaoEstaHabilitada()) {
                        selecionarRelacao(false);
                    }
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (relacaoEstaHabilitada()) {
                        mostrarFrameGerenciarRelacao(true);
                    }
                }
            });

            // index 1 para que a seta fique embaixo do ponto de extensao
            getAreaDeDiagramas().addComponenteAoQuadro(painelSeta, 1);
        }

        JPanel primeiroLinha = getLinhasDaRelacao().get(getLinhasDaRelacao().size() - 1);

        Point localizacaoPainelSeta = new Point();

        switch (getOrientacaoDaRelacao()) {
            case SUL -> {
                pontosXDaSeta[0] = 0; pontosXDaSeta[1] = TAMANHO_SETA/2; pontosXDaSeta[2] = TAMANHO_SETA;
                pontosYDaSeta[0] = 0; pontosYDaSeta[1] = TAMANHO_SETA; pontosYDaSeta[2] = 0;
                localizacaoPainelSeta.x = primeiroLinha.getX() - ((TAMANHO_SETA - TAMANHO_LINHAS_RELACAO)/2);
                localizacaoPainelSeta.y = primeiroLinha.getY() + primeiroLinha.getHeight();
            }
            case NORTE -> {
                pontosXDaSeta[0] = 0; pontosXDaSeta[1] = TAMANHO_SETA/2; pontosXDaSeta[2] = TAMANHO_SETA;
                pontosYDaSeta[0] = TAMANHO_SETA; pontosYDaSeta[1] = 0; pontosYDaSeta[2] = TAMANHO_SETA;
                localizacaoPainelSeta.x = primeiroLinha.getX() - ((TAMANHO_SETA - TAMANHO_LINHAS_RELACAO)/2);
                localizacaoPainelSeta.y = primeiroLinha.getY() - TAMANHO_SETA;
            }
            case OESTE -> {
                pontosXDaSeta[0] = TAMANHO_SETA; pontosXDaSeta[1] = 0; pontosXDaSeta[2] = TAMANHO_SETA;
                pontosYDaSeta[0] = TAMANHO_SETA; pontosYDaSeta[1] = TAMANHO_SETA/2; pontosYDaSeta[2] = 0;
                localizacaoPainelSeta.x = primeiroLinha.getX() - TAMANHO_SETA;
                localizacaoPainelSeta.y = primeiroLinha.getY() - ((TAMANHO_SETA - TAMANHO_LINHAS_RELACAO)/2);
            }
            case LESTE -> {
                pontosXDaSeta[0] = 0; pontosXDaSeta[1] = TAMANHO_SETA; pontosXDaSeta[2] = 0;
                pontosYDaSeta[0] = TAMANHO_SETA; pontosYDaSeta[1] = TAMANHO_SETA/2; pontosYDaSeta[2] = 0;
                localizacaoPainelSeta.x = primeiroLinha.getX() + primeiroLinha.getWidth();
                localizacaoPainelSeta.y = primeiroLinha.getY() - ((TAMANHO_SETA - TAMANHO_LINHAS_RELACAO)/2);
            }
        }

        int MARGEM = 10;
        for (int i = 0; i < pontosYDaSeta.length; i++) {
            pontosXDaSeta[i] += MARGEM;
            pontosYDaSeta[i] += MARGEM;
        }

        painelSeta.setBounds(
            localizacaoPainelSeta.x - MARGEM, localizacaoPainelSeta.y - MARGEM,
            TAMANHO_SETA + MARGEM * 2, TAMANHO_SETA + MARGEM * 2
        );

        setEmMudancaDeSelecao(selecionado -> {
            painelSeta.setCursor(Cursor.getPredefinedCursor(selecionado ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            painelSeta.repaint();
            getAreaDeDiagramas().revalidarQuadroBranco();
        });
    }

    @Override
    public Point getLocalizacaoDoPontoDeExtensao() {
        Point localizacaoDaSeta = painelSeta.getLocation();
        Point localizacaoPontoDeExtensao = new Point();

        switch (getOrientacaoDaRelacao()) {
            case SUL -> {
                localizacaoPontoDeExtensao.x = localizacaoDaSeta.x + ((painelSeta.getWidth() - TAMANHO_PONTO_DE_EXTENSAO)/2);
                localizacaoPontoDeExtensao.y = localizacaoDaSeta.y + painelSeta.getHeight() - TAMANHO_PONTO_DE_EXTENSAO/2;
            }
            case NORTE -> {
                localizacaoPontoDeExtensao.x = localizacaoDaSeta.x + ((painelSeta.getWidth() - TAMANHO_PONTO_DE_EXTENSAO)/2);
                localizacaoPontoDeExtensao.y = localizacaoDaSeta.y - TAMANHO_PONTO_DE_EXTENSAO/2;
            }
            case OESTE -> {
                localizacaoPontoDeExtensao.x = localizacaoDaSeta.x - TAMANHO_PONTO_DE_EXTENSAO/2;
                localizacaoPontoDeExtensao.y = localizacaoDaSeta.y + ((painelSeta.getHeight() - TAMANHO_PONTO_DE_EXTENSAO)/2);
            }
            case LESTE -> {
                localizacaoPontoDeExtensao.x = localizacaoDaSeta.x + painelSeta.getWidth() - TAMANHO_PONTO_DE_EXTENSAO/2;
                localizacaoPontoDeExtensao.y = localizacaoDaSeta.y + ((painelSeta.getHeight() - TAMANHO_PONTO_DE_EXTENSAO)/2);
            }
        }

        return localizacaoPontoDeExtensao;
    }

    @Override
    public void aoClicarPontoDeExtensao() {
        JPanel primeiraLinha = getLinhasDaRelacao().get(getLinhasDaRelacao().size() - 1);
        int TAMANHO_EXTRA = TAMANHO_SETA + TAMANHO_PONTO_DE_EXTENSAO/2;

        switch (getOrientacaoDaRelacao()) {
            case SUL -> primeiraLinha.setSize(primeiraLinha.getWidth(), primeiraLinha.getHeight() + TAMANHO_EXTRA);
            case NORTE -> primeiraLinha.setBounds(
                primeiraLinha.getX(), primeiraLinha.getY() - TAMANHO_EXTRA,
                primeiraLinha.getWidth(), primeiraLinha.getHeight() + TAMANHO_EXTRA
            );
            case OESTE -> primeiraLinha.setBounds(
                primeiraLinha.getX() - TAMANHO_EXTRA, primeiraLinha.getY(),
                primeiraLinha.getWidth() + TAMANHO_EXTRA, primeiraLinha.getHeight()
            );
            case LESTE -> primeiraLinha.setSize(primeiraLinha.getWidth() + TAMANHO_EXTRA, primeiraLinha.getHeight());
        }

        painelSeta.setVisible(false);

        super.aoClicarPontoDeExtensao();
    }

    @Override
    public void estenderRelacao(ArrayList<JPanel> novosPaineis, Point ultimoPonto) {
        super.estenderRelacao(novosPaineis, ultimoPonto);
        painelSeta.setVisible(true);
    }

    @Override
    protected void initFrameGerenciarRelacao() {
        GerenciadorDeRecursos gerenciadorDeRecursos = GerenciadorDeRecursos.getInstancia();

        JLabel labelGeneralizacao = new JLabel(gerenciadorDeRecursos.getString("relacao_generalizacao_maiuscula"), JLabel.CENTER);
        labelGeneralizacao.setFont(gerenciadorDeRecursos.getRobotoBlack(16));
        labelGeneralizacao.setPreferredSize(new Dimension(
            labelGeneralizacao.getPreferredSize().width * 2,
            labelGeneralizacao.getPreferredSize().height
        ));

        JPanel painelLabelGeneralizacao = new JPanel(new MigLayout("insets 20 40 20 40","[grow]"));
        painelLabelGeneralizacao.setBorder(BorderFactory.createMatteBorder(
            0, 0, 3, 0, gerenciadorDeRecursos.getColor("black")
        ));
        painelLabelGeneralizacao.add(labelGeneralizacao, "align center");
        painelLabelGeneralizacao.setOpaque(false);

        // ----------------------------------------------------------------------------

        JPanel painelTrocarSentido = new JPanel(new MigLayout("fill, insets 5 15 5 15"));
        painelTrocarSentido.setBackground(gerenciadorDeRecursos.getColor("white"));
        painelTrocarSentido.setBorder(BorderFactory.createMatteBorder(
            1, 1, 1, 1, gerenciadorDeRecursos.getColor("raisin_black")
        ));

        JLabel labelTrocarSentido = new JLabel(gerenciadorDeRecursos.getString("relacao_trocar_sentido"));
        labelTrocarSentido.setFont(gerenciadorDeRecursos.getRobotoMedium(14));
        labelTrocarSentido.setForeground(gerenciadorDeRecursos.getColor("black"));

        // ----------------------------------------------------------------------------

        JPanel painelExcluirRelacao = new JPanel(new MigLayout("fill, insets 5 15 5 15"));
        painelExcluirRelacao.setBackground(gerenciadorDeRecursos.getColor("white"));
        painelExcluirRelacao.setBorder(BorderFactory.createMatteBorder(
            1, 1, 1, 1, gerenciadorDeRecursos.getColor("raisin_black")
        ));

        JLabel labelExcluirRelacao = new JLabel("Excluir Relação");
        labelExcluirRelacao.setFont(gerenciadorDeRecursos.getRobotoMedium(14));
        labelExcluirRelacao.setForeground(gerenciadorDeRecursos.getColor("black"));

        painelExcluirRelacao.add(labelExcluirRelacao, "align center");
        painelTrocarSentido.add(labelTrocarSentido, "align center");

        // ----------------------------------------------------------------------------

        MouseAdapter adapterCorDosBotoes = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                ((JPanel) e.getSource()).setBackground(gerenciadorDeRecursos.getColor("raisin_black"));
                ((JPanel) e.getSource()).getComponent(0).setForeground(gerenciadorDeRecursos.getColor("white"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                ((JPanel) e.getSource()).setBackground(gerenciadorDeRecursos.getColor("white"));
                ((JPanel) e.getSource()).getComponent(0).setForeground(gerenciadorDeRecursos.getColor("black"));
            }
        };

        painelTrocarSentido.addMouseListener(adapterCorDosBotoes);
        painelExcluirRelacao.addMouseListener(adapterCorDosBotoes);

        // ----------------------------------------------------------------------------

        painelTrocarSentido.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                inverterSentidoDaRelacao();
            }
        });

        painelExcluirRelacao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mostrarFrameGerenciarRelacao(false);
                getAreaDeDiagramas().removerComponenteDoQuadro(painelSeta);
                excluirRelacao();
            }
        });

        // ----------------------------------------------------------------------------

        JPanel painelInteriorGerenciarRelacao = new JPanel(new MigLayout("insets 20 10 15 10", "[grow, fill]"));
        painelInteriorGerenciarRelacao.setBackground(gerenciadorDeRecursos.getColor("white"));

        painelInteriorGerenciarRelacao.add(painelTrocarSentido, "wrap, gapbottom 8");
        painelInteriorGerenciarRelacao.add(painelExcluirRelacao, "wrap, grow");

        JPanel painelGerenciarRelacao = new JPanel(new MigLayout("insets 20 0 10 0", "","[grow, fill]"));
        painelGerenciarRelacao.setBackground(gerenciadorDeRecursos.getColor("white"));

        painelGerenciarRelacao.add(painelLabelGeneralizacao, "grow, wrap, gapleft 20, gapright 20");
        painelGerenciarRelacao.add(painelInteriorGerenciarRelacao, "grow, wrap, gapleft 20, gapright 20");

        getFrameGerenciarRelacao().add(painelGerenciarRelacao);
        getFrameGerenciarRelacao().pack();
    }

    /*@Override
    public String toString() {
        StringBuilder toStringGeneralizacao = new StringBuilder();

        toStringGeneralizacao.append(sentidoDaSeta);
        toStringGeneralizacao.append("\n");

        toStringGeneralizacao.append(getListaPaineisRelacao().size() - 1);
        toStringGeneralizacao.append("\n");

        for (JComponent componenteDeRelacao : getListaPaineisRelacao()) {
            if (componenteDeRelacao instanceof JPanel) {
                toStringGeneralizacao.append(componenteDeRelacao.getX());
                toStringGeneralizacao.append("\n");
                toStringGeneralizacao.append(componenteDeRelacao.getY());
                toStringGeneralizacao.append("\n");
                toStringGeneralizacao.append(componenteDeRelacao.getWidth());
                toStringGeneralizacao.append("\n");
                toStringGeneralizacao.append(componenteDeRelacao.getHeight());
                toStringGeneralizacao.append("\n");
            }
        }

        return toStringGeneralizacao.toString();
    }

    public void setSentidoDaSeta(String sentidoDaSeta) {
        this.sentidoDaSeta = sentidoDaSeta;
    }*/
}