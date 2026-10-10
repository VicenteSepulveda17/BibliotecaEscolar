
package vista;

import controlador.ControladorReportes;
import modelo.Prestamo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VentanaReportes extends JFrame {

    private final ControladorReportes controlador;
    private final DefaultTableModel modeloTabla;
    private final JTable tabla;
    private final JComboBox<String> cmbTipoReporte;
    private final JTextField txtIdEstudiante;

    public VentanaReportes() {
        controlador = new ControladorReportes();

        setTitle("Reportes de la biblioteca");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        modeloTabla = new DefaultTableModel(new String[]{}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);

        cmbTipoReporte = new JComboBox<>(new String[]{
                "Libros más prestados",
                "Préstamos activos",
                "Historial de estudiante"
        });

        txtIdEstudiante = new JTextField(8);

        construirInterfaz();
        actualizarColumnas();
    }

    private void construirInterfaz() {
        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel(
                "Reportes de la biblioteca escolar",
                SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.add(new JLabel("Tipo de reporte:"));
        panelSuperior.add(cmbTipoReporte);
        panelSuperior.add(new JLabel("ID estudiante:"));
        panelSuperior.add(txtIdEstudiante);

        JButton btnGenerar = new JButton("Generar reporte");
        JButton btnLimpiar = new JButton("Limpiar tabla");

        panelSuperior.add(btnGenerar);
        panelSuperior.add(btnLimpiar);

        JPanel panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.add(titulo, BorderLayout.CENTER);

        principal.add(panelTitulo, BorderLayout.NORTH);
        principal.add(panelSuperior, BorderLayout.CENTER);
        principal.add(new JScrollPane(tabla), BorderLayout.SOUTH);

        JPanel contenido = new JPanel(new BorderLayout(5, 5));
        contenido.add(panelSuperior, BorderLayout.NORTH);
        contenido.add(new JScrollPane(tabla), BorderLayout.CENTER);

        principal.add(contenido, BorderLayout.CENTER);

        cmbTipoReporte.addActionListener(e -> actualizarColumnas());
        btnGenerar.addActionListener(e -> generarReporte());
        btnLimpiar.addActionListener(e -> modeloTabla.setRowCount(0));

        setContentPane(principal);
    }

    private void actualizarColumnas() {
        String tipo = (String) cmbTipoReporte.getSelectedItem();

        modeloTabla.setRowCount(0);
        modeloTabla.setColumnIdentifiers(
                "Libros más prestados".equals(tipo)
                        ? new String[]{"Libro", "Autor", "Veces prestado"}
                        : new String[]{
                        "ID préstamo", "Estudiante", "Libro",
                        "Fecha préstamo", "Fecha límite", "Estado"
                }
        );

        txtIdEstudiante.setEnabled(
                "Historial de estudiante".equals(tipo));
    }

    private void generarReporte() {
        try {
            String tipo = (String) cmbTipoReporte.getSelectedItem();
            modeloTabla.setRowCount(0);

            if ("Libros más prestados".equals(tipo)) {
                mostrarLibrosMasPrestados(
                        controlador.obtenerLibrosMasPrestados());
                return;
            }

            List<Prestamo> prestamos;

            if ("Préstamos activos".equals(tipo)) {
                prestamos = controlador.obtenerPrestamosActivos();
            } else {
                String textoId = txtIdEstudiante.getText().trim();

                if (textoId.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            this, "Ingresa el ID del estudiante.");
                    return;
                }

                int idEstudiante = Integer.parseInt(textoId);
                prestamos = controlador.obtenerHistorialEstudiante(idEstudiante);
            }

            for (Prestamo p : prestamos) {
                String estado;

                if (p.isDevuelto()) {
                    estado = "Devuelto";
                } else if (p.getFechaDevolucion() != null
                        && p.getFechaDevolucion().isBefore(LocalDate.now())) {
                    estado = "Atrasado";
                } else {
                    estado = "Activo";
                }

                modeloTabla.addRow(new Object[]{
                        p.getId(),
                        p.getEstudiante() != null
                                ? p.getEstudiante().getNombre() : "",
                        p.getLibro() != null
                                ? p.getLibro().getTitulo() : "",
                        p.getFechaPrestamo(),
                        p.getFechaDevolucion(),
                        estado
                });
            }

            if (prestamos.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this, "No se encontraron registros para este reporte.");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this, "El ID del estudiante debe ser un número entero.");
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo generar el reporte: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarLibrosMasPrestados(List<Prestamo> prestamos) {
        Map<Integer, Object[]> conteo = new LinkedHashMap<>();

        for (Prestamo p : prestamos) {
            if (p.getLibro() == null) {
                continue;
            }

            int idLibro = p.getLibro().getId();

            if (conteo.containsKey(idLibro)) {
                Object[] fila = conteo.get(idLibro);
                fila[2] = (int) fila[2] + 1;
            } else {
                conteo.put(idLibro, new Object[]{
                        p.getLibro().getTitulo(),
                        p.getLibro().getAutor(),
                        1
                });
            }
        }

        for (Object[] fila : conteo.values()) {
            modeloTabla.addRow(fila);
        }

        if (conteo.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "No se encontraron préstamos registrados.");
        }
    }
}
