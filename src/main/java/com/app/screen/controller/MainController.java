package com.app.screen.controller;

import com.app.machineCommunication.Connection;
import com.app.service.AppMain;
import com.app.service.graph.Graph;
import com.app.service.graph.GraphService;
import com.app.service.notification.NotificationType;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.fx.ChartCanvas;
import org.jfree.chart.fx.ChartViewer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimerTask;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class MainController implements Initializable {

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

    @FXML
    AnchorPane upperPane;

    @FXML
    Pane lowerPane;

    @FXML
    VBox VBox1;


    private Node useWorkaround(ChartViewer viewer) {
        if (true) {
            return new StackPane(viewer);
        }
        return viewer;
    }

    public void runMeasurement(MouseEvent event) {
        // TODO: run measurement and graph

        try {
            GraphService graphService = new GraphService(upperPane);
            graphService.createGraphRun();
        } catch (NoSuchMethodError e) { // catches exception/error that occurs always when running graph, but works
            throw e;
        }
    }

    public void toggleAutoSave(MouseEvent event) {
        // TODO: change autoSaveMode in global props global props
    }

    public void resetInstrument(MouseEvent event) {
        // TODO: send command to reset instrument
    }

    public void triggerCalibration(MouseEvent event) {
        try {
            AppMain.calibrationService.openCalibration();
        } catch (Exception e) {
            AppMain.notificationService.createNotification("Calibration window could not be open! Please, restart the app.", NotificationType.ERROR).show();
        }
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
        otherSweepType.getItems().addAll("LINEAR", "LOG");
        otherSweepType.getSelectionModel().select(0);
        otherHighSpeed.getItems().addAll("OFF", "ON");
        otherHighSpeed.getSelectionModel().select(0);
        otherAutoSweep.getItems().addAll("ON", "OFF");
        otherAutoSweep.getSelectionModel().select(0);
        // -----
    }

    public void runConnection(MouseEvent mouseEvent) throws Exception {
        if (AppMain.communicationService.connect())
            gpibMenu.setText("GPIB connection: ACTIVE");
        else
            gpibMenu.setText("GPIB connection: INACTIVE");
    }
}
