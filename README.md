LevelUpGamer es una aplicación nativa de Android desarrollada en Kotlin bajo la arquitectura MVVM. La aplicación permite a los usuarios descubrir videojuegos, consultar noticias del sector, y gestionar su perfil de usuario con persistencia de datos local y remota.

Integrantes del Equipo:

[Juan Pablo Rivera] 

[Olov Juengt]

[Misael Diaz]

Funcionalidades Principales

1. Gestión de Usuarios 

Registro e Inicio de Sesión: Validación de credenciales contra base de datos local (SQLite/Room).

Perfil de Usuario: Visualización y edición de datos del perfil (nombre, correo, avatar) con actualización reactiva en la UI.

2. Catálogo de Videojuegos

Búsqueda y Exploración: Consumo de API externa para listar juegos con imágenes y descripciones.

Persistencia: Almacenamiento local de productos para acceso offline mediante Room Database.

3. Noticias y Novedades

Visualización de noticias destacadas del mundo gaming.

Integración con microservicio propio para feed de noticias.

4. Arquitectura Técnica

MVVM (Model-View-ViewModel): Separación clara de la lógica de negocio y la interfaz.

Jetpack Compose: Interfaz de usuario moderna y declarativa.

Corrutinas y Flow: Manejo eficiente de operaciones asíncronas.

Inyección de Dependencias: Gestión modular de componentes.

 Endpoints y APIs

El proyecto integra datos de una fuente principal:

1. API Externa (Nexarda)

Utilizada para la búsqueda y obtención de información de videojuegos.

Método

Endpoint (Ejemplo)

Descripción

GET

https://www.nexarda.com/api/v3/search

Búsqueda de juegos por nombre (Query param: q).

GET

.../games/{id}

Detalles específicos de un videojuego.

2. Microservicio Propio (Backend)

Utilizado para noticias o lógica de negocio personalizada.

Método

Endpoint

Descripción

GET

[URL_Base_Local]/api/news

Obtiene el listado de noticias recientes.

POST

[URL_Base_Local]/api/sync



*Pasos para Ejecutar el Proyecto*

Para probar la aplicación en un entorno local, sigue estos pasos:

Clonar el Repositorio:

git clone https://github.com/olovj222/Pixelium2


Abrir en Android Studio:

Abre Android Studio Narwhal.

Selecciona Open y busca la carpeta del proyecto clonado.

Sincronizar Gradle:

Espera a que se descarguen todas las dependencias listadas en build.gradle.kts.


Ejecutar:

Selecciona un dispositivo virtual (AVD) o conecta tu físico.

Presiona el botón Run (Shift + F10).

📦 Entregables: APK Firmado y Keystore

A continuación se adjunta la evidencia de la generación del APK firmado para producción.

1. Captura de Generación de APK

<img width="868" height="308" alt="image" src="https://github.com/user-attachments/assets/30eaf2c8-5368-4518-90ad-7ecc17d4ffd0" />


© 2025 GameVerse-Pixelium Project
