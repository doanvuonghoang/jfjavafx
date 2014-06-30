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

package com.jf.javafx;

import com.jf.javafx.services.Database;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 *
 * @author Hoàng Doãn
 */
public class DataSource implements javax.sql.DataSource {
    
    private String dsString;

    public DataSource() {
    }

    public String getDsString() {
        return dsString;
    }

    public void setDsString(String dsString) {
        this.dsString = dsString;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return Application._getService(Database.class).getConnection(dsString);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return Application._getService(Database.class).getConnection(dsString);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return new PrintWriter(System.out);
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        // Do nothing
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
