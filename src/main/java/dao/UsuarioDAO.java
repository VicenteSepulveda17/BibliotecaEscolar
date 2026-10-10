
package dao;

import modelo.Usuario;
import java.util.List;

public interface UsuarioDAO {

    Usuario autenticar(String correo, String contrasena);

    List<Usuario> listar();

    boolean insertar(Usuario usuario);

    boolean actualizar(Usuario usuario);

    boolean eliminar(int id);
}

