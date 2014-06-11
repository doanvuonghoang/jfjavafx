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
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.controlsfx.dialog.Dialogs;

/**
 *
 * @author Hoàng
 */
public class Application extends javafx.application.Application {

    /**
     * JF_HOME
     */
    protected static String JF_HOME;
    /**
     * config path
     */
    protected static String JF_CONF;
    /**
     * template path
     */
    protected static String JF_TEMPLATES;
    /**
     * resources path
     */
    protected static String JF_RESOURCES;
    /**
     * config
     */
    private Configuration config;
    
    private Stage pStage;
    private Dictionary<String, Scene> sceneMap = new Hashtable<String, Scene>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        _initEnvVars();
        _initStage(primaryStage);
    }

    /**
     * Start application
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initialize environment variables.
     * @throws Exception 
     */
    private void _initEnvVars() throws Exception {
        // set jf_home
        JF_HOME = (new File(Application.class.getProtectionDomain().getCodeSource().getLocation().getPath())).getParent();
        //set jf_conf path
        JF_CONF = JF_HOME + File.separator + "conf";
        File f = new File(JF_CONF);
        if (!f.exists()) {
            f.mkdirs();
        }
        // set jf_templates path
        JF_TEMPLATES = JF_HOME + File.separator + "templates";
        f = new File(JF_TEMPLATES);
        if (!f.exists()) {
            f.mkdirs();
        }
        // set jf_resources path
        JF_RESOURCES = JF_HOME + File.separator + "resources";
        f = new File(JF_TEMPLATES);
        if (!f.exists()) {
            f.mkdirs();
        }
        // load properties
        _initConfigurations();
    }

    /**
     * Start the application
     * @param primaryStage 
     */
    private void _initStage(Stage primaryStage) throws IOException {
        this.pStage = primaryStage;
        
        primaryStage.setTitle(JF_HOME);
        
        // test load fxml
        navigate("Global", false);
        
        // handle on closing window
        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            _commitProperties();
        });

        primaryStage.show();
    }

    /**
     * Get properties file in string
     *
     * @return properties file in string
     */
    protected String _getJFPropertiesFile() {
        return JF_CONF + File.separator + "jf.properties";
    }

    /**
     * Invoke to save changes to properties file.
     */
    private void _commitProperties() {
        try {
            ((XMLConfiguration) getConfiguration()).save(_getJFPropertiesFile());
        } catch (ConfigurationException ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Initialize configurations.
     */
    private void _initConfigurations() {
        File f = new File(_getJFPropertiesFile());
        if (f.exists()) {
            try {
                config = new XMLConfiguration(f);
            } catch (ConfigurationException ex) {
                Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            config = new XMLConfiguration();
            ((XMLConfiguration) getConfiguration()).setFileName(_getJFPropertiesFile());
        }
    }

    /**
     * Get configuration object.
     * @return the configuration object
     */
    public Configuration getConfiguration() {
        return config;
    }
    
    public void navigate(String path) throws Exception {
        navigate(path, false);
    }
    
    public void navigate(String path, boolean refresh) {
        // check if have scene
        Scene cur = this.sceneMap.get(path);
        
        if(cur != null) {
            if(!refresh) {
                this.pStage.setScene(cur);
                return; // end navigation here
            }
        }
        
        File fxml = new File(JF_TEMPLATES + File.separator + path + ".fxml");
        Parent root;
        try {
            final Application app = this;
            ResourceBundle rb;
            try {
                rb = ResourceBundle.getBundle("controllers/" + path, Locale.getDefault(), new URLClassLoader(new URL[] {(new File(JF_RESOURCES)).toURL()}));
            } catch(Exception ex) {
                Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
                rb = null;
            }
            
            root = FXMLLoader.load(fxml.toURL(), rb, new JavaFXBuilderFactory(), (Class<?> param) -> {
                try {
                    Class cls = Controller.class;
                    
                    if(cls.isAssignableFrom(param)) {
                        try {
                            return param.getConstructor(Application.class).newInstance(app);
                        } catch (Exception ex) {
                            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
                            return param.newInstance();
                        }
                    }
                    else 
                        return param.newInstance();
                } catch(IllegalAccessException | IllegalArgumentException | InstantiationException | SecurityException ex) {
                    showException(ex);
                    return null;
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
            showException(ex, "Error while navigate to path: " + path);
            
            root = new AnchorPane();
        }
        cur = new Scene(root);
        this.sceneMap.put(path, cur);
        
        // navigate to scene
        this.pStage.setScene(cur);
    }
    
    public void showException(Exception ex) {
        showException("Exception", ex, null);
    }
    
    public void showException(Exception ex, String message) {
        showException("Exception", ex, message);
    }
    
    public void showException(String title, Exception ex, String message) {
        Dialogs.create().title(title).message(message == null ? ex.getMessage() : message).showException(ex);
    }
}
