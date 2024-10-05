# PAM (API REST for UFPS PAM project)

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

El backend de PAM es una API REST desarrollada para la gestión de las admisiones para el
[Programa de Maestría en Tecnologías de Información y Comunicación (TIC) Aplicadas a la Educación](https://educaiton.cloud.ufps.edu.co/index.php?id=2)
(Universidad Francisco de Paula Santander). Proporciona un conjunto de endpoints para gestionar el proceso de admisiones
para el programa de posgrado.
## Tecnologías usadas

- Java 17
- Spring Boot 2.7.9
- Spring Data JPA
- Spring Validation
- Spring Web
- Spring DevTools
- jjwt 0.9.1
- Jackson Dataformat XML
- MySQL Connector/J
- Lombok
- Spring Security
- Spring Cloud AWS
- Spring Boot Mail

## Requisitos

- Java 17
- Maven
- MySQL

## Primeros Pasos

Siga las instrucciones que se indican a continuación para obtener una copia del proyecto en su equipo local con fines de desarrollo y prueba.

### Instalación

1. Clona el repositorio

    *  Clonar con HTTP
   ```bash
   git clone https://github.com/JuanPCT/PAM.git
   ```
    * Clonar con SSH
   ```bash
   git clone git@github.com:JuanPCT/PAM.git
   ```
2. Cambiar de directorio:

   ```bash
   cd pam
   ```

3. Construir el proyecto usando Maven:

   ```bash
   mvn clean install
   ```

### Configuración

El proyecto requiere algunas configuraciones para funcionar correctamente. Siga los siguientes pasos para establecer las configuraciones necesarias:

1. Abra el archivo `application.properties` ubicado en el directorio `src/main/resources`.

2. Modifique las propiedades de conexión a la base de datos de acuerdo con su configuración de MySQL:

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/pam?useSSL=false&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=your-password
   ```

3. Guarda los cambios.

### Utilización

1. Inicia la aplicación ejecutando el siguiente comando:

   ```bash
   mvn spring-boot:run
   ```

2. La aplicación se ejecutará en `http://localhost:8080`.


## API Endpoints

La siguiente tabla enumera los endpoints de la API disponibles:

| Endpoint                                        | Method | Description                                                             |
|-------------------------------------------------|--------|-------------------------------------------------------------------------|
| `/aspirante`                                    | POST   | Agregar la  información de un aspirante.                                |
| `/aspirante`                                    | GET    | Obtener la información personal de un aspirante por su id.              |
| `/aspirante/{idAccount}`                        | GET    | Obtener toda la información de un aspirante.                            |
| `/aspirante/info`                               | GET    | Metodo usado para no dañar la implementación del front                  |
| `/aspirante/{emailAspirante}/desactivar`        | POST   | Desactivar Aspirante.                                                   |
| `/aspirante/{emailAspirante}/activar`           | POST   | Activar Aspirante.                                                      |
| `/aspirante/listar`                             | GET    | Listar Aspirantes por Cohorte.                                          |
| `/aspirante/listarCalificaciones`               | GET    | Listar todas las calificaciones de los aspirantes                       |    
| `/aspirante/horarioEntrevista`                  | GET    | Asigna la fecha de la entrevista a un aspirante.                        |
| `/aspirante/entrevistaPrueba`                   | GET    | Muestra los enlaces y fechas  de la prueba y entrevista                 |
| `/aspirante/calificacionDocumentos`             | POST   | Califica los documentos de un aspirante.                                |
| `/aspirante/calificacionPrueba`                 | POST   | Califica la prueba de un aspirante.                                     |
| `/aspirante/calificacionEntrevista`             | POST   | Califica la entrevista de un aspirante.                                 |
| `/aspirante/admitir`                            | POST   | Admite a un aspirante.                                                  | 
| `/aspirante/admitidos`                          | GET    | Listado de los aspirantes admitidos.                                    |    
| `/aspirante/historicos`                         | GET    | listado de aspirantes históricos de un cohorte.                         |
| `/cohorte`                                      | GET    | Listar Cohortes.                                                        |
| `/cohorte/abrir`                                | POST   | Abrir Cohorte.                                                          |
| `/cohorte/cerrar`                               | POST   | Cerrar Cohorte.                                                         |
| `/cohorte/abierto`                              | GET    | Comprobar si hay un Cohorte Abierto.                                    |
| `/cohorte/entrevistaEnlace`                     | POST   | Obtiene un enlace para la entrevista de la cohorte abierta.             |
| `/cohorte/prueba`                               | POST   | Habilita la prueba para la cohorte abierta.                             |
| `/doc/aprobar/{documentoId}/{aspiranteId}`      | PUT    | Aprobar un documento.                                                   |
| `/doc/rechazar/{documentoId}/{aspiranteId}`     | PUT    | Rechazar un documento.                                                  |
| `/doc/listar`                                   | GET    | Listar los documentos asociados a un aspirante.                         |
| `/doc/listarDoc`                                | GET    | Listar los documentos asociados a un aspirante con toda la información. |
| `/doc/retroalimentacion`                        | POST   | Retroalimentación relacionada a un documento.                           |
| `/doc/crearDocs`                                | POST   | Crear los documentos para un aspirante específico.                      |
| `/doc/filtrar`                                  | GET    | listar los aspirantes que tienen un estado de documento específico.     |
| `/documentos/listFiles/{idAccount}`             | GET    | listar los archivos en un bucket de Amazon S3.                          |
| `/documentos/downloadFile`                      | GET    | descargar un archivo de Amazon S3.                                      |
| `/documentos/deleteObject`                      | DELETE | eliminar un archivo de Amazon S3.                                       |
| `/documentos/uploadFile/{tipoDocumento}`        | POST   | sube un archivo a un bucket de AWS S3.                                  |
| `/documentos/downloadFolder`                    | GET    | Descarga los documentos de un aspirante en una carpeta comprimida.      |
| `/notificacion/listar`                          | GET    | Lista las notificaciones de un aspirante                                |
| `/notificacion/checkRead`                       | GET    | Marca las notificaciones de un usuario como leídas                      |
| `/tiposDoc`                                     | GET    | listar todos los tipos de documentos.                                   |
| `/users`                                        | POST   | Crear usuarios en la base de datos.                                     |
| `/users/confirmar/{token}`                      | GET    | Confirma el token                                                       |
| `/users/encargado`                              | POST   | Crear usuarios con el rol de auxiliar.                                  |
| `/users/{rol}`                                  | GET    | obtener los usuarios según el rol.                                      |
| `/users/reestablecer`                           | POST   | Reestablecer contraseña.                                                |
| `/users/reestablecer/email`                     | POST   | Reestablecer contraseña con el correo electrónico.                      |
| `/users/eliminar/{email} `                      | DELETE | Eliminar auxiliares por email                                           |

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contacto con Equipo Operativo
Para cualquier consulta o comentario, póngase en contacto con

También puedes encontrarnos en LinkedIn:

- Juan Correa: [LinkedIn](https://www.linkedin.com/in/juan-pablo-correa-tarazona-6725661b1/)
- Julian Riveros: [LinkedIn](https://www.linkedin.com/in/julian-riveros/)
- Angel Duque: [LinkedIn](https://www.linkedin.com/in/angel-duque-b10dqcrck/)
- Angel García: [LinkedIn](https://www.linkedin.com/in/angel-gabriel-garcia-rangel-a783b8254)
- Gibson Arbey: [LinkedIn](https://co.linkedin.com/in/gibson-rodr%C3%ADguez-143167209)
- Ingrid Florez: [LinkedIn](https://www.linkedin.com/in/ingrid-neileth-florez-garay-31636a224/)
- Miguel Lara: [LinkedIn](https://www.linkedin.com/in/miguel-lara-villa5/)
- Gerson Díaz: [LinkedIn](https://www.linkedin.com/in/gerson-israel-diaz-de-la-garza-669317152/)
- Javier Lopez [LinkedIn](https://www.linkedin.com/in/javier-lopez-56822a181)
