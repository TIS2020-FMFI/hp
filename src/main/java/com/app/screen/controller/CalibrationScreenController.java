package com.app.screen.controller;

import com.app.screen.handler.ControlledScreen;
import com.app.screen.handler.ScreensController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
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
    @FXML
    ToggleGroup calibrationType;
    // actionContainer
    @FXML
    Button runCalibrationBtn;


    public void runCalibration() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        RadioButton rb = (RadioButton) calibrationType.getSelectedToggle();
        rb.getText();
    }

    @Override
    public void setScreenParent(ScreensController screenPage) {
        myController = screenPage;
    }
}
