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
package com.jf.javafx.services;

import com.jf.javafx.AbstractService;
import com.jf.javafx.Application;
import java.io.File;
import net.xeoh.plugins.base.Plugin;
import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;

/**
 *
 * @author Hoàng Doãn
 */
public class Plugins extends AbstractService {
    private PluginManager pm;

    @Override
    protected void _initService() {
        pm = PluginManagerFactory.createPluginManager();
        pm.addPluginsFrom((new File(getPluginsPath())).toURI());
    }
    
    /**
     * Get plugin path
     * @return String
     */
    public String getPluginsPath() {
        return Application.JF_HOME + File.separator + "plugins";
    }

    /**
     * Get plugin instance
     * @param <T> class extends Plugin
     * @param cls class of T
     * @return instance of T 
     */
    public <T extends Plugin> T getPlugin(Class<T> cls) {
        return pm.getPlugin(cls);
    }
    
    public PluginManager getPluginManager() {
        return pm;
    }
}
