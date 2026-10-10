
package controlador;

import dao.PrestamoDAO;
import dao.impl.PrestamoDAOImpl;
import modelo.Prestamo;

import java.time.LocalDate;
import java.util.List;

public class ControladorPrestamos {

    private final PrestamoDAO prestamoDAO;

    public ControladorPrestamos() {
        this.prestamoDAO = new PrestamoDAOImpl();
    }

    public List<Prestamo> listarPrestamos() {
        return prestamoDAO.listar();
    }

    public boolean registrarPrestamo(Prestamo prestamo) {
        if (prestamo == null
                || prestamo.getEstudiante() == null
                || prestamo.getLibro() == null
                || prestamo.getEstudiante().getId() <= 0
                || prestamo.getLibro().getId() <= 0
                || prestamo.getFechaPrestamo() == null
                || prestamo.getFechaDevolucion() == null
                || prestamo.getFechaDevolucion()
                .isBefore(prestamo.getFechaPrestamo())) {
            return false;
        }

        return prestamoDAO.registrarPrestamo(prestamo);
    }

    public boolean registrarDevolucion(int idPrestamo) {
        if (idPrestamo <= 0) {
            return false;
        }

        return prestamoDAO.registrarDevolucion(idPrestamo);
    }

    public List<Prestamo> listarPrestamosActivos() {
        return prestamoDAO.listarPrestamosActivos();
    }


    public List<Prestamo> listarPrestamosActivosPorEstudiante(int idEstudiante) {
        if (idEstudiante <= 0) {
            return List.of();
        }

        return prestamoDAO.listarPorEstudiante(idEstudiante)
                .stream()
                .filter(prestamo -> !prestamo.isDevuelto())
                .toList();
    }


    public List<Prestamo> listarPrestamosAtrasados() {
        return prestamoDAO.listarPrestamosAtrasados();
    }

    public List<Prestamo> listarHistorialEstudiante(int idEstudiante) {
        if (idEstudiante <= 0) {
            return List.of();
        }

        return prestamoDAO.listarPorEstudiante(idEstudiante);
    }

    public LocalDate calcularFechaDevolucion(LocalDate fechaPrestamo) {
        if (fechaPrestamo == null) {
            return null;
        }

        return fechaPrestamo.plusDays(14);
    }


    public int obtenerIdEstudiante(String rut) {
        if (rut == null || rut.trim().isEmpty()) {
            return -1;
        }

        dao.EstudianteDAO estudianteDAO =
                new dao.impl.EstudianteDAOImpl();

        modelo.Estudiante estudiante =
                estudianteDAO.buscarPorRut(rut.trim());

        if (estudiante == null) {
            return -1;
        }

        return estudiante.getId();
    }

}

