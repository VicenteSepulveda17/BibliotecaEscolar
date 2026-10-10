package dao;

import modelo.Estudiante;
import java.util.List;

public interface EstudianteDAO {

    Estudiante buscarPorRut(String rut);

    List<Estudiante> listar();

    Estudiante buscarPorId(int id);

    boolean insertar(Estudiante estudiante);

    boolean actualizar(Estudiante estudiante);

    boolean eliminar(int id);
}

