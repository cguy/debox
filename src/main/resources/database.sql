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

SET FOREIGN_KEY_CHECKS=0;

-- --------------------- --
-- OVERALL CONFIGURATION --
-- --------------------- --
CREATE TABLE IF NOT EXISTS `configurations` (
    `key` VARCHAR(255) PRIMARY KEY,
    `value` VARCHAR(255) NOT NULL
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `users_configurations` (
    `user_id` VARCHAR(32) NOT NULL,
    `key` VARCHAR(255) NOT NULL,
    `value` VARCHAR(255),
    PRIMARY KEY (`user_id`, `key`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

-- ---------------- --
-- USERS MANAGEMENT --
-- ---------------- --
CREATE TABLE IF NOT EXISTS `users` (
    `id` VARCHAR(32) PRIMARY KEY,
    `firstname` VARCHAR(50),
    `lastname` VARCHAR(50),
    `avatar` TEXT
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `medias_sources` (
    `id` VARCHAR(32) PRIMARY KEY,
    `type` VARCHAR(32) NOT NULL,
    `data` TEXT NOT NULL,
    `user_id` VARCHAR(32) NOT NULL,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `accounts` (
    `id` VARCHAR(32) PRIMARY KEY,
    `username` VARCHAR(255) NOT NULL,
    `password` VARCHAR(255),
    `password_salt` varchar(255),
    FOREIGN KEY (`id`) REFERENCES `users`(`id`) ON UPDATE CASCADE ON DELETE CASCADE,
    UNIQUE KEY (`username`)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `thirdparty_accounts` (
    `user_id` VARCHAR(32) NOT NULL,
    `thirdparty_account_id` VARCHAR(255) NOT NULL,
    `thirdparty_name` VARCHAR(255) NOT NULL,
    `token` VARCHAR(255),
    PRIMARY KEY (`user_id`, `thirdparty_name`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `roles` (
    `id` VARCHAR(32) PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL UNIQUE KEY
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `users_roles` (
    `user_id` VARCHAR(32) NOT NULL,
    `role_id` VARCHAR(32) NOT NULL,
    PRIMARY KEY (`user_id`, `role_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (`role_id`) REFERENCES `roles`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `users_permissions` (
    `user_id` VARCHAR(32) NOT NULL,
    `domain` VARCHAR(32) NOT NULL,
    `actions` VARCHAR(255) NOT NULL,
    `instance` VARCHAR(32) NOT NULL,
    PRIMARY KEY (`user_id`, `domain`, `instance`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

-- ------------------- --
-- ACCESSES MANAGEMENT --
-- ------------------- --
CREATE TABLE IF NOT EXISTS `accounts_accesses` (
    `user_id` VARCHAR(32),
    `album_id` VARCHAR(32),
    PRIMARY KEY (`user_id`, `album_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (`album_id`) REFERENCES `albums`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `tokens` (
    `id` VARCHAR(32) PRIMARY KEY,
    `label` VARCHAR(255) NOT NULL,
    `owner_id` VARCHAR(32) NOT NULL,
    FOREIGN KEY (`owner_id`) REFERENCES `users`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `albums_tokens` (
    `album_id` VARCHAR(32),
    `token_id` VARCHAR(32),
    PRIMARY KEY (`album_id`, `token_id`),
    FOREIGN KEY (`album_id`) REFERENCES `albums`(`id`) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (`token_id`) REFERENCES `tokens`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

-- ------ --
-- ALBUMS --
-- ------ --
CREATE TABLE IF NOT EXISTS `albums` (
    `id` VARCHAR(32) PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL,
    `description` TEXT,
    `begin_date` DATETIME,
    `end_date` DATETIME,
    `photos_count` INTEGER NOT NULL,
    `videos_count` INTEGER NOT NULL,
    `downloadable` TINYINT(1) NOT NULL,
    `relative_path` TEXT NOT NULL,
    `parent_id` VARCHAR(32),
    `public` TINYINT(1) NOT NULL,
    `cover` VARCHAR(32),
    `owner_id` VARCHAR(32) NOT NULL,
    FOREIGN KEY (`owner_id`) REFERENCES `users`(`id`) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (`parent_id`) REFERENCES `albums`(`id`) ON UPDATE CASCADE ON DELETE CASCADE,
    INDEX USING BTREE (`relative_path`(255))
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

-- ------ --
-- PHOTOS --
-- ------ --
CREATE TABLE IF NOT EXISTS `photos` (
    `id` VARCHAR(32) PRIMARY KEY,
    `filename` VARCHAR(255) NOT NULL,
    `title` VARCHAR(255),
    `date` DATETIME,
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
    PRIMARY KEY (`id`, `size`)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

-- ------ --
-- VIDEOS --
-- ------ --
CREATE TABLE IF NOT EXISTS `videos` (
    `id` VARCHAR(32) PRIMARY KEY,
    `filename` VARCHAR(255) NOT NULL,
    `title` VARCHAR(255),
    `date` DATETIME,
    `relative_path` TEXT NOT NULL,
    `thumbnail` TINYINT(1) NOT NULL,
    `ogg` TINYINT(1) NOT NULL,
    `h264` TINYINT(1) NOT NULL,
    `webm` TINYINT(1) NOT NULL,
    `album_id` VARCHAR(32),
    FOREIGN KEY (`album_id`) REFERENCES `albums`(`id`) ON UPDATE CASCADE ON DELETE CASCADE,
    INDEX USING BTREE (`album_id`),
    INDEX USING BTREE (`relative_path`(255))
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

-- -------- --
-- COMMENTS --
-- -------- --
CREATE TABLE IF NOT EXISTS `comments` (
    `id` VARCHAR(32) PRIMARY KEY,
    `author_id` VARCHAR(32) NOT NULL,
    `publish_time` TIMESTAMP NOT NULL DEFAULT NOW(),
    `last_modification` TIMESTAMP,
    `content` TEXT NOT NULL,
    FOREIGN KEY (`author_id`) REFERENCES `users`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `albums_comments` (
    `comment_id` VARCHAR(32),
    `album_id` VARCHAR(32),
    PRIMARY KEY (`comment_id`),
    FOREIGN KEY (`album_id`) REFERENCES `albums`(`id`) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (`comment_id`) REFERENCES `comments`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `photos_comments` (
    `comment_id` VARCHAR(32) PRIMARY KEY,
    `photo_id` VARCHAR(32),
    FOREIGN KEY (`photo_id`) REFERENCES `photos`(`id`) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (`comment_id`) REFERENCES `comments`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `videos_comments` (
    `comment_id` VARCHAR(32) PRIMARY KEY,
    `video_id` VARCHAR(32),
    FOREIGN KEY (`video_id`) REFERENCES `videos`(`id`) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (`comment_id`) REFERENCES `comments`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

-- ------------- --
-- NOTIFICATIONS --
-- ------------- --
-- CREATE TABLE IF NOT EXISTS `notifications` (
--     `id` VARCHAR(32) PRIMARY KEY,
--     `source_id` VARCHAR(32) NOT NULL,
--     `action_time` TIMESTAMP NOT NULL DEFAULT NOW(),
--     `message` VARCHAR(50) NOT NULL,
--     FOREIGN KEY (`source_id`) REFERENCES `users`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
-- ) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;
-- 
-- CREATE TABLE IF NOT EXISTS `notifications_albums` (
--     `notification_id` VARCHAR(32) PRIMARY KEY,
--     `album_id` VARCHAR(32) NOT NULL,
--     FOREIGN KEY (`notification_id`) REFERENCES `notifications`(`id`) ON UPDATE CASCADE ON DELETE CASCADE,
--     FOREIGN KEY (`album_id`) REFERENCES `albums`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
-- ) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;
-- 
-- CREATE TABLE IF NOT EXISTS `notifications_photos` (
--     `notification_id` VARCHAR(32) PRIMARY KEY,
--     `photo_id` VARCHAR(32) NOT NULL,
--     FOREIGN KEY (`notification_id`) REFERENCES `notifications`(`id`) ON UPDATE CASCADE ON DELETE CASCADE,
--     FOREIGN KEY (`photo_id`) REFERENCES `photos`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
-- ) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;
-- 
-- CREATE TABLE IF NOT EXISTS `albums_notifications_subscriptions` (
--     `album_id` VARCHAR(32) NOT NULL,
--     `user_id` VARCHAR(32) NOT NULL,
--     PRIMARY KEY (`album_id`, `user_id`),
--     FOREIGN KEY (`album_id`) REFERENCES `albums`(`id`) ON UPDATE CASCADE ON DELETE CASCADE,
--     FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
-- ) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;
-- 
-- CREATE TABLE IF NOT EXISTS `photos_notifications_subscriptions` (
--     `photo_id` VARCHAR(32) NOT NULL,
--     `user_id` VARCHAR(32) NOT NULL,
--     PRIMARY KEY (`photo_id`, `user_id`),
--     FOREIGN KEY (`photo_id`) REFERENCES `photos`(`id`) ON UPDATE CASCADE ON DELETE CASCADE,
--     FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
-- ) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

SET FOREIGN_KEY_CHECKS=1;
