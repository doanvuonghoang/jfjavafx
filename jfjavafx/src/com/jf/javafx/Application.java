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

import ensemble.controls.WindowButtons;
import ensemble.controls.WindowResizeButton;
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
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.TimelineBuilder;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
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

    private ResourceBundle rb;

    private Stage pStage;
    private Dictionary<String, Node> pageMap = new Hashtable<String, Node>();
    private WindowResizeButton windowResizeButton;
    private BorderPane root;
    private StackPane modalDimmer;
    private double mouseDragOffsetX = 0;
    private double mouseDragOffsetY = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {
        _initEnvVars();
        // load properties
        _initConfigurations();
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
     *
     * @throws Exception
     */
    private void _initEnvVars() throws Exception {
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
     * Start the application
     *
     * @param primaryStage
     */
    private void _initStage(Stage primaryStage) throws IOException {
        this.pStage = primaryStage;
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle(getResourceBundle().getString("app.title"));
        
        windowResizeButton = new WindowResizeButton(primaryStage, 1020,700);
        
        //Stack layer
        StackPane layerPane = new StackPane();
        Scene s = new Scene(layerPane);
        s.getStylesheets().add(getResource("global.css").toURL().toString());
        
        // create root
        root = new BorderPane() {
            @Override protected void layoutChildren() {
                super.layoutChildren();
                windowResizeButton.autosize();
                windowResizeButton.setLayoutX(getWidth() - windowResizeButton.getLayoutBounds().getWidth());
                windowResizeButton.setLayoutY(getHeight() - windowResizeButton.getLayoutBounds().getHeight());
            }
        };
        root.getStyleClass().add("application");
        root.setId("root");
        layerPane.getChildren().add(root);

        // create modal dimmer, to dim screen when showing modal dialogs
        modalDimmer = new StackPane();
        modalDimmer.setId("ModalDimmer");
        modalDimmer.setOnMouseClicked((MouseEvent t) -> {
            t.consume();
            hideModalMessage();
        });
        modalDimmer.setVisible(false);
        layerPane.getChildren().add(modalDimmer);
        
        // create main toolbar
        ToolBar toolBar = new ToolBar();
        toolBar.setId("mainToolBar");
        ImageView logo = new ImageView(new Image(getResource("logo.png").toURL().toString()));
        HBox.setMargin(logo, new Insets(0,0,0,5));
        toolBar.getItems().add(logo);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        toolBar.getItems().add(spacer);
        
        // add close min max
        final WindowButtons windowButtons = new WindowButtons(primaryStage);
        toolBar.getItems().add(windowButtons);
        // add window header double clicking
        toolBar.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                windowButtons.toogleMaximized();
            }
        });
        // add window dragging
        toolBar.setOnMousePressed((MouseEvent event) -> {
            mouseDragOffsetX = event.getSceneX();
            mouseDragOffsetY = event.getSceneY();
        });
        toolBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                if(!windowButtons.isMaximized()) {
                    primaryStage.setX(event.getScreenX()-mouseDragOffsetX);
                    primaryStage.setY(event.getScreenY()-mouseDragOffsetY);
                }
            }
        });
        
        root.setTop(toolBar);
        // add window resize button so its on top
        windowResizeButton.setManaged(false);
        root.getChildren().add(windowResizeButton);
        
        if (config.getBoolean("installed", false)) {
            navigate(config.getString("default_scene", "Global"), false);
        } else {
            navigate(config.getString("install_scene", "Install"));
        }

        // handle on closing window
        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            _commitProperties();
        });

        primaryStage.setScene(s);
        primaryStage.show();
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
     *
     * @return the configuration object
     */
    public Configuration getConfiguration() {
        return config;
    }

    public void navigate(String path) {
        navigate(path, false);
    }

    public void navigate(String path, boolean refresh) {
        // check if have scene
        Node cur = this.pageMap.get(path);

        if (cur != null) {
            if (!refresh) {
                _setDisplayNode(cur);
                return; // end navigation here
            }
        }

        // can inject controls here
        cur = getNode(path);

        _setDisplayNode(cur);
        // save scene to map
        this.pageMap.put(path, cur);
    }
    
    private void _setDisplayNode(Node n) {
//        this.root.getChildren().clear();
//        this.root.getChildren().add(n);
        this.root.setCenter(n);
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

    public void showInformation(String title, String message) {
        Dialogs.create().title(title).message(message).showInformation();
    }

    public org.controlsfx.control.action.Action showConfirm(String title, String message) {
        return Dialogs.create().title(title).message(message).showConfirm();
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

    /**
     * Get template file.
     *
     * @param path
     * @return
     */
    public File getTemplateFile(String path) {
        return new File(JF_TEMPLATES + File.separator + path + ".fxml");
    }

    /**
     * Load a node and its controller.
     *
     * @param app
     * @param path
     * @return the node
     */
    public Node getNode(String path) {
        final Application app = this;
        File fxml = getTemplateFile(path);
        Node root;
        try {
            ResourceBundle rb;
            try {
                rb = getResourceBundle("controllers/" + path);
            } catch (Exception ex) {
                Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
                rb = null;
            }

            root = FXMLLoader.load(fxml.toURL(), rb, new JavaFXBuilderFactory(), (Class<?> param) -> {
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
                    showException(ex);
                    return null;
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
            showException(ex, "Error while navigate to path: " + path);

            root = new AnchorPane();
        }

        return root;
    }

    public Scene getCurrentScene() {
        return pStage.getScene();
    }
    
    /**
     * Show the given node as a floating dialog over the whole application, with 
     * the rest of the application dimmed out and blocked from mouse events.
     * 
     * @param message 
     */
    public void showModalMessage(Node message) {
        modalDimmer.getChildren().add(message);
        modalDimmer.setOpacity(0);
        modalDimmer.setVisible(true);
        modalDimmer.setCache(true);
        TimelineBuilder.create().keyFrames(
            new KeyFrame(Duration.seconds(1), 
                new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent t) {
                        modalDimmer.setCache(false);
                    }
                },
                new KeyValue(modalDimmer.opacityProperty(),1, Interpolator.EASE_BOTH)
        )).build().play();
    }
    
    /**
     * Hide any modal message that is shown
     */
    public void hideModalMessage() {
        modalDimmer.setCache(true);
        TimelineBuilder.create().keyFrames(
            new KeyFrame(Duration.seconds(1), 
                new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent t) {
                        modalDimmer.setCache(false);
                        modalDimmer.setVisible(false);
                        modalDimmer.getChildren().clear();
                    }
                },
                new KeyValue(modalDimmer.opacityProperty(),0, Interpolator.EASE_BOTH)
        )).build().play();
    }
}
