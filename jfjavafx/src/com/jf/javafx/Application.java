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
import java.net.MalformedURLException;
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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

/**
 *
 * @author Hoàng
 */
public class Application extends javafx.application.Application {

    /**
     * JF_HOME
     */
    public static String JF_HOME;
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

    private ResourceBundle rb;

    private final Dictionary<Class, Service> services = new Hashtable();

    private Stage pStage;
    
    private static Application sapp = null;

    /**
     * Start the application
     *
     * @param primaryStage
     * @throws java.lang.Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Application.sapp = this;
        this.pStage = primaryStage;
        
        // set env variables
        _initVars();
        // load properties
        _initConfigurations();

        _initStartupServices();

        // handle on closing window
        primaryStage.setOnHidden((WindowEvent event) -> {
            _commitProperties();
        });
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle(getResourceBundle().getString("app.title"));
        primaryStage.show();
    }

    /**
     * Initialize environment variables.
     *
     * @throws Exception
     */
    private void _initVars() throws Exception {
        // set jf_home
//        JF_HOME = (new File(Application.class.getProtectionDomain().getCodeSource().getLocation().getPath())).getParent();
        JF_HOME = System.getProperty("user.dir");
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
    
    private void _initStartupServices() {
        for(String cls : config.getStringArray("startup_services.service")) {
            try {
                Class<?> p = Class.forName(cls);
                getService((Class<Service>) p);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
    
    public ResourceBundle getResourceBundle() throws MalformedURLException {
        if (this.rb == null) {
            String s = null;
            if ((s = config.getString("resource_bundle")) == null) {
                s = "App";
                config.setProperty("resource_bundle", s);
            }
            this.rb = getResourceBundle(s);
        }

        return this.rb;
    }

    /**
     * Get configuration object.
     *
     * @return the configuration object
     */
    public Configuration getConfiguration() {
        return config;
    }

    /**
     * Get a running service of application
     * @param <T> class extends Service
     * @param n class of T
     * @return the service object
     */
    public <T extends Service> T getService(Class<T> n) {
        Service s = this.services.get(n);
        if (s == null) {
            try {
                s = (T) n.newInstance();
                this.services.put(n, s);
                s.init(this);
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return (T) s;
    }
    
    public static <T extends Service> T _getService(Class<T> n) {
        return Application.sapp.getService(n);
    }

    /**
     * Get resource bundle.
     *
     * @param rs resource path
     * @return the resource
     * @throws MalformedURLException
     */
    public ResourceBundle getResourceBundle(String rs) throws MalformedURLException {
        return ResourceBundle.getBundle(rs, Locale.getDefault(), new URLClassLoader(new URL[]{(new File(JF_RESOURCES)).toURL()}));
    }

    /**
     * Get resource of application context.
     *
     * @param path
     * @return
     */
    public File getResource(String path) {
        return new File(JF_RESOURCES + File.separator + path);
    }
    
    public File getConfig(String path) {
        return new File(JF_CONF + File.separator + path);
    }

    /**
     * Get template file.
     *
     * @param path
     * @return
     */
    public File getTemplateFile(String path) {
        return new File(JF_TEMPLATES + File.separator + path);
    }

    /**
     * Load a node and its controller.
     * 
     * @param path
     * @return the node
     */
    public Pair<Node, ?> createNode(String path) {
        final Application app = this;
        File fxml = getTemplateFile(path + ".fxml");
        Node node;
        Object controller = null;
        try {
            ResourceBundle bundle;
            try {
                bundle = getResourceBundle("controllers/" + path);
            } catch (Exception ex) {
                bundle = null;
            }

            FXMLLoader loader = new FXMLLoader(fxml.toURL(), bundle, new JavaFXBuilderFactory(), (Class<?> param) -> {
                try {
                    Class cls = Controller.class;

                    if (cls.isAssignableFrom(param)) {
                        try {
                            return param.getConstructor(Application.class).newInstance(app);
                        } catch (Exception ex) {
                            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
                            return param.newInstance();
                        }
                    } else {
                        return param.newInstance();
                    }
                } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | SecurityException ex) {
                    MsgBox.showException(ex);
                    return null;
                }
            });
            node = loader.load();
            controller = loader.getController();
        } catch (IOException ex) {
            MsgBox.showException(ex, "Error while navigate to path: " + path);

            // customize error screen
            AnchorPane p = new AnchorPane();
            
//            StringWriter sw = new StringWriter();
//            ex.printStackTrace(new PrintWriter(sw));
//            p.getChildren().add(new Label(sw.toString()));

            node = p;
        }

        return new Pair(node, controller);
    }
    
    public Stage getStage() {
        return this.pStage;
    }

    public Scene getScene() {
        return pStage.getScene();
    }
    
    public void setScene(Scene s) {
        pStage.setScene(s);
    }

    /**
     * Start application
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
