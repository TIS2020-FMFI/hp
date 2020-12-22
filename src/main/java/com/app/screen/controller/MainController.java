package com.app.screen.controller;

import com.app.machineCommunication.Connection;
import com.app.service.AppMain;
import com.app.service.graph.Graph;
import com.app.service.notification.NotificationType;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.fx.ChartCanvas;
import org.jfree.chart.fx.ChartViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class MainController implements Initializable {

    @FXML
    VBox mainContainer;
    @FXML
    VBox notificationContainer;

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
    Pane lowerPane;

    @FXML
    VBox VBox1;

    public MainController() throws IOException {
    }


    private Node useWorkaround(ChartViewer viewer) {
        if (true) {
            return new StackPane(viewer);
        }
        return viewer;
    }

    public void runMeasurement(MouseEvent event) {
        // TODO: run measurement and graph
//        VBox1 = new VBox();
        JFrame frame=new JFrame("Chart");
        Graph rtcp=new Graph("Chart", "Resistance", "Capacity", "Frequency");
//        ChartViewer chartViewer = new ChartViewer(rtcp.chart);
//        chartViewer.setPrefHeight(680);
//        chartViewer.setPrefWidth(260);
//        chartViewer.setVisible(true);

//        lowerPane = new Pane();
//        lowerPane.setPrefSize(680, 260);
//        ChartCanvas canvas = chartViewer.getCanvas();
//        canvas.heightProperty().bind(lowerPane.heightProperty());
//        canvas.widthProperty().bind(lowerPane.widthProperty());
//        lowerPane.getChildren().addAll(canvas);
//
//        ChartPanel chartPanel = new ChartPanel(rtcp.chart);
////        chartPanel.setPreferredSize(new java.awt.Dimension(680, 260));
//
//        upperPane = new AnchorPane();
//        upperPane.setPrefSize(680, 260);
//        upperPane.getChildren().addAll(chartViewer);
//        upperPane.setVisible(true);
//        VBox1.getChildren().add(upperPane);
//        Scene primaryScene = new Scene(VBox1);


//        final SwingNode swingNode = new SwingNode();
//        swingNode.setContent(chartPanel);
//        upperPane.getChildren().addAll(swingNode);
//
//        Stage stage = new Stage();
//        stage.setScene(new Scene(upperPane, 0, 0));
//        stage.show();


        frame.getContentPane().add(rtcp,new BorderLayout().CENTER);
        frame.pack();
        frame.setVisible(true);

//        (new Thread(rtcp)).start(); 1st version

        // real-time plotting
        final double[] poc = {0};
        //now make your timer
        int delay = 500; //milliseconds
        ActionListener timerAction = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                //add new data point and actualize graph
                rtcp.series1.add(poc[0], (double) (Math.random()*20+80));  // tu budu pribudat values
                rtcp.series2.add(poc[0], (double) (Math.random()*20+80));  // tu budu pribudat values
                poc[0]++;

            }
        };
        new Timer(delay, timerAction).start();

//        frame.addWindowListener(new WindowAdapter()
//        {
//            public void windowClosing(WindowEvent windowevent)
//            {
//                System.exit(0);
//            }
//
//        });

    }

    public void toggleAutoSave(MouseEvent event) {
        // TODO: change autoSaveMode in global props global props
    }

    public void resetInstrument(MouseEvent event) {
        // TODO: send command to reset instrument
    }

    public void triggerCalibration(MouseEvent event) {
        try {
            AppMain.calibrationService.openCalibration();
        } catch (Exception e) {
            AppMain.notificationService.createNotification("Calibration window could not be open! Please, restart the app.", NotificationType.ERROR).show();
        }
    }

    public void quitApp(MouseEvent event) {
        // TODO: if not all data saved -> notification and abort quit
        // save global props into config
        Platform.exit();
        System.exit(0);
    }

    public void showHelpWindow(MouseEvent event) {
        //TODO: create simple window with links, details, description
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: read config here

        // ----- initialize all dropbox -> coz its not possible to do so in sceneBuilder yet
        otherSweepType.getItems().addAll("LINEAR", "LOG");
        otherSweepType.getSelectionModel().select(0);
        otherHighSpeed.getItems().addAll("OFF", "ON");
        otherHighSpeed.getSelectionModel().select(0);
        otherAutoSweep.getItems().addAll("ON", "OFF");
        otherAutoSweep.getSelectionModel().select(0);
        // -----
    }
    Connection con = new Connection();
    public void runConnection(MouseEvent mouseEvent) throws Exception {
        if (con.connect())
            gpibMenu.setText("GPIB connection: ACTIVE");
        else
            gpibMenu.setText("GPIB connection: INACTIVE");
    }
}
