package com.jf.javafx.plugins.security.controllers;

import com.jf.javafx.Application;
import com.jf.javafx.Controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 *
 * @author Hoàng Doãn
 */
public class Management extends Controller {
    
    @FXML
    private Tab tabUser;
    
    @FXML
    private Tab tabRole;
    
    @FXML
    private TabPane userPane;
    
    @FXML
    private TabPane rolePane;
    
    private StringProperty viewState = new SimpleStringProperty("");

    public Management(Application app) {
        super(app);
    }

    @Override
    protected void _init() {
        viewState.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if(oldValue.equals("user")) userPane.setVisible(false);
            else rolePane.setVisible(false);
            
            if(newValue.equals("user")) userPane.setVisible(true);
            else rolePane.setVisible(true);
        });
    }

    protected StringProperty viewStateProperty() {
        return viewState;
    }
}
