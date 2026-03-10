TEST PLAN - SpringMVC

Objetivo
- Asegurar calidad funcional y de seguridad en el flujo de peliculas, autenticacion y perfil.

Alcance
- Backend: controladores, servicios, repositorios.
- Frontend: validaciones basicas de vistas y flujos (manual).
- Seguridad: login, roles, accesos a endpoints.

Tipos de prueba
- Unitarias: servicios y controladores con mocks.
- Integracion (Spring Boot Test + MockMvc): endpoints, seguridad y vistas.
- Manuales UI: navegacion, paginacion, ordenacion, formularios.

Datos de prueba
- Usuarios: ADMIN, EDITOR, VIEWER, usuario inactivo.
- Peliculas: minimo 20 registros con generos e interpretes variados.

Casos principales
1. Autenticacion
- Login correcto/incorrecto.
- Usuario inactivo no puede iniciar sesion.
- Logout limpia sesion.

2. Registro
- Registro exitoso crea usuario y asigna VIEWER.
- Usuario duplicado por username/email muestra error.
- Validaciones obligatorias en formulario.

3. Peliculas (CRUD)
- Listado con paginacion, busqueda y orden.
- Crear, editar, eliminar segun rol.
- Validaciones de formulario.

4. Perfil
- Ver perfil de usuario autenticado.
- Actualizar nombre/email con validaciones.

5. Seguridad
- VIEWER solo lectura.
- EDITOR/ADMIN pueden crear/editar/borrar.
- Acceso anonimo redirige a /login.

Cobertura objetivo
- Servicios: 80%+.
- Controladores: rutas principales con flujos OK/ERROR.

Integracion en tests
- Unitarias: LoginControllerTest, PeliculaControllerTest, UsuarioServiceTest, PeliculaServiceTest.
- Integracion MockMvc: MvcSecurityIntegrationTest (login, registro, peliculas, perfil, roles).
