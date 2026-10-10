
package vista;

import controlador.ControladorPrincipal;
import modelo.Usuario;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    private final Usuario usuario;
    private final ControladorPrincipal controlador;

    public VentanaPrincipal(Usuario usuario) {
        this.usuario = usuario;
        this.controlador = new ControladorPrincipal(usuario);

        setTitle("Biblioteca Escolar - Menú principal");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        construirInterfaz();
    }

    private void construirInterfaz() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel titulo = new JLabel(
                "Biblioteca Escolar",
                SwingConstants.CENTER
        );
        titulo.setFont(new Font("Arial", Font.BOLD, 26));

        JLabel lblUsuario = new JLabel(
                "Usuario: " + usuario.getNombre()
                        + " | Rol: " + usuario.getRol(),
                SwingConstants.CENTER
        );

        JPanel encabezado = new JPanel(new GridLayout(2, 1, 5, 5));
        encabezado.add(titulo);
        encabezado.add(lblUsuario);

        panel.add(encabezado, BorderLayout.NORTH);

        JPanel botones = new JPanel(new GridLayout(0, 2, 12, 12));

        JButton btnLibros = new JButton("Gestionar libros");
        JButton btnEstudiantes = new JButton("Gestionar estudiantes");
        JButton btnPrestamos = new JButton("Préstamos");
        JButton btnDevoluciones = new JButton("Devoluciones");
        JButton btnReportes = new JButton("Reportes");
        JButton btnCerrarSesion = new JButton("Cerrar sesión");

        btnLibros.setEnabled(controlador.puedeGestionarLibros());
        btnEstudiantes.setEnabled(controlador.puedeGestionarEstudiantes());
        btnPrestamos.setEnabled(controlador.puedeRegistrarPrestamos());
        btnDevoluciones.setEnabled(controlador.puedeRegistrarDevoluciones());
        btnReportes.setEnabled(controlador.puedeVerReportes());

        btnLibros.addActionListener(e ->
                new VentanaLibros().setVisible(true));

        btnEstudiantes.addActionListener(e ->
                new VentanaEstudiantes().setVisible(true));

        btnPrestamos.addActionListener(e ->
                new VentanaPrestamos(usuario).setVisible(true));

        btnDevoluciones.addActionListener(e ->
                new VentanaDevoluciones(usuario).setVisible(true));

        btnReportes.addActionListener(e ->
                new VentanaReportes().setVisible(true));

        btnCerrarSesion.addActionListener(e -> {
            new VentanaLogin().setVisible(true);
            dispose();
        });

        botones.add(btnLibros);
        botones.add(btnEstudiantes);
        botones.add(btnPrestamos);
        botones.add(btnDevoluciones);
        botones.add(btnReportes);
        botones.add(btnCerrarSesion);

        panel.add(botones, BorderLayout.CENTER);

        add(panel);
    }
}
