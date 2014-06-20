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
import com.jf.javafx.plugins.ResourceRepository;
import com.jf.javafx.plugins.impl.datamodels.Resource;
import com.jf.javafx.services.Database;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.Init;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;
import net.xeoh.plugins.base.annotations.meta.Author;
import net.xeoh.plugins.base.annotations.meta.Version;

/**
 *
 * @author Hoàng Doãn
 */
@PluginImplementation
@Author(name = "Hoang Doan")
@Version(version = 1000)
public class ResourceRepositoryImpl implements ResourceRepository {
    
    private Dao<Resource, Long> dao;
    
    @InjectPlugin
    public PluginRepository pr;
    
    @Init
    public void init() {
        dao = Application._getService(Database.class).createAppDao(Resource.class);
        
        if(!pr.isInstalled(this.getClass().getName())) pr.install(this.getClass().getName());
    }
    
    public void installPlugin() throws SQLException {
        // install resource table
        TableUtils.createTableIfNotExists(new DataSourceConnectionSource(
                Application._getService(Database.class).getAppDataSource(),
                Application._getService(Database.class).getAppDBUrl()), 
                Resource.class);
    }

    @Override
    public void delete(Resource r) throws ResourceException {
        if(!r.isDeployed) {
            r.action = 'D';
            try {
                dao.update(r);
            } catch (SQLException ex) {
                throw new ResourceException(ex.getLocalizedMessage());
            }
        } else throw new ResourceException("can not delete deployed resource");
    }

    @Override
    public void upload(Resource r) throws ResourceException {
        try {
            dao.create(r);
        } catch (SQLException ex) {
            throw new ResourceException((ex.getLocalizedMessage()));
        }
    }

    @Override
    public void deploy(Resource r, String toPath) throws ResourceException {
        if(!r.isDeployed) {
            File dest;
            
            if(r.resourceType == Resource.ResourceType.TEMPLATE) {
                dest = Application._getService(com.jf.javafx.services.Resource.class).getTemplateFile(toPath);
            }
            else {
                dest = Application._getService(com.jf.javafx.services.Resource.class).getResourceFile(toPath);
            }
            
            if(dest.exists()) throw new ResourceException("Can not deployed to existing resource: " + toPath);
            
            try {
                Files.copy((new URI(r.sourceURI)).toURL().openStream(),
                        Paths.get(dest.toURI()),
                        StandardCopyOption.REPLACE_EXISTING
                );
                
                r.deployPath = toPath;
                r.isDeployed = true;
                dao.update(r);
            } catch (IOException | SQLException | URISyntaxException ex) {
                throw new ResourceException(ex.getLocalizedMessage());
            }
        } else throw new ResourceException("can not deployed a deployed resource");
    }

    @Override
    public void undeploy(Resource r) throws ResourceException {
        // delete deployed file
        if(r.isDeployed) {
            Application._getService(com.jf.javafx.services.Resource.class).getResourceFile(r.deployPath).delete();
            
            r.isDeployed = false;
            try {
                dao.update(r);
            } catch (SQLException ex) {
                throw new ResourceException(ex.getLocalizedMessage());
            }
        } else throw new ResourceException("can not undeployed an undeployed resource");
    }
}
