package com.app.screen.controller;

import com.app.AppMain;
import com.app.screen.handler.ControlledScreen;
import com.app.screen.handler.ScreensController;
import com.sun.tools.javac.Main;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MainScreenController implements Initializable, ControlledScreen {

    ScreensController myController;

    @FXML
    VBox mainContainer;
    @FXML
    VBox notificationContainer;

    @FXML
    TextField frequencyStart;
    @FXML
    TextField frequencyStop;
    @FXML
    TextField frequencyStep;
    @FXML
    TextField frequencySpot;

    @FXML
    TextField voltageStart;
    @FXML
    TextField voltageStop;
    @FXML
    TextField voltageStep;
    @FXML
    TextField voltageSpot;

    @FXML
    TextField otherCapacitance;
    @FXML
    TextField otherElectricalLength;
    @FXML
    ChoiceBox<String> otherSweepType;
    @FXML
    ChoiceBox<String> otherHighSpeed;
    @FXML
    ChoiceBox<String> otherAutoSweep;
    @FXML
    ChoiceBox<String> otherManualSweep;
    @FXML
    ChoiceBox<String> otherTriggerMode;

    @FXML
    Button upperGraphRun;
    @FXML
    Button upperGraphLoad;
    @FXML
    Button upperGraphExport;
    @FXML
    Button upperGraphSave;
    @FXML
    Button upperGraphPoint;

    @FXML
    Button lowerGraphRun;
    @FXML
    Button lowerGraphLoad;
    @FXML
    Button lowerGraphExport;
    @FXML
    Button lowerGraphSave;
    @FXML
    Button lowerGraphPoint;

    @FXML
    Button gpibMenu;
    @FXML
    Button calibrationMenu;
    @FXML
    ToggleButton autoSaveMenu;
    @FXML
    Button savingDirMenu;
    @FXML
    Button restartInstrumentMenu;
    @FXML
    Button helpMenu;
    @FXML
    Button quitMenu;


    public void toggleAutoSave(MouseEvent event) {
        // TODO: change autoSaveMode in global props global props
    }

    public void resetInstrument(MouseEvent event) {
        // TODO: send command to reset instrument
    }

    public void triggerCalibration(MouseEvent event) {
        Stage calibration = new Stage();

        Group root = new Group();
        root.getChildren().addAll(myController.getScreen("calibrationScreen"));
        calibration.setTitle("Calibration");
        calibration.setScene(new Scene(root));
        calibration.show();
        // TODO: open calibration window
    }

    public void quitApp(MouseEvent event) {
        // TODO: if not all data saved -> notification and abort quit
        // save global props into config
        Platform.exit();
        System.exit(0);
    }

    public void showHelpWindow(MouseEvent event) {
        //TODO: create simple window with links, details, description
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: read config here

        // ----- initialize all dropbox -> coz its not possible to do so in sceneBuilder yet
        otherSweepType.getItems().addAll("OFF", "ON");
        otherSweepType.getSelectionModel().select(0);
        otherHighSpeed.getItems().addAll("OFF", "ON");
        otherHighSpeed.getSelectionModel().select(0);
        otherAutoSweep.getItems().addAll("OFF", "ON");
        otherAutoSweep.getSelectionModel().select(0);
        otherManualSweep.getItems().addAll("OFF","ON");
        otherManualSweep.getSelectionModel().select(0);
        otherTriggerMode.getItems().addAll("INTERNAL", "EXTERNAL");
        otherTriggerMode.getSelectionModel().select(0);
        // -----
    }

    @Override
    public void setScreenParent(ScreensController screenPage) {
        myController = screenPage;
    }

}
