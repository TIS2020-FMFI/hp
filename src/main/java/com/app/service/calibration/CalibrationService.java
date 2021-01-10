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
    private final LinkedList<Boolean> toggleGroupTypeDisabled;
    private Stage stage;

    public CalibrationService(String controllerPath) {
        path = controllerPath;
        toggleGroupType = new LinkedList<>();
        toggleGroupTypeDisabled = new LinkedList<>(Arrays.asList(false, false, false));
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
        notificationService.createNotification(content, type).show();
    }

    public void addRadioButtons(LinkedList<RadioButton> radioButtons) {
        // TODO: how do I know state of calibration while init
        toggleGroupType = radioButtons;
        for (int i=0; i < radioButtons.size(); i++) {
            toggleGroupType.get(i).setDisable(toggleGroupTypeDisabled.get(i));
        }
        setNextRadioButtonActive();
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
        for (Boolean isDisabled : toggleGroupTypeDisabled) {
            if (!isDisabled) {
                return false;
            }
        }
        return true;
    }

    public boolean isCalibrationInProcess() {
        for (Boolean isDisabled : toggleGroupTypeDisabled) {
            if (isDisabled) {
                return true;
            }
        }
        return false;
    }

    private void setNextRadioButtonActive() {
        for (int i=0; i < toggleGroupTypeDisabled.size(); i++) {
            if (!toggleGroupTypeDisabled.get(i)) {
                toggleGroupType.get(i).selectedProperty().set(true);
                return;
            }
        }

    }

    public void runCalibration(String calibrationType) {
        try {
            CalibrationType requestedCalibrationType;
            switch (calibrationType) {
                case "short":
                    requestedCalibrationType = CalibrationType.SHORT;
                    break;
                case "load":
                    requestedCalibrationType = CalibrationType.LOAD;
                    break;
                case "open":
                    requestedCalibrationType = CalibrationType.OPEN;
                    break;
                default:
                    throw new NoSuchAttributeException("Calibration type not recognized!");
            }

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
                setNextRadioButtonActive();
            }
        } catch (NoSuchAttributeException | IOException | RuntimeException | InterruptedException e) {
            showNotification("upss! -> " + e.getMessage(), NotificationType.ERROR);
        }
    }
}
