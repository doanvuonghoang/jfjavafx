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
package com.jf.javafx.datamodels;

import com.j256.ormlite.field.DatabaseField;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Hoàng Doãn
 */
public class TrackableEntity extends GeneratedIdEntity {

    @DatabaseField
    protected String creator;

    @DatabaseField
    protected Date createdTime = Calendar.getInstance().getTime();

    @DatabaseField
    protected String lastModifier;

    @DatabaseField
    protected Date lastModifiedTime;

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

}
