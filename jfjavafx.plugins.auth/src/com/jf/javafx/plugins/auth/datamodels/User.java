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

package com.jf.javafx.plugins.auth.datamodels;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Date;

/**
 *
 * @author Hoàng Doãn
 */
@DatabaseTable(tableName = "Users")
public class User {
    @DatabaseField(generatedId = true)
    public long id;
    
    @DatabaseField(unique = true, canBeNull = false)
    public String userName;
    
    @DatabaseField(canBeNull = false)
    public String password;
    
    @DatabaseField(canBeNull = false, defaultValue = "true")
    public boolean valid;
    
    @DatabaseField(canBeNull = false, defaultValue = "false")
    public boolean deleted;
    
    @DatabaseField(canBeNull = false, defaultValue = "false")
    public boolean mustChangePass;
    
    @DatabaseField
    public Date lastChangePass;
    
    @DatabaseField(canBeNull = false)
    public Date createdTime;
    
    @DatabaseField(canBeNull = false, foreign = true)
    public User creator;
    
    @DatabaseField
    public Date lastModifiedTime;
    
    @DatabaseField(foreign = true)
    public User lastModifier;
    
    @DatabaseField(canBeNull = false, defaultValue = "0")
    public int loginFailedCount;

    public User() {}
}