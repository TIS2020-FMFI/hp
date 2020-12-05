package com.app.screen.controller;

import com.app.screen.handler.ControlledScreen;
import com.app.screen.handler.ScreensController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class CalibrationScreenController implements Initializable, ControlledScreen {

    ScreensController myController;

    @FXML
    VBox calibrationContainer;

    @FXML
    HBox notificationContainer;
    @FXML
    GridPane contentContainer;
    @FXML
    HBox actionContainer;

    // notificationContainer

    // contentContainer

    // actionContainer
    @FXML
    Button runCalibrationBtn;
    @FXML
    Button closeCalibrationBtn;


    public void create(ActionEvent event) {

    }

    public void openCalibration() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void setScreenParent(ScreensController screenPage) {
        myController = screenPage;
    }
}
