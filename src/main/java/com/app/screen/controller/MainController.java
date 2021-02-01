package com.app.screen.controller;

import com.app.service.AppMain;
import com.app.service.file.parameters.*;
import com.app.service.graph.GraphService;
import com.app.service.graph.GraphState;
import com.app.service.graph.GraphType;
import com.app.service.measurement.DisplayAOption;
import com.app.service.measurement.DisplayBOption;
import com.app.service.measurement.MeasurementState;
import com.app.service.notification.NotificationType;
import com.app.service.utils.Utils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class MainController implements Initializable {

    GraphService gs;
    EnvironmentParameters ep;

    Timer graphWatcher;
    GraphState oldGraphState;

    Timer connectionWatcher = new Timer();
    Boolean oldConnectionState;

    @FXML
    VBox VBox1;

    @FXML
    VBox mainContainer;
    @FXML
    VBox notificationContainer;

    @FXML
    AnchorPane upperPane;
    @FXML
    ToolBar upperToolbar;
    @FXML
    Button upperGraphRun;
    @FXML
    Button upperGraphLoad;
    @FXML
    Button upperGraphExport;
    @FXML
    Button upperGraphSave;
    @FXML
    Button upperPointNext;
    @FXML
    ToggleGroup toggleUpperXAxis;

    @FXML
    AnchorPane lowerPane;
    @FXML
    ToolBar lowerToolbar;
    @FXML
    Button lowerGraphRun;
    @FXML
    Button lowerGraphLoad;
    @FXML
    Button lowerGraphExport;
    @FXML
    Button lowerGraphSave;
    @FXML
    Button lowerPointNext;
    @FXML
    ToggleGroup toggleLowerXAxis;

    @FXML
    TabPane parametersTabPane;
    @FXML
    Tab upperGraphTab;
    @FXML
    Tab lowerGraphTab;

    // upper graph params
    @FXML
    ToggleGroup displayAUpper;

    @FXML
    CheckBox displayAUpperABS;

    @FXML
    ToggleGroup displayBUpper;

    @FXML
    TextField frequencyStartUpper;
    @FXML
    TextField frequencyStopUpper;
    @FXML
    TextField frequencyStepUpper;
    @FXML
    TextField frequencySpotUpper;

    @FXML
    TextField voltageStartUpper;
    @FXML
    TextField voltageStopUpper;
    @FXML
    TextField voltageStepUpper;
    @FXML
    TextField voltageSpotUpper;

    @FXML
    TextField otherCapacitanceUpper;
    @FXML
    TextField otherElectricalLengthUpper;
    @FXML
    ChoiceBox<String> otherSweepTypeUpper;
    @FXML
    ChoiceBox<String> otherHighSpeedUpper;
    @FXML
    ChoiceBox<String> otherAutoSweepUpper;

    @FXML
    TextArea commentInputUpper;

    // lower graph params
    @FXML
    ToggleGroup displayALower;

    @FXML
    ToggleGroup displayBLower;

    @FXML
    CheckBox displayALowerABS;

    @FXML
    TextField frequencyStartLower;
    @FXML
    TextField frequencyStopLower;
    @FXML
    TextField frequencyStepLower;
    @FXML
    TextField frequencySpotLower;

    @FXML
    TextField voltageStartLower;
    @FXML
    TextField voltageStopLower;
    @FXML
    TextField voltageStepLower;
    @FXML
    TextField voltageSpotLower;

    @FXML
    TextField otherCapacitanceLower;
    @FXML
    TextField otherElectricalLengthLower;
    @FXML
    ChoiceBox<String> otherSweepTypeLower;
    @FXML
    ChoiceBox<String> otherHighSpeedLower;
    @FXML
    ChoiceBox<String> otherAutoSweepLower;

    @FXML
    TextArea commentInputLower;

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
    TextArea currentValueDisplay;


    public void updateComment(MouseEvent event) {
        ep.getActive().setComment(ep.getActiveGraphType().equals(GraphType.UPPER) ? commentInputUpper.getText() : commentInputLower.getText());
    }

    private void toggleDisabling() {
        boolean isConnected = AppMain.communicationService != null && AppMain.communicationService.isConnected();
        boolean isUpperEmpty = gs.getGraphByType(GraphType.UPPER) == null || gs.getGraphByType(GraphType.UPPER).getState().equals(GraphState.EMPTY);
        boolean isLowerEmpty = gs.getGraphByType(GraphType.LOWER) == null || gs.getGraphByType(GraphType.LOWER).getState().equals(GraphState.EMPTY);
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

    public void runUpperGraph(MouseEvent event) {
        GraphType type = GraphType.UPPER;
        if (gs.isRunningGraph() && gs.getGraphByType(type).getState().equals(GraphState.RUNNING)) {
            gs.abortGraph(type);
            toggleDisabling();
            upperGraphRun.setText("Run");
            upperToolbar.getItems().remove(upperPointNext);
//            upperToolbar.getItems().remove(currentValueDisplay);
        } else if (!gs.isRunningGraph()) {
            if (gs.getGraphByType(type).getMeasurement() != null && gs.getGraphByType(type).getMeasurement().canLooseData()) {
                AppMain.abortDataDialog.openDialog(type);
            } else {
                parametersTabPane.getSelectionModel().select(upperGraphTab);
                runMeasurement(GraphType.UPPER, (Button) event.getSource());
            }
        }
    }

    public void runLowerGraph(MouseEvent event) {
        GraphType type = GraphType.LOWER;
        if (gs.isRunningGraph() && gs.getGraphByType(type).getState().equals(GraphState.RUNNING)) {
            gs.abortGraph(type);
            toggleDisabling();
            lowerGraphRun.setText("Run");
            lowerToolbar.getItems().remove(lowerPointNext);
//            lowerToolbar.getItems().remove(currentValueDisplay);
        } else if (!gs.isRunningGraph()) {
            if (gs.getGraphByType(type).getMeasurement() != null && gs.getGraphByType(type).getMeasurement().canLooseData()) {
                AppMain.abortDataDialog.openDialog(type);
            } else {
                parametersTabPane.getSelectionModel().select(lowerGraphTab);
                runMeasurement(GraphType.LOWER, (Button) event.getSource());
            }
        }
    }

    /**
     * Sends to CommunicationService notification that machine should do one next step of measurement.
     * If IOException occurs during this, notifies that error occurred.
     */
    public void runNextStep(TextArea currentValueDisplay) {
        try {
            AppMain.communicationService.nextStep(gs.getRunningGraph().getMeasurement());
//            currentValueDisplay.setText(String.valueOf(getRunningGraph().getMeasurement().getData().get(getRunningGraph().getMeasurement().getData().size()-1)));
        } catch (IOException | InterruptedException e) {
            AppMain.notificationService.createNotification("Error occurred while running next step -> " + e.getMessage(), NotificationType.ERROR);
        } catch (NullPointerException e) {
            lowerToolbar.getItems().remove(lowerPointNext);
            upperToolbar.getItems().remove(upperPointNext);
        }
    }

    private void runMeasurement(GraphType graphType, Button triggerButton) {
        ep.setActiveGraphType(graphType);
        createGraphWatcher(graphType);

        try {
            DisplayYY newDisplayYY = ep.getActive().getDisplayYY();
            RadioButton selectedDisplayA = (RadioButton) (graphType.equals(GraphType.UPPER) ? displayAUpper : displayALower).getSelectedToggle();
            RadioButton selectedDisplayB = (RadioButton) (graphType.equals(GraphType.UPPER) ? displayBUpper : displayBLower).getSelectedToggle();

            if (selectedDisplayA == null || selectedDisplayB == null) {
                throw new NullPointerException("Values not properly set! Display A or B not set.. or both :)");
            }

            String displayA = selectedDisplayA.getText();
            if ((graphType.equals(GraphType.LOWER) && displayALowerABS.isSelected() && DisplayAOption.isAbsOption(DisplayAOption.valueOf(displayA))) ||
                    (graphType.equals(GraphType.UPPER) && displayAUpperABS.isSelected() && DisplayAOption.isAbsOption(DisplayAOption.valueOf(displayA)))) {
                displayA = DisplayAOption.getAbsOption(DisplayAOption.valueOf(displayA));
            }

            newDisplayYY.setA(displayA);
            newDisplayYY.setB(selectedDisplayB.getText());

            RadioButton selectedDisplayX = (RadioButton) ((graphType.equals(GraphType.UPPER) ? toggleUpperXAxis : toggleLowerXAxis).getSelectedToggle());
            newDisplayYY.setX(selectedDisplayX.getText().equals("Frequency") ? MeasuredQuantity.FREQUENCY : MeasuredQuantity.VOLTAGE);

            ep.getActive().setDisplayYY(newDisplayYY);

            FrequencySweep newFrequencySweep = ep.getActive().getFrequencySweep();
            newFrequencySweep.setStart(Double.parseDouble((graphType.equals(GraphType.UPPER) ? frequencyStartUpper : frequencyStartLower).getText()));
            newFrequencySweep.setStop(Double.parseDouble((graphType.equals(GraphType.UPPER) ? frequencyStopUpper : frequencyStopLower).getText()));
            newFrequencySweep.setStep(Double.parseDouble((graphType.equals(GraphType.UPPER) ? frequencyStepUpper : frequencyStepLower).getText()));
            newFrequencySweep.setSpot(Double.parseDouble((graphType.equals(GraphType.UPPER) ? frequencySpotUpper : frequencySpotLower).getText()));
            ep.getActive().setFrequencySweep(newFrequencySweep);

            VoltageSweep newVoltageSweep = new VoltageSweep();
            newVoltageSweep.setStart(Double.parseDouble((graphType.equals(GraphType.UPPER) ? voltageStartUpper : voltageStartLower).getText()));
            newVoltageSweep.setStop(Double.parseDouble((graphType.equals(GraphType.UPPER) ? voltageStopUpper : voltageStopLower).getText()));
            newVoltageSweep.setStep(Double.parseDouble((graphType.equals(GraphType.UPPER) ? voltageStepUpper : voltageStepLower).getText()));
            newVoltageSweep.setSpot(Double.parseDouble((graphType.equals(GraphType.UPPER) ? voltageSpotUpper : voltageSpotUpper).getText()));
            ep.getActive().setVoltageSweep(newVoltageSweep);

            Other newOther = new Other();
            newOther.setCapacitance(Double.parseDouble((graphType.equals(GraphType.UPPER) ? otherCapacitanceUpper : otherCapacitanceLower).getText()));
            newOther.setElectricalLength(Double.parseDouble((graphType.equals(GraphType.UPPER) ? otherElectricalLengthUpper : otherElectricalLengthLower).getText()));
            newOther.setAutoSweep((graphType.equals(GraphType.UPPER) ? otherAutoSweepUpper : otherAutoSweepLower).getValue().equals("ON"));
            newOther.setHighSpeed((graphType.equals(GraphType.UPPER) ? otherHighSpeedUpper : otherHighSpeedLower).getValue().equals("ON"));
            newOther.setSweepType((graphType.equals(GraphType.UPPER) ? otherSweepTypeUpper : otherSweepTypeLower).getValue().equals("LINEAR") ? SweepType.LINEAR : SweepType.LOG);
            ep.getActive().setOther(newOther);

            ep.getActive().checkAll();

            if ((graphType.equals(GraphType.UPPER) ? otherAutoSweepUpper : otherAutoSweepLower).getValue().equals("OFF")) {
                AppMain.notificationService.createNotification("Auto sweep is off", NotificationType.ANNOUNCEMENT);
                Button pointButton = new Button("Next");
//                currentValueDisplay = new TextArea();
//                currentValueDisplay.setId("currentValueDisplay");
                pointButton.setOnMouseReleased(e -> runNextStep(currentValueDisplay));
                if (graphType.equals(GraphType.UPPER)) {
                    upperPointNext = pointButton;
                    upperPointNext.setId("upperPointNext");
                    upperToolbar.getItems().add(upperPointNext);
//                    upperToolbar.getItems().add(currentValueDisplay);
                } else {
                    lowerPointNext = pointButton;
                    lowerPointNext.setId("lowerPointNext");
                    lowerToolbar.getItems().add(lowerPointNext);
//                    lowerToolbar.getItems().add(currentValueDisplay);

                }
            } else {
                AppMain.notificationService.createNotification("Auto sweep is on", NotificationType.ANNOUNCEMENT);
            }
            gs.run(graphType);
            toggleDisabling();
            triggerButton.setText("Abort");
        } catch (NullPointerException e) {
            if (graphType.equals(GraphType.UPPER)) {
                gs.upperGraph.setState(GraphState.EMPTY);
                upperToolbar.getItems().remove(upperPointNext);
            } else {
                gs.lowerGraph.setState(GraphState.EMPTY);
                lowerToolbar.getItems().remove(lowerPointNext);
            }
            triggerButton.setText("Run");
            AppMain.notificationService.createNotification("Error occurred when starting measurement -> " + e.getMessage(), NotificationType.ERROR);
        }
    }

    public void loadUpperGraph(MouseEvent event) {
        GraphType type = GraphType.UPPER;
        parametersTabPane.getSelectionModel().select(upperGraphTab);
        if (gs.getGraphByType(type).getMeasurement() != null && gs.getGraphByType(type).getMeasurement().canLooseData()) {
            AppMain.abortDataDialog.openDialog(type);
            return;
        }
        gs.loadGraph(type);
        if (gs.upperGraph.getState().equals(GraphState.LOADED)) {
            ep.setUpperGraphParameters(gs.upperGraph.getMeasurement().getParameters());
            initializeUpper();
        }
    }

    public void loadLowerGraph(MouseEvent event) {
        GraphType type = GraphType.LOWER;
        parametersTabPane.getSelectionModel().select(lowerGraphTab);
        if (gs.getGraphByType(type).getMeasurement() != null && gs.getGraphByType(type).getMeasurement().canLooseData()) {
            AppMain.abortDataDialog.openDialog(type);
            return;
        }
        gs.loadGraph(type);
        if (gs.lowerGraph.getState().equals(GraphState.LOADED)) {
            ep.setLowerGraphParameters(gs.lowerGraph.getMeasurement().getParameters());
            initializeLower();
        }
    }

    public void toggleAutoSave(MouseEvent event) {
        AppMain.fileService.setAutoSave(!AppMain.fileService.isAutoSave());
        autoSaveMenu.setText("Auto save: " + (AppMain.fileService.isAutoSave() ? "ON" : "OFF"));
    }

    public void resetInstrument(MouseEvent event) {
        // TODO: are we still doing it ???
    }

    public void triggerCalibration(MouseEvent event) {
        if (AppMain.communicationService.isConnected()) {
            AppMain.calibrationService.openCalibration();
        } else {
            AppMain.notificationService.createNotification("Machine not connected!", NotificationType.ANNOUNCEMENT);
        }
    }

    public void quitApp(MouseEvent event) {
        if (gs.isRunningGraph()) {
            AppMain.notificationService.createNotification("There is a measurement in process, either wait or abort it.", NotificationType.WARNING);
        } else if (AppMain.fileService.isAutoSave()) {
            if (AppMain.fileService.saveConfig()) {
                Utils.closeApp();
            }
        } else if ((gs.getGraphByType(GraphType.UPPER).getMeasurement() == null || !gs.getGraphByType(GraphType.UPPER).getMeasurement().canLooseData())
                && (gs.getGraphByType(GraphType.LOWER).getMeasurement() == null || !gs.getGraphByType(GraphType.LOWER).getMeasurement().canLooseData())) {
                Utils.closeApp();
        } else {
            AppMain.dataNotSavedDialog.openDialog();
        }
    }

    public void showHelpWindow(MouseEvent event) {
        AppMain.helpService.openHelp();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AppMain.ps.setOnCloseRequest(request -> quitApp(null));


        gs = AppMain.graphService;
        ep = AppMain.environmentParameters;

        toggleDisabling();

        initializeUpper();
        initializeLower();

        savingDirMenu.setText(AppMain.fileService.getAutoSavingDir());
        createConstraintListener();

        connectionWatcher.schedule(new TimerTask() {
            @Override
            public void run() {
                if (AppMain.communicationService != null && (oldConnectionState == null || oldConnectionState != AppMain.communicationService.isConnected())) {
                    oldConnectionState = AppMain.communicationService.isConnected();
                    updateGpibMenu(AppMain.communicationService.isConnected());
                    toggleDisabling();
                }
            }
        }, 100, 100);
    }

    private void createGraphWatcher(GraphType type) {
        graphWatcher = new Timer();
        graphWatcher.schedule(new TimerTask() {
            @Override
            public void run() {
                if (oldGraphState != gs.getStateByType(type)) {
                    toggleDisabling();
                    oldGraphState = gs.getStateByType(type);
                    if (GraphState.isStatic(gs.getStateByType(type))) {
                        Platform.runLater(() -> {
                            (type.equals(GraphType.UPPER) ? upperGraphRun : lowerGraphRun).setText("Run");
                            if (type.equals(GraphType.UPPER)) {
                                upperToolbar.getItems().remove(upperPointNext);
//                                upperToolbar.getItems().remove(currentValueDisplay);
                            } else {
                                lowerToolbar.getItems().remove(lowerPointNext);
//                                lowerToolbar.getItems().remove(currentValueDisplay);
                            }
                        });
                    }
                    if (gs.getGraphByType(type).getMeasurement() != null && gs.getGraphByType(type).getMeasurement().getState().equals(MeasurementState.ABORTED)) {
                        cancel();
                    }
                }
            }
        }, 100, 100);
    }

    private void initializeUpper() {
        System.out.println("upper -> " + ep.getByType(GraphType.UPPER).getDisplayYY().getA());
        String displayA = ep.getByType(GraphType.UPPER).getDisplayYY().getA();

        if (DisplayAOption.isAbsOption(displayA)) {
            displayAUpperABS.setSelected(true);
            displayA = DisplayAOption.getOptionFromAbs(displayA).toString();
        }

        String finalDisplayA = displayA;
        displayAUpper.getToggles().forEach(item -> {
            ToggleButton btn = (ToggleButton) item;
            if (btn.getText().equals(finalDisplayA)) {
                item.setSelected(true);
            }
        });
        displayBUpper.getToggles().forEach(item -> {
            ToggleButton btn = (ToggleButton) item;
            if (btn.getText().equals(ep.getByType(GraphType.UPPER).getDisplayYY().getB())) {
                item.setSelected(true);
            }
        });

        frequencyStartUpper.setText("" + ep.getByType(GraphType.UPPER).getFrequencySweep().getStart());
        frequencyStopUpper.setText("" + ep.getByType(GraphType.UPPER).getFrequencySweep().getStop());
        frequencySpotUpper.setText("" + ep.getByType(GraphType.UPPER).getFrequencySweep().getSpot());
        frequencyStepUpper.setText("" + ep.getByType(GraphType.UPPER).getFrequencySweep().getStep());

        voltageStartUpper.setText("" + ep.getByType(GraphType.UPPER).getVoltageSweep().getStart());
        voltageStopUpper.setText("" + ep.getByType(GraphType.UPPER).getVoltageSweep().getStop());
        voltageSpotUpper.setText("" + ep.getByType(GraphType.UPPER).getVoltageSweep().getSpot());
        voltageStepUpper.setText("" + ep.getByType(GraphType.UPPER).getVoltageSweep().getStep());

        otherCapacitanceUpper.setText("" + ep.getByType(GraphType.UPPER).getOther().getCapacitance());
        otherElectricalLengthUpper.setText("" + ep.getByType(GraphType.UPPER).getOther().getElectricalLength());

        otherSweepTypeUpper.getItems().addAll("LINEAR", "LOG");
        otherSweepTypeUpper.getSelectionModel().select(ep.getByType(GraphType.UPPER).getOther().getSweepType().equals(SweepType.LINEAR) ? 0 : 1);

        otherHighSpeedUpper.getItems().addAll("ON", "OFF");
        otherHighSpeedUpper.getSelectionModel().select(ep.getByType(GraphType.UPPER).getOther().isHighSpeed() ? 0 : 1);

        otherAutoSweepUpper.getItems().addAll("ON", "OFF");
        otherAutoSweepUpper.getSelectionModel().select(ep.getByType(GraphType.UPPER).getOther().isAutoSweep() ? 0 : 1);
    }

    private void initializeLower() {
        System.out.println("lower -> " + ep.getByType(GraphType.LOWER).getDisplayYY().getA());
        String displayA = ep.getByType(GraphType.LOWER).getDisplayYY().getA();

        if (DisplayAOption.isAbsOption(displayA)) {
            displayALowerABS.setSelected(true);
            displayA = DisplayAOption.getOptionFromAbs(displayA).toString();
        }

        String finalDisplayA = displayA;
        displayALower.getToggles().forEach(item -> {
            ToggleButton btn = (ToggleButton) item;
            if (btn.getText().equals(finalDisplayA)) {
                item.setSelected(true);
            }
        });
        displayBLower.getToggles().forEach(item -> {
            ToggleButton btn = (ToggleButton) item;
            if (btn.getText().equals(ep.getByType(GraphType.LOWER).getDisplayYY().getB())) {
                item.setSelected(true);
            }
        });

        frequencyStartLower.setText("" + ep.getByType(GraphType.LOWER).getFrequencySweep().getStart());
        frequencyStopLower.setText("" + ep.getByType(GraphType.LOWER).getFrequencySweep().getStop());
        frequencySpotLower.setText("" + ep.getByType(GraphType.LOWER).getFrequencySweep().getSpot());
        frequencyStepLower.setText("" + ep.getByType(GraphType.LOWER).getFrequencySweep().getStep());

        voltageStartLower.setText("" + ep.getByType(GraphType.LOWER).getVoltageSweep().getStart());
        voltageStopLower.setText("" + ep.getByType(GraphType.LOWER).getVoltageSweep().getStop());
        voltageSpotLower.setText("" + ep.getByType(GraphType.LOWER).getVoltageSweep().getSpot());
        voltageStepLower.setText("" + ep.getByType(GraphType.LOWER).getVoltageSweep().getStep());

        otherCapacitanceLower.setText("" + ep.getByType(GraphType.LOWER).getOther().getCapacitance());
        otherElectricalLengthLower.setText("" + ep.getByType(GraphType.LOWER).getOther().getElectricalLength());
        // ----- initialize all dropbox -> coz its not possible to do so in sceneBuilder yet
        otherSweepTypeLower.getItems().addAll("LINEAR", "LOG");
        otherSweepTypeLower.getSelectionModel().select(ep.getByType(GraphType.LOWER).getOther().getSweepType().equals(SweepType.LINEAR) ? 0 : 1);

        otherHighSpeedLower.getItems().addAll("ON", "OFF");
        otherHighSpeedLower.getSelectionModel().select(ep.getByType(GraphType.LOWER).getOther().isHighSpeed() ? 0 : 1);

        otherAutoSweepLower.getItems().addAll("ON", "OFF");
        otherAutoSweepLower.getSelectionModel().select(ep.getByType(GraphType.LOWER).getOther().isAutoSweep() ? 0 : 1);
    }

    private void createConstraintListener() {
        displayAUpper.selectedToggleProperty().addListener((observableValue, oldValue, newValue) -> {
            List<DisplayBOption> bOptions = DisplayBOption.getBOptionsByA(DisplayAOption.getOptionFromString(((RadioButton) newValue).getText()));
            if (bOptions != null) {
                if (bOptions.contains(DisplayBOption.getOptionFromString(((RadioButton) displayBUpper.getSelectedToggle()).getText()))) {
                    return;
                }
                DisplayBOption first = bOptions.stream().findFirst().get();
                displayBUpper.getToggles().forEach(item -> {
                    ToggleButton btn = (ToggleButton) item;
                    if (btn.getText().contains(first.toString())) {
                        item.setSelected(true);
                    }
                });
            } else {
                AppMain.notificationService.createNotification("Display B upper values array is empty!", NotificationType.ERROR);
            }
        });
        displayBUpper.selectedToggleProperty().addListener((observableValue, oldValue, newValue) -> {
            List<DisplayAOption> aOptions = DisplayAOption.getAOptionsByB(DisplayBOption.getOptionFromString(((RadioButton) newValue).getText()));
            if (aOptions != null) {
                if (aOptions.contains(DisplayAOption.getOptionFromString(((RadioButton) displayAUpper.getSelectedToggle()).getText()))) {
                    return;
                }
                DisplayAOption first = aOptions.stream().findFirst().get();
                displayAUpper.getToggles().forEach(item -> {
                    ToggleButton btn = (ToggleButton) item;
                    if (btn.getText().contains(first.toString())) {
                        item.setSelected(true);
                    }
                });
            } else {
                AppMain.notificationService.createNotification("Display A upper values array is empty!", NotificationType.ERROR);
            }
        });
        displayALower.selectedToggleProperty().addListener((observableValue, oldValue, newValue) -> {
            List<DisplayBOption> bOptions = DisplayBOption.getBOptionsByA(DisplayAOption.getOptionFromString(((RadioButton) newValue).getText()));
            if (bOptions != null) {
                if (bOptions.contains(DisplayBOption.getOptionFromString(((RadioButton) displayBLower.getSelectedToggle()).getText()))) {
                    return;
                }
                DisplayBOption first = bOptions.stream().findFirst().get();
                displayBLower.getToggles().forEach(item -> {
                    ToggleButton btn = (ToggleButton) item;
                    if (btn.getText().contains(first.toString())) {
                        item.setSelected(true);
                    }
                });
            } else {
                AppMain.notificationService.createNotification("Display B upper values array is empty!", NotificationType.ERROR);
            }
        });
        displayBLower.selectedToggleProperty().addListener((observableValue, oldValue, newValue) -> {
            List<DisplayAOption> aOptions = DisplayAOption.getAOptionsByB(DisplayBOption.getOptionFromString(((RadioButton) newValue).getText()));
            if (aOptions != null) {
                if (aOptions.contains(DisplayAOption.getOptionFromString(((RadioButton) displayALower.getSelectedToggle()).getText()))) {
                    return;
                }
                DisplayAOption first = aOptions.stream().findFirst().get();
                displayALower.getToggles().forEach(item -> {
                    ToggleButton btn = (ToggleButton) item;
                    if (btn.getText().contains(first.toString())) {
                        item.setSelected(true);
                    }
                });
            } else {
                AppMain.notificationService.createNotification("Display B lower values array is empty!", NotificationType.ERROR);
            }
        });
    }

    public void runConnection(MouseEvent mouseEvent) {
        gpibMenu.setText("GPIB connection: " + (AppMain.communicationService.connect() ? "ACTIVE" : "INACTIVE"));
        toggleDisabling();
    }

    public void setAutoSaveDirectory(MouseEvent mouseEvent) {
        String newDirPath = AppMain.fileService.setNewAutoSaveDirectory();
        if (newDirPath.length() >= 50) {
            newDirPath = newDirPath.substring(0,3)+ "..." + newDirPath.substring(newDirPath.length() - 50 + newDirPath.substring(newDirPath.length() - 50).indexOf("/"));
        }
        savingDirMenu.setText(newDirPath);
    }

    public void exportUpperGraph(MouseEvent mouseEvent) {
        if(gs.upperGraph.getMeasurement() != null){
            if(AppMain.fileService.exportAs(gs.upperGraph.getMeasurement())){
                AppMain.notificationService.createNotification("The measurement in the upper graph was exported.", NotificationType.SUCCESS);
            }else{
                AppMain.notificationService.createNotification("The measurement in the upper graph failed to export.", NotificationType.ERROR);
            }
        }else{
            AppMain.notificationService.createNotification("Measurement not found in the upper graph", NotificationType.ERROR);
        }
    }

    public void exportLowerGraph(MouseEvent mouseEvent) {
        if(gs.lowerGraph.getMeasurement() != null){
            if(AppMain.fileService.exportAs(gs.lowerGraph.getMeasurement())){
                AppMain.notificationService.createNotification("The measurement in the lower graph was exported.", NotificationType.SUCCESS);
            }else{
                AppMain.notificationService.createNotification("The measurement in the lower graph failed to export.", NotificationType.ERROR);
            }
        }else{
            AppMain.notificationService.createNotification("Measurement not found in the lower graph", NotificationType.ERROR);
        }
    }

    public void saveUpperGraph(MouseEvent mouseEvent) {
        if (gs.upperGraph.getMeasurement() != null) {
            if (AppMain.fileService.saveAsMeasurement(gs.upperGraph.getMeasurement())) {
                gs.upperGraph.getMeasurement().setState(MeasurementState.SAVED);
                AppMain.notificationService.createNotification("The measurement in the upper graph was saved.", NotificationType.SUCCESS);
            } else {
                AppMain.notificationService.createNotification("The measurement in the upper graph failed to save.", NotificationType.ERROR);
            }
        } else {
            AppMain.notificationService.createNotification("Measurement not found in the upper graph", NotificationType.ERROR);
        }
    }

    public void saveLowerGraph(MouseEvent mouseEvent) {
        if (gs.lowerGraph.getMeasurement() != null) {
            if (AppMain.fileService.saveAsMeasurement(gs.lowerGraph.getMeasurement())) {
                gs.lowerGraph.getMeasurement().setState(MeasurementState.SAVED);
                AppMain.notificationService.createNotification("The measurement in the lower graph was saved.", NotificationType.SUCCESS);
            } else {
                AppMain.notificationService.createNotification("The measurement in the lower graph failed to save.", NotificationType.ERROR);
            }
        } else {
            AppMain.notificationService.createNotification("Measurement not found in the lower graph", NotificationType.ERROR);
        }
    }

    public void updateGpibMenu(boolean status) {
        Platform.runLater(() -> gpibMenu.setText("GPIB connection: " + (status ? "ACTIVE" : "INACTIVE")));
    }

}