package com.app.machineCommunication;


import com.app.service.AppMain;
import com.app.service.file.parameters.EnvironmentParameters;

import java.io.*;

public class Connection {

    private boolean connected = false;
    boolean cmd = false;
    Process p;
    BufferedReader readEnd = new BufferedReader(new InputStreamReader(p.getInputStream()));
    BufferedWriter writeEnd = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
    EnvironmentParameters environmentParameters;

    public Connection() throws IOException {
        p = Runtime.getRuntime().exec("D:/hpctrl-main/src/Debug/hpctrl.exe -i"); // TODO: set to default within project
//        environmentParameters = AppMain.fileService.getEnvironmentParameters();
        environmentParameters = new EnvironmentParameters();
    }

    public boolean connect() throws IOException, InterruptedException {
        //ak sa nepripoji, exception -> pipe is being closed
        if (connected) {
            // TODO: check cmd mode before exit
            write("exit");
        } else {
            write("connect");
        }
        connected = !connected;
        return connected;
    }

    public void toggleCmdMode() throws IOException, InterruptedException {
        if (connected) {
            write("cmd");
            StringBuilder result = new StringBuilder();
            while (readEnd.ready()) {
                result.append((char) readEnd.read());
            }
            if (!result.toString().equals("!not ready, try again later (cmd)")) {
                cmd = !cmd;
            } else {/*notifikacia o nepripojeni do cmd modu ? treba odkliknut tie dve chyby na pristroji pomocou hocijakého tlačidla*/}
        } else {/*notifikacia ze treba najprv pripojit zariadenie ? */}

    }

    public void write(String text) throws IOException, InterruptedException {
        // TODO: extract to own thread
        writeEnd.write(text);
        writeEnd.newLine();
        writeEnd.flush();
        Thread.sleep(1000);
    }


    public void startMeasurement() throws IOException, InterruptedException {
        write("s WU");
        String result = "";
        while (readEnd.ready()) {
            //poskladanie znakov do stringu + treba odoslat meranie -> do pola ?
            if ((char) readEnd.read() == '\n') {
                // TODO: tu bude posli result
                result = "";
            } else
                result += (char) readEnd.read();
        }

    }

    public void measurement() throws IOException, InterruptedException { //parametre merania ?
        if (connected) {
            if (!cmd)
                toggleCmdMode();

            if (cmd) {
                //poslanie vsetkych parametrov od janciho do zvoleneho merania + (chýba ->  display functions + others)
                //example frequency sweep

                //if frequency sweep
                write("s TF" + environmentParameters.getFrequencySweep().getStart() + "EN");
                write("s PF" + environmentParameters.getFrequencySweep().getStop() + "EN");
                write("s SF" + environmentParameters.getFrequencySweep().getStep() + "EN");
                write("s FR" + environmentParameters.getFrequencySweep().getSpot() + "EN");

                //if voltage sweep
                //BI spot SB step TB start PB stop
                startMeasurement();

            }
        }


    }

}

