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

package com.jf.javafx.annotations;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.DefaultPropertyEditorFactory;

/**
 *
 * @author Hoàng Doãn
 */
public class BeanUtils {
    private final static DefaultPropertyEditorFactory pef = new DefaultPropertyEditorFactory();
    
    public static final ObservableList<PropertySheet.Item> getProperties(Object o) {
        ObservableList<PropertySheet.Item> l = FXCollections.observableArrayList();
        
        for(Field f : o.getClass().getDeclaredFields()) {
            PropertyInfo info = f.getAnnotation(PropertyInfo.class);
            if(info != null) l.add(new BeanProperty(o, info));
        }
        
        return l;
    }
    
    static class BeanProperty implements PropertySheet.Item {
        Object o;
        PropertyInfo info;

        public BeanProperty(Object o, PropertyInfo info) {
            this.o = o;
            this.info = info;
        }

        @Override
        public Class<?> getType() {
            if(info != null) return info.type();
            return null;
        }

        @Override
        public String getCategory() {
            if(info != null) return info.category();
            return "Default";
        }

        @Override
        public String getName() {
            if(info != null) return info.name();
            return "[no name]";
        }

        @Override
        public String getDescription() {
            if(info != null) return info.description();
            return "";
        }

        @Override
        public Object getValue() {
            try {
                return this.o.getClass().getMethod(info.getValue()).invoke(this.o);
            } catch (Exception ex) {
                Logger.getLogger(BeanUtils.class.getName()).log(Level.SEVERE, null, ex);
                
                return null;
            }
        }

        @Override
        public void setValue(Object o) {
            try {
                if(info.editable())
                    this.o.getClass().getMethod(info.setValue(), o.getClass()).invoke(this.o, o);
            } catch (Exception ex) {
                Logger.getLogger(BeanUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        @Override
        public boolean isEditable() {
            return info.editable();
        }
    }
}
