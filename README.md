# Administrador de Tareas del Hogar

Una API REST diseñada para la gestión de tareas del hogar. Este repositorio contiene toda la información sobre su funcionamiento, lógica de negocio, endpoints y medidas de seguridad.

## Nombre del Proyecto
Administrador de Tareas del Hogar

## Descripción del Proyecto
La API gestionará las tareas del hogar mediante documentos representados en la base de datos. A continuación, se describen los documentos principales que intervendrán en el proyecto, junto con sus campos:

### Documentos y Campos

#### 1. Usuario
Representa a los usuarios registrados en la plataforma.
- **id** (string, único): Identificador único del usuario.
- **nombre** (string): Nombre del usuario.
- **email** (string, único): Correo electrónico del usuario.
- **contraseña** (string): Contraseña cifrada del usuario.
- **rol** (string): Rol del usuario ("USER" o "ADMIN").

#### 2. Tarea
Representa una tarea asignada dentro del sistema.
- **id** (string, único): Identificador único de la tarea.
- **titulo** (string): Título breve de la tarea.
- **descripcion** (string): Descripción detallada de la tarea.
- **estado** (string): Estado de la tarea ("pendiente", "completada").
- **fechaCreacion** (date): Fecha en la que se creó la tarea.
- **asignadoA** (string): ID del usuario al que se le asignó la tarea.

## Endpoints

### Usuario

1. **POST /usuarios/registro**
   - **Descripción:** Registra un nuevo usuario en la plataforma.
   - **Campos requeridos:** `nombre`, `email`, `contraseña`.
   - **Respuesta:** Código 201 si el registro es exitoso.

2. **POST /usuarios/login**
   - **Descripción:** Permite a un usuario iniciar sesión.
   - **Campos requeridos:** `email`, `contraseña`.
   - **Respuesta:** Código 200 con un token de autenticación.

### Tarea

1. **GET /tareas**
   - **Descripción:** Obtiene todas las tareas (solo accesible para ADMIN).
   - **Respuesta:** Código 200 con la lista de tareas.

2. **GET /tareas/mis-tareas**
   - **Descripción:** Obtiene las tareas asignadas al usuario que realiza la solicitud.
   - **Respuesta:** Código 200 con la lista de tareas del usuario.

3. **POST /tareas**
   - **Descripción:** Crea una nueva tarea.
   - **Campos requeridos:** `titulo`, `descripcion`, `asignadoA` (solo ADMIN puede asignar a otros).
   - **Respuesta:** Código 201 si la creación es exitosa.

4. **PATCH /tareas/:id**
   - **Descripción:** Marca una tarea como completada.
   - **Respuesta:** Código 200 si se actualizó exitosamente.

5. **DELETE /tareas/:id**
   - **Descripción:** Elimina una tarea. Los usuarios solo pueden eliminar sus propias tareas, mientras que los ADMIN pueden eliminar cualquier tarea.
   - **Respuesta:** Código 200 si se elimina correctamente.

## Lógica de Negocio

1. Los usuarios con rol `USER` solo pueden ver, crear, modificar y eliminar sus propias tareas.
2. Los usuarios con rol `ADMIN` pueden ver, crear, modificar y eliminar tareas de cualquier usuario.
3. El estado de una tarea puede ser "pendiente" o "completada".
4. Solo los ADMIN pueden asignar tareas a otros usuarios.

## Excepciones y Códigos de Estado

1. **400 Bad Request:** Se genera cuando faltan campos requeridos o los datos son inválidos.
2. **401 Unauthorized:** Se genera cuando un usuario no autenticado intenta acceder a recursos protegidos.
3. **403 Forbidden:** Se genera cuando un usuario intenta acceder o modificar recursos que no le pertenecen.
4. **404 Not Found:** Se genera cuando no se encuentra un recurso solicitado (por ejemplo, una tarea).
5. **500 Internal Server Error:** Se genera cuando ocurre un error inesperado en el servidor.

## Restricciones de Seguridad

1. **Autenticación:** Uso de tokens JWT para proteger los endpoints. Solo los usuarios autenticados pueden acceder a los recursos.
2. **Autorización:** Verificación de roles para restringir el acceso a ciertos endpoints (por ejemplo, ADMIN para ver todas las tareas).
3. **Cifrado de Contraseñas:** Las contraseñas se almacenan de forma cifrada utilizando algoritmos seguros como bcrypt.
4. **Validación de Datos:** Validación exhaustiva de los datos recibidos en las solicitudes para evitar inyecciones y datos malformados.
