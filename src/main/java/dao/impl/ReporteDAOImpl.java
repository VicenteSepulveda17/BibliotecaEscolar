
package dao.impl;

import conexion.DatabaseConnection;
import dao.ReporteDAO;
import modelo.Estudiante;
import modelo.Libro;
import modelo.Prestamo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAOImpl implements ReporteDAO {

    private Connection obtenerConexion() {
        return DatabaseConnection.getInstancia().getConexion();
    }

    private String consultaBase() {
        return "SELECT p.id, p.id_estudiante, p.id_libro, " +
                "p.fecha_prestamo, p.fecha_devolucion, p.devuelto, " +
                "e.nombre AS nombre_estudiante, e.rut AS rut_estudiante, " +
                "e.curso, e.correo AS correo_estudiante, " +
                "l.titulo, l.autor, l.isbn, l.editorial, l.stock " +
                "FROM prestamos p " +
                "JOIN estudiantes e ON p.id_estudiante = e.id " +
                "JOIN libros l ON p.id_libro = l.id ";
    }

    private Prestamo crearPrestamo(ResultSet rs) throws SQLException {
        Estudiante estudiante = new Estudiante(
                rs.getInt("id_estudiante"),
                rs.getString("nombre_estudiante"),
                rs.getString("rut_estudiante"),
                rs.getString("curso"),
                rs.getString("correo_estudiante")
        );

        Libro libro = new Libro(
                rs.getInt("id_libro"),
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getString("isbn"),
                rs.getString("editorial"),
                rs.getInt("stock"),
                null
        );

        Date fechaPrestamo = rs.getDate("fecha_prestamo");
        Date fechaDevolucion = rs.getDate("fecha_devolucion");

        return new Prestamo(
                rs.getInt("id"),
                estudiante,
                libro,
                fechaPrestamo == null ? null : fechaPrestamo.toLocalDate(),
                fechaDevolucion == null ? null : fechaDevolucion.toLocalDate(),
                rs.getBoolean("devuelto")
        );
    }


    @Override
    public List<Prestamo> obtenerLibrosMasPrestados() {
        List<Prestamo> resultados = new ArrayList<>();

        String sql = consultaBase()
                + "JOIN (SELECT id_libro, COUNT(*) AS total "
                + "FROM prestamos GROUP BY id_libro) resumen "
                + "ON resumen.id_libro = p.id_libro "
                + "ORDER BY resumen.total DESC, l.titulo ASC";

        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resultados.add(crearPrestamo(rs));
            }

        } catch (SQLException | NullPointerException e) {
            System.err.println(
                    "Error al obtener libros más prestados: " + e.getMessage());
        }

        return resultados;
    }


    @Override
    public List<Prestamo> obtenerPrestamosActivos() {
        List<Prestamo> resultados = new ArrayList<>();

        String sql = consultaBase() +
                "WHERE p.devuelto = FALSE " +
                "ORDER BY p.fecha_devolucion";

        try (Connection conexion = obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resultados.add(crearPrestamo(rs));
            }

        } catch (SQLException | NullPointerException e) {
            System.err.println("Error al obtener préstamos activos: "
                    + e.getMessage());
        }

        return resultados;
    }

    @Override
    public List<Prestamo> obtenerHistorialEstudiante(int idEstudiante) {
        List<Prestamo> resultados = new ArrayList<>();

        String sql = consultaBase() +
                "WHERE p.id_estudiante = ? " +
                "ORDER BY p.fecha_prestamo DESC";

        try (Connection conexion = obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultados.add(crearPrestamo(rs));
                }
            }

        } catch (SQLException | NullPointerException e) {
            System.err.println("Error al obtener historial del estudiante: "
                    + e.getMessage());
        }

        return resultados;
    }
}
