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
import com.jf.javafx.Controller;
import com.jf.javafx.MsgBox;
import com.jf.javafx.annotations.BeanUtils;
import com.jf.javafx.plugins.menu.MenuPlugin;
import com.jf.javafx.plugins.menu.impl.datamodels.Menu;
import com.jf.javafx.services.Plugins;
import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

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

    @FXML
    private TextField txtNewMenu;

    private TreeItem<Menu> rootNode;
    private PropertySheet ps;

    private final MenuPlugin p = Application._getService(Plugins.class).getPlugin(MenuPlugin.class);
    private final ValidationSupport validationSupport = new ValidationSupport();
    private final Map<Menu, ObservableList<PropertySheet.Item>> map = new HashMap<>();

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

            ObservableList<PropertySheet.Item> cur = map.get(newValue.getValue());
            if (cur == null) {
                cur = BeanUtils.getProperties(newValue.getValue());
                map.put(newValue.getValue(), cur);
            }

            ps.getItems().addAll(cur);
        });

        renderTree();

        ps = new PropertySheet();
        pane.getItems().add(ps);

        validationSupport.registerValidator(txtNewMenu, Validator.createEmptyValidator(resources.getString("newMenuInvalid.text")));
    }

    private void renderTree() {
        try {
            Collection<Menu> l = p.getAllMenues();

            renderTree(rootNode, l);
        } catch (Exception ex) {
            Logger.getLogger(ManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void renderTree(TreeItem<Menu> node, Collection<Menu> l) {
        l.forEach((m) -> {
            if ((m.getParent() != null && m.getParent().equals(node.getValue())) || (m.getParent() == null && node.getValue().getId() == 0)) {
                TreeItem<Menu> sub = createTreeItem(m);

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

    public void onAddNewMenu_click() throws Exception {
        if (!validationSupport.isInvalid()) {
            Menu parent = treeView.getSelectionModel().getSelectedItem().getValue();

            Menu m = new Menu();
            m.setText(txtNewMenu.getText());
            m.setParent(parent);
            p.create(m);
            
            parent.setHasChildren(true);
            p.save(parent);
            
            treeView.getSelectionModel().getSelectedItem().getChildren().add(createTreeItem(m));
        }
    }

    public void onDeleteMenu_click() {
        Menu m = treeView.getSelectionModel().getSelectedItem().getValue();

        if (m != null) {
            Action a = MsgBox.showConfirm(resources.getString("confirmTitle.text"), resources.getString("confirmMsg.text"));
            if (a == Dialog.Actions.YES) {
                try {
                    //remove on map
                    map.remove(m);
                    // remove on tree
                    TreeItem<Menu> parent = treeView.getSelectionModel().getSelectedItem().getParent();
                    parent.getChildren().remove(treeView.getSelectionModel().getSelectedItem());
                    
                    // delete on repos
                    p.delete(m);
                } catch (Exception ex) {
                    Logger.getLogger(ManagementController.class.getName()).log(Level.WARNING, null, ex);
                }
            }
        }
    }

    private TreeItem<Menu> createTreeItem(Menu m) {
        TreeItem<Menu> sub = new TreeItem<>(m);
        sub.setExpanded(true);
        displayPublishedIcon(sub, m.getPublished());

        m.addPropertyChangeListener((PropertyChangeEvent evt) -> {
            if (evt.getPropertyName().equals("published")) {
                displayPublishedIcon(sub, (Boolean)evt.getNewValue());
            }

            try {
                p.save(m);
            } catch (Exception ex) {
                MsgBox.showException(ex);
            }
            
            if (evt.getPropertyName().equals("showSequence")) {
                sortTree(sub.getParent());
            }
        });
        return sub;
    }

    private void sortTree(TreeItem<Menu> t) {
        if(t != null) {
            t.getChildren().sort((TreeItem<Menu> o1, TreeItem<Menu> o2) -> o1.getValue().getShowSequence().compareTo(o2.getValue().getShowSequence()));
        }
    }
}
