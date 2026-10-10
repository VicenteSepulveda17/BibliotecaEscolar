
package controlador;

import modelo.Usuario;

public class ControladorPrincipal {

    private Usuario usuarioActual;

    public ControladorPrincipal(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public void setUsuarioActual(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
    }

    public boolean puedeGestionarLibros() {
        return esBibliotecario();
    }

    public boolean puedeGestionarEstudiantes() {
        return esBibliotecario();
    }

    public boolean puedeGestionarUsuarios() {
        return esBibliotecario();
    }

    public boolean puedeRegistrarPrestamos() {
        return esBibliotecario() || esEstudiante();
    }

    public boolean puedeRegistrarDevoluciones() {
        return esBibliotecario() || esEstudiante();
    }

    public boolean puedeVerReportes() {
        return esBibliotecario();
    }

    private boolean esBibliotecario() {
        return usuarioActual != null
                && usuarioActual.getRol() != null
                && usuarioActual.getRol().equalsIgnoreCase("bibliotecario");
    }

    private boolean esEstudiante() {
        return usuarioActual != null
                && usuarioActual.getRol() != null
                && usuarioActual.getRol().equalsIgnoreCase("estudiante");
    }
}

