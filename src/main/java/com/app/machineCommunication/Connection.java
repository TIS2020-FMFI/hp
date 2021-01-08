package com.app.machineCommunication;



import com.app.service.AppMain;
import com.app.service.calibration.CalibrationType;
import com.app.service.file.parameters.EnvironmentParameters;
import com.app.service.notification.NotificationType;
import java.io.*;

public class Connection {


    private boolean connected = false;
    boolean cmd = false;
    Process p;
    BufferedReader readEnd;
    BufferedWriter writeEnd;
    EnvironmentParameters environmentParameters;

    public Connection(){
        try {
            p = Runtime.getRuntime().exec("D:/hpctrl-main/src/Debug/hpctrl.exe -i"); // TODO: set to default within project
            readEnd = new BufferedReader(new InputStreamReader(p.getInputStream()));
            writeEnd = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
        } catch (IOException e) {
            AppMain.notificationService.createNotification("hpctrl script missing, read help for more info", NotificationType.ERROR);
        }
        environmentParameters = new EnvironmentParameters();
    }

    public boolean connect() throws IOException, InterruptedException {
        //ak sa nepripoji, exception -> pipe is being closed
        if (connected){
            if(cmd)
                write(".");
            write("exit");
        }
        else
            write("connect");

        cmd = false;
        connected = !connected;

        return connected;
    }

    public void toggleCmdMode() throws IOException, InterruptedException {
        if (connected) {
            write("cmd");
            StringBuilder result = read();

            if (!result.toString().equals("!not ready, try again later (cmd)")) {
                cmd = !cmd;

            } else {/*notifikacia o nepripojeni do cmd modu ? treba odkliknut tie dve chyby na pristroji pomocou hocijakého tlačidla*/}

        } else {/*notifikacia ze treba najprv pripojit zariadenie ? */}
    }

    public StringBuilder read() throws IOException {
        StringBuilder result = new StringBuilder();
        while (readEnd.ready()) {
            result.append((char) readEnd.read());
        }
        return result;

    }


    private void write(String text) throws IOException, InterruptedException {
        // TODO: extract to own thread
        writeEnd.write(text);
        writeEnd.newLine();
        writeEnd.flush();
        Thread.sleep(1000);
    }


    public void startMeasurement() throws IOException, InterruptedException {
        write("s WU");
        StringBuilder result = new StringBuilder();
        while (readEnd.ready()) {
            char letter = (char) readEnd.read();
            if (letter == '\n') {
                // TODO: tu bude posli result
                result = new StringBuilder();
            } else
                result.append(letter);
        }

    }

    public void measurement(String sweep) throws IOException, InterruptedException {
        if (connected) {
            if (!cmd)
                toggleCmdMode();

            if (cmd) {
                // TODO:function for display functions
                others();
                if (sweep == "F")
                    frequencySweep();
                if (sweep == "V")
                    voltageSweep();

                startMeasurement();
            }
        }
    }

    public void others() throws IOException, InterruptedException {
        if (environmentParameters.getOther().isHighSpeed())
            write("s H1");
        else
            write("s H0");
    }

    public void frequencySweep() throws IOException, InterruptedException {
        write("s TF" + environmentParameters.getFrequencySweep().getStart() + "EN");
        write("s PF" + environmentParameters.getFrequencySweep().getStop() + "EN");
        write("s SF" + environmentParameters.getFrequencySweep().getStep() + "EN");
        write("s FR" + environmentParameters.getFrequencySweep().getSpot() + "EN");
    }



    public void voltageSweep()  throws IOException, InterruptedException {
        write("s TB" + environmentParameters.getVoltageSweep().getStart() + "EN");
        write("s PB" + environmentParameters.getVoltageSweep().getStop() + "EN");
        write("s SB" + environmentParameters.getVoltageSweep().getStep() + "EN");
        write("s BI" + environmentParameters.getVoltageSweep().getSpot() + "EN");
    }

    public boolean calibrationHandler(CalibrationType calibrationType) throws IOException {
        return true;
    }

}

