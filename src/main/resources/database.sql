--
-- #%L
-- debox-photos
-- %%
-- Copyright (C) 2012 Debox
-- %%
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU Affero General Public License as published by
-- the Free Software Foundation, either version 3 of the License, or
-- (at your option) any later version.
-- 
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
-- 
-- You should have received a copy of the GNU Affero General Public License
-- along with this program.  If not, see <http://www.gnu.org/licenses/>.
-- #L%
--
DROP DATABASE IF EXISTS `debox-photos`;

CREATE DATABASE IF NOT EXISTS `debox-photos` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

USE `debox-photos`;

DROP TABLE IF EXISTS `photos_generation`;
DROP TABLE IF EXISTS `photos`;
DROP TABLE IF EXISTS `albums_tokens`;
DROP TABLE IF EXISTS `tokens`;
DROP TABLE IF EXISTS `albums`;
DROP TABLE IF EXISTS `roles`;
DROP TABLE IF EXISTS `visibilities`;
DROP TABLE IF EXISTS `users`;
DROP TABLE IF EXISTS `configurations`;

CREATE TABLE IF NOT EXISTS `configurations` (
    `key` VARCHAR(255) PRIMARY KEY,
    `value` VARCHAR(255) NOT NULL
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `users` (
    `id` VARCHAR(32) PRIMARY KEY,
    `username` VARCHAR(255) NOT NULL UNIQUE KEY,
    `password` VARCHAR(255) NOT NULL,
    `password_salt` varchar(255) NOT NULL
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `visibilities` (
    `id` VARCHAR(20) PRIMARY KEY
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

INSERT INTO `visibilities` VALUES ('public'),('private'),('token_access');

CREATE TABLE IF NOT EXISTS `tokens` (
    `id` VARCHAR(32) PRIMARY KEY,
    `label` VARCHAR(255) NOT NULL
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `roles` (
    `id` VARCHAR(32) PRIMARY KEY,
    `label` VARCHAR(255) NOT NULL
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `albums` (
    `id` VARCHAR(32) PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL,
    `date` DATE,
    `photos_count` INTEGER NOT NULL,
    `downloadable` TINYINT(1) NOT NULL,
    `relative_path` TEXT NOT NULL,
    `parent_id` VARCHAR(32),
    `visibility` VARCHAR(32) NOT NULL,
    FOREIGN KEY (`parent_id`) REFERENCES `albums`(`id`) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (`visibility`) REFERENCES `visibilities`(`id`),
    INDEX USING BTREE (`relative_path`(255))
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `albums_tokens` (
    `album_id` VARCHAR(32),
    `token_id` VARCHAR(32),
    PRIMARY KEY (`album_id`, `token_id`),
    FOREIGN KEY (`album_id`) REFERENCES `albums`(`id`) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (`token_id`) REFERENCES `tokens`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `photos` (
    `id` VARCHAR(32) PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL,
    `relative_path` TEXT NOT NULL,
    `album_id` VARCHAR(32),
    FOREIGN KEY (`album_id`) REFERENCES `albums`(`id`) ON UPDATE CASCADE ON DELETE CASCADE,
    INDEX USING BTREE (`album_id`),
    INDEX USING BTREE (`relative_path`(255))
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `photos_generation` (
    `id` VARCHAR(32) NOT NULL,
    `size` VARCHAR(255) NOT NULL,
    `time` TIMESTAMP NOT NULL,
    PRIMARY KEY (`id`, `size`),
    FOREIGN KEY (`id`) REFERENCES `photos`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;
