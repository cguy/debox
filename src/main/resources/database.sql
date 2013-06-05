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

-- --------------------- --
-- OVERALL CONFIGURATION --
-- --------------------- --
CREATE TABLE IF NOT EXISTS configurations (
    ckey VARCHAR(255) PRIMARY KEY,
    cvalue VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS users_configurations (
    user_id VARCHAR(32) NOT NULL,
    ckey VARCHAR(255) NOT NULL,
    cvalue VARCHAR(255),
    PRIMARY KEY (user_id, ckey)
);

-- ---------------- --
-- USERS MANAGEMENT --
-- ---------------- --
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(32) PRIMARY KEY,
    firstname VARCHAR(50),
    lastname VARCHAR(50),
    avatar TEXT
);

CREATE TABLE IF NOT EXISTS medias_sources (
    id VARCHAR(32) PRIMARY KEY,
    type VARCHAR(32) NOT NULL,
    data TEXT NOT NULL,
    user_id VARCHAR(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS accounts (
    id VARCHAR(32) PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255),
    password_salt varchar(255)
);
ALTER TABLE accounts ADD UNIQUE (username);

CREATE TABLE IF NOT EXISTS users_anonymous (
    id VARCHAR(32) PRIMARY KEY,
    label VARCHAR(255) NOT NULL,
    creator VARCHAR(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS thirdparty_accounts (
    user_id VARCHAR(32) NOT NULL,
    thirdparty_account_id VARCHAR(255) NOT NULL,
    thirdparty_name VARCHAR(255) NOT NULL,
    token VARCHAR(255),
    PRIMARY KEY (user_id, thirdparty_name)
);

CREATE TABLE IF NOT EXISTS roles (
    id VARCHAR(32) PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);
ALTER TABLE roles ADD UNIQUE (name);

CREATE TABLE IF NOT EXISTS users_roles (
    user_id VARCHAR(32) NOT NULL,
    role_id VARCHAR(32) NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS users_albums_permissions (
    user_id VARCHAR(32) NOT NULL,
    instance VARCHAR(32) NOT NULL,
    actions VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id, instance, actions)
);

CREATE TABLE IF NOT EXISTS users_photos_permissions (
    user_id VARCHAR(32) NOT NULL,
    instance VARCHAR(32) NOT NULL,
    actions VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id, instance, actions)
);

CREATE TABLE IF NOT EXISTS users_videos_permissions (
    user_id VARCHAR(32) NOT NULL,
    instance VARCHAR(32) NOT NULL,
    actions VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id, instance, actions)
);

-- ------ --
-- ALBUMS --
-- ------ --
CREATE TABLE IF NOT EXISTS albums (
    id VARCHAR(32) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    begin_date DATETIME,
    end_date DATETIME,
    photos_count INTEGER NOT NULL,
    videos_count INTEGER NOT NULL,
    downloadable TINYINT(1) NOT NULL,
    relative_path TEXT NOT NULL,
    parent_id VARCHAR(32),
    public TINYINT(1) NOT NULL,
    cover VARCHAR(32),
    owner_id VARCHAR(32) NOT NULL
);

-- ------ --
-- PHOTOS --
-- ------ --
CREATE TABLE IF NOT EXISTS photos (
    id VARCHAR(32) PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    title VARCHAR(255),
    date DATETIME,
    relative_path TEXT NOT NULL,
    album_id VARCHAR(32)
);

CREATE TABLE IF NOT EXISTS photos_generation (
    id VARCHAR(32) NOT NULL,
    size VARCHAR(255) NOT NULL,
    time TIMESTAMP NOT NULL,
    PRIMARY KEY (id, size)
);

-- ------ --
-- VIDEOS --
-- ------ --
CREATE TABLE IF NOT EXISTS videos (
    id VARCHAR(32) PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    title VARCHAR(255),
    date DATETIME,
    relative_path TEXT NOT NULL,
    thumbnail TINYINT(1) NOT NULL,
    ogg TINYINT(1) NOT NULL,
    h264 TINYINT(1) NOT NULL,
    webm TINYINT(1) NOT NULL,
    album_id VARCHAR(32)
);

-- -------- --
-- COMMENTS --
-- -------- --
CREATE TABLE IF NOT EXISTS comments (
    id VARCHAR(32) PRIMARY KEY,
    author_id VARCHAR(32) NOT NULL,
    publish_time TIMESTAMP NOT NULL DEFAULT NOW(),
    last_modification TIMESTAMP,
    content TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS albums_comments (
    comment_id VARCHAR(32),
    album_id VARCHAR(32),
    PRIMARY KEY (comment_id)
);

CREATE TABLE IF NOT EXISTS photos_comments (
    comment_id VARCHAR(32) PRIMARY KEY,
    photo_id VARCHAR(32)
);

CREATE TABLE IF NOT EXISTS videos_comments (
    comment_id VARCHAR(32) PRIMARY KEY,
    video_id VARCHAR(32)
);

-- ------------- --
-- NOTIFICATIONS --
-- ------------- --
-- CREATE TABLE IF NOT EXISTS notifications (
--     id VARCHAR(32) PRIMARY KEY,
--     source_id VARCHAR(32) NOT NULL,
--     action_time TIMESTAMP NOT NULL DEFAULT NOW(),
--     message VARCHAR(50) NOT NULL,
--     FOREIGN KEY (source_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE
-- );
-- 
-- CREATE TABLE IF NOT EXISTS notifications_albums (
--     notification_id VARCHAR(32) PRIMARY KEY,
--     album_id VARCHAR(32) NOT NULL,
--     FOREIGN KEY (notification_id) REFERENCES notifications(id) ON UPDATE CASCADE ON DELETE CASCADE,
--     FOREIGN KEY (album_id) REFERENCES albums(id) ON UPDATE CASCADE ON DELETE CASCADE
-- );
-- 
-- CREATE TABLE IF NOT EXISTS notifications_photos (
--     notification_id VARCHAR(32) PRIMARY KEY,
--     photo_id VARCHAR(32) NOT NULL,
--     FOREIGN KEY (notification_id) REFERENCES notifications(id) ON UPDATE CASCADE ON DELETE CASCADE,
--     FOREIGN KEY (photo_id) REFERENCES photos(id) ON UPDATE CASCADE ON DELETE CASCADE
-- );
-- 
-- CREATE TABLE IF NOT EXISTS albums_notifications_subscriptions (
--     album_id VARCHAR(32) NOT NULL,
--     user_id VARCHAR(32) NOT NULL,
--     PRIMARY KEY (album_id, user_id),
--     FOREIGN KEY (album_id) REFERENCES albums(id) ON UPDATE CASCADE ON DELETE CASCADE,
--     FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE
-- );
-- 
-- CREATE TABLE IF NOT EXISTS photos_notifications_subscriptions (
--     photo_id VARCHAR(32) NOT NULL,
--     user_id VARCHAR(32) NOT NULL,
--     PRIMARY KEY (photo_id, user_id),
--     FOREIGN KEY (photo_id) REFERENCES photos(id) ON UPDATE CASCADE ON DELETE CASCADE,
--     FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE
-- );

-- ------------ --
-- FOREIGN KEYS --
-- ------------ --
ALTER TABLE users_configurations ADD FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE medias_sources ADD FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE accounts ADD FOREIGN KEY (id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE thirdparty_accounts ADD FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE users_roles ADD FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE users_roles ADD FOREIGN KEY (role_id) REFERENCES roles(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE users_albums_permissions ADD FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE users_albums_permissions ADD FOREIGN KEY (instance) REFERENCES albums(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE users_photos_permissions ADD FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE users_photos_permissions ADD FOREIGN KEY (instance) REFERENCES photos(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE users_videos_permissions ADD FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE users_videos_permissions ADD FOREIGN KEY (instance) REFERENCES videos(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE users_anonymous ADD FOREIGN KEY (id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE users_anonymous ADD FOREIGN KEY (creator) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE albums ADD FOREIGN KEY (owner_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE albums ADD FOREIGN KEY (parent_id) REFERENCES albums(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE photos ADD FOREIGN KEY (album_id) REFERENCES albums(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE videos ADD FOREIGN KEY (album_id) REFERENCES albums(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE comments ADD FOREIGN KEY (author_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE albums_comments ADD FOREIGN KEY (album_id) REFERENCES albums(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE albums_comments ADD FOREIGN KEY (comment_id) REFERENCES comments(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE photos_comments ADD FOREIGN KEY (photo_id) REFERENCES photos(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE photos_comments ADD FOREIGN KEY (comment_id) REFERENCES comments(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE videos_comments ADD FOREIGN KEY (video_id) REFERENCES videos(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE videos_comments ADD FOREIGN KEY (comment_id) REFERENCES comments(id) ON UPDATE CASCADE ON DELETE CASCADE;
