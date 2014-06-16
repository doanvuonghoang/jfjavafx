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

package com.jf.javafx;

import javafx.geometry.Pos;
import org.controlsfx.control.Notifications;
import org.controlsfx.dialog.Dialogs;

/**
 *
 * @author Hoàng Doãn
 */
public class MsgBox {
    public static void showException(Exception ex) {
        showException("Exception", ex, null);
    }

    public static void showException(Exception ex, String message) {
        showException("Exception", ex, message);
    }

    public static void showException(String title, Exception ex, String message) {
        Dialogs.create().title(title).message(message == null ? ex.getMessage() : message).showException(ex);
    }

    public static void showInformation(String title, String message) {
        Dialogs.create().title(title).message(message).showInformation();
    }

    public static org.controlsfx.control.action.Action showConfirm(String title, String message) {
        return Dialogs.create().title(title).message(message).showConfirm();
    }
    
    public static void showNotification(String message) {
        Notifications.create().position(Pos.TOP_RIGHT).text(message).showError();
    }
    
    public static void showNotification(String message, String title, Object owner) {
        Notifications.create().owner(owner).position(Pos.TOP_RIGHT).title(title).text(message).showError();
    }
}
