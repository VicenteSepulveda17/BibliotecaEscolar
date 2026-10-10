
package vista;

import controlador.ControladorEstudiantes;
import modelo.Estudiante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VentanaEstudiantes extends JFrame {

    private final ControladorEstudiantes controlador;
    private final DefaultTableModel modeloTabla;
    private final JTable tabla;

    public VentanaEstudiantes() {
        controlador = new ControladorEstudiantes();

        setTitle("Biblioteca Escolar - Gestión de estudiantes");
        setSize(850, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        modeloTabla = new DefaultTableModel(
                new String[]{"ID", "Nombre", "RUT", "Curso", "Correo"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);

        construirInterfaz();
        cargarEstudiantes();
    }

    private void construirInterfaz() {
        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel(
                "Gestión de estudiantes", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        principal.add(titulo, BorderLayout.NORTH);

        principal.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout());

        JButton btnAgregar = new JButton("Agregar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnActualizar = new JButton("Actualizar");

        botones.add(btnAgregar);
        botones.add(btnEditar);
        botones.add(btnEliminar);
        botones.add(btnActualizar);

        principal.add(botones, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> agregarEstudiante());
        btnEditar.addActionListener(e -> editarEstudiante());
        btnEliminar.addActionListener(e -> eliminarEstudiante());
        btnActualizar.addActionListener(e -> cargarEstudiantes());

        add(principal);
    }

    private void cargarEstudiantes() {
        try {
            mostrarEstudiantes(controlador.listarEstudiantes());
        } catch (RuntimeException e) {
            mostrarError("No se pudieron cargar los estudiantes.", e);
        }
    }

    private void mostrarEstudiantes(List<Estudiante> estudiantes) {
        modeloTabla.setRowCount(0);

        for (Estudiante estudiante : estudiantes) {
            modeloTabla.addRow(new Object[]{
                    estudiante.getId(),
                    estudiante.getNombre(),
                    estudiante.getRut(),
                    estudiante.getCurso(),
                    estudiante.getCorreo()
            });
        }
    }

    private void agregarEstudiante() {
        mostrarFormulario(null);
    }

    private void editarEstudiante() {
        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(
                    this, "Selecciona un estudiante de la tabla.");
            return;
        }

        int id = (int) modeloTabla.getValueAt(fila, 0);
        Estudiante estudiante = controlador.buscarEstudiante(id);

        if (estudiante == null) {
            JOptionPane.showMessageDialog(
                    this, "No se encontró el estudiante seleccionado.");
            cargarEstudiantes();
            return;
        }

        mostrarFormulario(estudiante);
    }

    private void mostrarFormulario(Estudiante existente) {
        boolean esEdicion = existente != null;

        JTextField txtNombre = new JTextField(
                esEdicion ? existente.getNombre() : "", 20);
        JTextField txtRut = new JTextField(
                esEdicion ? existente.getRut() : "", 20);
        JTextField txtCurso = new JTextField(
                esEdicion ? existente.getCurso() : "", 20);
        JTextField txtCorreo = new JTextField(
                esEdicion ? existente.getCorreo() : "", 20);

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));

        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);
        panel.add(new JLabel("RUT:"));
        panel.add(txtRut);
        panel.add(new JLabel("Curso:"));
        panel.add(txtCurso);
        panel.add(new JLabel("Correo:"));
        panel.add(txtCorreo);

        while (true) {
            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    panel,
                    esEdicion ? "Editar estudiante" : "Agregar estudiante",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (opcion != JOptionPane.OK_OPTION) {
                return;
            }

            String nombre = txtNombre.getText().trim();
            String rut = txtRut.getText().trim();
            String curso = txtCurso.getText().trim();
            String correo = txtCorreo.getText().trim();

            if (nombre.isEmpty() || rut.isEmpty()
                    || curso.isEmpty() || correo.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Completa todos los campos.",
                        "Datos incompletos",
                        JOptionPane.WARNING_MESSAGE);
                continue;
            }

            if (!correo.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                JOptionPane.showMessageDialog(
                        this,
                        "Ingresa un correo válido.",
                        "Correo inválido",
                        JOptionPane.WARNING_MESSAGE);
                continue;
            }

            int id = esEdicion ? existente.getId() : 0;

            Estudiante estudiante = new Estudiante(
                    id, nombre, rut, curso, correo);

            boolean guardado = esEdicion
                    ? controlador.actualizarEstudiante(estudiante)
                    : controlador.registrarEstudiante(estudiante);

            if (guardado) {
                JOptionPane.showMessageDialog(
                        this,
                        esEdicion
                                ? "Estudiante actualizado correctamente."
                                : "Estudiante agregado correctamente.");

                cargarEstudiantes();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo guardar el estudiante. "
                                + "Revisa la consola de IntelliJ.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

            return;
        }
    }

    private void eliminarEstudiante() {
        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(
                    this, "Selecciona un estudiante de la tabla.");
            return;
        }

        int id = (int) modeloTabla.getValueAt(fila, 0);

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Quieres eliminar al estudiante seleccionado?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            if (controlador.eliminarEstudiante(id)) {
                JOptionPane.showMessageDialog(
                        this, "Estudiante eliminado correctamente.");
                cargarEstudiantes();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo eliminar al estudiante. "
                                + "Puede tener préstamos asociados.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mostrarError(String mensaje, Exception e) {
        System.err.println(mensaje);
        e.printStackTrace();

        JOptionPane.showMessageDialog(
                this,
                mensaje + "\nRevisa la consola de IntelliJ.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
