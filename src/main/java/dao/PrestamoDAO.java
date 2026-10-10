
package dao;

import modelo.Prestamo;
import java.util.List;

public interface PrestamoDAO {

    List<Prestamo> listar();

    Prestamo buscarPorId(int id);

    boolean registrarPrestamo(Prestamo prestamo);

    boolean registrarDevolucion(int idPrestamo);

    List<Prestamo> listarPorEstudiante(int idEstudiante);

    List<Prestamo> listarPrestamosActivos();

    List<Prestamo> listarPrestamosAtrasados();
}

