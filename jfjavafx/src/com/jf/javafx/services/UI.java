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
import com.jf.javafx.Pair;
import com.jf.javafx.controllers.MasterController;
import java.io.File;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 *
 * @author Hoàng Doãn
 */
public class UI extends AbstractService {
    private MasterController mc;
    
    @Override
    protected void _initService() {
        Pair p = app.createNode("Master");
        mc = (MasterController) p.second;
        Scene s = new Scene((Parent) p.first);
        
        try {
            s.getStylesheets().add(app.getResource("global.css").toURL().toString());
        } catch (MalformedURLException ex) {
            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        mc.setLogo("logo.png");
        app.getStage().setScene(s);
    }

    public void setContent(Node n) {
        mc.setContent(n);
    }
    
    public File getTemplateFile(String path) {
        return app.getTemplateFile(path);
    }
}
