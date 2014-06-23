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
package com.jf.javafx.controllers;

import com.jf.javafx.Application;
import com.jf.javafx.Controller;
import com.jf.javafx.MsgBox;
import com.jf.javafx.services.Router;
import com.jf.javafx.services.Security;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

/**
 *
 * @author Hoàng Doãn
 */
public class LoginController extends Controller {

    @FXML
    private TextField txtUser;

    @FXML
    private PasswordField txtPass;

    @FXML
    private Button btnLogin;

    public LoginController(Application app) {
        super(app);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources); //To change body of generated methods, choose Tools | Templates.

        ValidationSupport vs = new ValidationSupport();
        vs.registerValidator(txtUser, true, Validator.createEmptyValidator(this.resources.getString("usernotnull.text")));

        vs.validationResultProperty().addListener((ObservableValue<? extends ValidationResult> observable, ValidationResult oldValue, ValidationResult newValue) -> {
            btnLogin.setDisable(vs.isInvalid());
        });
    }

    public void onLogin(ActionEvent e) {
        try {
            app.getService(Security.class).login(txtUser.getText(), txtPass.getText());
        } catch (UnknownAccountException uae) {
            //username wasn't in the system, show them an error message?
            MsgBox.showNotification(resources.getString("unknownAccount.text"), resources.getString("loginFailed.title"), app.getStage());
        } catch (IncorrectCredentialsException ice) {
            //password didn't match, try again?
            MsgBox.showNotification(resources.getString("wrongPassword.text"), resources.getString("loginFailed.title"), app.getStage());
        } catch (LockedAccountException lae) {
            //account for that username is locked - can't login.  Show them a message?
            MsgBox.showNotification(resources.getString("lockedAccount.text"), resources.getString("loginFailed.title"), app.getStage());
        } catch (AuthenticationException ae) {
            //unexpected condition - error?
            MsgBox.showNotification(ae.getMessage(), resources.getString("loginFailed.title"), app.getStage());
        }

        app.getService(Router.class).back();
    }
}
