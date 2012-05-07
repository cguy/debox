/*
 * #%L
 * debox-photos
 * %%
 * Copyright (C) 2012 Debox
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.debox.photo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.shiro.util.JdbcUtils;
import org.debox.photo.model.Album;
import org.debox.photo.model.Token;
import org.debox.photo.util.DatabaseUtils;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class TokenDao {

    protected static String SQL_CREATE = "INSERT INTO tokens VALUES (?, ?) ON DUPLICATE KEY UPDATE label = ?";
    protected static String SQL_CREATE_TOKEN_ALBUM = "INSERT IGNORE INTO albums_tokens VALUES (?, ?)";
    protected static String SQL_DELETE_TOKEN_ALBUM = "DELETE FROM albums_tokens WHERE token_id = ?";
    protected static String SQL_DELETE = "DELETE FROM tokens WHERE id = ?";
    
    protected static String SQL_GET_ALL = ""
            + "SELECT tokens.id id, label, album_id, name FROM tokens "
            + "LEFT JOIN albums_tokens ON id = token_id "
            + "LEFT JOIN albums on album_id = albums.id "
            + "ORDER BY label, tokens.id";
    
    protected static String SQL_GET_BY_ID = ""
            + "SELECT tokens.id id, label, album_id, name FROM tokens "
            + "LEFT JOIN albums_tokens ON id = token_id "
            + "LEFT JOIN albums on album_id = albums.id "
            + "WHERE tokens.id = ? ORDER BY label, tokens.id";

    public void save(Token token) throws SQLException {
        Connection connection = DatabaseUtils.getConnection();
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL_CREATE);
            statement.setString(1, token.getId());
            statement.setString(2, token.getLabel());
            statement.setString(3, token.getLabel());
            statement.executeUpdate();
            
            statement = connection.prepareStatement(SQL_DELETE_TOKEN_ALBUM);
            statement.setString(1, token.getId());
            statement.executeUpdate();
            
            statement = connection.prepareStatement(SQL_CREATE_TOKEN_ALBUM);
            for (Album album : token.getAlbums()) {
                statement.setString(1, album.getId());
                statement.setString(2, token.getId());
                statement.addBatch();
            }
            statement.executeBatch();

        } finally {
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
    }
    
    public Token getById(String id) throws SQLException {
        Token result = null;
        Connection connection = DatabaseUtils.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL_GET_BY_ID);
            statement.setString(1, id);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String label = resultSet.getString("label");
                String albumId = resultSet.getString("album_id");
                String albumName = resultSet.getString("name");
                if (result == null || !result.getId().equals(id)) {
                    result = new Token();
                    result.setId(id);
                    result.setLabel(label);
                }
                if (albumId != null) {
                    Album album = new Album();
                    album.setId(albumId);
                    album.setName(albumName);
                    result.getAlbums().add(album);
                }
            }

        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
        return result;
    }

    public List<Token> getAll() throws SQLException {
        List<Token> result = new ArrayList<>();
        Connection connection = DatabaseUtils.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL_GET_ALL);
            resultSet = statement.executeQuery();

            Token token = null;
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String label = resultSet.getString("label");
                String albumId = resultSet.getString("album_id");
                String albumName = resultSet.getString("name");
                if (token == null || !token.getId().equals(id)) {
                    token = new Token();
                    token.setId(id);
                    token.setLabel(label);
                    result.add(token);
                }

                if (albumId != null) {
                    Album album = new Album();
                    album.setId(albumId);
                    album.setName(albumName);
                    token.getAlbums().add(album);
                }
            }

        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
        return result;
    }
    
    public void delete(String id) throws SQLException {
        Connection connection = DatabaseUtils.getConnection();
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL_DELETE);
            statement.setString(1, id);
            statement.executeUpdate();

        } finally {
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
    }
    
}
