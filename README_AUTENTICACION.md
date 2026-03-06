# Sistema de Login con Control de Acceso por Roles

## Descripción

Se ha implementado un sistema completo de autenticación y autorización con Spring Security que permite a los usuarios iniciar sesión y acceder a la aplicación según sus roles asignados.

## Características Principales

### 1. **Autenticación de Usuarios**
- Login seguro con contraseinas encriptadas usando BCrypt
- Registro de nuevos usuarios
- Gestión de sesiones

### 2. **Control de Acceso Basado en Roles (RBAC)**

El sistema implementa 3 roles principales:

| Rol | Descripción | Permisos |
|-----|-------------|----------|
| **VIEWER** | Usuario solo lectura | Ver películas |
| **EDITOR** | Puede ver y editar | Ver, crear, editar y eliminar películas |
| **ADMIN** | Control total | Acceso completo al sistema |

### 3. **Seguridad**
- Contraseñas encriptadas con BCrypt
- Protección CSRF (excepto en desarrollo)
- Restricción de acceso por anotaciones `@PreAuthorize`
- Validación de datos con Hibernate Validator

## Usuarios de Prueba

La aplicación incluye 3 usuarios de prueba precargados:

### Usuario VIEWER
```
Username: viewer
Contraseña: viewer123
Email: viewer@movies.com
Rol: VIEWER (Solo lectura)
```

### Usuario EDITOR
```
Username: editor
Contraseña: editor123
Email: editor@movies.com
Rol: EDITOR (Lectura y modificación)
```

### Usuario ADMIN
```
Username: admin
Contraseña: admin123
Email: admin@movies.com
Rol: ADMIN (Administrador)
```

## Estructura de Datos

### Entidades Creadas

#### 1. **Usuario** (`Usuario.java`)
```java
- id: Integer (PK)
- username: String (Único)
- password: String (Encriptada)
- nombre: String
- email: String (Único)
- activo: Boolean
- roles: Set<Rol> (Relación Many-to-Many)
```

#### 2. **Rol** (`Rol.java`)
```java
- id: Integer (PK)
- nombre: String (Único)
- descripcion: String
- usuarios: Set<Usuario> (Relación Many-to-Many)
```

### Tablas de Base de Datos

- `t_usuarios` - Tabla de usuarios
- `t_roles` - Tabla de roles
- `t_usuario_roles` - Tabla de asociación many-to-many

## Flujo de Acceso

### 1. **Página de Login** (`/login`)
- Formulario de autenticación
- Opción para registrarse
- Información sobre los roles disponibles

### 2. **Registro** (`/registro`)
- Registro de nuevos usuarios
- Validación de datos
- Los nuevos usuarios reciben rol VIEWER por defecto

### 3. **Películas** (`/peliculas`)
- Todos los usuarios autenticados pueden ver películas
- Solo EDITOR y ADMIN pueden crear, editar y eliminar películas
- VIEWER solo puede ver

### 4. **Perfil** (`/perfil`)
- Ver información del usuario
- Ver roles asignados
- Editar nombre y email

## Anotaciones de Seguridad Utilizadas

### `@PreAuthorize` en Controladores

```java
// Solo administratores
@PreAuthorize("hasRole('ADMIN')")

// Editores o Administradores
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")

// Cualquier usuario autenticado
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
```

## Configuración de Seguridad

### Rutas Públicas (sin autenticación):
- `/` - Inicio (redirecciona a login o películas)
- `/login` - Página de login
- `/registro` - Página de registro
- `/css/**`, `/js/**` - Recursos estáticos

### Rutas Protegidas:
- `/peliculas` - Requiere autenticación (VIEWER+)
- `/peliculas/nuevo` - Requiere ADMIN o EDITOR
- `/peliculas/editar/{id}` - Requiere ADMIN o EDITOR
- `/peliculas/guardar` - Requiere ADMIN o EDITOR
- `/peliculas/borrar/{id}` - Requiere ADMIN o EDITOR
- `/perfil` - Requiere autenticación

## Archivos Modificados/Creados

