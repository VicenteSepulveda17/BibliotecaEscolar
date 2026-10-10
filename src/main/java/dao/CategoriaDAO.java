
package dao;

import modelo.Categoria;
import java.util.List;

public interface CategoriaDAO {

    List<Categoria> listar();

    Categoria buscarPorId(int id);

    boolean insertar(Categoria categoria);

    boolean actualizar(Categoria categoria);

    boolean eliminar(int id);
}

