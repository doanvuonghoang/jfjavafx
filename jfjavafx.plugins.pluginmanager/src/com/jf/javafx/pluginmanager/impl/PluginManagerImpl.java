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

package com.jf.javafx.pluginmanager.impl;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.jf.javafx.Application;
import com.jf.javafx.pluginmanager.PluginManager;
import com.jf.javafx.services.Database;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.xeoh.plugins.base.Plugin;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.Init;

/**
 *
 * @author Hoàng Doãn
 */
@PluginImplementation
public class PluginManagerImpl implements PluginManager {
    
    private Dao<com.jf.javafx.pluginmanager.impl.datamodels.Plugin, Long> dao;
    
    @Init
    public void init() {
        if(!isInstalled(this.getClass().getName())) install(this.getClass().getName());
        
        dao = Application._getService(Database.class).createAppDao(com.jf.javafx.pluginmanager.impl.datamodels.Plugin.class);
    }

    @Override
    public boolean isInstalled(String pluginName) {
        if(dao != null) {
            try {
                return !dao.queryForEq("pluginClassName", pluginName).isEmpty();
            } catch (SQLException ex) {
                Logger.getLogger(PluginManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return false;
    }

    @Override
    public void install(Plugin p) {
        try {
            p.getClass().getMethod("installPlugin").invoke(p);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(PluginManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        com.jf.javafx.pluginmanager.impl.datamodels.Plugin model = new com.jf.javafx.pluginmanager.impl.datamodels.Plugin();
        model.pluginClassName = p.getClass().getName();
        model.creator = this.getClass().getName();
        model.createdTime = Calendar.getInstance().getTime();
        
        if(dao != null) try {
            dao.create(model);
        } catch (SQLException ex) {
            Logger.getLogger(PluginManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void install(Class<Plugin> p) {
        try {
            Plugin ist = p.newInstance();
            install(ist);
        } catch (IllegalAccessException | InstantiationException ex) {
            Logger.getLogger(PluginManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void install(String pluginClassName) {
        try {
            install((Class<Plugin>) Class.forName(pluginClassName));
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PluginManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void uninstall(String pluginClassName) {
    }
    
    public void installPlugin() throws SQLException {
        TableUtils.createTableIfNotExists(new JdbcConnectionSource(Application._getService(Database.class).getAppDBUrl()), com.jf.javafx.pluginmanager.impl.datamodels.Plugin.class);
    }
}
