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
package com.jf.javafx.plugins.menu.datamodels;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.jf.javafx.annotations.PropertyInfo;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Hoàng Doãn
 */
@DatabaseTable(tableName = "Menues")
public class Menu implements Serializable {
    
    public final static String FIELD_PUBLISHED = "published";
    public final static String FIELD_SHOW_SEQUENCE = "showSequence";

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(foreign = true)
    private Menu parent;

    @PropertyInfo(name = "Text", type = String.class, setValue = "setText", getValue = "getText")
    @DatabaseField(canBeNull = false)
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
    
    @PropertyInfo(name = "Published", type = Boolean.class, setValue = "setPublished", getValue = "getPublished")
    @DatabaseField(defaultValue = "false", columnName = FIELD_PUBLISHED)
    private Boolean published = Boolean.FALSE;
    
    @PropertyInfo(name = "Show sequence", type = Integer.class, setValue = "setShowSequence", getValue = "getShowSequence")
    @DatabaseField(defaultValue = "0", columnName = FIELD_SHOW_SEQUENCE)
    private Integer showSequence = 0;

    @PropertyInfo(name = "Created time", type = Date.class, editable = false, getValue = "getCreatedTime")
    @DatabaseField(canBeNull = false)
    private Date createdTime = Calendar.getInstance().getTime();

    @PropertyInfo(name = "Creator", type = String.class, editable = false, getValue = "getCreator")
    @DatabaseField(canBeNull = false)
    private String creator = "SYS";

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
    
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

     public void addPropertyChangeListener(PropertyChangeListener listener) {
         this.pcs.addPropertyChangeListener(listener);
     }

     public void removePropertyChangeListener(PropertyChangeListener listener) {
         this.pcs.removePropertyChangeListener(listener);
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
        String oldVal = this.text;
        this.text = text;
        this.pcs.firePropertyChange("text", oldVal, this.text);
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
        String oldVal = this.icon;
        this.icon = icon;
        this.pcs.firePropertyChange("icon", oldVal, this.icon);
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
        Boolean oldVal = this.hasChildren;
        this.hasChildren = hasChildren;
        this.pcs.firePropertyChange("hasChildren", oldVal, this.hasChildren);
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
        MenuType oldVal = this.menuType;
        this.menuType = menuType;
        this.pcs.firePropertyChange("menuType", oldVal, this.menuType);
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
        ActionType oldVal = this.actionType;
        this.actionType = actionType;
        this.pcs.firePropertyChange("actionType", oldVal, this.actionType);
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
        String oldVal = this.actionSource;
        this.actionSource = actionSource;
        this.pcs.firePropertyChange("actionSource", oldVal, this.actionSource);
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
        Boolean oldVal = this.published;
        this.published = published;
        this.pcs.firePropertyChange("published", oldVal, this.published);
    }

    /**
     * @return the showSequence
     */
    public Integer getShowSequence() {
        return showSequence;
    }

    /**
     * @param showSequence the showSequence to set
     */
    public void setShowSequence(Integer showSequence) {
        Integer oldVal = this.showSequence;
        this.showSequence = showSequence;
        this.pcs.firePropertyChange("showSequence", oldVal, this.showSequence);
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
