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
package com.jf.javafx.plugins.security.impl;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.jf.javafx.Application;
import com.jf.javafx.plugins.PluginRepository;
import com.jf.javafx.plugins.security.SecurityPlugin;
import com.jf.javafx.plugins.security.datamodels.Permission;
import com.jf.javafx.plugins.security.datamodels.Role;
import com.jf.javafx.plugins.security.datamodels.User;
import com.jf.javafx.plugins.security.datamodels.UserRole;
import com.jf.javafx.services.Database;
import java.util.Collection;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.Init;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;
import net.xeoh.plugins.base.annotations.meta.Author;
import net.xeoh.plugins.base.annotations.meta.Version;
import org.apache.shiro.authc.credential.DefaultPasswordService;

/**
 *
 * @author Hoàng Doãn
 */
@PluginImplementation
@Author(name = "Hoang Doan")
@Version(version = 1000)
public class SecurityPluginImpl implements SecurityPlugin {

    @InjectPlugin
    public PluginRepository pr;
    
    private final Dao<User, String> udao = Application._getService(Database.class).createAppDao(User.class);
    private final Dao<Role, String> rdao = Application._getService(Database.class).createAppDao(Role.class);
    private final Dao<UserRole, Long> urdao = Application._getService(Database.class).createAppDao(UserRole.class);
    private final Dao<Permission, Long> pdao = Application._getService(Database.class).createAppDao(Permission.class);
    private final DefaultPasswordService dps = new DefaultPasswordService();

    @Init
    public void init() throws Exception {
        pr.install(this);
    }

    @Override
    public void createUser(String username, String rawpassword) throws Exception {
        String encryptedPassword = dps.encryptPassword(rawpassword);
        User u = new User();
        u.username = username;
        u.password = encryptedPassword;

        udao.create(u);
    }

    public void createUser(User newuser) throws Exception {
        newuser.password = dps.encryptPassword(newuser.password);
        
        udao.create(newuser);
    }
    
    @Override
    public Collection<User> getAllUsers() throws Exception {
        return udao.queryForAll();
    }

    @Override
    public void deleteUsers(User... users) throws Exception {
        for(User u : users) {
            if(!u.isSystemUser) {
                udao.delete(u);
            }
        }
    }

    @Override
    public void createRole(String rolename) throws Exception {
        Role r = new Role();
        r.roleName = rolename;
        
        rdao.create(r);
    }
    
    public void createRole(Role r) throws Exception {
        rdao.create(r);
    }

    @Override
    public void addRole(String username, String rolename) throws Exception {
        UserRole ur = new UserRole();
        ur.user = new User();
        ur.user.username = username;
        ur.role = new Role();
        ur.role.roleName = rolename;
        
        urdao.create(ur);
    }

    @Override
    public void deleteRoles(Role... roles) throws Exception {
        for(Role r : roles) {
            rdao.delete(r);
        }
    }

    @Override
    public Collection<Role> getAllRoles() throws Exception {
        return rdao.queryForAll();
    }

    @Override
    public void addPermission(String rolename, String permission) throws Exception {
        Permission p = new Permission();
        p.role = new Role();
        p.role.roleName = rolename;
        p.permission = permission;
        
        pdao.create(p);
    }

    public void installPlugin() throws Exception {
        DataSourceConnectionSource ds = new DataSourceConnectionSource(
                Application._getService(Database.class).getAppDataSource(),
                Application._getService(Database.class).getAppDBUrl());

        TableUtils.createTable(ds, User.class);
        TableUtils.createTable(ds, Role.class);
        TableUtils.createTable(ds, UserRole.class);
        TableUtils.createTable(ds, Permission.class);

        User admin = new User();
        admin.username = "admin";
        admin.password = "admin";
        admin.isSystemUser = Boolean.TRUE;
        createUser(admin);
        
        Role adminGroup = new Role();
        adminGroup.roleName = "Administrators";
        adminGroup.isSystemRole = Boolean.TRUE;
        createRole(adminGroup);
        
        addRole("admin", "Administrators");
        addPermission("Administrators", "*");
    }
    
    public void uninstallPlugin() throws Exception {
        DataSourceConnectionSource ds = new DataSourceConnectionSource(
                Application._getService(Database.class).getAppDataSource(),
                Application._getService(Database.class).getAppDBUrl());
        
        TableUtils.dropTable(ds, Permission.class, true);
        TableUtils.dropTable(ds, UserRole.class, true);
        TableUtils.dropTable(ds, Role.class, true);
        TableUtils.dropTable(ds, User.class, true);
    }
}
