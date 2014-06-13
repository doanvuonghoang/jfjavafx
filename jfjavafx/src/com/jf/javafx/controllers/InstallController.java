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

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.jf.javafx.Application;
import com.jf.javafx.Controller;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

/**
 *
 * @author Hoàng Doãn
 */
public class InstallController extends Controller {
    @FXML
    private TextField txtDBUrl;
    
    @FXML
    private TextField txtDBUser;
    
    @FXML
    private PasswordField txtDBPass;
    
    @FXML
    private TextField txtAppUser;
    
    @FXML
    private PasswordField txtAppPass;
    
    @FXML
    private PasswordField txtAppPassConfirm;
    
    @FXML
    private Button btnNext;
    
    public InstallController(Application app) {
        super(app);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources); //To change body of generated methods, choose Tools | Templates.
        
        ValidationSupport vs = new ValidationSupport();
        vs.registerValidator(txtDBUrl, true, Validator.createEmptyValidator(this.resources.getString("urlnotnull.text")));
        vs.registerValidator(txtAppUser, true, Validator.createEmptyValidator(this.resources.getString("usernotnull.text")));
        vs.registerValidator(txtAppPassConfirm, (Control t, String u) -> {
            return ValidationResult.fromErrorIf(t, this.resources.getString("passConfirm.text"), !txtAppPass.getText().equals(txtAppPassConfirm.getText()));
        });
        vs.validationResultProperty().addListener((ObservableValue<? extends ValidationResult> observable, ValidationResult oldValue, ValidationResult newValue) -> {
            btnNext.setDisable(vs.isInvalid());
        });
    }
    
    public void onNext_Click(ActionEvent e) {
        try {
            // check for database connection
            ConnectionSource cs = new JdbcConnectionSource(txtDBUrl.getText());
        } catch (Exception ex) {
            this.app.showException(this.resources.getString("dialog.cancel.title"), ex, ex.getLocalizedMessage());
            Logger.getLogger(InstallController.class.getName()).log(Level.SEVERE, null, ex);
            
            return;
        }
        
        
    }
    
    public void onCancel_Click(ActionEvent e) {
        try {
            Action r = this.app.showConfirm(this.resources.getString("dialog.cancel.title"), this.resources.getString("dialog.cancel.text"));
            if(r == Dialog.Actions.YES) Platform.exit();
        } catch (Exception ex) {
            Logger.getLogger(InstallController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
