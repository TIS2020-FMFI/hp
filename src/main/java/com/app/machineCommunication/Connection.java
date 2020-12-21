package com.app.machineCommunication;


import java.io.*;

public class Connection {

    boolean connected = false;
    Runtime r;
    Process p;
    BufferedReader readEnd;
    BufferedWriter writeEnd;

    public boolean connect() throws Exception {
        if (connected){
            exit();
            return false;
        }
        else{
        r = Runtime.getRuntime();
        p = r.exec("D:/hpctrl-main/src/Debug/hpctrl.exe -i");
        readEnd = new BufferedReader(new InputStreamReader(p.getInputStream()));
        writeEnd = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
        writeEnd.write("connect");
        writeEnd.newLine();
        writeEnd.flush();
        Thread.sleep(1000);
        connected = true;
        return true;}
    }

    public void exit() throws Exception{
        writeEnd.write("exit");
        writeEnd.newLine();
        writeEnd.flush();
        Thread.sleep(1000);
        connected = false;
    }
}
