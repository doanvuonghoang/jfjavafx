/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jf.javafx.plugins.security;

import net.xeoh.plugins.base.Plugin;

/**
 *
 * @author Hoàng Doãn
 */
public interface SecurityPlugin extends Plugin {
    public void createUser(String username, String rawpassword) throws Exception;
    
    public void createRole(String rolename) throws Exception;
    
    public void addRole(String username, String rolename) throws Exception;
    
    public void addPermission(String rolename, String permission) throws Exception;
}
