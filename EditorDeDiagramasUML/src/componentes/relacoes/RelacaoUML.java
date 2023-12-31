package componentes.relacoes;

import auxiliares.GerenciadorDeRecursos;
import componentes.alteracoes.relacoes.RelacaoModificada;
import componentes.modelos.relacoes.DirecaoDeRelacao;
import componentes.modelos.relacoes.OrientacaoDeSeta;
import componentes.modelos.relacoes.Relacao;
import componentes.modelos.relacoes.TipoDeRelacao;
import interfacegrafica.AreaDeDiagramas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Classe base para relações UML. Fornece métodos para alterar certos componentes gráficos como nome, multiplicidade,
 * e setas, além de permitir ao usuário expandir as linhas em qualquer direção.
 * @see Agregacao
 * @see Associacao
 * @see Composicao
 * @see Dependencia
 * @see Generalizacao
 * @see Realizacao
 */
public abstract class RelacaoUML {
    private Relacao modeloAtual = new Relacao();
    private final AreaDeDiagramas areaDeDiagramas;
    private final JLabel labelNome = new JLabel();
    private final JLabel labelMultiplicidadeA = new JLabel();
    private final JLabel labelMultiplicidadeB = new JLabel();
    private final JPanel painelSetaA;
    private final JPanel painelSetaB;
    private final Point[] pontosDaSetaA = { new Point(), new Point(), new Point() };
    private final Point[] pontosDaSetaB = { new Point(), new Point(), new Point() };
    private final JPanel pontoDeExtensao;
    private Consumer<RelacaoUML> aoClicarPontoDeExtensao;
    private final TipoDeRelacao tipoDeRelacao;
    private final JFrame frameGerenciarRelacao = new JFrame();
    private boolean relacaoSelecionada;
    public static final int TAMANHO_LINHAS_RELACAO = 4;
    public static final int TAMANHO_SETA = 14;
    public static final int TAMANHO_PONTO_DE_EXTENSAO = 14;
    public static final Color COR_PADRAO = GerenciadorDeRecursos.getInstancia().getColor("dark_charcoal");
    public static final Color COR_SELECIONAR = GerenciadorDeRecursos.getInstancia().getColor("red");
    private static final Color COR_PONTO_DE_EXTENSAO = GerenciadorDeRecursos.getInstancia().getColor("green");

