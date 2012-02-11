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
package org.debox.photo.dao.mysql;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class JdbcMysqlRealm extends JdbcRealm {

    private static final Logger logger = LoggerFactory.getLogger(JdbcMysqlRealm.class);
    
    protected static final RandomNumberGenerator GENERATOR = new SecureRandomNumberGenerator();
    
    protected static ComboPooledDataSource comboPooledDataSource;

    public JdbcMysqlRealm() {
        this.setDataSource(getDataSource());
        this.setSaltStyle(SaltStyle.COLUMN);
    }

    public static synchronized ComboPooledDataSource getDataSource() {
        if (comboPooledDataSource == null) {
            try {
                comboPooledDataSource = new ComboPooledDataSource();
                comboPooledDataSource.setDriverClass("com.mysql.jdbc.Driver"); //loads the jdbc driver            
                comboPooledDataSource.setJdbcUrl("jdbc:mysql://localhost/debox-photos?autoReconnect=true");
                comboPooledDataSource.setUser("root");
                comboPooledDataSource.setPassword("");
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        return comboPooledDataSource;
    }
    
}
