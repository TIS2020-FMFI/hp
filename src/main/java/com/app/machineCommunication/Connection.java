package com.app.machineCommunication;


import java.io.*;

public class Connection {

    boolean connected = false;
    Runtime r;
    Process p;
    BufferedReader readEnd;
    BufferedWriter writeEnd;

    public void connect() throws Exception {
        r = Runtime.getRuntime();
        p = r.exec("Debug/hpctrl.exe -i");
        readEnd = new BufferedReader(new InputStreamReader(p.getInputStream()));
        writeEnd = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
        writeEnd.write("connect");
        writeEnd.newLine();
        writeEnd.flush();
        Thread.sleep(1000);
        while (readEnd.ready())
        {
            System.out.print((char)readEnd.read());
        }
        connected = true;
    }

    public void exit() throws Exception{
        writeEnd.write("exit");
        writeEnd.newLine();
        writeEnd.flush();
        Thread.sleep(1000);
        while (readEnd.ready())
        {
            System.out.print((char)readEnd.read());
        }
        connected = false;
    }
}
