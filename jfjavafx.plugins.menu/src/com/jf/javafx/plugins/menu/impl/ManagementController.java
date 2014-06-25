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
import com.jf.javafx.MsgBox;
import com.jf.javafx.annotations.BeanUtils;
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
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import org.controlsfx.control.PropertySheet;

/**
 * FXML Controller class
 *
 * @author Hoàng Doãn
 */
public class ManagementController extends Controller {

    @FXML
    private TreeView<Menu> treeView;

    @FXML
    private SplitPane pane;

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
    protected void _init() {
        rootNode = new TreeItem<>(new Menu());
        rootNode.setExpanded(true);
        treeView.setRoot(rootNode);
        treeView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<Menu>> observable, TreeItem<Menu> oldValue, TreeItem<Menu> newValue) -> {
            ps.getItems().clear();
            ps.getItems().addAll(BeanUtils.getProperties(newValue.getValue()));
        });

        renderTree();

        ps = new PropertySheet();
        pane.getItems().add(ps);
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
            if ((m.getParent() != null && m.getParent().equals(node.getValue())) || (m.getParent() == null && node.getValue().getId() == 0)) {
                TreeItem<Menu> sub = new TreeItem<>(m);
                sub.setExpanded(true);
                displayPublishedIcon(sub, m.getPublished());

                m.publishedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    displayPublishedIcon(sub, newValue);
                });
                m.changedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    if (newValue) {
                        try {
                            p.save(m);
                        } catch (Exception ex) {
                            MsgBox.showException(ex);
                        }
                    }
                });

                node.getChildren().add(sub);

                renderTree(sub, l);
            }
        });
    }

    private void displayPublishedIcon(TreeItem<Menu> sub, Boolean published) {
        if (published) {
            sub.setGraphic(new ImageView(this.getClass().getResource("publish.png").toString()));
        } else {
            sub.setGraphic(new ImageView(this.getClass().getResource("unpublish.png").toString()));
        }
    }

}
