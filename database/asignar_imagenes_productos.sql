-- Ejecuta este archivo sobre la base de datos piltarija.
-- Las imágenes se guardan como rutas relativas a la carpeta del proyecto.
SET @agregar_columna_imagen = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 'productos'
       AND COLUMN_NAME = 'imagen') = 0,
    'ALTER TABLE productos ADD COLUMN imagen VARCHAR(500) NULL',
    'SELECT 1'
);
PREPARE sentencia_imagen FROM @agregar_columna_imagen;
EXECUTE sentencia_imagen;
DEALLOCATE PREPARE sentencia_imagen;

UPDATE productos
SET imagen = CASE
    WHEN LOWER(REPLACE(REPLACE(REPLACE(nombre, ' ', ''), '-', ''), '_', '')) LIKE '%bioyogurt%' THEN 'src/assets/productos/bioyogurt.jpg'
    WHEN LOWER(REPLACE(REPLACE(REPLACE(nombre, ' ', ''), '-', ''), '_', '')) LIKE '%yogurtbio%' THEN 'src/assets/productos/bioyogurt.jpg'
    WHEN LOWER(REPLACE(REPLACE(REPLACE(nombre, ' ', ''), '-', ''), '_', '')) LIKE '%cremadeleche%' THEN 'src/assets/productos/cremadeleche.jpg'
    WHEN LOWER(REPLACE(REPLACE(REPLACE(nombre, ' ', ''), '-', ''), '_', '')) LIKE '%dulcedeleche%' THEN 'src/assets/productos/dulcedeleche.jpg'
    WHEN LOWER(REPLACE(REPLACE(REPLACE(nombre, ' ', ''), '-', ''), '_', '')) LIKE '%karpil%230%' THEN 'src/assets/productos/karpil230ml.jpg'
    WHEN LOWER(REPLACE(REPLACE(REPLACE(nombre, ' ', ''), '-', ''), '_', '')) LIKE '%karpil%900%' THEN 'src/assets/productos/karpil900ml.jpg'
    WHEN LOWER(REPLACE(REPLACE(REPLACE(nombre, ' ', ''), '-', ''), '_', '')) LIKE '%lecheenpolvo%' THEN 'src/assets/productos/lecheenpolvo.jpg'
    WHEN LOWER(REPLACE(REPLACE(REPLACE(nombre, ' ', ''), '-', ''), '_', '')) LIKE '%lecheentera%' THEN 'src/assets/productos/lecheentera.jpg'
    WHEN LOWER(REPLACE(REPLACE(REPLACE(nombre, ' ', ''), '-', ''), '_', '')) LIKE '%mantequilla%' THEN 'src/assets/productos/mantequilla.jpg'
    WHEN LOWER(REPLACE(REPLACE(REPLACE(nombre, ' ', ''), '-', ''), '_', '')) LIKE '%nutrilac%' THEN 'src/assets/productos/nutrilac.jpg'
    ELSE imagen
END
WHERE imagen IS NULL OR TRIM(imagen) = '';
