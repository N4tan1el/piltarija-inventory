# PilTarija | Gestión de productos e inventario

Aplicación de escritorio académica desarrollada con Java Swing y MySQL. Reúne pantallas de acceso y registro junto con herramientas para administrar productos e imágenes, y scripts SQL relacionados con estado y stock.

## Tecnologías

- Java 11 · Swing
- Maven
- MySQL Connector/J
- MySQL

## Funciones visibles en el código

- Inicio de sesión y registro de usuarios
- Administración de productos: alta, edición, baja y habilitación
- Carga de imágenes para productos
- Scripts de apoyo para estado y control transaccional de stock

## Requisitos

- JDK 11 o posterior
- Maven
- MySQL con una base de datos `piltarija` y el esquema compatible con los scripts de `database/`

## Configuración de la conexión

Define estas variables de entorno antes de iniciar la aplicación:

```text
PILTARIJA_DB_URL=jdbc:mysql://localhost:3306/piltarija
PILTARIJA_DB_USER=tu_usuario
PILTARIJA_DB_PASSWORD=tu_contraseña
```

No guardes credenciales reales en el repositorio. La conexión exige estas variables; no contiene credenciales predeterminadas.

## Ejecución

1. Crea la base de datos y prepara el esquema requerido por la aplicación.
2. Revisa los scripts SQL en `database/` y ejecútalos solo si el esquema coincide.
3. Ejecuta `mvn package`.
4. Inicia `piltarija.menu` desde tu IDE o con el JAR generado.

> Proyecto académico y demostrativo. La estructura de la base de datos original no está incluida en este repositorio; la configuración puede requerir ajustes según tu entorno.