### Nuevos Archivos:
1. **Entidades:**
   - `src/main/java/com/hibernate/entity/Usuario.java`
   - `src/main/java/com/hibernate/entity/Rol.java`

2. **Repositorios:**
   - `src/main/java/com/hibernate/repository/IUsuarioRepository.java`
   - `src/main/java/com/hibernate/repository/IRolRepository.java`

3. **Servicios:**
   - `src/main/java/com/spring/mvc/service/UsuarioService.java`

4. **Configuración:**
   - `src/main/java/com/spring/mvc/config/SecurityConfig.java`

5. **Controladores:**
   - `src/main/java/com/spring/mvc/controller/LoginController.java`

6. **Vistas:**
   - `src/main/resources/templates/login.html`
   - `src/main/resources/templates/registro.html`
   - `src/main/resources/templates/perfil.html`

### Archivos Modificados:
1. `pom.xml` - Agregadas dependencias de Spring Security
2. `PeliculaController.java` - Añadidas anotaciones `@PreAuthorize`
3. `DataInitializer.java` - Inicialización de roles y usuarios
4. `peliculas.html` - Interfaz mejorada con seguridad

## Dependencias Agregadas

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- Thymeleaf Security Integration -->
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity6</artifactId>
</dependency>
```

## Cómo Usar

### 1. **Compilar del Proyecto**
```bash
mvn clean install
```

### 2. **Ejecutar la Aplicación**
```bash
mvn spring-boot:run
```

### 3. **Acceder a la Aplicación**
```
http://localhost:8080
```

### 4. **Login con un Usuario de Prueba**
- Ingresa al formulario de login
- Usa las credenciales de es de los usuarios de prueba
- Se redirigirá al catálogo de películas

### 5. **Probar Diferentes Roles**
- **VIEWER**: Solo puede ver películas
- **EDITOR**: Puede crear, editar y eliminar películas
- **ADMIN**: Control total

## Crear Nuevos Usuarios

### Vía Interfaz
1. Accede a `/registro`
2. Completa el formulario
3. Se asignará automáticamente el rol VIEWER
4. Los administradores pueden asignar otros roles mediante base de datos

### Vía Base de Datos
```sql
-- Insertar nuevo usuario
INSERT INTO t_usuarios (username, password, nombre, email, activo)
VALUES ('nuevouser', '$2a\$10\$...bcrypt_password...', 'Nuevo Usuario', 'nuevo@email.com', true);

-- Asignar rol
INSERT INTO t_usuario_roles (usuario_id, rol_id)
SELECT u.id, r.id FROM t_usuarios u, t_roles r
WHERE u.username = 'nuevouser' AND r.nombre = 'EDITOR';
```

## Seguridad en Producción

**IMPORTANTE:** Para producción, realiza estos cambios en `SecurityConfig.java`:

1. **Habilitar CSRF:**
```java
// En SecurityConfig
.csrf(csrf -> csrf.enable()) // Cambiar a enable()
```

2. **HTTPS obligatorio:**
```java
.requiresChannel(channel -> channel.anyRequest().requiresSecure())
```

3. **Headers de seguridad:**
```java
.headers(headers -> headers
    .httpStrictTransportSecurity()
    .xssProtection()
    .frameOptions().deny()
)
```

## Personalización

### Agregar Nuevos Roles

En `DataInitializer.java`:
```java
Rol nuevoRol = rolRepository.save(new Rol("NUEVO_ROL", "Descripción"));
```

### Cambiar Permisos por Rol

En `SecurityConfig.java`:
```java
.requestMatchers("/ruta/**").hasAnyRole("NUEVO_ROL", ...)
```

## Resolución de Problemas

### Error: "Access Denied"
- Verifica que el usuario tiene el rol correcto
- Comprueba los permisos en `SecurityConfig.java`

### Contraseña no funciona
- Las contraseñas están encriptadas con BCrypt
- No pueden compararse directamente con la BD

### Usuario no elimina
- Verifica referencia de claves foráneas
- Comprueba los permisos de eliminación

## Soporte

Para más información sobre Spring Security:
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/index.html)
