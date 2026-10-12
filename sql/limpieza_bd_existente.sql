-- =====================================================================
-- SASREC - Limpieza de una BD YA EXISTENTE (solo elimina lo que ya no se usa)
-- Haz un respaldo antes (pg_dump). Ejecutar una sola vez sobre gescont_db.
-- =====================================================================
BEGIN;

-- 1. Tablas eliminadas (SUNAT, correo, documentos, notificaciones)
DROP TABLE IF EXISTS detalles_declaracion  CASCADE;
DROP TABLE IF EXISTS declaraciones         CASCADE;
DROP TABLE IF EXISTS aprobaciones_externas CASCADE;
DROP TABLE IF EXISTS envios_correo         CASCADE;
DROP TABLE IF EXISTS notificaciones        CASCADE;
DROP TABLE IF EXISTS documentos            CASCADE;

-- 2. Estados eliminados: ENVIADA (planillas) y ENVIADA_CLIENTE (postulaciones)
ALTER TABLE planillas     DROP CONSTRAINT IF EXISTS planillas_estado_check;
ALTER TABLE postulaciones DROP CONSTRAINT IF EXISTS postulaciones_estado_check;
UPDATE planillas     SET estado = 'CALCULADA' WHERE estado = 'ENVIADA';
UPDATE postulaciones SET estado = 'APROBADA'  WHERE estado = 'ENVIADA_CLIENTE';
ALTER TABLE planillas     ADD CONSTRAINT planillas_estado_check
    CHECK (estado IN ('BORRADOR','CALCULADA','CERRADA'));
ALTER TABLE postulaciones ADD CONSTRAINT postulaciones_estado_check
    CHECK (estado IN ('RECIBIDA','EN_REVISION','PRESELECCIONADA','ENTREVISTA','APROBADA','RECHAZADA','CONTRATADA'));

-- 3. Rol eliminado: Contador (sus usuarios pasan a Planillas)
UPDATE usuario_roles SET rol_id = (SELECT id FROM roles WHERE nombre = 'Planillas')
 WHERE rol_id IN (SELECT id FROM roles WHERE nombre = 'Contador')
   AND NOT EXISTS (SELECT 1 FROM usuario_roles ur2
                    WHERE ur2.usuario_id = usuario_roles.usuario_id
                      AND ur2.rol_id = (SELECT id FROM roles WHERE nombre = 'Planillas'));
DELETE FROM usuario_roles WHERE rol_id IN (SELECT id FROM roles WHERE nombre = 'Contador');
DELETE FROM roles WHERE nombre = 'Contador';

COMMIT;
