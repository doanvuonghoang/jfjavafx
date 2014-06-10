/*
 * Copyright (C) 2014 Hoàng
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Hoàng
 */
public class Application extends javafx.application.Application {
    protected static String JF_HOME;
    protected static String JF_CONF;
    private static Properties properties;

    @Override
    public void start(Stage primaryStage) throws Exception {
        initEnvVars();
        initStage(primaryStage);
    }
    
    public static void main(String[] args) {
        launch(args);
    }

    private void initEnvVars() throws Exception {
        JF_HOME = (new File(Application.class.getProtectionDomain().getCodeSource().getLocation().getPath())).getParent();
        
        JF_CONF = JF_HOME + File.separator + "conf";
        File f = new File(JF_CONF);
        if(!f.exists()) f.mkdirs();
        
        properties = new Properties();
        f = new File(getJFPropertiesFile());
        if(!f.exists()) f.createNewFile();
        else properties.load(new FileInputStream(f));
    }

    private void initStage(Stage primaryStage) {
        primaryStage.setTitle(JF_HOME);
        
        primaryStage.onShownProperty().addListener((ObservableValue<? extends EventHandler<WindowEvent>> observable, EventHandler<WindowEvent> oldValue, EventHandler<WindowEvent> newValue) -> {
            commitProperties();
        });
        
        primaryStage.show();
    }

    protected String getJFPropertiesFile() {
        return JF_CONF + File.separator + "jf.properties";
    }

    private void commitProperties() {
        try {
            properties.store(new FileOutputStream(getJFPropertiesFile()), "");
        } catch (IOException ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
