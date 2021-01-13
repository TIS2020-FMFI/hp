package com.app.screen.controller;

import com.app.service.AppMain;
import com.app.service.file.parameters.*;
import com.app.service.measurement.Measurement;
import com.app.service.measurement.MeasurementState;
import com.app.service.notification.NotificationType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

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
    TextArea commentInput;

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
    AnchorPane lowerPane;
    @FXML
    ToggleGroup toogleUpperXAxis;
    @FXML
    ToggleGroup toogleLowerXAxis;


    @FXML
    VBox VBox1;

    public void updateComment(MouseEvent event) {
        AppMain.measurement.updateComment(commentInput.getText());
    }

    public void setUpperPaneRun(MouseEvent event) {
        AppMain.graphService.setUpperRunning();
    }

    public void setLowerPaneRun(MouseEvent event) {
        AppMain.graphService.setLowerRunning();
    }

    public void setUpperPaneLoad(MouseEvent event) {
        AppMain.graphService.setUpperLoaded();
    }

    public void setLowerPaneLoad(MouseEvent event) {
        AppMain.graphService.setLowerLoaded();
    }

    public void runMeasurement(MouseEvent event) {
        // TODO: run measurement and graph

        if (AppMain.graphService.isRunning()) {
            return;
        }

        EnvironmentParameters newParameters = AppMain.measurement.getParameters();

        //doplnit ABS

        DisplayYY newDisplayYY = newParameters.getDisplayYY();
        RadioButton selectedDisplayA = (RadioButton) displayA.getSelectedToggle();
        if(selectedDisplayA != null) newDisplayYY.setA(selectedDisplayA.getText());
        RadioButton selectedDisplayB = (RadioButton) displayB.getSelectedToggle();
        if(selectedDisplayB != null) newDisplayYY.setA(selectedDisplayB.getText());

        if(event.getSource() == upperGraphRun){
            RadioButton selectedDisplayX = (RadioButton) toogleUpperXAxis.getSelectedToggle();
            if(selectedDisplayX.getText() == "Frequency"){
                newDisplayYY.setX(MeasuredQuantity.FREQUENCY);
            }else{
                newDisplayYY.setX(MeasuredQuantity.VOLTAGE);
            }
        }else{
            RadioButton selectedDisplayX = (RadioButton) toogleLowerXAxis.getSelectedToggle();
            if(selectedDisplayX.getText() == "Frequency"){
                newDisplayYY.setX(MeasuredQuantity.FREQUENCY);
            }else{
                newDisplayYY.setX(MeasuredQuantity.VOLTAGE);
            }
        }

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
            RadioButton selectedRadioButtonUpper = (RadioButton) toogleUpperXAxis.getSelectedToggle();
            String toogleGroupValueUpper = selectedRadioButtonUpper.getText();

            RadioButton selectedRadioButtonLower = (RadioButton) toogleLowerXAxis.getSelectedToggle();
            String toogleGroupValueLower = selectedRadioButtonLower.getText();

            AppMain.graphService.createGraphRun(toogleGroupValueUpper, toogleGroupValueLower);
        } catch (Exception e) {
            AppMain.notificationService.createNotification("Error occured during run", NotificationType.ERROR);
        }
    }

    public void loadGraph(MouseEvent event) {
        // TODO: load data from file, parse them, and add them to graph series

        try {
            AppMain.graphService.LoadGraph();
        } catch (Exception e) {
            AppMain.notificationService.createNotification("Error occured during loading graph", NotificationType.ERROR);
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
            AppMain.notificationService.createNotification("Calibration window could not be open! Please, restart the app.", NotificationType.ERROR);
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
        } else if (!AppMain.measurement.getState().equals(MeasurementState.SAVED)){
            AppMain.notificationService.createNotification("There is some data that has not been saved yet, do you want to quit anyway?", NotificationType.WARNING);
        }
    }

    public void showHelpWindow(MouseEvent event) {
        AppMain.helpService.openHelp();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: read config here
        EnvironmentParameters parameters;
        try {
            parameters = AppMain.fileService.loadConfig();
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

        LocalDate localDate = LocalDate.now();
        String dir = System.getProperty("user.dir");
        String path = dir + "\\" + localDate.getYear() + "\\" + localDate.getMonth() + "\\" + localDate.getDayOfMonth() + "\\";
        AppMain.fileService.setAutoSavingDir(path);

        savingDirMenu.setText(path);

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
            String newAutoSavingDir = dir.getAbsolutePath() + "\\" + localDate.getYear() + "\\" + localDate.getMonth() + "\\" + localDate.getDayOfMonth() + "\\";
            AppMain.fileService.setAutoSavingDir(newAutoSavingDir);
            savingDirMenu.setText(newAutoSavingDir);
//            System.out.println(dir.getAbsolutePath());
        }
    }
}
