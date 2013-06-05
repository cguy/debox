package org.debox.photo.dao;

/*
 * #%L
 * debox-photos
 * %%
 * Copyright (C) 2012 - 2013 Debox
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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.debox.photo.model.Album;
import org.debox.photo.model.user.AnonymousUser;
import org.debox.photo.model.user.DeboxPermission;
import org.debox.photo.util.DatabaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class PermissionDao extends DeboxJdbcRealm {
    
    private static final Logger log = LoggerFactory.getLogger(PermissionDao.class);
    
    protected static final String SAVE_PERMISSION = "INSERT INTO %s VALUES (?, ?, ?)";
    protected static final String UPDATE_PERMISSION = "UPDATE %s SET actions = ? WHERE user_id = ? AND instance = ?";
    protected static final String DELETE_PERMISSION = "DELETE FROM %s WHERE user_id = ? AND instance = ?";
    protected static final String DELETE_READ_PERMISSIONS_FOR_USER = "DELETE FROM %s WHERE user_id = ? AND instance = ? AND actions LIKE '%read%'";
    protected static final String DELETE_READ_PERMISSIONS = "DELETE FROM %s WHERE instance = ? AND actions LIKE '%read%'";
    protected static final String DELETE_INSTANCE_PERMISSIONS = "DELETE FROM %s WHERE instance = ?";
    protected static final String SELECT_PERMISSION = "SELECT actions, instance FROM %s WHERE user_id = ? AND instance = ?";
    protected static final String SELECT_USERS_FOR_READ_PERMISSIONS = "SELECT user_id FROM %s WHERE instance = ? AND actions LIKE '%read%'";
    protected static final String SELECT_READ_PERMISSIONS = "SELECT user_id, actions, instance FROM %s WHERE instance = ? AND actions LIKE '%read%'";
    
    
    protected static AlbumDao albumDao = new AlbumDao();
    
    public Set<String> getAuthorizedUsers(DeboxPermission permission) {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        String query = SELECT_USERS_FOR_READ_PERMISSIONS.replace("%s", getPermissionsTable(permission));
        Set<String> result = null;
        try {
            result = queryRunner.query(query, new ResultSetHandler<Set<String>>() {
                @Override
                public Set<String> handle(ResultSet rs) throws SQLException {
                    Set<String> result = new HashSet<>();
                    while (rs.next()) {
                        result.add(rs.getString(1));
                    }
                    return result;
                }
            }, permission.getInstance());
        } catch (SQLException ex) {
            log.error(ex.getMessage(), ex);
        }
        return result;
    }
    
    protected DeboxPermission getPermission(Connection c, String userId, final String domain, String instanceId) {
        QueryRunner queryRunner = new QueryRunner();
        String query = String.format(SELECT_PERMISSION, getPermissionsTable(domain));
        DeboxPermission result = null;
        try {
            result = queryRunner.query(c, query, new ResultSetHandler<DeboxPermission>() {
                @Override
                public DeboxPermission handle(ResultSet rs) throws SQLException {
                    if (!rs.next()) {
                        return null;
                    }
                    String actions = rs.getString(1);
                    String instance = rs.getString(2);
                    return new DeboxPermission(domain, actions, instance);
                }
            }, userId, instanceId);
        } catch (SQLException ex) {
            log.error(ex.getMessage(), ex);
        }
        return result;
    }
   
    public void save(Connection connection, String userId, DeboxPermission permission) throws SQLException {
        DeboxPermission existing = getPermission(connection, userId, permission.getDomain(), permission.getInstance());
        if (existing == null) {
            QueryRunner queryRunner = new QueryRunner();
            String query = String.format(SAVE_PERMISSION, getPermissionsTable(permission));
            queryRunner.update(connection, query, userId, permission.getInstance(), permission.getAction());
            
        } else {
            QueryRunner queryRunner = new QueryRunner();
            String query = String.format(UPDATE_PERMISSION, getPermissionsTable(permission));
            queryRunner.update(connection, query, permission.getAction(), userId, permission.getInstance());
        }
    }
    
    public void delete(Connection connection, String userId, DeboxPermission permission) throws SQLException {
        String query = DELETE_READ_PERMISSIONS_FOR_USER.replace("%s", getPermissionsTable(permission));
        
        QueryRunner queryRunner = new QueryRunner();
        queryRunner.update(connection, query, userId, permission.getInstance());
    }
    
    public void deleteReadPermissionsForInstance(Connection connection, DeboxPermission permission) throws SQLException {
        String query = DELETE_READ_PERMISSIONS.replace("%s", getPermissionsTable(permission));
        
        QueryRunner queryRunner = new QueryRunner();
        queryRunner.update(connection, query, permission.getInstance());
    }
    
    protected String getPermissionsTable(DeboxPermission permission) {
        return getPermissionsTable(permission.getDomain());
    }
    
    protected String getPermissionsTable(String domain) {
        String table = "users_albums_permissions"; // Default table
        switch (domain) {
            case "photo":
                table = "users_photos_permissions";
                break;
            case "video":
                table = "users_videos_permissions";
                break;
        }
        return table;
    }
    
    public void savePermission(Album album) throws SQLException {
        List<Pair<String, DeboxPermission>> permissions = getReadPermissions(album);
        try (Connection c = DatabaseUtils.getConnection()) {
            c.setAutoCommit(false);
            
            for (Pair<String, DeboxPermission> permission : permissions) {
                
                String[] actions = permission.getValue().getAction().split(",");
                Set<String> actionsSet = new HashSet(Arrays.asList(actions));
                if (album.isDownloadable()) {
                    actionsSet.add("download");
                } else {
                    actionsSet.remove("download");
                }
                
                permission.getValue().setAction(StringUtils.join(actionsSet, ","));
                
                save(c, permission.getKey(), permission.getValue());
                
            }
            
            
            c.commit();
        }
    }
    
    protected List<Pair<String, DeboxPermission>> getReadPermissions(Album album) {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        String query = SELECT_READ_PERMISSIONS.replace("%s", getPermissionsTable("album"));
        List<Pair<String, DeboxPermission>> result = null;
        try {
            result = queryRunner.query(query, new ResultSetHandler<List<Pair<String, DeboxPermission>>>() {
                @Override
                public List<Pair<String, DeboxPermission>> handle(ResultSet rs) throws SQLException {
                    List<Pair<String, DeboxPermission>> result = new ArrayList<>();
                    while (rs.next()) {
                        String userId = rs.getString(1);
                        String actions = rs.getString(2);
                        String instance = rs.getString(3);
                        
                        DeboxPermission permission = new DeboxPermission(DeboxPermission.DOMAIN_ALBUM, actions, instance);
                        result.add(Pair.of(userId, permission));
                    }
                    return result;
                }
            }, album.getId());
        } catch (SQLException ex) {
            log.error(ex.getMessage(), ex);
        }
        return result;
    }
    
    /**
     * Provides read permission to user identified by its id (<code>userId</code>) for given <code>album</code> and its (grand) parent album.
     */
    public void saveReadPermission(String userId, Album album) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            connection.setAutoCommit(false);
            while (album != null) {
                saveReadPermission(connection, userId, album);
                album = albumDao.getAlbum(album.getParentId());
            }
            connection.commit();
        }
    }
    
    /**
     * Provides read permission to user identified by its id (<code>userId</code>) for given <code>album</code> and its (grand) parent album.
     */
    protected void saveReadPermission(Connection connection, String userId, Album album) throws SQLException {
        save(connection, userId, new DeboxPermission("album", "read", album.getId()));
    }
    
    /**
     * Delete read permission to <code>userId</code> for given <code>album</code> and its sub-albums.
     */
    public void deleteReadPermission(String userId, Album album) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            connection.setAutoCommit(false);
            deleteReadPermission(connection, userId, album);
            connection.commit();
        }
    }
    
    /**
     * Delete read permission to <code>userId</code> for given <code>album</code> and its sub-albums.
     */
    protected void deleteReadPermission(Connection connection, String userId, Album album) throws SQLException {
        delete(connection, userId, new DeboxPermission("album", "read", album.getId()));
        List<Album> children = albumDao.getAlbums(userId, album.getId());
        for (Album child : children) {
            deleteReadPermission(connection, userId, child);
        }
    }

}
