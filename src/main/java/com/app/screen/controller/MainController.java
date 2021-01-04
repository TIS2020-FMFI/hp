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
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
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
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimerTask;

import javafx.geometry.Insets;
import javafx.scene.Scene;
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
    ToggleGroup displayA;

    @FXML
    ToggleGroup displayB;

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

        EnvironmentParameters newParameters = AppMain.measurement.getParameters();

        //doplnit ABS

        DisplayYY newDisplayYY = newParameters.getDisplayYY();
        RadioButton selectedDisplayA = (RadioButton) displayA.getSelectedToggle();
        if(selectedDisplayA != null) newDisplayYY.setA(selectedDisplayA.getText());
        RadioButton selectedDisplayB = (RadioButton) displayB.getSelectedToggle();
        if(selectedDisplayB != null) newDisplayYY.setA(selectedDisplayB.getText());

        //doplnit displayX


        newParameters.setDisplayYY(newDisplayYY);

        FrequencySweep newFrequencySweep = newParameters.getFrequencySweep();
        newFrequencySweep.setStart(Double.parseDouble(frequencyStart.getText()));
        newFrequencySweep.setStop(Double.parseDouble(frequencyStop.getText()));
        newFrequencySweep.setStep(Double.parseDouble(frequencyStep.getText()));
        newFrequencySweep.setSpot(Double.parseDouble(frequencySpot.getText()));
        newParameters.setFrequencySweep(newFrequencySweep);

        VoltageSweep newVoltageSweep = newParameters.getVoltageSweep();
        newVoltageSweep.setStart(Double.parseDouble(voltageStart.getText()));
        newVoltageSweep.setStop(Double.parseDouble(voltageStop.getText()));
        newVoltageSweep.setStep(Double.parseDouble(voltageStep.getText()));
        newVoltageSweep.setSpot(Double.parseDouble(voltageSpot.getText()));
        newParameters.setVoltageSweep(newVoltageSweep);

        Other newOther = newParameters.getOther();
        newOther.setCapacitance(Double.parseDouble(otherCapacitance.getText()));
        newOther.setElectricalLength(Double.parseDouble(otherElectricalLength.getText()));
        newOther.setAutoSweep(otherAutoSweep.getValue().equals("ON"));
        newOther.setHighSpeed(otherHighSpeed.getValue().equals("ON"));
        if (otherSweepType.getValue().equals("LINEAR")) {
            newOther.setSweepType(SweepType.LINEAR);
        } else newOther.setSweepType(SweepType.LOG);

        AppMain.measurement.setParameters(newParameters);
        AppMain.measurement.setState(MeasurementState.STARTED);
        AppMain.fileService.setEnvironmentParameters(AppMain.measurement.getParameters());

        try {
            GraphService graphService = new GraphService(upperPane);
            graphService.createGraphRun();
        } catch (NoSuchMethodError e) { // catches exception/error that occurs always when running graph, but works
            throw e;
        }
    }

    public void toggleAutoSave(MouseEvent event) {
        // TODO: change autoSaveMode in global props global props
        if(AppMain.fileService.isAutoSave()){
            autoSaveMenu.setText("Auto save: OFF");
            AppMain.fileService.setAutoSave(false);
        }else {
            autoSaveMenu.setText("Auto save: ON");
            AppMain.fileService.setAutoSave(true);
        }
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
        if(AppMain.measurement.getState() == MeasurementState.SAVED ||
                AppMain.measurement.getState() == MeasurementState.ABORTED ||
                AppMain.measurement.getState() == MeasurementState.WAITING) {
            try {
                AppMain.fileService.setEnvironmentParameters(AppMain.measurement.getParameters());
                AppMain.fileService.saveConfig();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Platform.exit();
            System.exit(0);
        }else{

        }
    }

    public void showHelpWindow(MouseEvent event) {
        //TODO: create simple window with links, details, description
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: read config here
        EnvironmentParameters parameters = null;
        try {
            parameters = AppMain.fileService.loadConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            AppMain.measurement = new Measurement(parameters);
            frequencyStart.setText("" + AppMain.measurement.getParameters().getFrequencySweep().getStart());
            frequencyStop.setText("" + AppMain.measurement.getParameters().getFrequencySweep().getStop());
            frequencySpot.setText("" + AppMain.measurement.getParameters().getFrequencySweep().getSpot());
            frequencyStep.setText("" + AppMain.measurement.getParameters().getFrequencySweep().getStep());

            voltageStart.setText("" + AppMain.measurement.getParameters().getVoltageSweep().getStart());
            voltageStop.setText("" + AppMain.measurement.getParameters().getVoltageSweep().getStop());
            voltageSpot.setText("" + AppMain.measurement.getParameters().getVoltageSweep().getSpot());
            voltageStep.setText("" + AppMain.measurement.getParameters().getVoltageSweep().getStep());

            otherCapacitance.setText("" + AppMain.measurement.getParameters().getOther().getCapacitance());
            otherElectricalLength.setText("" + AppMain.measurement.getParameters().getOther().getElectricalLength());

            // ----- initialize all dropbox -> coz its not possible to do so in sceneBuilder yet
            otherSweepType.getItems().addAll("LINEAR", "LOG");
            if(AppMain.measurement.getParameters().getOther().getSweepType() == SweepType.LINEAR){
                otherSweepType.getSelectionModel().select(0);
            }else otherSweepType.getSelectionModel().select(1);

            otherHighSpeed.getItems().addAll("ON", "OFF" );
            if(AppMain.measurement.getParameters().getOther().isHighSpeed()){
                otherHighSpeed.getSelectionModel().select(0);
            }else otherHighSpeed.getSelectionModel().select(1);

            otherAutoSweep.getItems().addAll("ON", "OFF");
            if(AppMain.measurement.getParameters().getOther().isAutoSweep()){
                otherAutoSweep.getSelectionModel().select(0);
            }else otherAutoSweep.getSelectionModel().select(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // -----

        LocalDate localDate = LocalDate.now();
        AppMain.fileService.setAutoSavingDir("/" + localDate.getYear() + "/" + localDate.getMonth() + "/" + localDate.getDayOfMonth());

        //doplnit text do autoSaveMenu
    }

    public void runConnection(MouseEvent mouseEvent) throws Exception {
        if (AppMain.communicationService.connect())
            gpibMenu.setText("GPIB connection: ACTIVE");
        else
            gpibMenu.setText("GPIB connection: INACTIVE");
    }

    public void AutoSaveDirectory(MouseEvent mouseEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dir = directoryChooser.showDialog(AppMain.ps);
        if (dir != null) {
            LocalDate localDate = LocalDate.now();
            String newAutoSavingDir = dir.getAbsolutePath() + "/" + localDate.getYear() + "/" + localDate.getMonth() + "/" + localDate.getDayOfMonth();
            AppMain.fileService.setAutoSavingDir(newAutoSavingDir);
            savingDirMenu.setText(newAutoSavingDir);
//            System.out.println(dir.getAbsolutePath());
        }
    }
}
