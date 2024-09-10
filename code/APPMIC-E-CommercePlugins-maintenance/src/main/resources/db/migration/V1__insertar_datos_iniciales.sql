INSERT INTO categories (id, description, is_active, parent_category)
VALUES (1, 'Tecnología', true, NULL);
INSERT INTO categories (id, description, is_active, parent_category)
VALUES (2, 'Audio', true, 1);
INSERT INTO categories (id, description, is_active, parent_category)
VALUES (3, 'Parlantes Bluetooth', true, 2);
INSERT INTO categories (id, description, is_active, parent_category)
VALUES (4, 'Equipos de Sonido', true, 2);
INSERT INTO categories (id, description, is_active, parent_category)
VALUES (5, 'Audífonos', true, 2);
INSERT INTO categories (id, description, is_active, parent_category)
VALUES (6, 'Electrohogar', true, NULL);
INSERT INTO categories (id, description, is_active, parent_category)
VALUES (7, 'Refrigeradoras', true, 6);
INSERT INTO categories (id, description, is_active, parent_category)
VALUES (8, 'Congeladoras', true, 7);
INSERT INTO categories (id, description, is_active, parent_category)
VALUES (9, 'Frigobares', true, 7);

INSERT INTO brands (id, description, is_active)
VALUES (1, 'JBL', true);
INSERT INTO brands (id, description, is_active)
VALUES (2, 'HARMAN KARDON', true);