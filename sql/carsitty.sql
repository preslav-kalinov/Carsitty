SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

CREATE TABLE `cars` (
  `id` bigint(20) NOT NULL,
  `model` varchar(1024) NOT NULL,
  `carBrandId` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `car_brands` (
  `id` bigint(20) NOT NULL,
  `brand` varchar(128) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `car_parts` (
  `carId` bigint(12) NOT NULL,
  `partId` bigint(12) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `categories` (
  `id` bigint(20) NOT NULL,
  `name` varchar(128) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `logs` (
  `id` bigint(20) NOT NULL,
  `incidentTime` datetime NOT NULL DEFAULT current_timestamp(),
  `message` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `parts` (
  `id` bigint(20) NOT NULL,
  `pictureUrl` varchar(1024) DEFAULT NULL,
  `name` varchar(1024) CHARACTER SET utf8 NOT NULL,
  `oem` varchar(128) CHARACTER SET utf8 NOT NULL,
  `quantity` bigint(20) UNSIGNED NOT NULL,
  `price` decimal(10,2) UNSIGNED NOT NULL DEFAULT 0.00,
  `discount` int(3) NOT NULL DEFAULT 0,
  `categoryId` bigint(20) NOT NULL,
  `userId` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `roles` (
  `id` bigint(20) NOT NULL,
  `role` enum('Employee','Manager','Administrator','') NOT NULL DEFAULT 'Employee' COMMENT '1 - Employee(default),\r\n2 - Manager,\r\n3 - Administrator'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `sales` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `partId` bigint(20) NOT NULL,
  `soldQuantity` bigint(20) UNSIGNED NOT NULL,
  `saleProfit` decimal(10,2) UNSIGNED NOT NULL DEFAULT 0.00,
  `userId` bigint(20) NOT NULL,
  `saleDate` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL,
  `username` varchar(128) NOT NULL,
  `displayName` varchar(1024) NOT NULL,
  `password` varchar(1024) NOT NULL,
  `email` varchar(1024) NOT NULL,
  `roleId` bigint(20) NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `cars`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `model` (`model`) USING HASH,
  ADD KEY `carBrandId` (`carBrandId`) USING BTREE;

ALTER TABLE `car_brands`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `brand` (`brand`);

ALTER TABLE `car_parts`
  ADD KEY `fk_car_parts_car` (`carId`),
  ADD KEY `fk_car_parts_part` (`partId`);

ALTER TABLE `categories`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

ALTER TABLE `logs`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `parts`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `oem` (`oem`),
  ADD KEY `categoryId` (`categoryId`),
  ADD KEY `userId` (`userId`) USING BTREE;

ALTER TABLE `roles`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `sales`
  ADD PRIMARY KEY (`id`),
  ADD KEY `partId` (`partId`) USING BTREE,
  ADD KEY `userId` (`userId`) USING BTREE;

ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`) USING HASH,
  ADD KEY `roleId` (`roleId`) USING BTREE;

ALTER TABLE `cars`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

ALTER TABLE `car_brands`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

ALTER TABLE `categories`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

ALTER TABLE `logs`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

ALTER TABLE `parts`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

ALTER TABLE `roles`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

ALTER TABLE `sales`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

ALTER TABLE `users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

ALTER TABLE `cars`
  ADD CONSTRAINT `cars_ibfk_1` FOREIGN KEY (`carBrandId`) REFERENCES `car_brands` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `car_parts`
  ADD CONSTRAINT `fk_car_parts_car` FOREIGN KEY (`carId`) REFERENCES `cars` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_car_parts_part` FOREIGN KEY (`partId`) REFERENCES `parts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `parts`
  ADD CONSTRAINT `parts_ibfk_2` FOREIGN KEY (`categoryId`) REFERENCES `categories` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `parts_ibfk_3` FOREIGN KEY (`userId`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `sales`
  ADD CONSTRAINT `sales_ibfk_1` FOREIGN KEY (`partId`) REFERENCES `parts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `sales_ibfk_2` FOREIGN KEY (`userId`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `users`
  ADD CONSTRAINT `users_ibfk_1` FOREIGN KEY (`roleId`) REFERENCES `roles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;