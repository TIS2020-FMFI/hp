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
import java.io.IOException;
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
    ToolBar upperToolbar;
    @FXML
    ToolBar lowerToolbar;


    @FXML
    VBox VBox1;

    public void updateComment(MouseEvent event) {
        AppMain.comment = commentInput.getText();
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

    public void point() throws IOException, InterruptedException {
        EnvironmentParameters newParameters = AppMain.environmentParameters;
        if (newParameters.getDisplayYY().getX() == MeasuredQuantity.VOLTAGE)
            AppMain.communicationService.runMeasurement(MeasuredQuantity.VOLTAGE);
        else
            AppMain.communicationService.runMeasurement(MeasuredQuantity.FREQUENCY);
        //TODO: Do this, call machine to step one measruement
    }

    public void runMeasurement(MouseEvent event) throws IOException, InterruptedException {
        // TODO: run measurement and graph
        //ABORT??
        if (AppMain.graphService.isRunning()) {
            return;
        }

        EnvironmentParameters newParameters = AppMain.environmentParameters;

        //doplnit ABS

        DisplayYY newDisplayYY = newParameters.getDisplayYY();
        RadioButton selectedDisplayA = (RadioButton) displayA.getSelectedToggle();
        if(selectedDisplayA != null) newDisplayYY.setA(selectedDisplayA.getText());
        RadioButton selectedDisplayB = (RadioButton) displayB.getSelectedToggle();
        if(selectedDisplayB != null) newDisplayYY.setA(selectedDisplayB.getText());

        if(event.getSource() == upperGraphRun){
            RadioButton selectedDisplayX = (RadioButton) toogleUpperXAxis.getSelectedToggle();
            if(selectedDisplayX.getText().equals("Frequency")){
                newDisplayYY.setX(MeasuredQuantity.FREQUENCY);
            }else{
                newDisplayYY.setX(MeasuredQuantity.VOLTAGE);
            }
        }else{
            RadioButton selectedDisplayX = (RadioButton) toogleLowerXAxis.getSelectedToggle();
            if(selectedDisplayX.getText().equals("Frequency")){
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

        newParameters.checkAll();
        AppMain.environmentParameters = newParameters;
//        AppMain.measurement.setParameters(newParameters);
//        AppMain.measurement.setState(MeasurementState.STARTED);
//        AppMain.fileService.setMeasurement(AppMain.measurement);

        try {

            String YaxisQuantity1;
            String YaxisQuantity2;
            try {
                YaxisQuantity1 = selectedDisplayA.getText();
                YaxisQuantity2 = selectedDisplayB.getText();
            } catch (Exception e) {
                AppMain.notificationService.createNotification("Running parameters not set", NotificationType.ERROR);
                throw e;
            }


            try {
                if (AppMain.graphService.isUpperRunning()) {
                    RadioButton selectedRadioButtonUpper = (RadioButton) toogleUpperXAxis.getSelectedToggle();
                    String toogleXaxis = selectedRadioButtonUpper.getText();
                    AppMain.graphService.createGraphRun(toogleXaxis, YaxisQuantity1, YaxisQuantity2);

                }
                if (AppMain.graphService.isLowerRunning()) {
                    RadioButton selectedRadioButtonLower = (RadioButton) toogleLowerXAxis.getSelectedToggle();
                    String toogleXaxis = selectedRadioButtonLower.getText();
                    AppMain.graphService.createGraphRun(toogleXaxis, YaxisQuantity1, YaxisQuantity2); //TODO: may drop because these values may not be set ?!
                }

            } catch (Exception e) {
                throw e;
            }

            if (AppMain.graphService.isRunning() & otherAutoSweep.getValue().equals("OFF")) {
                if (AppMain.graphService.isUpperRunning()) {
                    Button button = new Button("Point");
                    button.setId("upperPoint");
                    button.setOnKeyPressed(e -> {
                        try {
                            point();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    });
                    upperToolbar.getItems().add(button);
                    //TODO: tu metoda, ktora prida measurement data, ktore v grafe uz on checkuje, je treba aj cez abort znicit ten button potom
                }
                if (AppMain.graphService.isLowerRunning()) {
                    Button button = new Button("Point");
                    button.setId("lowerPoint");
                    button.setOnKeyPressed(e -> {
                        try {
                            point();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    });
                    lowerToolbar.getItems().add(button);

                    //TODO: tu metoda, ktora prida measurement data, ktore v grafe uz on checkuje, je treba aj cez abort znicit ten button potom
                }

            } else {
                AppMain.notificationService.createNotification("Auto sweep is on", NotificationType.ANNOUNCEMENT);
            }

        } catch (Exception e) {
            AppMain.notificationService.createNotification("Error occured during run", NotificationType.ERROR);
        }
        if (newParameters.getDisplayYY().getX() == MeasuredQuantity.VOLTAGE)
            AppMain.communicationService.runMeasurement(MeasuredQuantity.VOLTAGE);
        else
            AppMain.communicationService.runMeasurement(MeasuredQuantity.FREQUENCY);

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
        //treba prerobit kvoli zmenam v measurement
/*        if(AppMain.measurement.getState().equals(MeasurementState.SAVED) ||
                AppMain.measurement.getState().equals(MeasurementState.ABORTED) ||
                AppMain.measurement.getState().equals(MeasurementState.WAITING)) {
            try {
                if(!AppMain.measurement.getState().equals(MeasurementState.WAITING)) AppMain.fileService.saveConfig();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Platform.exit();
            System.exit(0);
        } else if (!AppMain.measurement.getState().equals(MeasurementState.SAVED)){
            AppMain.notificationService.createNotification("There is some data that has not been saved yet, do you want to quit anyway?", NotificationType.WARNING);
        }*/
    }

    public void showHelpWindow(MouseEvent event) {
        AppMain.helpService.openHelp();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            frequencyStart.setText("" + AppMain.environmentParameters.getFrequencySweep().getStart());
            frequencyStop.setText("" + AppMain.environmentParameters.getFrequencySweep().getStop());
            frequencySpot.setText("" + AppMain.environmentParameters.getFrequencySweep().getSpot());
            frequencyStep.setText("" + AppMain.environmentParameters.getFrequencySweep().getStep());

            voltageStart.setText("" + AppMain.environmentParameters.getVoltageSweep().getStart());
            voltageStop.setText("" + AppMain.environmentParameters.getVoltageSweep().getStop());
            voltageSpot.setText("" + AppMain.environmentParameters.getVoltageSweep().getSpot());
            voltageStep.setText("" + AppMain.environmentParameters.getVoltageSweep().getStep());

            otherCapacitance.setText("" + AppMain.environmentParameters.getOther().getCapacitance());
            otherElectricalLength.setText("" + AppMain.environmentParameters.getOther().getElectricalLength());

            // ----- initialize all dropbox -> coz its not possible to do so in sceneBuilder yet
            otherSweepType.getItems().addAll("LINEAR", "LOG");
            if(AppMain.environmentParameters.getOther().getSweepType() == SweepType.LINEAR){
                otherSweepType.getSelectionModel().select(0);
            }else otherSweepType.getSelectionModel().select(1);

            otherHighSpeed.getItems().addAll("ON", "OFF" );
            if(AppMain.environmentParameters.getOther().isHighSpeed()){
                otherHighSpeed.getSelectionModel().select(0);
            }else otherHighSpeed.getSelectionModel().select(1);

            otherAutoSweep.getItems().addAll("ON", "OFF");
            if(AppMain.environmentParameters.getOther().isAutoSweep()){
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