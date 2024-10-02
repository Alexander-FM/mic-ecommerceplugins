-- Desactivar claves foráneas
SET FOREIGN_KEY_CHECKS = 0;

-- Truncar las tablas
TRUNCATE TABLE maintenance_service.customers;
TRUNCATE TABLE maintenance_service.address;

-- Activar claves foráneas
SET FOREIGN_KEY_CHECKS = 1;