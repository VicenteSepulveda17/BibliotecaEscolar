
package vista;

import controlador.ControladorLibros;
import dao.CategoriaDAO;
import dao.impl.CategoriaDAOImpl;
import modelo.Categoria;
import modelo.Libro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VentanaLibros extends JFrame {

    private final ControladorLibros controlador;
    private final CategoriaDAO categoriaDAO;
    private final DefaultTableModel modeloTabla;
    private final JTable tabla;
    private final JTextField txtBuscar;

    public VentanaLibros() {
        controlador = new ControladorLibros();
        categoriaDAO = new CategoriaDAOImpl();

        setTitle("Biblioteca Escolar - Gestión de libros");
        setSize(1050, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        modeloTabla = new DefaultTableModel(
                new String[]{"ID", "Título", "Autor", "ISBN",
                        "Editorial", "Stock", "Categoría"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        txtBuscar = new JTextField(20);

        construirInterfaz();
        cargarLibros();
    }

    private void construirInterfaz() {
        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel(
                "Gestión de libros", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        principal.add(titulo, BorderLayout.NORTH);

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.add(new JLabel("Buscar por título:"));
        panelBusqueda.add(txtBuscar);

        JButton btnBuscar = new JButton("Buscar");
        JButton btnMostrarTodos = new JButton("Mostrar todos");

        panelBusqueda.add(btnBuscar);
        panelBusqueda.add(btnMostrarTodos);

        JPanel centro = new JPanel(new BorderLayout(5, 5));
        centro.add(panelBusqueda, BorderLayout.NORTH);
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);
        principal.add(centro, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout());

        JButton btnAgregar = new JButton("Agregar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnActualizar = new JButton("Actualizar");

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnActualizar);

        principal.add(panelBotones, BorderLayout.SOUTH);

        btnBuscar.addActionListener(e -> buscarLibros());
        btnMostrarTodos.addActionListener(e -> cargarLibros());
        btnActualizar.addActionListener(e -> cargarLibros());

        btnAgregar.addActionListener(e -> agregarLibro());
        btnEditar.addActionListener(e -> editarLibro());
        btnEliminar.addActionListener(e -> eliminarLibro());

        add(principal);
    }

    private void cargarLibros() {
        try {
            mostrarLibros(controlador.listarLibros());
        } catch (RuntimeException e) {
            mostrarError("No se pudieron cargar los libros.", e);
        }
    }

    private void buscarLibros() {
        try {
            String titulo = txtBuscar.getText().trim();
            mostrarLibros(controlador.buscarPorTitulo(titulo));
        } catch (RuntimeException e) {
            mostrarError("No se pudo realizar la búsqueda.", e);
        }
    }

    private void mostrarLibros(List<Libro> libros) {
        modeloTabla.setRowCount(0);

        for (Libro libro : libros) {
            String categoria = libro.getCategoria() == null
                    ? ""
                    : libro.getCategoria().getNombre();

            modeloTabla.addRow(new Object[]{
                    libro.getId(),
                    libro.getTitulo(),
                    libro.getAutor(),
                    libro.getIsbn(),
                    libro.getEditorial(),
                    libro.getStock(),
                    categoria
            });
        }
    }

    private void agregarLibro() {
        mostrarFormulario(null);
    }

    private void editarLibro() {
        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(
                    this, "Selecciona un libro de la tabla.");
            return;
        }

        int id = (int) modeloTabla.getValueAt(fila, 0);
        Libro libro = controlador.buscarLibro(id);

        if (libro == null) {
            JOptionPane.showMessageDialog(
                    this, "No se encontró el libro seleccionado.");
            cargarLibros();
            return;
        }

        mostrarFormulario(libro);
    }

    private void mostrarFormulario(Libro libroExistente) {
        boolean esEdicion = libroExistente != null;

        JTextField txtTitulo = new JTextField(
                esEdicion ? libroExistente.getTitulo() : "", 20);
        JTextField txtAutor = new JTextField(
                esEdicion ? libroExistente.getAutor() : "", 20);
        JTextField txtISBN = new JTextField(
                esEdicion ? libroExistente.getIsbn() : "", 20);
        JTextField txtEditorial = new JTextField(
                esEdicion ? libroExistente.getEditorial() : "", 20);
        JTextField txtStock = new JTextField(
                esEdicion ? String.valueOf(libroExistente.getStock()) : "0", 20);

        List<Categoria> categorias = categoriaDAO.listar();

        if (categorias.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No hay categorías disponibles en la base de datos.",
                    "Categorías",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<Categoria> cmbCategoria =
                new JComboBox<>(categorias.toArray(new Categoria[0]));

        if (esEdicion && libroExistente.getCategoria() != null) {
            int idCategoria = libroExistente.getCategoria().getId();

            for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
                Categoria categoria = cmbCategoria.getItemAt(i);

                if (categoria.getId() == idCategoria) {
                    cmbCategoria.setSelectedIndex(i);
                    break;
                }
            }
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));

        panel.add(new JLabel("Título:"));
        panel.add(txtTitulo);
        panel.add(new JLabel("Autor:"));
        panel.add(txtAutor);
        panel.add(new JLabel("ISBN:"));
        panel.add(txtISBN);
        panel.add(new JLabel("Editorial:"));
        panel.add(txtEditorial);
        panel.add(new JLabel("Stock:"));
        panel.add(txtStock);
        panel.add(new JLabel("Categoría:"));
        panel.add(cmbCategoria);

        String tituloDialogo = esEdicion
                ? "Editar libro"
                : "Agregar libro";

        while (true) {
            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    panel,
                    tituloDialogo,
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (opcion != JOptionPane.OK_OPTION) {
                return;
            }

            String titulo = txtTitulo.getText().trim();
            String autor = txtAutor.getText().trim();
            String isbn = txtISBN.getText().trim();
            String editorial = txtEditorial.getText().trim();
            String textoStock = txtStock.getText().trim();

            if (titulo.isEmpty() || autor.isEmpty()
                    || isbn.isEmpty() || editorial.isEmpty()
                    || textoStock.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Completa todos los campos.",
                        "Datos incompletos",
                        JOptionPane.WARNING_MESSAGE);
                continue;
            }

            int stock;

            try {
                stock = Integer.parseInt(textoStock);

                if (stock < 0) {
                    JOptionPane.showMessageDialog(
                            this, "El stock no puede ser negativo.");
                    continue;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                        this, "El stock debe ser un número entero.");
                continue;
            }

            Categoria categoriaSeleccionada =
                    (Categoria) cmbCategoria.getSelectedItem();

            if (categoriaSeleccionada == null) {
                JOptionPane.showMessageDialog(
                        this, "Selecciona una categoría.");
                continue;
            }

            int id = esEdicion ? libroExistente.getId() : 0;

            Libro libro = new Libro(
                    id,
                    titulo,
                    autor,
                    isbn,
                    editorial,
                    stock,
                    categoriaSeleccionada);

            boolean resultado = esEdicion
                    ? controlador.actualizarLibro(libro)
                    : controlador.registrarLibro(libro);

            if (resultado) {
                JOptionPane.showMessageDialog(
                        this,
                        esEdicion
                                ? "Libro actualizado correctamente."
                                : "Libro agregado correctamente.");

                cargarLibros();
                return;
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo guardar el libro. Revisa la consola "
                                + "de IntelliJ para ver el error de MySQL.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }

    private void eliminarLibro() {
        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(
                    this, "Selecciona un libro de la tabla.");
            return;
        }

        int id = (int) modeloTabla.getValueAt(fila, 0);

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Quieres eliminar el libro seleccionado?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            if (controlador.eliminarLibro(id)) {
                JOptionPane.showMessageDialog(
                        this, "Libro eliminado correctamente.");
                cargarLibros();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo eliminar el libro. Puede tener "
                                + "préstamos asociados.",
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
