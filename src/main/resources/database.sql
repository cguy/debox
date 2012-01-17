CREATE DATABASE IF NOT EXISTS `debox` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

USE `debox`;

CREATE TABLE IF NOT EXISTS `debox`.`users` (
    `id` VARCHAR(36) PRIMARY KEY,
    `username` VARCHAR(255) NOT NULL UNIQUE KEY,
    `password` VARCHAR(255) NOT NULL,
    `password_salt` varchar(255)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `debox`.`credentials` (
    `id` VARCHAR(36) PRIMARY KEY,
    `provider` VARCHAR(255) NOT NULL,
    `provider_user_id` VARCHAR(255) NOT NULL
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `debox`.`credentials_data` (
    `credentials_id` VARCHAR(36),
    `key` VARCHAR(255) NOT NULL,
    `value` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`credentials_id`, `key`),
    FOREIGN KEY (`credentials_id`) REFERENCES `credentials`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `debox`.`users_credentials` (
    `user_id` VARCHAR(36),
    `credentials_id` VARCHAR(36),
    PRIMARY KEY (`user_id`, `credentials_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (`credentials_id`) REFERENCES `credentials`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;
