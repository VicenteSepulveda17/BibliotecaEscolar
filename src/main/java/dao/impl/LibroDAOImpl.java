
package dao.impl;

import conexion.DatabaseConnection;
import dao.LibroDAO;
import modelo.Categoria;
import modelo.Libro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LibroDAOImpl implements LibroDAO {

    private Connection obtenerConexion() {
        return DatabaseConnection.getInstancia().getConexion();
    }

    private Libro crearLibro(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria(
                rs.getInt("id_categoria"),
                rs.getString("nombre_categoria")
        );

        return new Libro(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getString("isbn"),
                rs.getString("editorial"),
                rs.getInt("stock"),
                categoria
        );
    }

    @Override
    public List<Libro> listar() {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT l.*, c.nombre AS nombre_categoria "
                + "FROM libros l "
                + "INNER JOIN categorias c ON l.id_categoria = c.id";

        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                libros.add(crearLibro(rs));
            }
        } catch (SQLException | NullPointerException e) {
            System.err.println("Error al listar libros: " + e.getMessage());
        }

        return libros;
    }

    @Override
    public Libro buscarPorId(int id) {
        String sql = "SELECT l.*, c.nombre AS nombre_categoria "
                + "FROM libros l "
                + "INNER JOIN categorias c ON l.id_categoria = c.id "
                + "WHERE l.id = ?";

        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return crearLibro(rs);
                }
            }
        } catch (SQLException | NullPointerException e) {
            System.err.println("Error al buscar libro: " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean insertar(Libro libro) {
        String sql = "INSERT INTO libros (titulo, autor, isbn, editorial, stock, id_categoria) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setString(4, libro.getEditorial());
            ps.setInt(5, libro.getStock());
            ps.setInt(6, libro.getCategoria().getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException | NullPointerException e) {
            System.err.println("Error al insertar libro: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Libro libro) {
        String sql = "UPDATE libros SET titulo = ?, autor = ?, isbn = ?, editorial = ?, stock = ?, id_categoria = ? WHERE id = ?";

        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setString(4, libro.getEditorial());
            ps.setInt(5, libro.getStock());
            ps.setInt(6, libro.getCategoria().getId());
            ps.setInt(7, libro.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException | NullPointerException e) {
            System.err.println("Error al actualizar libro: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM libros WHERE id = ?";

        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException | NullPointerException e) {
            System.err.println("Error al eliminar libro: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Libro> buscarPorTitulo(String titulo) {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT l.*, c.nombre AS nombre_categoria "
                + "FROM libros l "
                + "INNER JOIN categorias c ON l.id_categoria = c.id "
                + "WHERE l.titulo LIKE ?";

        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, "%" + titulo + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    libros.add(crearLibro(rs));
                }
            }
        } catch (SQLException | NullPointerException e) {
            System.err.println("Error al buscar libros por título: " + e.getMessage());
        }

        return libros;
    }

    @Override
    public boolean actualizarStock(int idLibro, int nuevoStock) {
        if (nuevoStock < 0) {
            return false;
        }

        String sql = "UPDATE libros SET stock = ? WHERE id = ?";

        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setInt(1, nuevoStock);
            ps.setInt(2, idLibro);

            return ps.executeUpdate() > 0;
        } catch (SQLException | NullPointerException e) {
            System.err.println("Error al actualizar stock: " + e.getMessage());
            return false;
        }
    }
}

