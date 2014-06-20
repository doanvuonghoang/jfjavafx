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
import java.io.File;

/**
 *
 * @author Hoàng Doãn
 */
public class Resource extends AbstractService {
   
    @Override
    protected void _initService() {
    }

    /**
     * Get template file.
     * @param path
     * @return 
     */
    public File getTemplateFile(String path) {
        return app.getTemplateFile(path);
    }
    
    /**
     * Get resource file.
     * @param path
     * @return 
     */
    public File getResourceFile(String path) {
        return app.getResource(path);
    }
}
