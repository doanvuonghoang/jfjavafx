/*
 * Copyright (C) 2014 Hoàng Doãn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jf.javafx.services;

import com.jf.javafx.AbstractService;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;

/**
 *
 * @author Hoàng Doãn
 */
public class Database extends AbstractService {

    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private String jndiName;
    private String dsProvider;

    private BasicDataSource ds;

    @Override
    protected void _initService() {
        dbUrl = appConfig.getString("database.url");
        dbUsername = appConfig.getString("database.username");
        dbPassword = appConfig.getString("database.password");

        jndiName = appConfig.getString("database.jndiName");
        dsProvider = appConfig.getString("database.dsProvider");

        // publish JNDI name
    }

    /**
     * @return the dbUrl
     */
    public String getDbUrl() {
        return dbUrl;
    }

    /**
     * @return the dbUsername
     */
    public String getDbUsername() {
        return dbUsername;
    }

    /**
     * @return the dbPassword
     */
    public String getDbPassword() {
        return dbPassword;
    }

    public Connection getConnection() throws SQLException {
        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", dbUsername);
        connectionProps.put("password", dbPassword);

        conn = DriverManager.getConnection(dbUrl, connectionProps);

        System.out.println("Connected to database");
        return conn;
    }

    public DataSource getDataSource() {
        try {
            if (ds == null) {
                ds = (BasicDataSource) Class.forName(dsProvider).newInstance();
                ds.setUrl(dbUrl);
                ds.setUsername(dbUsername);
                ds.setPassword(dbPassword);
            }

            return ds;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
