
package controlador;

import dao.ReporteDAO;
import dao.impl.ReporteDAOImpl;
import modelo.Prestamo;

import java.util.List;

public class ControladorReportes {

    private final ReporteDAO reporteDAO;

    public ControladorReportes() {
        this.reporteDAO = new ReporteDAOImpl();
    }

    public List<Prestamo> obtenerLibrosMasPrestados() {
        return reporteDAO.obtenerLibrosMasPrestados();
    }

    public List<Prestamo> obtenerPrestamosActivos() {
        return reporteDAO.obtenerPrestamosActivos();
    }

    public List<Prestamo> obtenerHistorialEstudiante(int idEstudiante) {
        if (idEstudiante <= 0) {
            return List.of();
        }

        return reporteDAO.obtenerHistorialEstudiante(idEstudiante);
    }
}

