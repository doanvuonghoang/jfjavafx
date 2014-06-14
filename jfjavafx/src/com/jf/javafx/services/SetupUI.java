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
import ensemble.controls.WindowButtons;
import ensemble.controls.WindowResizeButton;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.TimelineBuilder;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 *
 * @author Hoàng Doãn
 */
public class SetupUI extends AbstractService {
    private double mouseDragOffsetX = 0;
    private double mouseDragOffsetY = 0;
    private StackPane modalDimmer;
    private BorderPane contentPane;
    
    @Override
    protected void _initService() {
        StackPane layerPane = new StackPane();
        Scene s = new Scene(layerPane);
        try {
            s.getStylesheets().add(app.getResource("global.css").toURL().toString());
        } catch (MalformedURLException ex) {
            Logger.getLogger(SetupUI.class.getName()).log(Level.SEVERE, null, ex);
        }

        _setupContentPane(layerPane);

        _setupModalDimmer(layerPane);

        _setupToolBar();

        app.getStage().setScene(s);
    }
    
    private void _setupContentPane(StackPane layerPane) {
        WindowResizeButton windowResizeButton = new WindowResizeButton(app.getStage(), 1020, 700);
        // create root
        contentPane = new BorderPane() {
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                windowResizeButton.autosize();
                windowResizeButton.setLayoutX(getWidth() - windowResizeButton.getLayoutBounds().getWidth());
                windowResizeButton.setLayoutY(getHeight() - windowResizeButton.getLayoutBounds().getHeight());
            }
        };
        contentPane.getStyleClass().add("application");
        contentPane.setId("root");
        layerPane.getChildren().add(contentPane);
        
        // add window resize button so its on top
        windowResizeButton.setManaged(false);
        contentPane.getChildren().add(windowResizeButton);
    }
    
    private void _setupModalDimmer(StackPane layerPane) {
        // create modal dimmer, to dim screen when showing modal dialogs
        modalDimmer = new StackPane();
        modalDimmer.setId("ModalDimmer");
        modalDimmer.setOnMouseClicked((MouseEvent t) -> {
            t.consume();
            hideModalMessage();
        });
        modalDimmer.setVisible(false);
        layerPane.getChildren().add(modalDimmer);
    }
    
    private void _setupToolBar() {
        // create main toolbar
        ToolBar toolBar = new ToolBar();
        toolBar.setId("mainToolBar");
        ImageView logo;
        try {
            logo = new ImageView(new Image(app.getResource("logo.png").toURL().toString()));
            HBox.setMargin(logo, new Insets(0, 0, 0, 5));
            toolBar.getItems().add(logo);
        } catch (MalformedURLException ex) {
            Logger.getLogger(SetupUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        toolBar.getItems().add(spacer);

        // add close min max
        final WindowButtons windowButtons = new WindowButtons(app.getStage());
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
        toolBar.setOnMouseDragged((MouseEvent event) -> {
            if (!windowButtons.isMaximized()) {
                app.getStage().setX(event.getScreenX() - mouseDragOffsetX);
                app.getStage().setY(event.getScreenY() - mouseDragOffsetY);
            }
        });

        contentPane.setTop(toolBar);
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
    
    public void setContent(Node n) {
//        this.root.getChildren().clear();
//        this.root.getChildren().add(n);
        contentPane.setCenter(n);
    }
}
