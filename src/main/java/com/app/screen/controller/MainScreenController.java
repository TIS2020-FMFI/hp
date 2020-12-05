package com.app.screen.controller;

import com.app.screen.handler.ControlledScreen;
import com.app.screen.handler.ScreensController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class MainScreenController implements Initializable, ControlledScreen {

    ScreensController myController;

    @FXML
    VBox mainContainer;

    @FXML
    HBox mainContainerMenu;
    @FXML
    Button gpibMenuBtn;
    @FXML
    Button calibrationMenuBtn;
    @FXML
    ToggleButton autoSaveMenuBtn;
    @FXML
    Button savingDirMenuBtn;
    @FXML
    Button restartInstrumentMenuBtn;
    @FXML
    Button helpMenuBtn;
    @FXML
    Button quitMenuBtn;


    public void create(ActionEvent event) {

    }

    public void openCalibration(MouseEvent mouseEvent) {
        myController.setScreen("calibrationScreen");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void setScreenParent(ScreensController screenPage) {
        myController = screenPage;
    }

}
