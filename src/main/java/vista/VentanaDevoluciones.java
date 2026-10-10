
package vista;

import controlador.ControladorPrestamos;
import modelo.Prestamo;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VentanaDevoluciones extends JFrame {

    private final ControladorPrestamos controlador;
    private final DefaultTableModel modeloTabla;
    private final JTable tabla;
    private final Usuario usuario;

    public VentanaDevoluciones(Usuario usuario) {
        this.usuario = usuario;
        controlador = new ControladorPrestamos();

        setTitle("Biblioteca Escolar - Devoluciones");
        setSize(850, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        modeloTabla = new DefaultTableModel(
                new String[]{"ID préstamo", "Estudiante", "Libro",
                        "Fecha préstamo", "Fecha límite", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);

        construirInterfaz();
        cargarPrestamos();
    }

    private void construirInterfaz() {
        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel(
                "Préstamos y devoluciones",
                SwingConstants.CENTER
        );
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        principal.add(titulo, BorderLayout.NORTH);

        principal.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout());

        JButton btnDevolver = new JButton("Registrar devolución");
        JButton btnActualizar = new JButton("Actualizar lista");

        botones.add(btnDevolver);
        botones.add(btnActualizar);

        principal.add(botones, BorderLayout.SOUTH);

        btnDevolver.addActionListener(e -> registrarDevolucion());
        btnActualizar.addActionListener(e -> cargarPrestamos());

        add(principal);
    }



    private void cargarPrestamos() {
        modeloTabla.setRowCount(0);

        List<Prestamo> prestamos;

        if (usuario.getRol() != null
                && usuario.getRol().equalsIgnoreCase("estudiante")) {

            int idEstudiante = controlador.obtenerIdEstudiante(
                    usuario.getRut()
            );

            prestamos = controlador.listarPrestamosActivosPorEstudiante(
                    idEstudiante
            );

        } else {
            prestamos = controlador.listarPrestamosActivos();
        }

        for (Prestamo prestamo : prestamos) {
            String estado = prestamo.getFechaDevolucion() != null
                    && prestamo.getFechaDevolucion()
                    .isBefore(java.time.LocalDate.now())
                    ? "Atrasado"
                    : "Activo";

            modeloTabla.addRow(new Object[]{
                    prestamo.getId(),
                    prestamo.getEstudiante().getNombre(),
                    prestamo.getLibro().getTitulo(),
                    prestamo.getFechaPrestamo(),
                    prestamo.getFechaDevolucion(),
                    estado
            });
        }
    }


    private void registrarDevolucion() {
        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un préstamo de la tabla."
            );
            return;
        }

        int idPrestamo = (int) modeloTabla.getValueAt(fila, 0);

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Confirmas la devolución del libro seleccionado?",
                "Confirmar devolución",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        if (controlador.registrarDevolucion(idPrestamo)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Devolución registrada correctamente."
            );
            cargarPrestamos();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo registrar la devolución. Comprueba si el préstamo sigue activo.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


}

