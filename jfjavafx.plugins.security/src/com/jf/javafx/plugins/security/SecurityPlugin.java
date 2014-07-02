/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jf.javafx.plugins.security;

import com.jf.javafx.plugins.security.datamodels.Role;
import com.jf.javafx.plugins.security.datamodels.User;
import java.util.Collection;
import net.xeoh.plugins.base.Plugin;

/**
 *
 * @author Hoàng Doãn
 */
public interface SecurityPlugin extends Plugin {
    public void createUser(String username, String rawpassword) throws Exception;
    
    public Collection<User> getAllUsers() throws Exception;
    
    public void deleteUsers(User... users) throws Exception;
    
    public void createRole(String rolename) throws Exception;
    
    public void addRole(String username, String rolename) throws Exception;
    
    public void deleteRoles(Role... roles) throws Exception;
    
    public Collection<Role> getAllRoles() throws Exception;
    
    public void addPermission(String rolename, String permission) throws Exception;
}
