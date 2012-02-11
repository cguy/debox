DROP DATABASE IF EXISTS `debox-photos`;

CREATE DATABASE IF NOT EXISTS `debox-photos` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

USE `debox-photos`;

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
    `source_path` TEXT NOT NULL,
    `target_path` TEXT NOT NULL,
    `parent_id` VARCHAR(32),
    `visibility` VARCHAR(32) NOT NULL,
    FOREIGN KEY (`parent_id`) REFERENCES `albums`(`id`) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (`visibility`) REFERENCES `visibilities`(`id`),
    INDEX USING BTREE (`source_path`(255)),
    INDEX USING BTREE (`target_path`(255))
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
    `source_path` TEXT NOT NULL,
    `target_path` TEXT NOT NULL,
    `album_id` VARCHAR(32),
    FOREIGN KEY (`album_id`) REFERENCES `albums`(`id`) ON UPDATE CASCADE ON DELETE CASCADE,
    INDEX USING BTREE (`album_id`),
    INDEX USING BTREE (`source_path`(255)),
    INDEX USING BTREE (`target_path`(255))
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;
