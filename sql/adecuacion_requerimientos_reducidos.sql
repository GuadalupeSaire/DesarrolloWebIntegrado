-- =====================================================================
-- SASREC - Adecuación de la BD al alcance reducido
-- Ejecutar UNA SOLA VEZ sobre gescont_db (haz un respaldo antes: pg_dump).
-- Orden: 1) este script  2) iniciar la aplicación (ddl-auto=update agrega columnas nuevas).
-- =====================================================================
BEGIN;

-- 1. Tablas que ya no forman parte del sistema (SUNAT, correo, custodia, notificaciones)
DROP TABLE IF EXISTS detalles_declaracion CASCADE;
DROP TABLE IF EXISTS declaraciones        CASCADE;
DROP TABLE IF EXISTS aprobaciones_externas CASCADE;
DROP TABLE IF EXISTS envios_correo        CASCADE;
DROP TABLE IF EXISTS notificaciones       CASCADE;
DROP TABLE IF EXISTS documentos           CASCADE;

-- 2. Quitar restricciones CHECK antiguas de los enums (Hibernate no las actualiza)
ALTER TABLE planillas     DROP CONSTRAINT IF EXISTS planillas_estado_check;
ALTER TABLE postulaciones DROP CONSTRAINT IF EXISTS postulaciones_estado_check;
ALTER TABLE vacantes      DROP CONSTRAINT IF EXISTS vacantes_estado_check;

-- 3. Migrar los valores antiguos a los nuevos
UPDATE planillas SET estado = 'CALCULADA' WHERE estado = 'ENVIADA';

UPDATE postulaciones SET estado = CASE estado
    WHEN 'RECIBIDA'        THEN 'POSTULADA'
    WHEN 'EN_REVISION'     THEN 'POSTULADA'
    WHEN 'ENVIADA_CLIENTE' THEN 'EN_TERNA'
    WHEN 'APROBADA'        THEN 'EN_TERNA'
    WHEN 'RECHAZADA'       THEN 'DESCARTADA'
    ELSE estado END;

UPDATE vacantes SET estado = CASE estado
    WHEN 'BORRADOR'  THEN 'ABIERTA'
    WHEN 'PUBLICADA' THEN 'ABIERTA'
    ELSE estado END;

-- 4. Restricciones CHECK nuevas
ALTER TABLE planillas     ADD CONSTRAINT planillas_estado_check
    CHECK (estado IN ('BORRADOR','CALCULADA','CERRADA'));
ALTER TABLE postulaciones ADD CONSTRAINT postulaciones_estado_check
    CHECK (estado IN ('POSTULADA','PRESELECCIONADA','ENTREVISTA','EN_TERNA','CONTRATADA','DESCARTADA'));
ALTER TABLE vacantes      ADD CONSTRAINT vacantes_estado_check
    CHECK (estado IN ('ABIERTA','CERRADA','CANCELADA'));

-- 5. Roles: Contador y Reclutador desaparecen
--    Reasigna a los usuarios antes de borrar los roles.
UPDATE usuario_roles SET rol_id = (SELECT id FROM roles WHERE nombre = 'Planillas')
 WHERE rol_id IN (SELECT id FROM roles WHERE nombre = 'Contador')
   AND NOT EXISTS (SELECT 1 FROM usuario_roles ur2
                    WHERE ur2.usuario_id = usuario_roles.usuario_id
                      AND ur2.rol_id = (SELECT id FROM roles WHERE nombre = 'Planillas'));
-- 'Reclutador' pasa a 'Analista' (el rol Analista lo crea la aplicación al iniciar;
-- si aún no existe, inicia la app una vez y vuelve a ejecutar solo este bloque).
UPDATE usuario_roles SET rol_id = (SELECT id FROM roles WHERE nombre = 'Analista')
 WHERE rol_id IN (SELECT id FROM roles WHERE nombre = 'Reclutador')
   AND (SELECT id FROM roles WHERE nombre = 'Analista') IS NOT NULL;
DELETE FROM usuario_roles WHERE rol_id IN (SELECT id FROM roles WHERE nombre IN ('Contador','Reclutador'));
DELETE FROM roles WHERE nombre IN ('Contador','Reclutador');

COMMIT;
