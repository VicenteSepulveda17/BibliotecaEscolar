
package controlador;

import dao.EstudianteDAO;
import dao.impl.EstudianteDAOImpl;
import modelo.Estudiante;

import java.util.List;

public class ControladorEstudiantes {

    private final EstudianteDAO estudianteDAO;

    public ControladorEstudiantes() {
        this.estudianteDAO = new EstudianteDAOImpl();
    }

    public List<Estudiante> listarEstudiantes() {
        return estudianteDAO.listar();
    }

    public Estudiante buscarEstudiante(int id) {
        if (id <= 0) {
            return null;
        }

        return estudianteDAO.buscarPorId(id);
    }

    public boolean registrarEstudiante(Estudiante estudiante) {
        if (!estudianteValido(estudiante)) {
            return false;
        }

        return estudianteDAO.insertar(estudiante);
    }

    public boolean actualizarEstudiante(Estudiante estudiante) {
        if (estudiante == null || estudiante.getId() <= 0
                || !estudianteValido(estudiante)) {
            return false;
        }

        return estudianteDAO.actualizar(estudiante);
    }

    public boolean eliminarEstudiante(int id) {
        if (id <= 0) {
            return false;
        }

        return estudianteDAO.eliminar(id);
    }

    private boolean estudianteValido(Estudiante estudiante) {
        return estudiante != null
                && estudiante.getNombre() != null
                && !estudiante.getNombre().trim().isEmpty()
                && estudiante.getRut() != null
                && !estudiante.getRut().trim().isEmpty()
                && estudiante.getCurso() != null
                && !estudiante.getCurso().trim().isEmpty()
                && estudiante.getCorreo() != null
                && !estudiante.getCorreo().trim().isEmpty();
    }
}
