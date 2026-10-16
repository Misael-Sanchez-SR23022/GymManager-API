-- Reglas de negocio de Gestionar Clases:
-- toda clase debe tener entrenador asignado y una capacidad valida.
ALTER TABLE clase ALTER COLUMN entrenador_id SET NOT NULL;
ALTER TABLE clase ALTER COLUMN capacidad_clase SET NOT NULL;
ALTER TABLE clase ADD CONSTRAINT chk_clase_capacidad_positiva CHECK (capacidad_clase > 0);