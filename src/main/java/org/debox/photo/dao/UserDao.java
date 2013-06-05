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

import com.restfb.DefaultFacebookClient;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.JdbcUtils;
import org.debox.photo.model.Album;
import org.debox.photo.model.user.DeboxUser;
import org.debox.photo.model.Role;
import org.debox.photo.model.user.AnonymousUser;
import org.debox.photo.model.user.DeboxPermission;
import org.debox.photo.model.user.ThirdPartyAccount;
import org.debox.photo.model.user.User;
import org.debox.photo.thirdparty.ServiceUtil;
import org.debox.photo.util.DatabaseUtils;
import org.debox.photo.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class UserDao {

    private static final Logger log = LoggerFactory.getLogger(UserDao.class);
    
    protected static final RandomNumberGenerator GENERATOR = new SecureRandomNumberGenerator();
    
    protected static String SQL_CREATE_USER = "INSERT INTO users (id, firstname, lastname) VALUES (?, ?, ?)";
    protected static String SQL_CREATE_USER_NO_INFOS = "INSERT INTO users (id) VALUES (?)";
    
    protected static String SQL_CREATE_USER_INFO = "INSERT INTO accounts VALUES (?, ?, ?, ?)";
    protected static String SQL_UPDATE_CREDENTIALS = "UPDATE accounts SET username = ?, password = ?, password_salt = ? WHERE id = ?";
    
    protected static String SQL_UPDATE_USER_INFO = "UPDATE users SET firstname = ?, lastname = ?, avatar = ? WHERE id = ?";
    protected static String SQL_CREATE_USER_THIRD_PARTY = "INSERT INTO thirdparty_accounts VALUES (?, ?, ?, ?)";
    protected static String SQL_UPDATE_USER_THIRD_PARTY = "UPDATE thirdparty_accounts SET token = ? WHERE user_id = ? AND thirdparty_name = ?";
    protected static String SQL_GET_USER_THIRD_PARTY_BY_PROVIDER = "SELECT count(*) c FROM thirdparty_accounts WHERE user_id = ? AND thirdparty_name = ?";
    
    protected static String SQL_GET_USER_ACCESSES = "SELECT thirdparty_account_id id, thirdparty_name provider, token FROM thirdparty_accounts WHERE user_id = ?";
    protected static String SQL_GET_USER_ACCESSES_COUNT = "SELECT count(thirdparty_account_id) count FROM thirdparty_accounts WHERE user_id = ?";
    
    protected static String SQL_GET_AUTHORIZED_ACCOUNTS = ""
            + "SELECT"
            + "    aa.user_id user_id, thirdparty_name provider, thirdparty_account_id id, token "
            + "FROM"
            + "    thirdparty_accounts ta "
            + "    INNER JOIN users_albums_permissions aa ON aa.user_id = ta.user_id "
            + "WHERE "
            + "    aa.instance = ? AND (aa.actions LIKE '%read%' OR actions = '*')";
    
    protected static String SQL_CREATE_ROLE = "INSERT INTO roles VALUES (?, ?)";
    protected static String SQL_CREATE_USER_ROLE = "INSERT INTO users_roles VALUES (?, ?)";
    protected static String GET_USER_BY_THIRD_PARTY_ACCOUNT = ""
            + "SELECT ta.user_id user_id, role_id, name role_name, thirdparty_account_id, thirdparty_name, token, firstname, lastname, avatar "
            + "FROM thirdparty_accounts ta "
            + "     INNER JOIN users_roles ur ON ur.user_id = ta.user_id "
            + "     INNER JOIN roles r ON ur.role_id = r.id "
            + "     INNER JOIN users u ON ta.user_id = u.id "
            + "WHERE thirdparty_name = ? AND thirdparty_account_id = ?";
    private static String SQL_DELETE_THIRD_PARTY_ACCOUNT = "DELETE FROM thirdparty_accounts WHERE user_id = ? AND thirdparty_name = ? AND thirdparty_account_id = ?";
    
    private static String GET_USER = "SELECT id, lastname, firstname, avatar FROM users u WHERE u.id = ?";
    private static String GET_ROLE = "SELECT id, name FROM roles WHERE name = ?";
    private static String DELETE_USER = "DELETE FROM users WHERE id = ?";
    
    /* ANONYMOUS USER */
    
    protected static String SQL_GET_ANONYMOUS_USERS_BY_ALBUM_ACCESS = ""
            + "SELECT "
            + "    ua.id id, ua.label label, ua.creator creator "
            + "FROM "
            + "    users_anonymous ua "
            + "    LEFT JOIN users u ON u.id = ua.id "
            + "    LEFT JOIN users_albums_permissions uap ON u.id = uap.user_id "
            + "WHERE uap.instance = ?";
    
    protected static String SQL_GET_ANONYMOUS_USERS_BY_CREATOR = "SELECT au.id id, au.label label, au.creator creator FROM users_anonymous au WHERE creator = ?";
    
    protected static String SQL_CREATE = "INSERT INTO users_anonymous VALUES (?, ?, ?)";
    protected static String SQL_UPDATE = "UPDATE users_anonymous SET id = ?, label = ? WHERE id = ?";
    
    protected static String SQL_DELETE_BY_USER = "DELETE FROM tokens where owner_id = ?";
    protected static String SQL_DELETE = "DELETE FROM tokens WHERE id = ?";
    
    protected static String SQL_GET_ALL_WITHOUT_ALBUMS = ""
            + "SELECT * FROM tokens "
            + "ORDER BY label, tokens.id";
    
    protected static String SQL_GET_BY_ALBUM_ID = ""
            + "SELECT tokens.id id FROM tokens "
            + "LEFT JOIN albums_tokens ON id = token_id "
            + "WHERE album_id = ?";
    
    protected static String SQL_GET_BY_ID = ""
            + "SELECT id, label, creator FROM users_anonymous "
            + "WHERE id = ? ";
    
    protected PermissionDao permissionDao = new PermissionDao();
    protected AlbumDao albumDao = new AlbumDao();

    public ThirdPartyAccount getUser(String provider, String providerAccountId) throws SQLException {
        ThirdPartyAccount result = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection connection = DatabaseUtils.getConnection();
        try {
            statement = connection.prepareStatement(GET_USER_BY_THIRD_PARTY_ACCOUNT);
            statement.setString(1, provider);
            statement.setString(2, providerAccountId);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String userId = resultSet.getString("user_id");
                String accountId = resultSet.getString("thirdparty_account_id");
                String roleId = resultSet.getString("role_id");
                String roleName = resultSet.getString("role_name");
                String token = resultSet.getString("token");
                String firstname = resultSet.getString("firstname");
                String lastname = resultSet.getString("lastname");
                String avatar = resultSet.getString("avatar");

                Role role = new Role();
                role.setId(roleId);
                role.setName(roleName);

                result = new ThirdPartyAccount();
                result.setId(userId);
                result.setProvider(ServiceUtil.getProvider(provider));
                result.setProviderAccountId(accountId);
                result.setToken(token);
                result.setRole(role);
                result.setFirstName(firstname);
                result.setLastName(lastname);
                result.setAvatar(avatar);
            }
        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
        return result;
    }

    public void save(Role role) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = DatabaseUtils.getConnection();

        try {
            statement = connection.prepareStatement(SQL_CREATE_ROLE);
            statement.setString(1, role.getId());
            statement.setString(2, role.getName());
            statement.executeUpdate();
        } finally {
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
    }
    
    public void save(AnonymousUser user) throws SQLException {
        QueryRunner queryRunner = new QueryRunner();
        try (Connection connection = DatabaseUtils.getConnection()) {
            connection.setAutoCommit(false);
            queryRunner.update(connection, SQL_CREATE_USER, user.getId(), null, null);
            queryRunner.update(connection, SQL_CREATE, user.getId(), user.getLabel(), user.getOwnerId());
            DbUtils.commitAndCloseQuietly(connection);
        }
    }
    
    public void save(AnonymousUser user, String oldId) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        queryRunner.update(SQL_UPDATE, user.getId(), user.getLabel(), oldId);
    }

    public void save(ThirdPartyAccount user, Role role) throws SQLException {
        Connection connection = DatabaseUtils.getConnection();
        connection.setAutoCommit(false);

        PreparedStatement userStatement = null;
        PreparedStatement accountStatement = null;
        PreparedStatement updateAccountStatement = null;
        PreparedStatement roleStatement = null;
        PreparedStatement accountsCountStatement;
        PreparedStatement updateUserStatement;
        ResultSet rs = null;
        try {
            if (user.getId() == null) {
                user.setId(StringUtils.randomUUID());

                userStatement = connection.prepareStatement(SQL_CREATE_USER_NO_INFOS);
                userStatement.setString(1, user.getId());
                userStatement.executeUpdate();

                if (role != null) {
                    roleStatement = connection.prepareStatement(SQL_CREATE_USER_ROLE);
                    roleStatement.setString(1, user.getId());
                    roleStatement.setString(2, role.getId());
                    roleStatement.executeUpdate();
                }
            }

            updateAccountStatement = connection.prepareStatement(SQL_UPDATE_USER_THIRD_PARTY);
            updateAccountStatement.setString(1, user.getToken());
            updateAccountStatement.setString(2, user.getId());
            updateAccountStatement.setString(3, user.getProviderId());
            int changedRows = updateAccountStatement.executeUpdate();
            
            if (changedRows == 0) {
                accountStatement = connection.prepareStatement(SQL_CREATE_USER_THIRD_PARTY);
                accountStatement.setString(1, user.getId());
                accountStatement.setString(2, user.getProviderAccountId());
                accountStatement.setString(3, user.getProviderId());
                accountStatement.setString(4, user.getToken());
                accountStatement.executeUpdate();

                accountsCountStatement = connection.prepareStatement(SQL_GET_USER_ACCESSES_COUNT);
                accountsCountStatement.setString(1, user.getId());
                rs = accountsCountStatement.executeQuery();
                if (rs.next() && rs.getInt(1) == 1) {
                    updateUserStatement = connection.prepareStatement(SQL_UPDATE_USER_INFO);
                    updateUserStatement.setString(1, user.getFirstName());
                    updateUserStatement.setString(2, user.getLastName());
                    updateUserStatement.setString(3, user.getAvatarUrl());
                    updateUserStatement.setString(4, user.getId());
                    updateUserStatement.executeUpdate();
                }
            }

            connection.commit();

        } catch (SQLException ex) {
            log.error("Unable to save user, reason:", ex);
            connection.rollback();

        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(userStatement);
            JdbcUtils.closeStatement(updateAccountStatement);
            JdbcUtils.closeStatement(accountStatement);
            JdbcUtils.closeStatement(roleStatement);
            connection.setAutoCommit(true);
            JdbcUtils.closeConnection(connection);
        }
    }

    public void save(DeboxUser user, Role role) throws SQLException {
        ByteSource salt = GENERATOR.nextBytes();
        String hashedPassword = new Sha256Hash(user.getPassword(), salt.toBase64(), 1024).toBase64();

        Connection connection = DatabaseUtils.getConnection();
        connection.setAutoCommit(false);

        PreparedStatement userStatement = null;
        PreparedStatement userInfosStatement = null;
        PreparedStatement roleStatement = null;
        try {
            userStatement = connection.prepareStatement(SQL_CREATE_USER);
            userStatement.setString(1, user.getId());
            userStatement.setString(2, user.getFirstName());
            userStatement.setString(3, user.getLastName());
            userStatement.executeUpdate();

            userInfosStatement = connection.prepareStatement(SQL_CREATE_USER_INFO);
            userInfosStatement.setString(1, user.getId());
            userInfosStatement.setString(2, user.getUsername());
            userInfosStatement.setString(3, hashedPassword);
            userInfosStatement.setString(4, salt.toBase64());
            userInfosStatement.executeUpdate();

            if (role != null) {
                roleStatement = connection.prepareStatement(SQL_CREATE_USER_ROLE);
                roleStatement.setString(1, user.getId());
                roleStatement.setString(2, role.getId());
                roleStatement.executeUpdate();
            }

            connection.commit();

        } catch (SQLException ex) {
            log.error("Unable to save user, reason:", ex);
            connection.rollback();
            throw ex;

        } finally {
            JdbcUtils.closeStatement(userStatement);
            JdbcUtils.closeStatement(userInfosStatement);
            JdbcUtils.closeStatement(roleStatement);
            connection.setAutoCommit(true);
            JdbcUtils.closeConnection(connection);
        }
    }
    
    public void updateUserInfo(DeboxUser user) throws SQLException {
        Connection connection = DatabaseUtils.getConnection();
        PreparedStatement updateUserStatement = null;
        try {
            updateUserStatement = connection.prepareStatement(SQL_UPDATE_USER_INFO);
            updateUserStatement.setString(1, user.getFirstName());
            updateUserStatement.setString(2, user.getLastName());
            updateUserStatement.setString(3, user.getAvatar());
            updateUserStatement.setString(4, user.getId());
            updateUserStatement.executeUpdate();

        } finally {
            JdbcUtils.closeStatement(updateUserStatement);
            JdbcUtils.closeConnection(connection);
        }
    }

    public void update(DeboxUser user) throws SQLException {
        ByteSource salt = GENERATOR.nextBytes();
        String hashedPassword = new Sha256Hash(user.getPassword(), salt.toBase64(), 1024).toBase64();
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        queryRunner.update(SQL_UPDATE_CREDENTIALS, user.getUsername(), hashedPassword, salt.toBase64(), user.getId());
    }
    
    public List<ThirdPartyAccount> getThirdPartyAccounts(User user) throws SQLException, IOException {
        Connection connection = DatabaseUtils.getConnection();
        List<ThirdPartyAccount> result = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {

            statement = connection.prepareStatement(SQL_GET_USER_ACCESSES);
            statement.setString(1, user.getId());
            rs = statement.executeQuery();
            while (rs.next()) {
                try {
                    ThirdPartyAccount access = convert(user.getId(), rs);
                    if (access != null) {
                        result.add(access);
                    }
                } catch (SQLException | IOException ex) {
                    log.error(ex.getMessage(), ex);
                }
            }
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
        return result;
    }
    
    public AnonymousUser getAnonymousUser(String userId) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        AnonymousUser result = queryRunner.query(SQL_GET_BY_ID, new ResultSetHandler<AnonymousUser>() {
            @Override
            public AnonymousUser handle(ResultSet rs) throws SQLException {
                AnonymousUser user = null;
                if (rs.next()) {
                    String id = rs.getString("id");
                    String label = rs.getString("label");
                    String creator = rs.getString("creator");

                    user = new AnonymousUser();
                    user.setId(id);
                    user.setLabel(label);
                    user.setOwnerId(creator);
                }
                return user;
            }
        }, userId);
        return result;
    }
    
    public List<AnonymousUser> getAllAnonymousUsersWithAccessToAlbum(Album album) throws SQLException {
        if (album == null) {
            return null;
        }
        
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        List<AnonymousUser> result = queryRunner.query(SQL_GET_ANONYMOUS_USERS_BY_ALBUM_ACCESS, new ResultSetHandler<List<AnonymousUser>>() {
            @Override
            public List<AnonymousUser> handle(ResultSet rs) throws SQLException {
                List<AnonymousUser> result = new ArrayList<>();
                while (rs.next()) {
                    String id = rs.getString("id");
                    String label = rs.getString("label");
                    String creator = rs.getString("creator");

                    AnonymousUser user = new AnonymousUser();
                    user.setId(id);
                    user.setLabel(label);
                    user.setOwnerId(creator);
                    
                    result.add(user);
                }
                return result;
            }
        }, album.getId());
        
        return result;
    }
    
    public List<Pair<AnonymousUser, List<Album>>> getAllAnonymousUsersByCreator(String ownerId) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        List<AnonymousUser> users = queryRunner.query(SQL_GET_ANONYMOUS_USERS_BY_CREATOR, new ResultSetHandler<List<AnonymousUser>>() {
            @Override
            public List<AnonymousUser> handle(ResultSet rs) throws SQLException {
                List<AnonymousUser> result = new ArrayList<>();
                while (rs.next()) {
                    String id = rs.getString("id");
                    String label = rs.getString("label");
                    String creator = rs.getString("creator");

                    AnonymousUser user = new AnonymousUser();
                    user.setId(id);
                    user.setLabel(label);
                    user.setOwnerId(creator);
                    
                    result.add(user);
                }
                return result;
            }
        }, ownerId);
        
        List<Pair<AnonymousUser, List<Album>>> result = new ArrayList<>(users.size());
        for (AnonymousUser user : users) {
            result.add(Pair.of(user, albumDao.getAllAlbums(user.getId())));
        }
        
        return result;
    }

    protected ThirdPartyAccount convert(String userId, ResultSet rs) throws SQLException, IOException {
        ThirdPartyAccount access = new ThirdPartyAccount(
                ServiceUtil.getProvider(rs.getString("provider")),
                rs.getString("id"),
                rs.getString("token"));
        access.setId(userId);

        if (access.getToken() != null) {
            switch (access.getProviderId()) {
                case "facebook":
                    DefaultFacebookClient client = new DefaultFacebookClient(access.getToken());
                    com.restfb.types.User fbUser = client.fetchObject("me", com.restfb.types.User.class);
                    access.setUsername(fbUser.getName());
                    access.setAccountUrl(fbUser.getLink());
                    access.setFirstName(fbUser.getFirstName());
                    access.setLastName(fbUser.getLastName());
                    break;
                default:
                    return null;
            }
        }

        return access;
    }
    
    public void saveAnonymousViewersForAlbum(Album album, List<String> users) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            connection.setAutoCommit(false);
            for (String user : users) {
                saveAnonymousViewerForAlbum(connection, album, user);
            }
            connection.commit();
        }
    }
    
    public void saveAnonymousViewerForAlbum(Connection connection, Album album, String userId) throws SQLException {
        DeboxPermission existing = permissionDao.getPermission(connection, userId, "album", album.getId());
        if (existing == null) {
            permissionDao.save(connection, userId, new DeboxPermission("album", "read", album.getId()));
        }
    }


    public List<ThirdPartyAccount> getAuthorizedThirdPartyAccounts(Album album) throws SQLException, IOException {
        List<ThirdPartyAccount> result = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet rs = null;
        Connection connection = DatabaseUtils.getConnection();
        try {
            statement = connection.prepareStatement(SQL_GET_AUTHORIZED_ACCOUNTS);
            statement.setString(1, album.getId());
            rs = statement.executeQuery();
            while (rs.next()) {
                ThirdPartyAccount access = new ThirdPartyAccount(
                    ServiceUtil.getProvider(rs.getString("provider")),
                    rs.getString("id"),
                    rs.getString("token"));
                access.setId(rs.getString("user_id"));
                result.add(access);
            }
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
        return result;
    }

    public User getUser(String userId) throws SQLException {
        Map<String, String> map = new HashMap<>(4);
        map.put("id", "id");
        map.put("firstname", "firstName");
        map.put("lastname", "lastName");
        map.put("avatar", "avatar");
        
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        DeboxUser user = queryRunner.query(GET_USER, new BeanHandler<>(DeboxUser.class, new BasicRowProcessor(new BeanProcessor(map))), userId);
        return user;
    }

    public Role getRole(String name) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        Role role = queryRunner.query(GET_ROLE, new BeanHandler<>(Role.class), name);
        return role;
    }

    public void deleteUser(DeboxUser user) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        queryRunner.update(DELETE_USER, user.getId());
    }

    public void delete(AnonymousUser user) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        queryRunner.update(DELETE_USER, user.getId());
    }
    
    public void delete(ThirdPartyAccount account) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        queryRunner.update(SQL_DELETE_THIRD_PARTY_ACCOUNT, account.getId(), account.getProviderId(), account.getProviderAccountId());
    }
    
}
