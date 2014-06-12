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
import java.net.URL;
import java.util.Collection;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.Stack;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Hoàng Doãn
 */
public class GlobalController extends Controller {
    @FXML private VBox mainPane;
    private Pane mainContent;
    
    private Collection<? extends Action> actions;
    private Collection<? extends Action> toolbarActions;
    
    private StringProperty pageProperty = new SimpleStringProperty();
    private Stack<String> histPages = new Stack<String>();
    private Hashtable<String, Node> pageMap = new Hashtable<String, Node>();

    public GlobalController(Application app) {
        super(app);
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        mainPane.getChildren().add(ActionUtils.createMenuBar(_getMenuActions()));
        mainPane.getChildren().add(ActionUtils.createToolBar(_getToolBarActions(), ActionUtils.ActionTextBehavior.SHOW));
        
        mainContent = new Pane();
        mainContent.idProperty().set("mainContent");
        VBox.setVgrow(mainContent, Priority.ALWAYS);
        mainPane.getChildren().add(mainContent);
        
        pageProperty.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            // navigate to new page
            _navigate(newValue);
            // add old page to history stack
            _history(oldValue);
        });
    }
    
    private Collection<? extends Action> _getMenuActions() {
        if(actions != null) return actions;
        
        return null;
    }
    
    private Collection<? extends Action> _getToolBarActions() {
        if(toolbarActions != null) return toolbarActions;
        
        return null;
    }
    
    protected void _setPage(Node n) {
        mainContent.getChildren().clear();
            
        mainContent.getChildren().add(n);
    }

    private void _navigate(String newValue) {
        if(pageMap.containsKey(newValue)) {
            _setPage(pageMap.get(newValue));            
            return;
        }
        
        Node page = this.app.getNode(newValue);
        _setPage(page);
        pageMap.put(newValue, page);
    }

    private void _history(String oldValue) {
        if(histPages.peek().equals(oldValue)) return;
        
        histPages.push(oldValue);
    }
}
