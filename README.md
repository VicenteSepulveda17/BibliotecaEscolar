# Biblioteca Escolar

Aplicación de escritorio desarrollada en Java para gestionar una biblioteca escolar. Permite administrar libros y estudiantes, registrar préstamos y devoluciones, y consultar reportes mediante una interfaz gráfica conectada a una base de datos MySQL.

## Funcionalidades

- Inicio de sesión con diferenciación de roles: bibliotecario y estudiante.
- Gestión de libros: registro, consulta, edición y eliminación.
- Gestión de estudiantes: registro, consulta, edición y eliminación.
- Registro de préstamos con validación de stock disponible.
- Registro de devoluciones y actualización del stock.
- Visualización de préstamos activos y atrasados.
- Consulta de reportes de la biblioteca.
- Filtrado de préstamos según el estudiante que inició sesión.
- Control de concurrencia para evitar registrar dos préstamos del último ejemplar disponible.

## Tecnologías utilizadas

- Java
- Java Swing para la interfaz gráfica.
- MySQL para la persistencia de datos.
- JDBC para la conexión con la base de datos.
- IntelliJ IDEA como entorno de desarrollo.

## Arquitectura

El proyecto utiliza una organización basada en el patrón MVC y el patrón DAO.

- **modelo:** clases que representan usuarios, estudiantes, libros, categorías y préstamos.
- **vista:** ventanas de la interfaz gráfica.
- **controlador:** conecta las acciones de la interfaz con la lógica de la aplicación.
- **dao:** interfaces para las operaciones de acceso a datos.
- **dao.impl:** implementaciones de las operaciones de acceso a datos.
- **conexion:** administración de la conexión con MySQL mediante Singleton.

## Base de datos

La aplicación utiliza una base de datos MySQL llamada `biblioteca`.

Para ejecutar el proyecto, se debe crear y poblar la base de datos con los scripts SQL correspondientes. La estructura debe incluir las tablas de usuarios, estudiantes, categorías, libros y préstamos.

Configura la conexión en `conexion/DatabaseConnection.java` según las credenciales de MySQL de tu entorno.

## Ejecución

1. Abrir el proyecto en IntelliJ IDEA.
2. Configurar la conexión con MySQL.
3. Verificar que la base de datos `biblioteca` esté creada y poblada.
4. Comprobar que el conector JDBC de MySQL esté disponible en el proyecto.
5. Ejecutar la clase `Main.java`.

## Pruebas realizadas

- Inicio de sesión y permisos según el rol.
- Registro y gestión de libros y estudiantes.
- Registro de préstamos y actualización del stock.
- Registro de devoluciones y restauración del stock.
- Visualización de préstamos activos y atrasados.
- Prueba de concurrencia con dos intentos simultáneos de solicitar el último ejemplar disponible.

## Autor

Proyecto académico desarrollado para la evaluación de programación.
