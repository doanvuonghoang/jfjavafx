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

package com.jf.javafx.controllers;

import com.jf.javafx.Application;
import com.jf.javafx.Controller;
import java.net.URL;
import java.util.ResourceBundle;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Hoàng Doãn
 */
public class GlobalController extends Controller {

    public GlobalController(Application app) {
        super(app);
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        Dialogs.create().title(rb.getString("message")).message("Ooops! " + this.app).showInformation();
    }
    
}
