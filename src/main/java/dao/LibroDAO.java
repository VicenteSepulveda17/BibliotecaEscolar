
package dao;

import modelo.Libro;
import java.util.List;

public interface LibroDAO {

    List<Libro> listar();

    Libro buscarPorId(int id);

    boolean insertar(Libro libro);

    boolean actualizar(Libro libro);

    boolean eliminar(int id);

    List<Libro> buscarPorTitulo(String titulo);

    boolean actualizarStock(int idLibro, int nuevoStock);
}
