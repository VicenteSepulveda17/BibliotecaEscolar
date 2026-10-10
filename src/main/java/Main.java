
import vista.VentanaLogin;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception e) {
                System.out.println(
                        "No se pudo aplicar el estilo del sistema."
                );
            }

            VentanaLogin ventana = new VentanaLogin();
            ventana.setVisible(true);
        });
    }
}

