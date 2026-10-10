package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca";
    private static final String USER = "root";
    private static final String PASSWORD = "Vicente124.";

    private static DatabaseConnection instancia;
    private Connection conexion;

    private DatabaseConnection() {
        try {
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión a la base de datos exitosa.");
        } catch (SQLException e) {
            System.out.println("Error al conectar con la base de datos: " + e.getMessage());
        }
    }

    public static synchronized DatabaseConnection getInstancia() {
        if (instancia == null) {
            instancia = new DatabaseConnection();
        }
        return instancia;
    }

    public synchronized Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()
                    || !conexion.isValid(2)) {

                conexion = DriverManager.getConnection(
                        URL, USER, PASSWORD
                );
            }
        } catch (SQLException e) {
            System.err.println(
                    "Error al recuperar la conexión: " + e.getMessage()
            );
            conexion = null;
        }

        return conexion;
    }
}
