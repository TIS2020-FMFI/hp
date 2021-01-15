package com.app.screen.controller;

import com.app.service.AppMain;
import com.app.service.file.parameters.*;
import com.app.service.graph.GraphService;
import com.app.service.graph.GraphState;
import com.app.service.graph.GraphType;
import com.app.service.notification.NotificationType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    GraphService gs;

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
        AppMain.environmentParameters.setComment(commentInput.getText());
    }

    private void toggleDisabling() {
        boolean isConnected = AppMain.communicationService != null && AppMain.communicationService.isConnected();
        boolean isUpperEmpty = gs.getGraph(GraphType.UPPER) == null || gs.getGraph(GraphType.UPPER).getState().equals(GraphState.EMPTY);
        boolean isLowerEmpty = gs.getGraph(GraphType.LOWER) == null || gs.getGraph(GraphType.LOWER).getState().equals(GraphState.EMPTY);
        boolean isUpperRunning = gs.getRunningGraph() != null && gs.getRunningGraph().getType().equals(GraphType.UPPER);
        boolean isLowerRunning = gs.getRunningGraph() != null && gs.getRunningGraph().getType().equals(GraphType.LOWER);

        upperGraphRun.setDisable(isLowerRunning || !isConnected);
        upperGraphSave.setDisable(isUpperRunning || isUpperEmpty);
        upperGraphLoad.setDisable(isUpperRunning);
        upperGraphExport.setDisable(isUpperRunning || isUpperEmpty);

        lowerGraphRun.setDisable(isUpperRunning || !isConnected);
        lowerGraphSave.setDisable(isLowerRunning || isLowerEmpty);
        lowerGraphLoad.setDisable(isLowerRunning);
        lowerGraphExport.setDisable(isLowerRunning || isLowerEmpty);
    }

    public void point() throws IOException, InterruptedException {
        EnvironmentParameters newParameters = AppMain.environmentParameters;
        if (newParameters.getDisplayYY().getX() == MeasuredQuantity.VOLTAGE)
            AppMain.communicationService.runMeasurement(MeasuredQuantity.VOLTAGE);
        else
            AppMain.communicationService.runMeasurement(MeasuredQuantity.FREQUENCY);
        //TODO: Do this, call machine to step one measruement
    }

    public void runUpperGraph(MouseEvent event) {
        if (gs.isRunningGraph() && gs.getGraph(GraphType.UPPER).getState().equals(GraphState.RUNNING)) {
            gs.abortGraph(GraphType.UPPER);
            toggleDisabling();
            upperGraphRun.setText("Run");
        } else if (!gs.isRunningGraph()) {
            runMeasurement(GraphType.UPPER, (Button) event.getSource());
        }
    }

    public void runLowerGraph(MouseEvent event) {
        if (gs.isRunningGraph() && gs.getGraph(GraphType.LOWER).getState().equals(GraphState.RUNNING)) {
            gs.abortGraph(GraphType.LOWER);
            toggleDisabling();
            lowerGraphRun.setText("Run");
        } else if (!gs.isRunningGraph()) {
            runMeasurement(GraphType.LOWER, (Button) event.getSource());
        }
    }

    private void runMeasurement(GraphType graphType, Button triggerButton) {
        EnvironmentParameters newParameters = AppMain.environmentParameters;

        // TODO: doplnit ABS

        try {

            DisplayYY newDisplayYY = newParameters.getDisplayYY();
            RadioButton selectedDisplayA = (RadioButton) displayA.getSelectedToggle();
            RadioButton selectedDisplayB = (RadioButton) displayB.getSelectedToggle();

            if (selectedDisplayA == null || selectedDisplayB == null) {
                throw new NullPointerException("Values not properly set! Display A or B not set.. or both :)");
            }
            newDisplayYY.setA(selectedDisplayA.getText());
            newDisplayYY.setA(selectedDisplayB.getText());

            RadioButton selectedDisplayX = (RadioButton) (graphType.equals(GraphType.UPPER) ? toogleUpperXAxis.getSelectedToggle():toogleLowerXAxis.getSelectedToggle());
            newDisplayYY.setX( selectedDisplayX.getText().equals("Frequency") ? MeasuredQuantity.FREQUENCY:MeasuredQuantity.VOLTAGE);

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
            gs.run(graphType);

            if (gs.isRunningGraph() && otherAutoSweep.getValue().equals("OFF")) {
                if (gs.getRunningGraph().getType().equals(GraphType.UPPER)) {
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
                } else {
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

//            AppMain.communicationService.runMeasurement(newParameters.getDisplayYY().getX()); // TODO: uncomment when testing with machine
            toggleDisabling();
            triggerButton.setText("Abort");
        } catch (NullPointerException e) {
            if (graphType.equals(GraphType.UPPER)) {
                gs.upperGraph.setState(GraphState.EMPTY);
            } else {
                gs.lowerGraph.setState(GraphState.EMPTY);
            }
            triggerButton.setText("Run");
            AppMain.notificationService.createNotification("Error occurred -> " + e.getMessage(), NotificationType.ERROR);
        }

    }

    public void loadUpperGraph(MouseEvent event) {
        gs.loadGraph(GraphType.UPPER);
    }

    public void loadLowerGraph(MouseEvent event) {
        gs.loadGraph(GraphType.LOWER);
    }

    public void toggleAutoSave(MouseEvent event) {
        // TODO: change autoSaveMode in global props global props
        if (AppMain.fileService.isAutoSave()) {
            autoSaveMenu.setText("Auto save: OFF");
            AppMain.fileService.setAutoSave(false);
        } else {
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
//        if (AppMain.measurement.getState().equals(MeasurementState.SAVED) ||
//                AppMain.measurement.getState().equals(MeasurementState.ABORTED) ||
//                AppMain.measurement.getState().equals(MeasurementState.WAITING)) {
//            try {
//                if (!AppMain.measurement.getState().equals(MeasurementState.WAITING)) AppMain.fileService.saveConfig();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            Platform.exit();
//            System.exit(0);
//        } else if (!AppMain.measurement.getState().equals(MeasurementState.SAVED)) {
//            AppMain.notificationService.createNotification("There is some data that has not been saved yet, do you want to quit anyway?", NotificationType.WARNING);
//        }
    }

    public void showHelpWindow(MouseEvent event) {
        AppMain.helpService.openHelp();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gs = AppMain.graphService;
        toggleDisabling();

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
        if (AppMain.environmentParameters.getOther().getSweepType() == SweepType.LINEAR) {
            otherSweepType.getSelectionModel().select(0);
        } else otherSweepType.getSelectionModel().select(1);

        otherHighSpeed.getItems().addAll("ON", "OFF");
        if (AppMain.environmentParameters.getOther().isHighSpeed()) {
            otherHighSpeed.getSelectionModel().select(0);
        } else otherHighSpeed.getSelectionModel().select(1);

        otherAutoSweep.getItems().addAll("ON", "OFF");
        if (AppMain.environmentParameters.getOther().isAutoSweep()) {
            otherAutoSweep.getSelectionModel().select(0);
        } else otherAutoSweep.getSelectionModel().select(1);

        LocalDate localDate = LocalDate.now();
        String dir = System.getProperty("user.dir");
        String path = dir + "\\" + localDate.getYear() + "\\" + localDate.getMonth() + "\\" + localDate.getDayOfMonth() + "\\";
        AppMain.fileService.setAutoSavingDir(path);

        savingDirMenu.setText(path);

    }

    public void runConnection(MouseEvent mouseEvent) throws Exception {
        if (AppMain.communicationService.connect()) {
            gpibMenu.setText("GPIB connection: ACTIVE");
        } else {
            gpibMenu.setText("GPIB connection: INACTIVE");
        }
        toggleDisabling();
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