    public RelacaoUML(
        ArrayList<JPanel> linhasDaRelacao, AreaDeDiagramas areaDeDiagramas,
        Point pontoInicialDaRelacao, Point pontoFinalDaRelacao, TipoDeRelacao tipoDeRelacao
    ) {
        modeloAtual.setLinhasDaRelacao(linhasDaRelacao);
        modeloAtual.setPontoLadoA(pontoFinalDaRelacao);
        this.areaDeDiagramas = areaDeDiagramas;
        this.tipoDeRelacao = tipoDeRelacao;

        MouseAdapter adapterGerenciarRelacao = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (areaDeDiagramas.componentesEstaoHabilitados()) {
                    selecionarRelacao(true);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (areaDeDiagramas.componentesEstaoHabilitados()) {
                    selecionarRelacao(false);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (areaDeDiagramas.componentesEstaoHabilitados()) {
                    mostrarFrameGerenciarRelacao(true);
                }
            }
        };

        for (JPanel linha : modeloAtual.getLinhasDaRelacao()) {
            linha.addMouseListener(adapterGerenciarRelacao);
        }

        painelSetaA = new JPanel() {
            public void paintComponent(Graphics g) {
                desenharSeta((Graphics2D) g, pontosDaSetaA);
            }
        };

        painelSetaB = new JPanel() {
            public void paintComponent(Graphics g) {
                desenharSeta((Graphics2D) g, pontosDaSetaB);
            }
        };

        painelSetaA.addMouseListener(adapterGerenciarRelacao);
        painelSetaB.addMouseListener(adapterGerenciarRelacao);

        if (pontoFinalDaRelacao != null) {
            modeloAtual.setOrientacaoLadoA(calcularOrientacaoDeLinha(
                pontoFinalDaRelacao, modeloAtual.getLinhasDaRelacao().get(modeloAtual.getLinhasDaRelacao().size() - 1))
            );
        }

        if (pontoInicialDaRelacao != null) {
            modeloAtual.setOrientacaoLadoB(calcularOrientacaoDeLinha(pontoInicialDaRelacao, modeloAtual.getLinhasDaRelacao().get(0)));
        }

        pontoDeExtensao = new JPanel() {
            public void paintComponent(Graphics g) {
                g.setColor(COR_PONTO_DE_EXTENSAO);
                g.fillOval(0, 0, TAMANHO_PONTO_DE_EXTENSAO, TAMANHO_PONTO_DE_EXTENSAO);
            }
        };
        pontoDeExtensao.setVisible(false);
        pontoDeExtensao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        pontoDeExtensao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                aoClicarPontoDeExtensao();
            }
        });

        pontoDeExtensao.setSize(TAMANHO_PONTO_DE_EXTENSAO, TAMANHO_PONTO_DE_EXTENSAO);

        labelNome.setFont(GerenciadorDeRecursos.getInstancia().getRobotoBlack(15));
        labelMultiplicidadeA.setFont(GerenciadorDeRecursos.getInstancia().getRobotoBlack(15));
        labelMultiplicidadeB.setFont(GerenciadorDeRecursos.getInstancia().getRobotoBlack(15));

        areaDeDiagramas.addComponenteAoQuadro(pontoDeExtensao, 0);
        // index 1 para que fiquem embaixo do ponto de extensao
        areaDeDiagramas.addComponenteAoQuadro(painelSetaA, 1);
        areaDeDiagramas.addComponenteAoQuadro(painelSetaB, 1);
        areaDeDiagramas.addComponenteAoQuadro(labelNome, 1);
        areaDeDiagramas.addComponenteAoQuadro(labelMultiplicidadeA, 1);
        areaDeDiagramas.addComponenteAoQuadro(labelMultiplicidadeB, 1);

        aplicarEstiloDasLinhas(modeloAtual.getLinhasDaRelacao());

        frameGerenciarRelacao.setResizable(false);
        frameGerenciarRelacao.setTitle(GerenciadorDeRecursos.getInstancia().getString("relacao_configurar"));
        frameGerenciarRelacao.setIconImage(GerenciadorDeRecursos.getInstancia().getImagem("icone_configurar_48").getImage());
        frameGerenciarRelacao.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frameGerenciarRelacao.addComponentListener(new ComponentAdapter() {
            Relacao modeloAntesDeAlteracoes;

            @Override
            public void componentShown(ComponentEvent e) {
                modeloAntesDeAlteracoes = modeloAtual.copiar();
                for (JPanel linha : modeloAntesDeAlteracoes.getLinhasDaRelacao()) {
                    linha.setBackground(GerenciadorDeRecursos.getInstancia().getColor("dark_charcoal"));
                }
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                if (modeloAntesDeAlteracoes.ehDiferente(modeloAtual)) {
                    areaDeDiagramas.addAlteracaoDeComponente(new RelacaoModificada(
                        RelacaoUML.this,
                        modeloAntesDeAlteracoes.copiar(),
                        modeloAtual.copiar()
                    ));
                }
            }
        });

        initFrameGerenciarRelacao();
    }

    public void mostrarNome(boolean mostrar) {
        if (mostrar) {
            atualizarLocalizacaoDoNome();
        }

        labelNome.setVisible(mostrar);
    }

    public void mostrarSetaLadoA(boolean mostrar) {
        if (mostrar) {
            atualizarPontosDeSeta(modeloAtual.getOrientacaoLadoA(), pontosDaSetaA);
            atualizarLocalizacaoDeSeta(
                painelSetaA, modeloAtual.getLinhasDaRelacao().get(modeloAtual.getLinhasDaRelacao().size() - 1),
                modeloAtual.getOrientacaoLadoA()
            );
        }
        painelSetaA.setVisible(mostrar);
        modeloAtual.setMostrandoSetaA(mostrar);
    }

    public void mostrarSetaLadoB(boolean mostrar) {
        if (mostrar) {
            atualizarPontosDeSeta(modeloAtual.getOrientacaoLadoB(), pontosDaSetaB);
            atualizarLocalizacaoDeSeta(painelSetaB, modeloAtual.getLinhasDaRelacao().get(0), modeloAtual.getOrientacaoLadoB());
        }
        painelSetaB.setVisible(mostrar);
        modeloAtual.setMostrandoSetaB(mostrar);
    }

    public void mostrarMultiplicidadeLadoA(boolean mostrar) {
        if (mostrar) {
            atualizarLocalizacaoDeMultiplicidade(
                labelMultiplicidadeA, modeloAtual.getLinhasDaRelacao().get(modeloAtual.getLinhasDaRelacao().size() - 1),
                modeloAtual.getOrientacaoLadoA()
            );
        }

        labelMultiplicidadeA.setVisible(mostrar);
    }

    public void mostrarMultiplicidadeLadoB(boolean mostrar) {
        if (mostrar) {
            atualizarLocalizacaoDeMultiplicidade(labelMultiplicidadeB, modeloAtual.getLinhasDaRelacao().get(0), modeloAtual.getOrientacaoLadoB());
        }

        labelMultiplicidadeB.setVisible(mostrar);
    }

    public void mostrarDirecao(DirecaoDeRelacao direacao) {
        modeloAtual.setDirecao(direacao);

        switch (direacao) {
            case A_ATE_B -> {
                labelNome.setIcon(GerenciadorDeRecursos.getInstancia().getImagem("misc_seta_direcaoAB"));
                labelNome.setHorizontalTextPosition(JLabel.LEFT);
                labelNome.setSize(labelNome.getPreferredSize());
            }
            case B_ATE_A -> {
                labelNome.setIcon(GerenciadorDeRecursos.getInstancia().getImagem("misc_seta_direcaoBA"));
                labelNome.setHorizontalTextPosition(JLabel.RIGHT);
                labelNome.setSize(labelNome.getPreferredSize());
            }
            default -> labelNome.setIcon(null);
        }
    }

    public void mostrarPontoDeExtensao(boolean mostrar) {
        if (mostrar) {
            atualizarLocalizacaoPontoDeExtensao();
        }
        pontoDeExtensao.setVisible(mostrar);
    }

    public void estenderRelacao(ArrayList<JPanel> novosPaineis, Point ultimoPonto) {
        Relacao modeloAntigo = modeloAtual.copiar();

        if (ultimoPonto != null) {
            modeloAtual.setPontoLadoA(ultimoPonto);
        }

        MouseAdapter adapterGerenciarRelacao = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (areaDeDiagramas.componentesEstaoHabilitados()) {
                    selecionarRelacao(true);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (areaDeDiagramas.componentesEstaoHabilitados()) {
                    selecionarRelacao(false);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (areaDeDiagramas.componentesEstaoHabilitados()) {
                    mostrarFrameGerenciarRelacao(true);
                }
            }
        };

        if (novosPaineis != null) {
            for (JPanel linha : novosPaineis) {
                linha.addMouseListener(adapterGerenciarRelacao);
            }

            modeloAtual.getLinhasDaRelacao().addAll(novosPaineis);

            modeloAtual.setOrientacaoLadoA(calcularOrientacaoDeLinha(
                modeloAtual.getPontoLadoA(), modeloAtual.getLinhasDaRelacao().get(modeloAtual.getLinhasDaRelacao().size() - 1)
            ));
        }

        mostrarSetaLadoA(modeloAtual.estaMostrandoSetaA());

        atualizarLocalizacaoPontoDeExtensao();

        if (!modeloAtual.getNome().isEmpty()) {
            atualizarLocalizacaoDoNome();
        }

        if (!modeloAtual.getMultiplicidadeLadoA().isEmpty()) {
            atualizarLocalizacaoDeMultiplicidade(
                labelMultiplicidadeA, modeloAtual.getLinhasDaRelacao().get(modeloAtual.getLinhasDaRelacao().size() - 1),
                modeloAtual.getOrientacaoLadoA()
            );
        }

        if (!modeloAtual.getMultiplicidadeLadoB().isEmpty()) {
            atualizarLocalizacaoDeMultiplicidade(labelMultiplicidadeB, modeloAtual.getLinhasDaRelacao().get(0), modeloAtual.getOrientacaoLadoB());
        }

        aplicarEstiloDasLinhas(modeloAtual.getLinhasDaRelacao());

        Relacao modeloAtualCopia = modeloAtual.copiar();

        if (modeloAtualCopia.ehDiferente(modeloAntigo)) {
            areaDeDiagramas.addAlteracaoDeComponente(new RelacaoModificada(this, modeloAntigo, modeloAtualCopia));
        }
    }

    public void inverterSeta() {
        if (modeloAtual.estaMostrandoSetaA()) {
            mostrarSetaLadoA(false);
            mostrarSetaLadoB(true);

        } else if (modeloAtual.estaMostrandoSetaB()) {
            mostrarSetaLadoA(true);
            mostrarSetaLadoB(false);
        }
    }

    public void adicionarRelacaoAoQuadroBranco() {
        for (JPanel linha : modeloAtual.getLinhasDaRelacao()) {
            areaDeDiagramas.addComponenteAoQuadro(linha);
        }

        areaDeDiagramas.addComponenteAoQuadro(painelSetaA, 0);
        areaDeDiagramas.addComponenteAoQuadro(painelSetaB, 0);
        areaDeDiagramas.addComponenteAoQuadro(pontoDeExtensao, 0);
        areaDeDiagramas.addComponenteAoQuadro(labelNome);
        areaDeDiagramas.addComponenteAoQuadro(labelMultiplicidadeA);
        areaDeDiagramas.addComponenteAoQuadro(labelMultiplicidadeB);

        areaDeDiagramas.addRelacaoAoDiagrama(this);
    }

    public void removerRelacaoDoQuadroBranco() {
        for (JPanel linha : modeloAtual.getLinhasDaRelacao()) {
            areaDeDiagramas.removerComponenteDoQuadro(linha);
        }

        areaDeDiagramas.removerComponenteDoQuadro(pontoDeExtensao);
        areaDeDiagramas.removerComponenteDoQuadro(painelSetaA);
        areaDeDiagramas.removerComponenteDoQuadro(painelSetaB);
        areaDeDiagramas.removerComponenteDoQuadro(labelNome);
        areaDeDiagramas.removerComponenteDoQuadro(labelMultiplicidadeA);
        areaDeDiagramas.removerComponenteDoQuadro(labelMultiplicidadeB);
        areaDeDiagramas.removerRelacaoDoDiagrama(this);
    }

    public void mostrarFrameGerenciarRelacao(boolean mostrar) {
        frameGerenciarRelacao.setVisible(mostrar);
        if (mostrar) {
            frameGerenciarRelacao.setLocationRelativeTo(null);
            frameGerenciarRelacao.requestFocusInWindow();
        }
    }

    private void aoClicarPontoDeExtensao() {
        if (modeloAtual.estaMostrandoSetaA()) {
            painelSetaA.setVisible(false);
        }

        mostrarPontoDeExtensao(false);
        aoClicarPontoDeExtensao.accept(this);
    }

    private OrientacaoDeSeta calcularOrientacaoDeLinha(Point pontoFinalDaLinha, JPanel linha) {
        OrientacaoDeSeta orientacaoDaLinha;

        if (linha.getWidth() == TAMANHO_LINHAS_RELACAO && linha.getY() < pontoFinalDaLinha.y) {
            orientacaoDaLinha = OrientacaoDeSeta.SUL;
        } else if (linha.getWidth() == TAMANHO_LINHAS_RELACAO && linha.getY() == pontoFinalDaLinha.y) {
            orientacaoDaLinha = OrientacaoDeSeta.NORTE;
        } else if (linha.getX() + TAMANHO_LINHAS_RELACAO / 2 == pontoFinalDaLinha.x) {
            orientacaoDaLinha = OrientacaoDeSeta.OESTE;
        } else {
            orientacaoDaLinha = OrientacaoDeSeta.LESTE;
        }

        return orientacaoDaLinha;
    }

    private void selecionarRelacao(boolean selecionar) {
        relacaoSelecionada = selecionar;

        for (JPanel linha : modeloAtual.getLinhasDaRelacao()) {
            linha.setCursor(Cursor.getPredefinedCursor(selecionar ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            linha.setBackground(selecionar ? COR_SELECIONAR : COR_PADRAO);
        }

        painelSetaA.setCursor(Cursor.getPredefinedCursor(selecionar ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
        painelSetaB.setCursor(Cursor.getPredefinedCursor(selecionar ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));

        aplicarEstiloDasLinhas(modeloAtual.getLinhasDaRelacao());

        areaDeDiagramas.revalidarQuadroBranco();
    }

    private void atualizarLocalizacaoDoNome() {
        JPanel maiorLinha = modeloAtual.getLinhasDaRelacao().get(0);

        for (JPanel linha : modeloAtual.getLinhasDaRelacao()) {
            int tamanhoDaLinha;

            if (linha.getWidth() == TAMANHO_LINHAS_RELACAO) {
                // Se a linha for uma linha vertical
                tamanhoDaLinha = linha.getHeight();
            } else {
                // Se a linha for horizontal
                tamanhoDaLinha = linha.getWidth();
            }

            if (maiorLinha.getWidth() == TAMANHO_LINHAS_RELACAO) {
                maiorLinha = (maiorLinha.getHeight() > tamanhoDaLinha)? maiorLinha : linha;
            } else {
                maiorLinha = (maiorLinha.getWidth() > tamanhoDaLinha)? maiorLinha : linha;
            }
        }

        Point localizaoNome;

        int MARGEM = 12;

        if (maiorLinha.getWidth() == TAMANHO_LINHAS_RELACAO) {
            // Se a linha do meio for uma linha vertical
            localizaoNome = maiorLinha.getLocation();
            localizaoNome.translate(
                    TAMANHO_LINHAS_RELACAO + MARGEM,
                    maiorLinha.getHeight() / 2 - labelNome.getPreferredSize().height / 2
            );

        } else {
            // Se a linha for horizontal
            localizaoNome = maiorLinha.getLocation();
            localizaoNome.translate(
                    maiorLinha.getWidth() / 2 - labelNome.getPreferredSize().height / 2,
                    -labelNome.getPreferredSize().height
            );
        }

        labelNome.setLocation(localizaoNome);
        labelNome.setSize(labelNome.getPreferredSize());
    }

    private void atualizarLocalizacaoDeMultiplicidade(
            JLabel labelMultiplicidade, JPanel linhaDaMultiplicidade, OrientacaoDeSeta orientacaoDaLinha
    ) {
        Point localizacaoMultiplicidade = linhaDaMultiplicidade.getLocation();

        int MARGEM_VERTICAL = 2;
        int MARGEM_HORIZONTAL = 14;

        switch (orientacaoDaLinha) {
            case NORTE -> localizacaoMultiplicidade.translate(
                    TAMANHO_LINHAS_RELACAO + MARGEM_HORIZONTAL,
                    MARGEM_VERTICAL
            );
            case SUL -> localizacaoMultiplicidade.translate(
                    TAMANHO_LINHAS_RELACAO + MARGEM_HORIZONTAL,
                    linhaDaMultiplicidade.getHeight() - labelMultiplicidade.getPreferredSize().height - MARGEM_VERTICAL
            );
            case OESTE -> localizacaoMultiplicidade.translate(
                    MARGEM_HORIZONTAL,
                    labelMultiplicidade.getPreferredSize().height + MARGEM_VERTICAL
            );
            case LESTE -> localizacaoMultiplicidade.translate(
                    linhaDaMultiplicidade.getWidth() - labelMultiplicidade.getPreferredSize().width - MARGEM_HORIZONTAL,
                    labelMultiplicidade.getPreferredSize().height + MARGEM_VERTICAL
            );
        }

        labelMultiplicidade.setLocation(localizacaoMultiplicidade);
        labelMultiplicidade.setSize(labelMultiplicidade.getPreferredSize());
    }

    private void atualizarLocalizacaoDeSeta(JPanel painelSeta, JPanel linhaDaSeta, OrientacaoDeSeta orientacaoDaSeta) {
        Point localizacaoDaSeta = new Point();

        switch (orientacaoDaSeta) {
            case SUL -> {
                localizacaoDaSeta.x = linhaDaSeta.getX() - ((TAMANHO_SETA - TAMANHO_LINHAS_RELACAO)/2);
                localizacaoDaSeta.y = linhaDaSeta.getY() + linhaDaSeta.getHeight() - TAMANHO_SETA;
            }
            case NORTE -> {
                localizacaoDaSeta.x = linhaDaSeta.getX() - ((TAMANHO_SETA - TAMANHO_LINHAS_RELACAO)/2);
                localizacaoDaSeta.y = linhaDaSeta.getY();
            }
            case OESTE -> {
                localizacaoDaSeta.x = linhaDaSeta.getX();
                localizacaoDaSeta.y = linhaDaSeta.getY() - ((TAMANHO_SETA - TAMANHO_LINHAS_RELACAO)/2);
            }
            case LESTE -> {
                localizacaoDaSeta.x = linhaDaSeta.getX() + linhaDaSeta.getWidth() - TAMANHO_SETA;
                localizacaoDaSeta.y = linhaDaSeta.getY() - ((TAMANHO_SETA - TAMANHO_LINHAS_RELACAO)/2);
            }
        }

        int MARGEM = 10;

        painelSeta.setBounds(
                localizacaoDaSeta.x - MARGEM, localizacaoDaSeta.y - MARGEM,
                TAMANHO_SETA + MARGEM * 2, TAMANHO_SETA + MARGEM * 2
        );
    }

    private void atualizarPontosDeSeta(OrientacaoDeSeta orientacaoDaSeta, Point[] pontosDaSeta) {
        switch (orientacaoDaSeta) {
            case SUL -> {
                pontosDaSeta[0].x = 0; pontosDaSeta[1].x = TAMANHO_SETA/2; pontosDaSeta[2].x = TAMANHO_SETA;
                pontosDaSeta[0].y = 0; pontosDaSeta[1].y = TAMANHO_SETA; pontosDaSeta[2].y = 0;
            }
            case NORTE -> {
                pontosDaSeta[0].x = 0; pontosDaSeta[1].x = TAMANHO_SETA/2; pontosDaSeta[2].x = TAMANHO_SETA;
                pontosDaSeta[0].y = TAMANHO_SETA; pontosDaSeta[1].y = 0; pontosDaSeta[2].y = TAMANHO_SETA;
            }
            case OESTE -> {
                pontosDaSeta[0].x = TAMANHO_SETA; pontosDaSeta[1].x = 0; pontosDaSeta[2].x = TAMANHO_SETA;
                pontosDaSeta[0].y = TAMANHO_SETA; pontosDaSeta[1].y = TAMANHO_SETA/2; pontosDaSeta[2].y = 0;
            }
            case LESTE -> {
                pontosDaSeta[0].x = 0; pontosDaSeta[1].x  = TAMANHO_SETA; pontosDaSeta[2].x  = 0;
                pontosDaSeta[0].y = TAMANHO_SETA; pontosDaSeta[1].y = TAMANHO_SETA/2; pontosDaSeta[2].y = 0;
            }
        }

        int MARGEM = 10;
        for (Point point : pontosDaSeta) {
            point.x += MARGEM;
            point.y += MARGEM;
        }
    }

    private void atualizarLocalizacaoPontoDeExtensao() {
        Point localizacaoPontoDeExtensao = new Point();
        Rectangle ancoraDoPonto = modeloAtual.estaMostrandoSetaA() ?
                painelSetaA.getBounds() : modeloAtual.getLinhasDaRelacao().get(modeloAtual.getLinhasDaRelacao().size() - 1).getBounds();

        switch (modeloAtual.getOrientacaoLadoA()) {
            case SUL -> {
                localizacaoPontoDeExtensao.x = ancoraDoPonto.x + ((ancoraDoPonto.width - TAMANHO_PONTO_DE_EXTENSAO)/2);
                localizacaoPontoDeExtensao.y = ancoraDoPonto.y + ancoraDoPonto.height - TAMANHO_PONTO_DE_EXTENSAO/2;
            }
            case NORTE -> {
                localizacaoPontoDeExtensao.x = ancoraDoPonto.x + ((ancoraDoPonto.width - TAMANHO_PONTO_DE_EXTENSAO)/2);
                localizacaoPontoDeExtensao.y = ancoraDoPonto.y - TAMANHO_PONTO_DE_EXTENSAO/2;
            }
            case OESTE -> {
                localizacaoPontoDeExtensao.x = ancoraDoPonto.x - TAMANHO_PONTO_DE_EXTENSAO/2;
                localizacaoPontoDeExtensao.y = ancoraDoPonto.y + ((ancoraDoPonto.height - TAMANHO_PONTO_DE_EXTENSAO)/2);
            }
            case LESTE -> {
                localizacaoPontoDeExtensao.x = ancoraDoPonto.x + ancoraDoPonto.width - TAMANHO_PONTO_DE_EXTENSAO/2;
                localizacaoPontoDeExtensao.y = ancoraDoPonto.y + ((ancoraDoPonto.height - TAMANHO_PONTO_DE_EXTENSAO)/2);
            }
        }

        pontoDeExtensao.setLocation(localizacaoPontoDeExtensao);
    }

    public ArrayList<JPanel> getLinhasDaRelacao() {
        return modeloAtual.getLinhasDaRelacao();
    }

    protected Relacao getModeloAtual() {
        return modeloAtual;
    }

    public AreaDeDiagramas getAreaDeDiagramas() {
        return areaDeDiagramas;
    }

    public TipoDeRelacao getTipoDeRelacao() {
        return tipoDeRelacao;
    }

    protected JFrame getFrameGerenciarRelacao() {
        return frameGerenciarRelacao;
    }

    /**
     * Retorna o ponto de inicio onde a relacao deve ser estendida. Esse ponto corresponde mais ou menos
     * com o inicio da primeira linha.
     */
    public Point getLocalizacaoDeExtensao() {
        Point localizacaoDeExtensao = modeloAtual.getLinhasDaRelacao().get(modeloAtual.getLinhasDaRelacao().size() - 1).getLocation();

        switch (modeloAtual.getOrientacaoLadoA()) {
            case NORTE, OESTE -> localizacaoDeExtensao.x += TAMANHO_LINHAS_RELACAO;
            case SUL -> {
                localizacaoDeExtensao.y += modeloAtual.getLinhasDaRelacao().get(modeloAtual.getLinhasDaRelacao().size() - 1).getHeight() - TAMANHO_LINHAS_RELACAO;
                localizacaoDeExtensao.x += TAMANHO_LINHAS_RELACAO;
            }
        }

        return localizacaoDeExtensao;
    }

    public boolean relacaoEstaSelecionada() {
        return relacaoSelecionada;
    }

    public void setNome(String nome) {
        modeloAtual.setNome(nome);
        labelNome.setText(nome);
    }

    public void setMultiplicidadeLadoA(String multiplicidade) {
        modeloAtual.setMultiplicidadeLadoA(multiplicidade);
        labelMultiplicidadeA.setText(multiplicidade);
    }

    public void setMultiplicidadeLadoB(String multiplicidade) {
        modeloAtual.setMultiplicidadeLadoB(multiplicidade);
        labelMultiplicidadeB.setText(multiplicidade);
    }

    public void setAoClicarPontoDeExtensao(Consumer<RelacaoUML> aoClicarPontoDeExtensao) {
        this.aoClicarPontoDeExtensao = aoClicarPontoDeExtensao;
    }

    public void setModelo(Relacao novoModelo) {
        for (JPanel linha : modeloAtual.getLinhasDaRelacao()) {
            areaDeDiagramas.removerComponenteDoQuadro(linha);
        }

        MouseAdapter adapterGerenciarRelacao = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (areaDeDiagramas.componentesEstaoHabilitados()) {
                    selecionarRelacao(true);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (areaDeDiagramas.componentesEstaoHabilitados()) {
                    selecionarRelacao(false);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (areaDeDiagramas.componentesEstaoHabilitados()) {
                    mostrarFrameGerenciarRelacao(true);
                }
            }
        };

        for (JPanel linha : novoModelo.getLinhasDaRelacao()) {
            areaDeDiagramas.addComponenteAoQuadro(linha);
            linha.addMouseListener(adapterGerenciarRelacao);
            linha.setBackground(GerenciadorDeRecursos.getInstancia().getColor("dark_charcoal"));
        }

        modeloAtual = novoModelo;

        modeloAtual.setOrientacaoLadoA(calcularOrientacaoDeLinha(
            novoModelo.getPontoLadoA(), novoModelo.getLinhasDaRelacao().get(novoModelo.getLinhasDaRelacao().size() - 1)
        ));

        setNome(novoModelo.getNome());

        mostrarNome(!novoModelo.getNome().isEmpty());
        mostrarDirecao(novoModelo.getDirecao());
        mostrarSetaLadoA(novoModelo.estaMostrandoSetaA());
        mostrarSetaLadoB(novoModelo.estaMostrandoSetaB());

        setMultiplicidadeLadoA(novoModelo.getMultiplicidadeLadoA());
        setMultiplicidadeLadoB(novoModelo.getMultiplicidadeLadoB());

        mostrarMultiplicidadeLadoA(!novoModelo.getMultiplicidadeLadoA().isEmpty());
        mostrarMultiplicidadeLadoB(!novoModelo.getMultiplicidadeLadoB().isEmpty());

        aplicarEstiloDasLinhas(modeloAtual.getLinhasDaRelacao());
    }

    @Override
    public String toString() {
        StringBuilder informacoesRelacao = new StringBuilder();

        informacoesRelacao.append("RELACAO_UML\n//Tipo de Relacao\n");
        informacoesRelacao.append(tipoDeRelacao);

        informacoesRelacao.append("\n//Nome\n");
        informacoesRelacao.append(modeloAtual.getNome());

        informacoesRelacao.append("\n//Numero de Linhas\n");
        informacoesRelacao.append(modeloAtual.getLinhasDaRelacao().size());

        for (JPanel linha : modeloAtual.getLinhasDaRelacao()) {
            informacoesRelacao.append("\n//Linha - X\n");
            informacoesRelacao.append(linha.getX());
            informacoesRelacao.append("\n//Linha - Y\n");
            informacoesRelacao.append(linha.getY());
            informacoesRelacao.append("\n//Linha - Largura\n");
            informacoesRelacao.append(linha.getWidth());
            informacoesRelacao.append("\n//Linha - Altura\n");
            informacoesRelacao.append(linha.getHeight());
        }

        informacoesRelacao.append("\n//Orientacao A A\n");
        informacoesRelacao.append(modeloAtual.getOrientacaoLadoA());

        informacoesRelacao.append("\n//Orientacao B\n");
        informacoesRelacao.append(modeloAtual.getOrientacaoLadoB());

        informacoesRelacao.append("\n//Multiplicidade A\n");
        informacoesRelacao.append(modeloAtual.getMultiplicidadeLadoA());

        informacoesRelacao.append("\n//Multiplicidade B\n");
        informacoesRelacao.append(modeloAtual.getMultiplicidadeLadoB());

        informacoesRelacao.append("\n//Mostrando Seta A\n");
        informacoesRelacao.append(modeloAtual.estaMostrandoSetaA());

        informacoesRelacao.append("\n//Mostrando Seta B\n");
        informacoesRelacao.append(modeloAtual.estaMostrandoSetaB());

        informacoesRelacao.append("\n//Direcao\n");
        informacoesRelacao.append(modeloAtual.getDirecao());

        informacoesRelacao.append("\n//Ponto Lado A - X\n");
        informacoesRelacao.append(modeloAtual.getPontoLadoA().x);

        informacoesRelacao.append("\n//Ponto Lado A - Y\n");
        informacoesRelacao.append(modeloAtual.getPontoLadoA().y);
        informacoesRelacao.append("\n");

        return informacoesRelacao.toString();
    }

    protected abstract void aplicarEstiloDasLinhas(ArrayList<JPanel> linhas);

    protected abstract void desenharSeta(Graphics2D g2, Point[] pontosDaSeta);

    protected abstract void initFrameGerenciarRelacao();
}
