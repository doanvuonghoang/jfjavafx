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

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;
import com.jf.javafx.Application;
import com.jf.javafx.MsgBox;
import com.jf.javafx.plugins.PluginRepository;
import com.jf.javafx.plugins.menu.MenuPlugin;
import com.jf.javafx.plugins.menu.datamodels.Menu;
import com.jf.javafx.services.Database;
import com.jf.javafx.services.Resource;
import com.jf.javafx.services.Router;
import com.jf.javafx.services.Security;
import com.jf.javafx.services.UI;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.ImageView;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.Init;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;
import net.xeoh.plugins.base.annotations.meta.Author;
import net.xeoh.plugins.base.annotations.meta.Version;

/**
 *
 * @author Hoàng Doãn
 */
@PluginImplementation
@Author(name = "Hoang Doan")
@Version(version = 1000)
public class MenuPluginImpl implements MenuPlugin {

    private final UI uiService = Application._getService(UI.class);
    private final Security secService = Application._getService(Security.class);
    private final Dao<Menu, Long> dao = Application._getService(Database.class).createAppDao(Menu.class);

    @InjectPlugin
    public PluginRepository pr;

    public void installPlugin() throws SQLException {
        TableUtils.createTable(new DataSourceConnectionSource(
                Application._getService(Database.class).getAppDataSource(),
                Application._getService(Database.class).getAppDBUrl()),
                Menu.class);

        Menu m = new Menu();
        m.setText("_Start");
        m.setPublished(Boolean.TRUE);
        m.setCreator("SYS");
        m.setCreatedTime(Calendar.getInstance().getTime());
//        m.icon = "start.png";
        m.setHasChildren(true);

        dao.create(m);

        Menu sub = new Menu();
        sub.setText("Menu management");
        sub.setParent(m);
        sub.setPublished(Boolean.TRUE);
        sub.setCreator("SYS");
        sub.setCreatedTime(m.getCreatedTime());
        sub.setIcon("images/menuManagement/ico.png");
        sub.setActionType(Menu.ActionType.TEMPLATE);
        sub.setActionSource("menuManagement/Management");

        dao.create(sub);
    }

    public void uninstallPlugin() throws SQLException {
        TableUtils.dropTable(new DataSourceConnectionSource(
                Application._getService(Database.class).getAppDataSource(),
                Application._getService(Database.class).getAppDBUrl()),
                Menu.class,
                true);
    }

    @Init
    public void init() throws Exception {
        pr.install(this);

        render();
    }

    @Override
    public void render() {
        try {
            Collection<Menu> list = getAvailableMenues();
            list.stream().filter((m) -> (m.getParent() == null)).map((m) -> {
                javafx.scene.control.Menu mui = new javafx.scene.control.Menu(m.getText());
                mui.setMnemonicParsing(true);
                bindGraphic(m, mui);

                if (m.getHasChildren()) {
                    renderMenu(mui, m.getId(), list);
                } else {
                    bindAction(m, mui);
                }

                return mui;
            }).forEach((mui) -> {
                uiService.addMenu(mui);
            });
        } catch (Exception ex) {
            Logger.getLogger(MenuPluginImpl.class.getName()).log(Level.WARNING, null, ex);
        }
    }

    @Override
    public Collection<Menu> getAvailableMenues() throws Exception {
        QueryBuilder<Menu, Long> builder = dao.queryBuilder();
        builder.where().eq(Menu.FIELD_PUBLISHED, Boolean.TRUE);
        builder.orderBy(Menu.FIELD_SHOW_SEQUENCE, true);

        return builder.query();
    }

    @Override
    public Collection<Menu> getAllMenues() throws Exception {
        QueryBuilder<Menu, Long> builder = dao.queryBuilder();
        builder.orderBy(Menu.FIELD_SHOW_SEQUENCE, true);

        return builder.query();
    }

    private void renderMenu(javafx.scene.control.Menu mui, long id, Collection<Menu> list) {
        list.stream().filter((m) -> (m.getParent() != null && m.getParent().getId() == id)).map((Menu m) -> {
            MenuItem mi;

            if (m.getHasChildren()) {
                mi = new javafx.scene.control.Menu(m.getText());
            } else if (m.getText().equals("-")) {
                mi = new SeparatorMenuItem();
            } else if (m.getMenuType() == Menu.MenuType.BUTTON) {
                Button btn = new Button(m.getText());
                mi = new CustomMenuItem(btn);
            } else if (m.getMenuType() == Menu.MenuType.CHECKBOX) {
                mi = new CheckMenuItem(m.getText());
            } else if (m.getMenuType() == Menu.MenuType.RADIO) {
                mi = new RadioMenuItem(m.getText());
            } else {
                mi = new MenuItem(m.getText());
            }

            mi.setMnemonicParsing(true);

            bindGraphic(m, mi);

            bindAction(m, mi);

            if (m.getHasChildren()) {
                renderMenu((javafx.scene.control.Menu) mi, m.getId(), list);
            }

            return mi;
        }).forEach((mi) -> {
            mui.getItems().add(mi);
        });
    }

    private void bindAction(Menu m, MenuItem mi) {
        if (m.getActionType() == Menu.ActionType.TEMPLATE) {
            mi.setOnAction((ActionEvent event) -> {
                Application._getService(Router.class).navigate(m.getActionSource());
            });
        } else if (m.getActionType() == Menu.ActionType.ACTION) {
            try {
                Class cls = Class.forName(m.getActionSource());
                if (EventHandler.class.isAssignableFrom(cls)) {
                    mi.setOnAction((EventHandler<ActionEvent>) cls.newInstance());
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                MsgBox.showException(ex);
            }
        }
    }

    private void bindGraphic(Menu m, MenuItem mi) {
        if (m.getIcon() != null) {
            try {
                mi.setGraphic(new ImageView(Application._getService(Resource.class).getResourceFile(m.getIcon()).toURL().toString()));
            } catch (MalformedURLException ex) {
                Logger.getLogger(MenuPluginImpl.class.getName()).log(Level.WARNING, null, ex);
            }
        }
    }

    @Override
    public void create(Menu menu) throws Exception {
        secService.checkPermission("sys:menu:edit");
        
        dao.create(menu);
    }

    @Override
    public void save(Collection<Menu> menues) throws Exception {
        secService.checkPermission("sys:menu:edit");
        
        menues.forEach((m) -> {
            try {
                m.setLastModifier(Application._getService(Security.class).getUserName());
                m.setLastModifiedTime(Calendar.getInstance().getTime());
                dao.update(m);
            } catch (SQLException ex) {
                Logger.getLogger(MenuPluginImpl.class.getName()).log(Level.WARNING, null, ex);
            }
        });
    }

    @Override
    public void save(Menu menu) throws Exception {
        secService.checkPermission("sys:menu:edit");
        
        menu.setLastModifier(Application._getService(Security.class).getUserName());
        menu.setLastModifiedTime(Calendar.getInstance().getTime());
        dao.update(menu);
    }

    @Override
    public void delete(Menu menu) throws Exception {
        secService.checkPermission("sys:menu:delete");
        
        dao.delete(menu);
    }

}
