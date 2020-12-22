package com.app.machineCommunication;


import java.io.*;

public class Connection {

    boolean connected = false;
    boolean cmd = false;
    Runtime r = Runtime.getRuntime();
    Process p = r.exec("D:/hpctrl-main/src/Debug/hpctrl.exe -i");
    BufferedReader readEnd = new BufferedReader(new InputStreamReader(p.getInputStream()));
    BufferedWriter writeEnd = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));;

    public Connection() throws IOException {
    }

    public boolean connect() throws Exception {
        //ak sa nepripoji, exception -> pipe is being closed
        if (connected){
            exit();
            return false;
        }
        else{
        writeEnd.write("connect");
        writeEnd.newLine();
        writeEnd.flush();
        Thread.sleep(1000);
        connected = !connected;
        return true;}
    }

    public void exit() throws Exception{
        //check cmd mode before exit
        writeEnd.write("exit");
        writeEnd.newLine();
        writeEnd.flush();
        Thread.sleep(1000);
        connected = !connected;
    }
    public void cmdMode() throws IOException, InterruptedException {
        if (connected) {
            writeEnd.write("cmd");
            writeEnd.newLine();
            writeEnd.flush();
            Thread.sleep(1000);
            String result = "";
            while (readEnd.ready()) {
                result += (char) readEnd.read();
            }
            if (result != "!not ready, try again later (cmd)")
                cmd = !cmd;

            else {/*notifikacia o nepripojeni do cmd modu ? treba odkliknut tie dve chyby na pristroji pomocou hocijakého tlačidla*/}
        }

        else{/*notifikacia ze treba najprv pripojit zariadenie ? */}

    }
    public void startFrequency(String par) throws IOException, InterruptedException {
        writeEnd.write(par);
        writeEnd.newLine();
        writeEnd.flush();
        Thread.sleep(1000);
    }
    public void stopFrequency(String par) throws IOException, InterruptedException {
        writeEnd.write(par);
        writeEnd.newLine();
        writeEnd.flush();
        Thread.sleep(1000);

    }
    public void stepFrequency(String par) throws IOException, InterruptedException {
        writeEnd.write(par);
        writeEnd.newLine();
        writeEnd.flush();
        Thread.sleep(1000);

    }
    public void spotFrequency(String par) throws IOException, InterruptedException {
        writeEnd.write(par);
        writeEnd.newLine();
        writeEnd.flush();
        Thread.sleep(1000);

    }
    public void startMeasurement() throws IOException, InterruptedException {
        writeEnd.write("s WU");  // start merania
        writeEnd.newLine();
        writeEnd.flush();
        Thread.sleep(1000);
        String result = "";
        while (readEnd.ready())
        {
            //poskladanie znakov do stringu + treba odoslat meranie -> do pola ?
            if ((char)readEnd.read() == '\n'){
                //tu bude posli result
                result = "";
            }
            else
                result += (char)readEnd.read();
        }

    }

    public void measurement() throws IOException, InterruptedException { //parametre merania ?
        if (connected){
            if (!cmd)
                cmdMode();

            if (cmd){
                //poslanie vsetkych parametrov od janciho do zvoleneho merania + (chýba ->  display functions + others)
                //example frequency sweep

                //if frequency sweep
                startFrequency("s TF300EN");
                stopFrequency("s PF500EN");
                stepFrequency("s SF25EN");
                spotFrequency("s FR25EN");

                //if voltage sweep
                   //BI spot SB step TB start PB stop
                startMeasurement();

            }
        }


    }

}

