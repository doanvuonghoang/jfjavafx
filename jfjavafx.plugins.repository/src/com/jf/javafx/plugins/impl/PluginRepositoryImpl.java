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
package com.jf.javafx.plugins.impl;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.jf.javafx.Application;
import com.jf.javafx.MsgBox;
import com.jf.javafx.plugins.PluginRepository;
import com.jf.javafx.plugins.ResourceRepository;
import com.jf.javafx.plugins.impl.datamodels.Resource;
import com.jf.javafx.services.Database;
import com.jf.javafx.services.Security;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.xeoh.plugins.base.Plugin;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.Init;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;
import net.xeoh.plugins.base.annotations.meta.Author;
import net.xeoh.plugins.base.annotations.meta.Version;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.shiro.authz.AuthorizationException;

/**
 *
 * @author Hoàng Doãn
 */
@PluginImplementation
@Author(name = "Hoang Doan")
@Version(version = 1000)
public class PluginRepositoryImpl implements PluginRepository {

    private Dao<com.jf.javafx.plugins.impl.datamodels.Plugin, Long> dao;
    
    @InjectPlugin
    public ResourceRepository rr;

    @Init
    public void init() {
        dao = Application._getService(Database.class).createAppDao(com.jf.javafx.plugins.impl.datamodels.Plugin.class);

        if (!isInstalled(this.getClass().getName())) {
            install(this.getClass().getName());
        }
        
        if (!isInstalled(rr.getClass().getName())) {
            install(rr);
        }
    }

    @Override
    public boolean isInstalled(String pluginName) {
        if (dao != null) {
            try {
                return !dao.queryForEq(com.jf.javafx.plugins.impl.datamodels.Plugin.FIELD_PLUGIN_CLASS_NAME,
                        pluginName).isEmpty();
            } catch (SQLException ex) {
//                Logger.getLogger(PluginRepositoryImpl.class.getName()).log(Level.SEVERE, null, ex);
                MsgBox.showException(ex);
            }
        }

        return false;
    }

    @Override
    public void install(Plugin p) {
        Application._getService(Security.class).checkPermission("plugin:install");

        if (isInstalled(p.getClass().getName())) {
            return;
        }

        try {
            p.getClass().getMethod("installPlugin").invoke(p);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(PluginRepositoryImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        com.jf.javafx.plugins.impl.datamodels.Plugin model = new com.jf.javafx.plugins.impl.datamodels.Plugin();
        model.pluginClassName = p.getClass().getName();
        model.author = p.getClass().getAnnotation(Author.class).name();
        model.version = p.getClass().getAnnotation(Version.class).version();
        model.creator = this.getClass().getName();
        model.createdTime = Calendar.getInstance().getTime();

        XMLConfiguration cfg = null;
        try {
            cfg = new XMLConfiguration(p.getClass().getResource("plugin.xml"));
            StringWriter sw = new StringWriter();
            cfg.save(sw);
            model.resourcesInString = sw.toString();
            try {
                sw.close();
            } catch (IOException ex) {
                Logger.getLogger(PluginRepositoryImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ConfigurationException ex) {
            System.out.println("Failed while loading plugin configuration of: " + p.getClass().getName());
        }

        if (dao != null) {
            try {
                dao.create(model);

                if (cfg != null) {
                    List<HierarchicalConfiguration> files = cfg.configurationsAt("resources.file");
                    for (HierarchicalConfiguration c : files) {
                        Resource r = new Resource();
                        r.plugin = model;
                        r.sourceURI = c.getString("[@src]");
                        r.resourceType = Resource.ResourceType.valueOf(c.getString("[@type]"));
                        r.deployPath = c.getString("[@deployPath]");
                        r.creator = model.creator;
                        r.createdTime = model.createdTime;
                        
                        try {
                            rr.upload(r);
                            
                            if(cfg.getBoolean("autoDeploy", false)) {
                                rr.deploy(r, r.deployPath, p);
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(PluginRepositoryImpl.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(PluginRepositoryImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void install(Class<Plugin> p) {
        try {
            Plugin ist = p.newInstance();
            install(ist);
        } catch (IllegalAccessException | InstantiationException ex) {
            Logger.getLogger(PluginRepositoryImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void install(String pluginClassName) {
        try {
            install((Class<Plugin>) Class.forName(pluginClassName));
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PluginRepositoryImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void uninstall(String pluginClassName) {
        if (!Application._getService(Security.class).isPermitted("plugin:install")) {
            throw new AuthorizationException("dont have permission to uninstall plugin");
        }

        try {
            dao.deleteBuilder().where().eq(com.jf.javafx.plugins.impl.datamodels.Plugin.FIELD_PLUGIN_CLASS_NAME, pluginClassName).query();
        } catch (SQLException ex) {
            Logger.getLogger(PluginRepositoryImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void installPlugin() throws SQLException {
        // install plugin table
        TableUtils.createTable(new DataSourceConnectionSource(
                Application._getService(Database.class).getAppDataSource(),
                Application._getService(Database.class).getAppDBUrl()),
                com.jf.javafx.plugins.impl.datamodels.Plugin.class);
    }
}
