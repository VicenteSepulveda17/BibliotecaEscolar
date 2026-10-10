
package vista;

import controlador.ControladorLogin;
import modelo.Usuario;

import javax.swing.*;
import java.awt.*;

public class VentanaLogin extends JFrame {

    private final JTextField txtCorreo;
    private final JPasswordField txtContrasena;
    private final JButton btnIngresar;
    private final ControladorLogin controlador;

    public VentanaLogin() {
        controlador = new ControladorLogin();

        setTitle("Biblioteca Escolar - Inicio de sesión");
        setSize(420, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("Biblioteca Escolar", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Correo:"), gbc);

        txtCorreo = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtCorreo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Contraseña:"), gbc);

        txtContrasena = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(txtContrasena, gbc);

        btnIngresar = new JButton("Ingresar");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(btnIngresar, gbc);

        add(panel);

        btnIngresar.addActionListener(e -> iniciarSesion());

        getRootPane().setDefaultButton(btnIngresar);
    }

    private void iniciarSesion() {
        String correo = txtCorreo.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());

        if (correo.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ingresa tu correo y contraseña.",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        btnIngresar.setEnabled(false);

        try {
            Usuario usuario = controlador.iniciarSesion(correo, contrasena);

            if (usuario == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Correo o contraseña incorrectos.",
                        "Error de inicio de sesión",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            VentanaPrincipal ventana = new VentanaPrincipal(usuario);
            ventana.setVisible(true);
            dispose();

        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo iniciar sesión. Revisa la conexión con la base de datos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } finally {
            btnIngresar.setEnabled(true);
        }
    }
}

