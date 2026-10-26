-- Agrega habilitado si aún no existe; los registros previos quedan habilitados.
SET @pil_habilitado_existe = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'productos'
      AND column_name = 'habilitado'
);
SET @pil_sql = IF(
    @pil_habilitado_existe = 0,
    'ALTER TABLE productos ADD COLUMN habilitado TINYINT(1) NOT NULL DEFAULT 1 AFTER stock',
    'SELECT ''La columna habilitado ya existe'''
);
PREPARE pil_migracion FROM @pil_sql;
EXECUTE pil_migracion;
DEALLOCATE PREPARE pil_migracion;
