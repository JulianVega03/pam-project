CREATE TABLE IF NOT EXISTS authorities (
                                           id SERIAL PRIMARY KEY,
                                           authority VARCHAR(20) NOT NULL
    );

CREATE TABLE IF NOT EXISTS estado (
                                      id SERIAL PRIMARY KEY,
                                      descripcion VARCHAR(50) NOT NULL
    );

CREATE TABLE IF NOT EXISTS estado_doc (
                                          id SERIAL PRIMARY KEY,
                                          nombre VARCHAR(20) NOT NULL
    );

CREATE TABLE IF NOT EXISTS tipo_documento (
                                              id SERIAL PRIMARY KEY,
                                              nombre VARCHAR(255) NOT NULL,
    url_formato VARCHAR(255)
    );

CREATE TABLE IF NOT EXISTS cohorte (
                                       id SERIAL PRIMARY KEY,
                                       fecha_inicio DATE NOT NULL,
                                       fecha_fin DATE NOT NULL,
                                       habilitado BOOLEAN DEFAULT FALSE,
                                       enlace_entrevista VARCHAR(255),
    enlace_prueba VARCHAR(255),
    fecha_max_prueba TIMESTAMP,
    CONSTRAINT cohorte_fecha_check CHECK (fecha_inicio <= fecha_fin)
    );

CREATE TABLE IF NOT EXISTS users (
                                     id SERIAL PRIMARY KEY,
                                     email VARCHAR(255) NOT NULL,
    encrypted_password VARCHAR(255) NOT NULL,
    correo_confirmado BOOLEAN DEFAULT FALSE,
    authority_id INTEGER NOT NULL,
    CONSTRAINT fk_authority_id FOREIGN KEY (authority_id) REFERENCES authorities (id)
    );

INSERT INTO authorities (id,authority) VALUES
                                           (1,'ROLE_ADMIN'),
                                           (2,'ROLE_ENCARGADO'),
                                           (3,'ROLE_USUARIO') ON CONFLICT DO NOTHING;

INSERT INTO estado_doc (id,nombre) VALUES
                                       (1,'SIN ENVIAR'),
                                       (2,'ENVIADO'),
                                       (3,'RECHAZADO'),
                                       (4,'APROBADO') ON CONFLICT DO NOTHING;

INSERT INTO estado (id,descripcion) VALUES
                                        (1,'INSCRIPCION'),
                                        (2,'ENVIO DOCUMENTOS'),
                                        (3,'DOCUMENTOS ENVIADOS'),
                                        (4,'DOCUMENTOS APROBADOS'),
                                        (5,'ENTREVISTA Y PRUEBA'),
                                        (6,'ADMITIDO'),
                                        (7,'EN ESPERA'),
                                        (8,'DESERTO'),
                                        (9,'DESACTIVADO') ON CONFLICT DO NOTHING;

INSERT INTO tipo_documento (id, nombre, url_formato) VALUES
                                                         (1,'Foto 3x4', ''),
                                                         (2,'Documento identidad', ''),
                                                         (3,'Diploma pregrado', ''),
                                                         (4,'Certificado calificaciones o acta de compromiso', ''),
                                                         (5,'Hoja de vida', 'educaiton.cloud.ufps.edu.co/rsc/formatos/inscripcion/formatohojadevida.docx'),
                                                         (6, 'Comprobante de pago', ''),
                                                         (7, 'Firma digitalizada', ''),
                                                         (8, 'Carta referencia 1', 'educaiton.cloud.ufps.edu.co/rsc/formatos/inscripcion/carta_modelo_de_referencia.docx'),
                                                         (9, 'Carta referencia 2', 'educaiton.cloud.ufps.edu.co/rsc/formatos/inscripcion/carta_modelo_de_referencia.docx'),
                                                         (10, 'Formato inscripcion', 'educaiton.cloud.ufps.edu.co/rsc/formatos/inscripcion/formatoinscripcion.docx'),
                                                         (11, 'Cedula de residente y/o visa de estudiante vigente', ''),
                                                         (12, 'Notas de pregrado apostilladas', ''),
                                                         (13, 'Diploma fondo negro apostillado', '') ON CONFLICT DO NOTHING;
