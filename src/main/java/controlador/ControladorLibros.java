
package controlador;

import dao.LibroDAO;
import dao.impl.LibroDAOImpl;
import modelo.Libro;

import java.util.List;

public class ControladorLibros {

    private final LibroDAO libroDAO;

    public ControladorLibros() {
        this.libroDAO = new LibroDAOImpl();
    }

    public List<Libro> listarLibros() {
        return libroDAO.listar();
    }

    public Libro buscarLibro(int id) {
        if (id <= 0) {
            return null;
        }

        return libroDAO.buscarPorId(id);
    }

    public List<Libro> buscarPorTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            return libroDAO.listar();
        }

        return libroDAO.buscarPorTitulo(titulo.trim());
    }

    public boolean registrarLibro(Libro libro) {
        if (!libroValido(libro)) {
            return false;
        }

        return libroDAO.insertar(libro);
    }

    public boolean actualizarLibro(Libro libro) {
        if (libro == null || libro.getId() <= 0 || !libroValido(libro)) {
            return false;
        }

        return libroDAO.actualizar(libro);
    }

    public boolean eliminarLibro(int id) {
        if (id <= 0) {
            return false;
        }

        return libroDAO.eliminar(id);
    }

    public boolean actualizarStock(int idLibro, int nuevoStock) {
        if (idLibro <= 0 || nuevoStock < 0) {
            return false;
        }

        return libroDAO.actualizarStock(idLibro, nuevoStock);
    }

    private boolean libroValido(Libro libro) {
        return libro != null
                && libro.getTitulo() != null
                && !libro.getTitulo().trim().isEmpty()
                && libro.getAutor() != null
                && !libro.getAutor().trim().isEmpty()
                && libro.getIsbn() != null
                && !libro.getIsbn().trim().isEmpty()
                && libro.getEditorial() != null
                && !libro.getEditorial().trim().isEmpty()
                && libro.getStock() >= 0
                && libro.getCategoria() != null
                && libro.getCategoria().getId() > 0;
    }
}

