package com.app.service.calibration;

import com.app.service.AppMain;
import com.app.service.notification.NotificationService;
import com.app.service.notification.NotificationType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.naming.directory.NoSuchAttributeException;
import java.io.IOException;
import java.util.*;


public class CalibrationService {
    private final String path;
    private NotificationService notificationService;
    private LinkedList<RadioButton> toggleGroupType;
    private Map<CalibrationType, RadioButton> calibrationButtons;
    private Map<CalibrationType, Boolean> calibrationStates;
    private final LinkedList<Boolean> toggleGroupTypeDisabled;
    private Stage stage;
    private double electricalLength;
    private double capacitance;
    private boolean isCalibrating;

    public CalibrationService(String controllerPath) {
        path = controllerPath;
        toggleGroupType = new LinkedList<>();
        toggleGroupTypeDisabled = new LinkedList<>(Arrays.asList(false, false, false));
        isCalibrating = false;
    }

    public void openCalibration() {
        try {
            stage = new Stage();

            Parent calibrationRoot = FXMLLoader.load(getClass().getResource(path));
            stage.setScene(new Scene(calibrationRoot));
            stage.setTitle("Calibration");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            VBox notificationContainer = (VBox) calibrationRoot.lookup("#notificationContainer");
            if (notificationContainer == null) {
                throw new NoSuchElementException("Notification container not found in calibration window!");
            }
            notificationService = new NotificationService(notificationContainer);
            stage.show();
        } catch (NoSuchElementException | IOException e) {
            AppMain.notificationService.createNotification(e.getMessage(), NotificationType.ERROR);
        }
    }

    public void closeCalibration() {
        stage.close();
    }

    private void showNotification(String content, NotificationType type) {
        notificationService.createNotification(content, type);
    }

    public void addRadioButtons(Map<CalibrationType, RadioButton> radioButtons) {
        calibrationButtons = radioButtons;
        setAnotherActive();
    }

    private Integer getAssociatedRadioButtonIndex(CalibrationType type) {
        for (int i=0; i < toggleGroupType.size(); i++) {
            if (CalibrationType.valueOf(toggleGroupType.get(i).getText().toUpperCase()).equals(type)) {
                return i;
            }
        }
        return null;
    }

    public boolean isCalibrated() {
        for (RadioButton radio : toggleGroupType) {
            if (!radio.isDisabled()) {
                return false;
            }
        }
        return true;
    }

    private CalibrationType getActiveType() {
        for (Map.Entry<CalibrationType, RadioButton> button : calibrationButtons.entrySet()) {
            if (button.getValue().isSelected()) {
                return button.getKey();
            }
        }
        return setAnotherActive();
    }

    public boolean isCalibrationInProcess() {
        for (RadioButton radio : toggleGroupType) {
            if (radio.isDisabled()) {
                return true;
            }
        }
        return false;
    }

    private CalibrationType setAnotherActive() {
        for (Map.Entry<CalibrationType, Boolean> cal : calibrationStates.entrySet()) {
            if (!cal.getValue()) {
                calibrationButtons.get(cal.getKey()).selectedProperty().set(true);
                return cal.getKey();
            }
        }
        return CalibrationType.SHORT;
    }

    public void setIsCalibrating(boolean status) {
        isCalibrating = status;
    }

    public void runCalibration(String calibrationType) {
        try {
            CalibrationType requestedCalibrationType = CalibrationType.getTypeFromString(calibrationType);
            isCalibrating = true;
            boolean calibrationSuccessful = AppMain.communicationService.runCalibration(requestedCalibrationType);
            if (!calibrationSuccessful) {
                throw new RuntimeException("Calibration failed!");
            } else {
                showNotification("Calibration " + requestedCalibrationType.toString() + " processed successfully.", NotificationType.SUCCESS);
                Integer associatedRadioButtonIndex = getAssociatedRadioButtonIndex(requestedCalibrationType);
                if (associatedRadioButtonIndex == null) {
                    throw new NoSuchElementException("Calibration type RadioButton not present!");
                }
                toggleGroupType.get(associatedRadioButtonIndex).setDisable(true);
                toggleGroupTypeDisabled.set(associatedRadioButtonIndex, true);
                setAnotherActive();
            }
        } catch (NoSuchAttributeException | IOException | RuntimeException | InterruptedException e) {
            showNotification("upss! -> " + e.getMessage(), NotificationType.ERROR);
        }
    }
}
