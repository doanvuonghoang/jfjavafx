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
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.TimelineBuilder;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author Hoàng Doãn
 */
public class MasterController extends Controller {

    @FXML
    private MenuBar menuBar;

    @FXML
    private Button btnWClose, btnWMin, btnWMax;

    @FXML
    private ImageView imgLogo;

    @FXML
    private BorderPane contentPane;
    
    @FXML
    private ToolBar headPane;
    
    @FXML
    private StackPane modalDimmer;

    private Rectangle2D backupWindowBounds = null;
    private boolean maximized = false;
    
    private double mouseDragOffsetX = 0;
    private double mouseDragOffsetY = 0;

    public MasterController(Application app) {
        super(app);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        final Stage stage = app.getStage();
        
        modalDimmer.setOnMouseClicked((MouseEvent t) -> {
            t.consume();
            hideModalMessage();
        });

        btnWClose.setOnAction((ActionEvent actionEvent) -> {
            stage.close();
        });
        btnWMin.setOnAction((ActionEvent actionEvent) -> {
            stage.setIconified(true);
        });
        btnWMax.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                toogleMaximized();
            }

            private void toogleMaximized() {
                final Screen screen = Screen.getScreensForRectangle(stage.getX(), stage.getY(), 1, 1).get(0);
                if (maximized) {
                    maximized = false;
                    if (backupWindowBounds != null) {
                        stage.setX(backupWindowBounds.getMinX());
                        stage.setY(backupWindowBounds.getMinY());
                        stage.setWidth(backupWindowBounds.getWidth());
                        stage.setHeight(backupWindowBounds.getHeight());
                    }
                } else {
                    maximized = true;
                    backupWindowBounds = new Rectangle2D(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
                    stage.setX(screen.getVisualBounds().getMinX());
                    stage.setY(screen.getVisualBounds().getMinY());
                    stage.setWidth(screen.getVisualBounds().getWidth());
                    stage.setHeight(screen.getVisualBounds().getHeight());
                }
            }
        });
        
        // add window dragging
        headPane.setOnMousePressed((MouseEvent event) -> {
            mouseDragOffsetX = event.getSceneX();
            mouseDragOffsetY = event.getSceneY();
        });
        headPane.setOnMouseDragged((MouseEvent event) -> {
            if (!isMaximized()) {
                app.getStage().setX(event.getScreenX() - mouseDragOffsetX);
                app.getStage().setY(event.getScreenY() - mouseDragOffsetY);
            }
        });
    }
    
    public boolean isMaximized() {
        return maximized;
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
                new KeyFrame(Duration.seconds(1), (ActionEvent t) -> {
                    modalDimmer.setCache(false);
                },
                        new KeyValue(modalDimmer.opacityProperty(), 1, Interpolator.EASE_BOTH)
                )).build().play();
    }

    /**
     * Hide any modal message that is shown
     */
    public void hideModalMessage() {
        modalDimmer.setCache(true);
        TimelineBuilder.create().keyFrames(
                new KeyFrame(Duration.seconds(1), (ActionEvent t) -> {
                    modalDimmer.setCache(false);
                    modalDimmer.setVisible(false);
                    modalDimmer.getChildren().clear();
                },
                        new KeyValue(modalDimmer.opacityProperty(), 0, Interpolator.EASE_BOTH)
                )).build().play();
    }
    
    /**
     * Set main content of scene
     * @param n 
     */
    public void setContent(Node n) {
        contentPane.setCenter(n);
    }
    
    /**
     * Set logo of application
     * @param path resource path
     */
    public void setLogo(String path) {
        try {
            imgLogo.setImage(new Image(app.getResource(path).toURL().openStream()));
        } catch (IOException ex) {
            Logger.getLogger(MasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * get menu bar of application
     * @return 
     */
    public MenuBar getMenuBar() {
        return menuBar;
    }
    
    /**
     * Add a menu to bar
     * @param m 
     */
    public void addMenu(Menu m) {
        menuBar.getMenus().add(m);
    }
}
