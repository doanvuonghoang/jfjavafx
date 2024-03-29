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

import com.jf.javafx.AbstractService;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.controlsfx.dialog.Dialogs;

/**
 *
 * @author Hoàng Doãn
 */
public class Security extends AbstractService {

    private boolean authenticationRequired;

    @Override
    protected void _initService() {
        authenticationRequired = appConfig.getBoolean("authentication.required", true);

        if (authenticationRequired) {
            try {
                Factory<org.apache.shiro.mgt.SecurityManager> factory = new IniSecurityManagerFactory("url:" + app.getConfig("shiro.ini").toURL().toString());
                org.apache.shiro.mgt.SecurityManager sm = factory.getInstance();
                SecurityUtils.setSecurityManager(sm);
            } catch (MalformedURLException ex) {
                Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Login.
     *
     * @param userName
     * @param rawPassword
     */
    public void login(String userName, String rawPassword) {
        Subject currentUser = SecurityUtils.getSubject();
        if (!currentUser.isAuthenticated()) {
            currentUser.login(new UsernamePasswordToken(userName, rawPassword));
        }
    }

    /**
     * Logout.
     */
    public void logout() {
        SecurityUtils.getSubject().logout();
    }

    public boolean isPermitted(String str) {
        if (!authenticationRequired) {
            return true;
        }
        return SecurityUtils.getSubject().isPermitted(str);
    }

    public void checkPermission(String str) {
        if (authenticationRequired) {
            Subject currentUser = SecurityUtils.getSubject();
            if (!currentUser.isAuthenticated()) {
                showLogin();
            }

            currentUser.checkPermission(str);
        }
    }

    public boolean hasRole(String str) {
        if (authenticationRequired) {
            return SecurityUtils.getSubject().hasRole(str);
        }
        return true;
    }

    public String getUserName() {
        if (authenticationRequired) {
            return SecurityUtils.getSubject().toString();
        }
        return "Guest";
    }

    private void showLogin() {
//        while (!SecurityUtils.getSubject().isAuthenticated()) {
        Dialogs.create()
                .showLogin(new Dialogs.UserInfo("user", "password"), info -> {
                    login(info.getUserName(), info.getPassword());
                    return null;
                }
                );
//        }
    }
}
