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

package com.jf.javafx.plugins.menu;

import com.jf.javafx.plugins.menu.datamodels.Menu;
import java.util.Collection;
import net.xeoh.plugins.base.Plugin;

/**
 *
 * @author Hoàng Doãn
 */
public interface MenuPlugin extends Plugin {
    public void render();
    
    public Collection<Menu> getAvailableMenues() throws Exception;
    
    public Collection<Menu> getAllMenues() throws Exception;
    
    public void create(Menu menu) throws Exception;
    
    public void save(Collection<Menu> menues) throws Exception;
    
    public void save(Menu menu) throws Exception;
    
    public void delete(Menu menu) throws Exception;
}
