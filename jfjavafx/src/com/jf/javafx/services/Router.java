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
import java.util.Dictionary;
import java.util.Hashtable;
import javafx.scene.Node;

/**
 *
 * @author Hoàng Doãn
 */
public class Router extends AbstractService {
    private Dictionary<String, Node> pageMap = new Hashtable();

    @Override
    protected void _initService() {
        if (appConfig.getBoolean("installed", false)) {
            navigate(appConfig.getString("default_scene", "Index"), false);
        } else {
            navigate(appConfig.getString("install_scene", "Install"));
        }
    }
    
    public void navigate(String path) {
        navigate(path, false);
    }

    public void navigate(String path, boolean refresh) {
        // check if have scene
        Node cur = this.pageMap.get(path);

        if (cur != null) {
            if (!refresh) {
                app.getService(UI.class).setContent(cur);
                return; // end navigation here
            }
        }

        // can inject controls here
        cur = app.createNode(path).first;

        app.getService(UI.class).setContent(cur);
        // save scene to map
        this.pageMap.put(path, cur);
    }
    
}
