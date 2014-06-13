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

package com.jf.javafx.datamodel;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Date;

/**
 *
 * @author Hoàng Doãn
 */
@DatabaseTable(tableName = "Permissions")
public class GroupPermission {
    public static final int ACTION_CANVIEW      = 0x0007;
    public static final int ACTION_CANEDIT      = 0x0070;
    public static final int ACTION_CANDELETE    = 0x0700;
    public static final int ACTION_CANEXECUTE   = 0x7000;
    
    @DatabaseField(id = true, generatedId = true)
    public long id;
    
    @DatabaseField(foreign = true, uniqueCombo = true, canBeNull = false)
    public Group group;
    
    @DatabaseField(uniqueCombo = true)
    public long objectId;
    
    @DatabaseField(uniqueCombo = true)
    public String objectClassName;
    
    @DatabaseField
    public int action;
    
    @DatabaseField(canBeNull = false)
    public Date createdTime;
    
    @DatabaseField(canBeNull = false, foreign = true)
    public User creator;
    
    @DatabaseField
    public Date lastModifiedTime;
    
    @DatabaseField(foreign = true)
    public User lastModifier;
    
    public GroupPermission() {}
}
