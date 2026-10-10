
package vista;

import controlador.ControladorEstudiantes;
import controlador.ControladorLibros;
import controlador.ControladorPrestamos;
import modelo.Estudiante;
import modelo.Libro;
import modelo.Prestamo;
import modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class VentanaPrestamos extends JFrame {

    private final ControladorEstudiantes controladorEstudiantes;
    private final ControladorLibros controladorLibros;
    private final ControladorPrestamos controladorPrestamos;

    private final JComboBox<Estudiante> cmbEstudiantes;
    private final JComboBox<Libro> cmbLibros;
    private final JTextField txtFechaPrestamo;
    private final JTextField txtFechaDevolucion;
    private final JButton btnRegistrar;

    private final Usuario usuario;

    public VentanaPrestamos(Usuario usuario) {
        this.usuario = usuario;

        controladorEstudiantes = new ControladorEstudiantes();
        controladorLibros = new ControladorLibros();
        controladorPrestamos = new ControladorPrestamos();

        setTitle("Biblioteca Escolar - Registrar préstamo");
        setSize(520, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        cmbEstudiantes = new JComboBox<>();
        cmbLibros = new JComboBox<>();

        txtFechaPrestamo = new JTextField(LocalDate.now().toString(), 12);
        txtFechaPrestamo.setEditable(false);

        txtFechaDevolucion = new JTextField(12);
        txtFechaDevolucion.setEditable(false);

        btnRegistrar = new JButton("Registrar préstamo");

        construirInterfaz();
        cargarEstudiantes();
        cargarLibros();
        calcularFechaDevolucion();
    }

    private void construirInterfaz() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        agregarFila(panel, gbc, 0, "Estudiante:", cmbEstudiantes);
        agregarFila(panel, gbc, 1, "Libro:", cmbLibros);
        agregarFila(panel, gbc, 2, "Fecha préstamo:", txtFechaPrestamo);
        agregarFila(panel, gbc, 3, "Fecha límite:", txtFechaDevolucion);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(btnRegistrar, gbc);

        btnRegistrar.addActionListener(e -> registrarPrestamo());

        add(panel);
    }

    private void agregarFila(
            JPanel panel,
            GridBagConstraints gbc,
            int fila,
            String texto,
            JComponent componente) {

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = fila;
        panel.add(new JLabel(texto), gbc);

        gbc.gridx = 1;
        panel.add(componente, gbc);
    }

    private void cargarEstudiantes() {
        cmbEstudiantes.removeAllItems();

        List<Estudiante> estudiantes =
                controladorEstudiantes.listarEstudiantes();

        for (Estudiante estudiante : estudiantes) {
            cmbEstudiantes.addItem(estudiante);
        }
    }

    private void cargarLibros() {
        cmbLibros.removeAllItems();

        List<Libro> libros = controladorLibros.listarLibros();

        for (Libro libro : libros) {
            if (libro.getStock() > 0) {
                cmbLibros.addItem(libro);
            }
        }
    }

    private void calcularFechaDevolucion() {
        LocalDate fecha = LocalDate.now();

        txtFechaPrestamo.setText(fecha.toString());
        txtFechaDevolucion.setText(
                controladorPrestamos
                        .calcularFechaDevolucion(fecha)
                        .toString()
        );
    }

    private void registrarPrestamo() {
        Estudiante estudiante =
                (Estudiante) cmbEstudiantes.getSelectedItem();

        Libro libro = (Libro) cmbLibros.getSelectedItem();

        if (estudiante == null || libro == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Debes seleccionar un estudiante y un libro disponible.",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        LocalDate fechaPrestamo = LocalDate.now();

        Prestamo prestamo = new Prestamo(
                0,
                estudiante,
                libro,
                fechaPrestamo,
                controladorPrestamos.calcularFechaDevolucion(fechaPrestamo),
                false
        );

        // Evita registrar varios préstamos mientras uno está en proceso.
        btnRegistrar.setEnabled(false);
        btnRegistrar.setText("Registrando...");

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {

            @Override
            protected Boolean doInBackground() {
                // Se ejecuta fuera del hilo de la interfaz.
                return controladorPrestamos.registrarPrestamo(prestamo);
            }

            @Override
            protected void done() {
                // Se ejecuta en el hilo de la interfaz Swing.
                btnRegistrar.setEnabled(true);
                btnRegistrar.setText("Registrar préstamo");

                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(
                                VentanaPrestamos.this,
                                "Préstamo registrado correctamente."
                        );

                        // Actualiza el stock disponible en el formulario.
                        cargarLibros();
                    } else {
                        JOptionPane.showMessageDialog(
                                VentanaPrestamos.this,
                                "No se pudo registrar el préstamo. "
                                        + "Comprueba el stock y la conexión.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );

                        cargarLibros();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            VentanaPrestamos.this,
                            "Ocurrió un error al registrar el préstamo: "
                                    + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );

                    cargarLibros();
                }
            }
        };

        worker.execute();
    }
}
