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
import com.jf.javafx.plugins.PluginRepository;
import com.jf.javafx.plugins.TemplateRepository;
import com.jf.javafx.plugins.impl.datamodels.Template;
import com.jf.javafx.services.Database;
import com.jf.javafx.services.Security;
import com.jf.javafx.services.UI;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.Init;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;
import net.xeoh.plugins.base.annotations.meta.Author;
import net.xeoh.plugins.base.annotations.meta.Version;
import org.apache.shiro.authz.AuthorizationException;

/**
 *
 * @author Hoàng Doãn
 */
@PluginImplementation
@Author(name = "Hoang Doan")
@Version(version = 1000)
public class TemplateRepositoryImpl implements TemplateRepository {
    
    private Dao<Template, Long> dao;
    
    @InjectPlugin
    protected PluginRepository repo;
    
    @Init
    public void init() {
        dao = Application._getService(Database.class).createAppDao(Template.class);
        
        if(!repo.isInstalled(this.getClass().getName())) repo.install(this);
    }

    @Override
    public boolean isExisted(String path) {
        if(dao != null) {
            try {
                return !dao.queryForEq(Template.FIELD_PATH, path).isEmpty();
            } catch (SQLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return false;
    }

    @Override
    public void install(URI templateURI, String toPath) {
        if(!Application._getService(Security.class).isPermitted("plugin:install"))
            throw new AuthorizationException("dont have permission to install plugin");
        
        if(isExisted(toPath)) return;
        
        // copy template to path
        Path dest = FileSystems.getDefault().getPath(toPath);
        try {
            Files.copy(templateURI.toURL().openStream(), dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Logger.getLogger(TemplateRepositoryImpl.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        // insert new record in db
        if(dao != null) {
            Template t = new Template();
            t.path = toPath;
            t.sourceURI = templateURI.toString();
            t.creator = Application._getService(Security.class).getUserName();
            t.createdTime = Calendar.getInstance().getTime();
            
            try {
                dao.create(t);
            } catch (SQLException ex) {
                Logger.getLogger(TemplateRepositoryImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void uninstall(String path) {
        // try to delete in DB
        if(dao != null) {
            try {
                dao.deleteBuilder().where().eq(Template.FIELD_PATH, path).query();
            } catch (SQLException ex) {
                Logger.getLogger(TemplateRepositoryImpl.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            
            // try to delete file
            File template = Application._getService(UI.class).getTemplateFile(path);
            template.delete();
        }
    }

    public void installPlugin() throws SQLException {
        // install table in database
        TableUtils.createTable(new DataSourceConnectionSource(
                Application._getService(Database.class).getAppDataSource(),
                Application._getService(Database.class).getAppDBUrl()), 
                Template.class);
    }
}
