package dao;

import modelo.Prestamo;
import java.util.List;

public interface ReporteDAO {

    List<Prestamo> obtenerLibrosMasPrestados();
    List<Prestamo> obtenerPrestamosActivos();
    List<Prestamo> obtenerHistorialEstudiante(int idEstudiante);

}
