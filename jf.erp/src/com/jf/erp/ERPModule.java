package com.jf.erp;

import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.jf.erp.datamodels.Department;
import com.jf.javafx.Application;
import com.jf.javafx.plugins.PluginRepository;
import com.jf.javafx.services.Database;
import net.xeoh.plugins.base.Plugin;
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
public class ERPModule implements Plugin {
    
    @InjectPlugin
    public PluginRepository pr;
    
    @Init
    public void init() throws Exception {
        System.out.println("Hello");
        pr.install(this);
    }
    
    public void installPlugin() throws Exception {
        TableUtils.createTable(new DataSourceConnectionSource(
                Application._getService(Database.class).getAppDataSource(),
                Application._getService(Database.class).getAppDBUrl()),
                Department.class);
    }
}
