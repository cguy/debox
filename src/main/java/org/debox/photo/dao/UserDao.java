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
import java.util.List;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.JdbcUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.debox.photo.model.Album;
import org.debox.photo.model.DeboxUser;
import org.debox.photo.model.Role;
import org.debox.photo.model.ThirdPartyAccount;
import org.debox.photo.model.User;
import org.debox.photo.thirdparty.ServiceUtil;
import org.debox.photo.util.DatabaseUtils;
import org.debox.photo.util.StringUtils;
import org.debox.util.HttpUtils;
import org.scribe.exceptions.OAuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);
    protected static final RandomNumberGenerator GENERATOR = new SecureRandomNumberGenerator();
    protected static String SQL_GET_USERS_COUNT = "SELECT count(id) FROM users";
    protected static String SQL_GET_ROLE_COUNT = "SELECT count(id) FROM roles";
    protected static String SQL_CREATE_USER = "INSERT IGNORE INTO users (id) VALUES (?)";
    protected static String SQL_CREATE_USER_INFO = "INSERT INTO accounts VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE username = ?, password = ?, password_salt = ?";
    protected static String SQL_CREATE_USER_THIRD_PARTY = "INSERT INTO thirdparty_accounts VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE token = ?";
    protected static String SQL_GET_USER_ACCESSES = "SELECT thirdparty_account_id id, thirdparty_name provider, token, fullname FROM thirdparty_accounts WHERE user_id = ?";
    protected static String SQL_GET_AUTHORIZED_ACCOUNTS = "SELECT aa.user_id user_id, thirdparty_name provider, thirdparty_account_id id, token, fullname FROM thirdparty_accounts ta INNER JOIN accounts_accesses aa ON aa.user_id = ta.user_id WHERE album_id = ?";
    protected static String SQL_CREATE_ROLE = "INSERT INTO roles VALUES (?, ?)";
    protected static String SQL_CREATE_USER_ROLE = "INSERT INTO users_roles VALUES (?, ?)";
    protected static String GET_USER_BY_THIRD_PARTY_ACCOUNT = ""
            + "select ta.user_id user_id, role_id, name role_name, thirdparty_account_id, thirdparty_name, token, firstname, lastname "
            + "from thirdparty_accounts ta "
            + "     LEFT JOIN users_roles ur ON ur.user_id = ta.user_id "
            + "     LEFT JOIN roles r ON ur.role_id = r.id"
            + "     LEFT JOIN users u ON ta.user_id = u.id"
            + "WHERE thirdparty_name = ? AND thirdparty_account_id = ?";
    private static String SQL_DELETE_THIRD_PARTY_ACCOUNT = "DELETE FROM thirdparty_accounts WHERE user_id = ? AND thirdparty_name = ? AND thirdparty_account_id = ?";
    private static String SQL_CREATE_THIRD_PARTY_ACCESS = "INSERT INTO accounts_accesses VALUES (?, ?) ON DUPLICATE KEY UPDATE album_id = ?";
    
    private static String GET_USER = "SELECT * FROM users u INNER JOIN accounts a ON a.id = u.id WHERE u.id = ?";

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
                String lastname = resultSet.getString("lasttname");

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
            }
        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
        return result;
    }

    public int getRoleCount() throws SQLException {
        int result = -1;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection connection = DatabaseUtils.getConnection();

        try {
            statement = connection.prepareStatement(SQL_GET_ROLE_COUNT);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getInt(1);
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

    public int getUsersCount() throws SQLException {
        int result = -1;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {

            Connection connection = DatabaseUtils.getConnection();
            statement = connection.prepareStatement(SQL_GET_USERS_COUNT);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getInt(1);
            }

        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
        }
        return result;
    }

    public void save(ThirdPartyAccount user) throws SQLException {
        Connection connection = DatabaseUtils.getConnection();
        connection.setAutoCommit(false);

        PreparedStatement userStatement = null;
        PreparedStatement accountStatement = null;
        PreparedStatement roleStatement = null;
        try {
            if (user.getId() == null) {
                user.setId(StringUtils.randomUUID());

                userStatement = connection.prepareStatement(SQL_CREATE_USER);
                userStatement.setString(1, user.getId());
                userStatement.executeUpdate();

                Role role = user.getRole();
                if (role != null) {
                    roleStatement = connection.prepareStatement(SQL_CREATE_USER_ROLE);
                    roleStatement.setString(1, user.getId());
                    roleStatement.setString(2, role.getId());
                    roleStatement.executeUpdate();
                }
            }

            accountStatement = connection.prepareStatement(SQL_CREATE_USER_THIRD_PARTY);
            accountStatement.setString(1, user.getId());
            accountStatement.setString(2, user.getProviderAccountId());
            accountStatement.setString(3, user.getProviderId());
            accountStatement.setString(4, user.getToken());
            accountStatement.setString(5, user.getToken());
            accountStatement.executeUpdate();

            connection.commit();

        } catch (SQLException ex) {
            logger.error("Unable to save user, reason:", ex);
            connection.rollback();

        } finally {
            JdbcUtils.closeStatement(userStatement);
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
            userStatement.executeUpdate();

            userInfosStatement = connection.prepareStatement(SQL_CREATE_USER_INFO);
            userInfosStatement.setString(1, user.getId());
            userInfosStatement.setString(2, user.getUsername());
            userInfosStatement.setString(3, hashedPassword);
            userInfosStatement.setString(4, salt.toBase64());
            userInfosStatement.setString(5, user.getUsername());
            userInfosStatement.setString(6, hashedPassword);
            userInfosStatement.setString(7, salt.toBase64());
            userInfosStatement.executeUpdate();

            if (role != null) {
                roleStatement = connection.prepareStatement(SQL_CREATE_USER_ROLE);
                roleStatement.setString(1, user.getId());
                roleStatement.setString(2, role.getId());
                roleStatement.executeUpdate();
            }

            connection.commit();

        } catch (SQLException ex) {
            logger.error("Unable to save user, reason:", ex);
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
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
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
            if (access.getProviderId().equals("facebook")) {
                DefaultFacebookClient client = new DefaultFacebookClient(access.getToken());
                com.restfb.types.User fbUser = client.fetchObject("me", com.restfb.types.User.class);
                access.setUsername(fbUser.getName());
                access.setAccountUrl(fbUser.getLink());
                access.setFirstName(fbUser.getFirstName());
                access.setLastName(fbUser.getLastName());

            } else if (access.getProviderId().equals("google")) {
                String response = HttpUtils.getResponse("https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + access.getToken());
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(response);

                if (node.get("error") != null && node.get("error").get("code") != null) {
                    if (node.get("error").get("code").asInt() == 401) {
                        throw new OAuthException("google");
                    }
                    return null;
                }

                access.setUsername(node.get("name").asText());
                access.setAccountUrl(node.get("link").asText());
                access.setFirstName(node.get("given_name").asText());
                access.setLastName(node.get("family_name").asText());
            } else {
                return null;
            }
        }

        return access;
    }

    public void delete(ThirdPartyAccount account) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = DatabaseUtils.getConnection();
        try {
            statement = connection.prepareStatement(SQL_DELETE_THIRD_PARTY_ACCOUNT);
            statement.setString(1, account.getId());
            statement.setString(2, account.getProviderId());
            statement.setString(3, account.getProviderAccountId());
            statement.executeUpdate();
        } finally {
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
    }

    public void saveAccess(List<ThirdPartyAccount> accounts, String albumId) throws SQLException {
        Connection connection = DatabaseUtils.getConnection();
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL_CREATE_THIRD_PARTY_ACCESS);
            for (ThirdPartyAccount account : accounts) {
                statement.setString(1, account.getId());
                statement.setString(2, albumId);
                statement.setString(3, albumId);
                statement.executeUpdate();
            }
            statement.executeBatch();
        } finally {
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
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
                ThirdPartyAccount access = convert(rs.getString("user_id"), rs);
                if (access != null) {
                    result.add(access);
                }
            }
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
        return result;
    }

    public User getUser(String userId) throws SQLException {
        DeboxUser user = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        Connection connection = DatabaseUtils.getConnection();
        try {
            statement = connection.prepareStatement(GET_USER);
            statement.setString(1, userId);
            
            rs = statement.executeQuery();
            if (rs.next()) {
                user = new DeboxUser();
                user.setId(rs.getString("id"));
                user.setFirstName(rs.getString("firstname"));
                user.setLastName(rs.getString("lastname"));
                user.setAvatar(rs.getString("avatar"));
                user.setUsername(rs.getString("username"));
            }
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
        return user;
    }
}
