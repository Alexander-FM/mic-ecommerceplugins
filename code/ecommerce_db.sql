-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: mysql
-- Tiempo de generación: 07-08-2024 a las 07:07:13
-- Versión del servidor: 9.0.0
-- Versión de PHP: 8.2.8

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `ecommerce_db`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `brands`
--

CREATE TABLE `brands` (
  `id` int NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `brands`
--

INSERT INTO `brands` (`id`, `description`, `is_active`) VALUES
(1, 'JBL', b'1'),
(2, 'Harman Kardon', b'1');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `categories`
--

CREATE TABLE `categories` (
  `id` int NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `parent_category` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `categories`
--

INSERT INTO `categories` (`id`, `description`, `is_active`, `parent_category`) VALUES
(1, 'Tecnología', b'1', NULL),
(2, 'ElectroHogar', b'1', NULL),
(3, 'Audio', b'1', 1),
(4, 'TV', b'1', 1),
(5, 'Cámaras y Drones', b'1', 1),
(6, 'Smart Home y Domótica', b'1', 1),
(7, 'Refrigeradoras', b'1', 2),
(8, 'Cocinas', b'1', 2),
(9, 'Audífonos', b'1', 3),
(10, 'Equipos de Sonido', b'1', 3),
(11, 'Parlantes', b'1', 3),
(12, 'Soundbar y Home Theater', b'1', 3),
(13, 'Accesorios', b'1', 3);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `products`
--

CREATE TABLE `products` (
  `id` int NOT NULL,
  `bar_code` varchar(255) DEFAULT NULL,
  `description` varchar(500) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `is_recommended` bit(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `stock` int DEFAULT NULL,
  `brand_id` int DEFAULT NULL,
  `category_id` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `products`
--

INSERT INTO `products` (`id`, `bar_code`, `description`, `is_active`, `is_recommended`, `name`, `price`, `stock`, `brand_id`, `category_id`) VALUES
(1, '113654235', 'La última versión de la Serie Charge de JBL Charge 5 incluye topes de silicona y un llamativo logo de JBL, El altavoz combina una estructura resistente y duradera con un diseño elegante y audaz que se destacará en cualquier escenario.\n\nVersión de Bluetooth: 5.1', b'1', b'1', 'Parlante JBL FLIP 6 BT', 999, 100, 1, 3),
(2, '20122009', 'Lleva el sonido a otro nivel gracias a la ingeniería acústica que integra el parlante de Harman Kardon con una duración aproximada de la batería: 12 Hrs tienes música para rato.', b'1', b'1', 'Harman Kardon Parlante Luna', 599, 100, 2, 3);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `product_details`
--

CREATE TABLE `product_details` (
  `id` int NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `product_id` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `product_details`
--

INSERT INTO `product_details` (`id`, `descripcion`, `nombre`, `product_id`) VALUES
(5, 'Nuevo', 'Condicion del producto', 1),
(6, 'Año de lanzamiento', '2023', 1),
(7, '5.1', 'Versión de Bluetooth', 1),
(8, 'La garantía no aplica si el daño al dispositivo fue causado por un mal manejo de parte del usuario.', 'Detalle de la garantía', 1),
(9, '40', 'Potencia RMS', 2),
(10, '12 Hrs', 'Duración aproximada de la batería', 2),
(11, 'Sí', 'Conexión Bluetooth', 2),
(12, 'No', 'Conexión WiFi', 2),
(13, 'Sin entradas', 'Entradas USB', 2);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `product_images`
--

CREATE TABLE `product_images` (
  `id` int NOT NULL,
  `imagen_url` varchar(255) DEFAULT NULL,
  `product_id` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `brands`
--
ALTER TABLE `brands`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK2tlysbpe2g5stfi28f9qrc4uy` (`description`);

--
-- Indices de la tabla `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKdpusovwfttipdju5vdp10kcbm` (`description`);

--
-- Indices de la tabla `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKa3a4mpsfdf4d2y6r8ra3sc8mv` (`brand_id`),
  ADD KEY `FKog2rp4qthbtt2lfyhfo32lsw9` (`category_id`);

--
-- Indices de la tabla `product_details`
--
ALTER TABLE `product_details`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKnfvvq3meg4ha3u1bju9k4is3r` (`product_id`);

--
-- Indices de la tabla `product_images`
--
ALTER TABLE `product_images`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKqnq71xsohugpqwf3c9gxmsuy` (`product_id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `brands`
--
ALTER TABLE `brands`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `categories`
--
ALTER TABLE `categories`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT de la tabla `products`
--
ALTER TABLE `products`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `product_details`
--
ALTER TABLE `product_details`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT de la tabla `product_images`
--
ALTER TABLE `product_images`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `products`
--
ALTER TABLE `products`
  ADD CONSTRAINT `FKa3a4mpsfdf4d2y6r8ra3sc8mv` FOREIGN KEY (`brand_id`) REFERENCES `brands` (`id`),
  ADD CONSTRAINT `FKog2rp4qthbtt2lfyhfo32lsw9` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`);

--
-- Filtros para la tabla `product_details`
--
ALTER TABLE `product_details`
  ADD CONSTRAINT `FKnfvvq3meg4ha3u1bju9k4is3r` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);

--
-- Filtros para la tabla `product_images`
--
ALTER TABLE `product_images`
  ADD CONSTRAINT `FKqnq71xsohugpqwf3c9gxmsuy` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
