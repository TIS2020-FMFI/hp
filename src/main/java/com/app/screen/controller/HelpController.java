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

public class HelpController implements Initializable {

    @FXML
    VBox mainContainer;

    @FXML
    HBox titleContainer;
    @FXML
    VBox contentContainer;
    @FXML
    HBox actionContainer;

    @FXML
    Button closeBtn;

    public void closeWindow(MouseEvent event) {
        AppMain.helpWindow.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

}
