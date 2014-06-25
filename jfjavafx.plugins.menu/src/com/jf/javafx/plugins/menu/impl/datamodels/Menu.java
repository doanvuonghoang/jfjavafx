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
package com.jf.javafx.plugins.menu.impl.datamodels;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.jf.javafx.annotations.PropertyInfo;
import java.io.Serializable;
import java.util.Date;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Hoàng Doãn
 */
@DatabaseTable(tableName = "Menues")
public class Menu implements Serializable {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(foreign = true)
    private Menu parent;

    @PropertyInfo(name = "Text", type = String.class, setValue = "setText", getValue = "getText")
    @DatabaseField(unique = true, canBeNull = false)
    private String text;

    @PropertyInfo(name = "Icon", type = String.class, setValue = "setIcon", getValue = "getIcon")
    @DatabaseField
    private String icon;

    @DatabaseField(defaultValue = "false")
    private Boolean hasChildren;

    @PropertyInfo(name = "Menu type", type = MenuType.class, setValue = "setMenuType", getValue = "getMenuType")
    @DatabaseField(defaultValue = "DEFAULT")
    private MenuType menuType;

    @PropertyInfo(name = "Action type", type = ActionType.class, setValue = "setActionType", getValue = "getActionType")
    @DatabaseField
    private ActionType actionType;

    @PropertyInfo(name = "Action source", type = String.class, setValue = "setActionSource", getValue = "getActionSource")
    @DatabaseField
    private String actionSource;
    
    @DatabaseField(defaultValue = "false")
    private Boolean published;

    @PropertyInfo(name = "Created time", type = Date.class, editable = false, getValue = "getCreatedTime")
    @DatabaseField(canBeNull = false)
    private Date createdTime;

    @PropertyInfo(name = "Creator", type = String.class, editable = false, getValue = "getCreator")
    @DatabaseField(canBeNull = false)
    private String creator;

    @PropertyInfo(name = "Last modified time", type = Date.class, editable = false, getValue = "getLastModifiedTime")
    @DatabaseField
    private Date lastModifiedTime;

    @PropertyInfo(name = "Last modifier", type = String.class, editable = false, getValue = "getLastModifier")
    @DatabaseField
    private String lastModifier;

    @ForeignCollectionField
    private ForeignCollection<Menu> children;

    public enum ActionType {

        TEMPLATE,
        ACTION,
        DEFAULT
    }

    public enum MenuType {

        CHECKBOX,
        RADIO,
        BUTTON,
        TEXT,
        DEFAULT
    }

    @Override
    public String toString() {
        return getText();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Menu) {
            return getId() == ((Menu) obj).getId();
        } else {
            return super.equals(obj);
        }
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the parent
     */
    public Menu getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(Menu parent) {
        this.parent = parent;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * @param icon the icon to set
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * @return the hasChildren
     */
    public boolean getHasChildren() {
        return hasChildren;
    }

    /**
     * @param hasChildren the hasChildren to set
     */
    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    /**
     * @return the menuType
     */
    public MenuType getMenuType() {
        return menuType;
    }

    /**
     * @param menuType the menuType to set
     */
    public void setMenuType(MenuType menuType) {
        this.menuType = menuType;
    }

    /**
     * @return the actionType
     */
    public ActionType getActionType() {
        return actionType;
    }

    /**
     * @param actionType the actionType to set
     */
    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    /**
     * @return the actionSource
     */
    public String getActionSource() {
        return actionSource;
    }

    /**
     * @param actionSource the actionSource to set
     */
    public void setActionSource(String actionSource) {
        this.actionSource = actionSource;
    }

    /**
     * @return the published
     */
    public Boolean getPublished() {
        return published;
    }

    /**
     * @param published the published to set
     */
    public void setPublished(Boolean published) {
        this.published = published;
    }

    /**
     * @return the createdTime
     */
    public Date getCreatedTime() {
        return createdTime;
    }

    /**
     * @param createdTime the createdTime to set
     */
    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    /**
     * @return the creator
     */
    public String getCreator() {
        return creator;
    }

    /**
     * @param creator the creator to set
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * @return the lastModifiedTime
     */
    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }

    /**
     * @param lastModifiedTime the lastModifiedTime to set
     */
    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    /**
     * @return the lastModifier
     */
    public String getLastModifier() {
        return lastModifier;
    }

    /**
     * @param lastModifier the lastModifier to set
     */
    public void setLastModifier(String lastModifier) {
        this.lastModifier = lastModifier;
    }

    /**
     * @return the children
     */
    public ForeignCollection<Menu> getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(ForeignCollection<Menu> children) {
        this.children = children;
    }
}
