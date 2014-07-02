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

package com.jf.javafx.plugins.security.datamodels;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Hoàng Doãn
 */
@DatabaseTable(tableName = "users")
public class User {
    @DatabaseField(id = true, canBeNull = false)
    public String username;
    
    @DatabaseField(canBeNull = false)
    public String password;
    
    @DatabaseField(columnName = "password_salt")
    public String passwordSalt;
    
    @DatabaseField(defaultValue = "false")
    public Boolean isSystemUser = Boolean.FALSE;
    
    @DatabaseField(canBeNull = false)
    public Date createdTime = Calendar.getInstance().getTime();
    
    @DatabaseField(foreign = true)
    public User creator;
    
    @DatabaseField
    public Date lastModifiedTime;
    
    @DatabaseField(foreign = true)
    public User lastModifier;
    
    public User() {}
}