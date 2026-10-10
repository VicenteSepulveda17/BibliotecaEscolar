
package dao.impl;

import conexion.DatabaseConnection;
import dao.PrestamoDAO;
import modelo.Estudiante;
import modelo.Libro;
import modelo.Prestamo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAOImpl implements PrestamoDAO {

    private static final Object BLOQUEO_STOCK = new Object();

    private Connection obtenerConexion() {
        return DatabaseConnection.getInstancia().getConexion();
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
                fechaPrestamo != null ? fechaPrestamo.toLocalDate() : null,
                fechaDevolucion != null ? fechaDevolucion.toLocalDate() : null,
                rs.getBoolean("devuelto")
        );
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

    @Override
    public List<Prestamo> listar() {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = consultaBase() + "ORDER BY p.id DESC";

        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                prestamos.add(crearPrestamo(rs));
            }
        } catch (SQLException | NullPointerException e) {
            System.err.println("Error al listar préstamos: " + e.getMessage());
        }

        return prestamos;
    }

    @Override
    public Prestamo buscarPorId(int id) {
        String sql = consultaBase() + "WHERE p.id = ?";

        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return crearPrestamo(rs);
                }
            }
        } catch (SQLException | NullPointerException e) {
            System.err.println("Error al buscar préstamo: " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean registrarPrestamo(Prestamo prestamo) {
        synchronized (BLOQUEO_STOCK) {

            Connection conexion = obtenerConexion();


            if (conexion == null || prestamo == null
                    || prestamo.getEstudiante() == null
                    || prestamo.getLibro() == null) {
                return false;
            }

            String consultarStock =
                    "SELECT stock FROM libros WHERE id = ? FOR UPDATE";

            String insertarPrestamo =
                    "INSERT INTO prestamos " +
                            "(id_estudiante, id_libro, fecha_prestamo, fecha_devolucion, devuelto) " +
                            "VALUES (?, ?, ?, ?, ?)";

            try {
                conexion.setAutoCommit(false);

                try (PreparedStatement psStock =
                             conexion.prepareStatement(consultarStock)) {

                    psStock.setInt(1, prestamo.getLibro().getId());

                    try (ResultSet rs = psStock.executeQuery()) {
                        if (!rs.next() || rs.getInt("stock") <= 0) {
                            conexion.rollback();
                            return false;
                        }
                    }
                }

                String descontarStock =
                        "UPDATE libros SET stock = stock - 1 WHERE id = ? AND stock > 0";

                try (PreparedStatement ps =
                             conexion.prepareStatement(descontarStock)) {
                    ps.setInt(1, prestamo.getLibro().getId());

                    if (ps.executeUpdate() == 0) {
                        conexion.rollback();
                        return false;
                    }
                }

                try (PreparedStatement ps =
                             conexion.prepareStatement(insertarPrestamo)) {

                    ps.setInt(1, prestamo.getEstudiante().getId());
                    ps.setInt(2, prestamo.getLibro().getId());
                    ps.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
                    ps.setDate(4, Date.valueOf(prestamo.getFechaDevolucion()));
                    ps.setBoolean(5, false);

                    ps.executeUpdate();
                }

                conexion.commit();
                return true;

            } catch (SQLException | NullPointerException e) {
                try {
                    conexion.rollback();
                } catch (SQLException errorRollback) {
                    System.err.println("Error al deshacer préstamo: "
                            + errorRollback.getMessage());
                }

                System.err.println("Error al registrar préstamo: " + e.getMessage());
                return false;

            } finally {
                try {
                    conexion.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Error al restaurar conexión: "
                            + e.getMessage());
                }
            }
        }
    }

    @Override
    public boolean registrarDevolucion(int idPrestamo) {

            synchronized (BLOQUEO_STOCK) {

                Connection conexion = obtenerConexion();

                if (conexion == null) {
                    return false;
                }

                try {
                    conexion.setAutoCommit(false);

                    int idLibro;

                    String consultar =
                            "SELECT id_libro, devuelto FROM prestamos WHERE id = ? FOR UPDATE";

                    try (PreparedStatement ps = conexion.prepareStatement(consultar)) {
                        ps.setInt(1, idPrestamo);

                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next() || rs.getBoolean("devuelto")) {
                                conexion.rollback();
                                return false;
                            }

                            idLibro = rs.getInt("id_libro");
                        }
                    }

                    try (PreparedStatement ps = conexion.prepareStatement(
                            "UPDATE prestamos SET devuelto = TRUE WHERE id = ?")) {
                        ps.setInt(1, idPrestamo);
                        ps.executeUpdate();
                    }

                    try (PreparedStatement ps = conexion.prepareStatement(
                            "UPDATE libros SET stock = stock + 1 WHERE id = ?")) {
                        ps.setInt(1, idLibro);
                        ps.executeUpdate();
                    }

                    conexion.commit();
                    return true;

                } catch (SQLException e) {
                    try {
                        conexion.rollback();
                    } catch (SQLException errorRollback) {
                        System.err.println("Error al deshacer devolución: "
                                + errorRollback.getMessage());
                    }

                    System.err.println("Error al registrar devolución: " + e.getMessage());
                    return false;

                } finally {
                    try {
                        conexion.setAutoCommit(true);
                    } catch (SQLException e) {
                        System.err.println("Error al restaurar conexión: "
                                + e.getMessage());
                    }
                }
            }
    }

    @Override
    public List<Prestamo> listarPorEstudiante(int idEstudiante) {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = consultaBase() + "WHERE p.id_estudiante = ? ORDER BY p.id DESC";

        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    prestamos.add(crearPrestamo(rs));
                }
            }
        } catch (SQLException | NullPointerException e) {
            System.err.println("Error al consultar historial: " + e.getMessage());
        }

        return prestamos;
    }

    @Override
    public List<Prestamo> listarPrestamosActivos() {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = consultaBase()
                + "WHERE p.devuelto = FALSE ORDER BY p.fecha_devolucion";

        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                prestamos.add(crearPrestamo(rs));
            }
        } catch (SQLException | NullPointerException e) {
            System.err.println("Error al listar préstamos activos: "
                    + e.getMessage());
        }

        return prestamos;
    }

    @Override
    public List<Prestamo> listarPrestamosAtrasados() {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = consultaBase()
                + "WHERE p.devuelto = FALSE AND p.fecha_devolucion < ? "
                + "ORDER BY p.fecha_devolucion";

        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(LocalDate.now()));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    prestamos.add(crearPrestamo(rs));
                }
            }
        } catch (SQLException | NullPointerException e) {
            System.err.println("Error al listar préstamos atrasados: "
                    + e.getMessage());
        }

        return prestamos;
    }
}

