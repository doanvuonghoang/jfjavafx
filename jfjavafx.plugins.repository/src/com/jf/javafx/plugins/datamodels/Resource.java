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

package com.jf.javafx.plugins.datamodels;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.jf.javafx.datamodels.RecordStatus;
import java.util.Date;

/**
 *
 * @author Hoàng Doãn
 */
@DatabaseTable(tableName = "Resources")
public class Resource {
    public static final String FIELD_PLUGIN = "pluginId";
    public static final String FIELD_RECORD_STATUS = "recordStatus";
    
    @DatabaseField(generatedId = true)
    public long id;
    
    @DatabaseField(foreign = true, columnName = FIELD_PLUGIN)
    public Plugin plugin;
    
    @DatabaseField(canBeNull = false)
    public String sourceURI;
    
    @DatabaseField(canBeNull = false, defaultValue = "GENERAL")
    public ResourceType resourceType;
    
    @DatabaseField(canBeNull = false, defaultValue = "false")
    public boolean isDeployed;
    
    @DatabaseField
    public String deployPath;
    
    @DatabaseField(canBeNull = false, defaultValue = "CREATE", columnName = FIELD_RECORD_STATUS)
    public RecordStatus recordStatus;
    
    @DatabaseField(canBeNull = false)
    public Date createdTime;
    
    @DatabaseField(canBeNull = false)
    public String creator;
    
    @DatabaseField
    public Date lastModifiedTime;
    
    @DatabaseField
    public String lastModifier;
    
    public enum ResourceType {
        TEMPLATE,
        BUNDLE,
        GENERAL
    }
}


