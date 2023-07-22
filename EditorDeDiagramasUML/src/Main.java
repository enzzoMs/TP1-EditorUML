import InterfaceGrafica.InterfaceGraficaUML;
import com.formdev.flatlaf.FlatLightLaf;
import utils.GerenciadorDeRecursos;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {

        // Configurando alguns aspectos do FlatLaf

        FlatLightLaf.setup();

        UIManager.put("ScrollBar.trackArc", 999);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.trackInsets", new Insets( 2, 4, 2, 4 ));
        UIManager.put("ScrollBar.thumbInsets", new Insets( 2, 2, 2, 2 ));
        UIManager.put("ScrollBar.thumb", GerenciadorDeRecursos.getInstancia().getColor("quickSilver"));

        UIManager.put("TextComponent.arc", 7);
        UIManager.put("Component.arc", 7 );

        UIManager.put("Component.arrowType", "triangle");

        UIManager.put("FileChooser.readOnly", Boolean.TRUE);

        // Iniciando o aplicativo. Cria um objeto InterfaceGraficaUML e mostra o frame principal

        SwingUtilities.invokeLater(() -> {
            InterfaceGraficaUML interfaceGrafica = new InterfaceGraficaUML();
            interfaceGrafica.mostrarInterfaceGrafica(true);
        });
    }
}