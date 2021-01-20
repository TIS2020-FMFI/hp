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

    public void closeWindow(MouseEvent event) {
        AppMain.dataNotSavedDialog.closeWithoutSaving();
    }

    public void saveAndCloseWindow(MouseEvent event) {
        AppMain.dataNotSavedDialog.saveAndCloseDialog();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

}
