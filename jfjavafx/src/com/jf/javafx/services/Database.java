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

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.jf.javafx.AbstractService;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.dbcp2.BasicDataSource;

/**
 * This service read application configuration file to extract database
 * information The structure of database information in file is like:<br/>
 * <code>database<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;- jndiName<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;- url<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;- username<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;- password<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;- dsProvider (required by jndiName)<br/>
 * </code>
 *
 * @author Hoàng Doãn
 */
public class Database extends AbstractService {

    private Map<String, DBInfo> infos = new Hashtable<>();
    private Map<String, DataSource> dss = new Hashtable<>();

    @Override
    protected void _initService() {
        try {
            XMLConfiguration c = new XMLConfiguration(app.getConfig("database.properties"));
            c.configurationsAt("databases.database").stream().forEach((t) -> {
                infos.put(t.getString("jndiName"), new DBInfo(
                        t.getString("jndiName"),
                        t.getString("url"),
                        t.getString("username"),
                        t.getString("password"),
                        t.getString("dsProvider")
                ));
            });
        } catch (ConfigurationException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            getDataSource("jdbc/sample").getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Connection getConnection(String jndiName) throws SQLException {
        DBInfo i = infos.get(jndiName);
        if (i == null) {
            return null;
        }

        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", i.dbUsername);
        connectionProps.put("password", i.dbPassword);

        conn = DriverManager.getConnection(i.dbUrl, connectionProps);

        System.out.println("Connected to database");
        return conn;
    }

    public DataSource getDataSource(String jndiName) {
        DBInfo i = infos.get(jndiName);
        if (i == null) {
            return null;
        }

        DataSource ds = dss.get(jndiName);
        if (ds == null) {
            BasicDataSource t = new BasicDataSource();
            t.setDriverClassName(i.dsProvider);
            t.setUrl(i.dbUrl);
            if(!i.dbUsername.isEmpty()) t.setUsername(i.dbUsername);
            if(!i.dbPassword.isEmpty()) t.setPassword(i.dbPassword);

            ds = t;
            dss.put(jndiName, ds);
        }

        return ds;
    }
    
    public Collection<String> getAvailableDataSources() {
        return infos.keySet();
    }
    
    public DataSource getAppDataSource() {
        return getDataSource(appConfig.getString("datasource"));
    }
    
    public String getAppDBUrl() {
        return infos.get(appConfig.getString("datasource")).dbUrl;
    }
    
    public <U, V> Dao<U, V> createAppDao(Class<U> cls) {
        DataSource ds = getAppDataSource();
        String url = getAppDBUrl();
        
        try { 
            Dao<U, V> dao = DaoManager.createDao(new DataSourceConnectionSource(ds, url), cls);
            
            return dao;
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            
            return null;
        }
    }

    private class DBInfo {

        public String jndiName;
        public String dbUrl;
        public String dbUsername;
        public String dbPassword;
        public String dsProvider;

        public DBInfo(String jndiName, String dbUrl, String dbUsername, String dbPassword, String dsProvider) {
            this.jndiName = jndiName;
            this.dbUrl = dbUrl;
            this.dbUsername = dbUsername;
            this.dbPassword = dbPassword;
            this.dsProvider = dsProvider;
        }
        
        @Override
        public String toString() {
            return jndiName;
        }
    }
}
