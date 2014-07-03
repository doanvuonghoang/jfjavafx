package com.jf.erp.datamodels;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.jf.javafx.datamodels.TrackableEntity;

/**
 *
 * @author Hoàng Doãn
 */
@DatabaseTable(tableName = "erp_departments")
public class Department extends TrackableEntity {

    @DatabaseField(foreign = true)
    public Department parent;

    @DatabaseField(canBeNull = false)
    public String name;

    @DatabaseField(width = 4000)
    public String description;

    @DatabaseField
    public String phone;

    @DatabaseField
    public String email;

    @DatabaseField
    public String fax;

    @ForeignCollectionField(foreignFieldName = "parent")
    public ForeignCollection<Department> children;
}
