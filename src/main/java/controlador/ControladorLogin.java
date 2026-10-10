
package controlador;

import dao.UsuarioDAO;
import dao.impl.UsuarioDAOImpl;
import modelo.Usuario;

public class ControladorLogin {

    private final UsuarioDAO usuarioDAO;

    public ControladorLogin() {
        this.usuarioDAO = new UsuarioDAOImpl();
    }

    public Usuario iniciarSesion(String correo, String contrasena) {
        if (correo == null || correo.trim().isEmpty()
                || contrasena == null || contrasena.isEmpty()) {
            return null;
        }

        return usuarioDAO.autenticar(correo.trim(), contrasena);
    }

    public boolean esBibliotecario(Usuario usuario) {
        return usuario != null
                && usuario.getRol() != null
                && usuario.getRol().equalsIgnoreCase("bibliotecario");
    }

    public boolean esEstudiante(Usuario usuario) {
        return usuario != null
                && usuario.getRol() != null
                && usuario.getRol().equalsIgnoreCase("estudiante");
    }
}

