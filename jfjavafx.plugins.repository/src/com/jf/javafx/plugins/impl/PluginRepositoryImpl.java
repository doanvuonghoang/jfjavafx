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
import com.jf.javafx.datamodels.RecordStatus;
import com.jf.javafx.plugins.PluginRepository;
import com.jf.javafx.plugins.ResourceRepository;
import com.jf.javafx.plugins.datamodels.Resource;
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

/**
 *
 * @author Hoàng Doãn
 */
@PluginImplementation
@Author(name = "Hoang Doan")
@Version(version = 1000)
public class PluginRepositoryImpl implements PluginRepository {

    private Dao<com.jf.javafx.plugins.datamodels.Plugin, Long> dao;

    @InjectPlugin
    public ResourceRepository rr;

    @Init
    public void init() throws Exception {
        dao = Application._getService(Database.class).createAppDao(com.jf.javafx.plugins.datamodels.Plugin.class);

        if (!isInstalled(this.getClass().getName())) {
            install(this);
        }

        if (!isInstalled(rr.getClass().getName())) {
            install(rr);
        }
    }

    @Override
    public boolean isInstalled(String pluginName) {
        try {
            return !dao.queryBuilder().where()
                    .eq(com.jf.javafx.plugins.datamodels.Plugin.FIELD_PLUGIN_CLASS_NAME, pluginName)
                    .and().ne(com.jf.javafx.plugins.datamodels.Plugin.FIELD_RECORD_STATUS, RecordStatus.DELETE)
                    .and().eq(com.jf.javafx.plugins.datamodels.Plugin.FIELD_DEBUG, false).query().isEmpty();
        } catch (SQLException ex) {
            Logger.getLogger(PluginRepositoryImpl.class.getName()).log(Level.INFO, null, ex);
        }

        return false;
    }

    @Override
    public void install(Plugin p) throws Exception {
        Application._getService(Security.class).checkPermission("plugin:install");

        XMLConfiguration cfg = getPluginConfig(p);

        com.jf.javafx.plugins.datamodels.Plugin model = createPluginModel(p, cfg);

        if (model.debug) {
            executeInternalUninstall(p);
            removePlugin(p.getClass().getName());
        } else if(isInstalled(p.getClass().getName())) return;

        executeInternalInstall(p);

        dao.create(model);

        if (cfg != null) {
            List<HierarchicalConfiguration> files = cfg.configurationsAt("resources.file");
            files.stream().map((c) -> {
                return createPluginResourceModel(model, c);
            }).forEach((r) -> {
                uploadResource(r, cfg);
            });
        }
    }

    @Override
    public void install(Class<Plugin> p) throws Exception {
        Plugin ist = p.newInstance();
        install(ist);
    }

    @Override
    public void install(String pluginClassName) throws Exception {
        install((Class<Plugin>) Class.forName(pluginClassName));
    }

    @Override
    public void uninstall(String pluginClassName) throws Exception {
        Application._getService(Security.class).checkPermission("plugin:install");

        dao.updateBuilder().updateColumnValue(com.jf.javafx.plugins.datamodels.Plugin.FIELD_RECORD_STATUS, RecordStatus.DELETE);
    }

    public void installPlugin() throws SQLException {
        // install plugin table
        TableUtils.createTable(new DataSourceConnectionSource(
                Application._getService(Database.class).getAppDataSource(),
                Application._getService(Database.class).getAppDBUrl()),
                com.jf.javafx.plugins.datamodels.Plugin.class);
    }

    public void uninstallPlugin() throws SQLException {
        // install plugin table
        TableUtils.dropTable(new DataSourceConnectionSource(
                Application._getService(Database.class).getAppDataSource(),
                Application._getService(Database.class).getAppDBUrl()),
                com.jf.javafx.plugins.datamodels.Plugin.class, true);
    }

    private XMLConfiguration getPluginConfig(Plugin p) {
        XMLConfiguration cfg = null;

        try {
            cfg = new XMLConfiguration(p.getClass().getResource("plugin.xml"));
        } catch (ConfigurationException ex) {
            System.out.println("Failed while loading plugin configuration of: " + p.getClass().getName());
        }

        return cfg;
    }

    private String getConfigInString(XMLConfiguration cfg) {
        StringWriter sw = new StringWriter();
        String result = "";
        try {
            cfg.save(sw);
            result = sw.toString();
        } catch (ConfigurationException ex) {
            // do nothing
        }
        try {
            sw.close();
        } catch (IOException ex) {
            Logger.getLogger(PluginRepositoryImpl.class.getName()).log(Level.INFO, null, ex);
        }

        return result;
    }

    private com.jf.javafx.plugins.datamodels.Plugin createPluginModel(Plugin p, XMLConfiguration cfg) {
        com.jf.javafx.plugins.datamodels.Plugin model = new com.jf.javafx.plugins.datamodels.Plugin();
        model.pluginClassName = p.getClass().getName();
        model.author = p.getClass().getAnnotation(Author.class).name();
        model.version = p.getClass().getAnnotation(Version.class).version();
        if (cfg != null) {
            model.resourcesInString = getConfigInString(cfg);
            model.debug = cfg.getBoolean("debug", true);
        }
        model.creator = this.getClass().getName();
        model.createdTime = Calendar.getInstance().getTime();

        return model;
    }

    private Resource createPluginResourceModel(com.jf.javafx.plugins.datamodels.Plugin model, HierarchicalConfiguration c) {
        Resource r = new Resource();
        r.plugin = model;
        r.sourceURI = c.getString("[@src]");
        r.resourceType = Resource.ResourceType.valueOf(c.getString("[@type]"));
        r.deployPath = c.getString("[@deployPath]");
        r.creator = model.creator;
        r.createdTime = model.createdTime;

        return r;
    }

    private void uploadResource(Resource r, XMLConfiguration cfg) {
        try {
            rr.save(r);

            if (cfg.getBoolean("autoDeploy", false)) {
                rr.deploy(r, r.deployPath);
            }
        } catch (Exception ex) {
            Logger.getLogger(PluginRepositoryImpl.class.getName()).log(Level.INFO, null, ex);
        }
    }

    private void executeInternalInstall(Plugin p) {
        try {
            p.getClass().getMethod("installPlugin").invoke(p);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(PluginRepositoryImpl.class.getName()).log(Level.INFO, null, ex);
        }
    }

    private void executeInternalUninstall(Plugin p) {
        try {
            p.getClass().getMethod("uninstallPlugin").invoke(p);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(PluginRepositoryImpl.class.getName()).log(Level.INFO, null, ex);
        }
    }

    private void removePlugin(String name) throws Exception {
        dao.queryForEq(com.jf.javafx.plugins.datamodels.Plugin.FIELD_PLUGIN_CLASS_NAME, name).forEach((m) -> {
            
            try {
//                rr.deletePluginResource(m.id);

                dao.delete(m);
            } catch (Exception ex) {
                Logger.getLogger(PluginRepositoryImpl.class.getName()).log(Level.INFO, null, ex);
            }
        });
    }
}
