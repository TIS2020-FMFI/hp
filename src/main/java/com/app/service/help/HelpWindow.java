package com.app.service.help;

import com.app.service.AppMain;
import com.app.service.Window;
import com.app.service.notification.NotificationType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * Help window to show so called 'about' information
 */
public class HelpWindow implements Window {
    private final String path;
    private Stage stage;

    /**
     * Initializes help service
     *
     * @param controllerPath path of the view controller
     */
    public HelpWindow(String controllerPath) {
        path = controllerPath;
    }

    @Override
    public void open() {
        try {
            stage = new Stage();

            Parent calibrationRoot = FXMLLoader.load(getClass().getResource(path));
            stage.setScene(new Scene(calibrationRoot));
            stage.setTitle("Help");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            AppMain.notificationService.createNotification(e.getMessage(), NotificationType.ERROR);
        }
    }

    @Override
    public void close() {
        stage.close();
    }

}
