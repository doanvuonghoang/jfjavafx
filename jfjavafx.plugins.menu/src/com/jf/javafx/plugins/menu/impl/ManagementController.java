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

package com.jf.javafx.plugins.menu.impl;

import com.jf.javafx.Application;
import java.net.URL;
import java.util.ResourceBundle;
import com.jf.javafx.Controller;
import com.jf.javafx.plugins.menu.MenuPlugin;
import com.jf.javafx.plugins.menu.impl.datamodels.Menu;
import com.jf.javafx.services.Plugins;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.FocusModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.BeanPropertyUtils;

/**
 * FXML Controller class
 *
 * @author Hoàng Doãn
 */
public class ManagementController extends Controller {
    @FXML
    private TreeView<Menu> treeView;
    
    @FXML
    private Pane pane;
    
    private TreeItem<Menu> rootNode;
    private PropertySheet ps;
    
    private final MenuPlugin p = Application._getService(Plugins.class).getPlugin(MenuPlugin.class);
    
    public ManagementController(Application app) {
        super(app);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rootNode = new TreeItem<>(new Menu());
        rootNode.setExpanded(true);
        treeView.setRoot(rootNode);
        treeView.focusModelProperty().addListener((ObservableValue<? extends FocusModel<TreeItem<Menu>>> observable, FocusModel<TreeItem<Menu>> oldValue, FocusModel<TreeItem<Menu>> newValue) -> {
            ps.getItems().clear();
            ps.getItems().addAll(BeanPropertyUtils.getProperties(newValue.getFocusedItem().getValue()));
        });
        
        renderTree();
        
        ps = new PropertySheet();
        pane.getChildren().add(ps);
    }    

    private void renderTree() {
        try {
            List<Menu> l = p.getAvailableMenues();
            
            renderTree(rootNode, l);
        } catch (SQLException ex) {
            Logger.getLogger(ManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void renderTree(TreeItem<Menu> node, List<Menu> l) {
        l.forEach((m) -> {
            if((m.parent != null && m.parent.equals(node.getValue())) || (m.parent == null && node.getValue().id == 0)) {
                TreeItem<Menu> sub = new TreeItem<>(m);
                sub.setExpanded(true);
                
                node.getChildren().add(sub);
                
                renderTree(sub, l);
            }
        });
    }
    
}
