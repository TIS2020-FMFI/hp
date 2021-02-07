package com.app.screen.controller;

import com.app.service.AppMain;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for data not saved pupup
 */
public class DataNotSavedDialogController implements Initializable {

    @FXML
    VBox mainContainer;

    @FXML
    HBox titleContainer;
    @FXML
    VBox contentContainer;
    @FXML
    HBox actionContainer;

    @FXML
    Button closeWithoutSavingBtn;
    @FXML
    Button saveAndCloseBtn;

    /**
     * Close window request
     *
     * @param event event that triggered the request
     */
    public void closeWindow(MouseEvent event) {
        AppMain.dataNotSavedWindow.close();
    }

    /**
     * Save and close app request
     *
     * @param event event that triggered the request
     */
    public void saveAndCloseWindow(MouseEvent event) {
        AppMain.dataNotSavedWindow.saveAndClose();
    }

    /**
     * Initializes controller
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

}
