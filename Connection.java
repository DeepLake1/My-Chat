package com.javarush.task.task30.task3008;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;


    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.out =  new ObjectOutputStream(socket.getOutputStream()) ;
        this.in = new ObjectInputStream(socket.getInputStream()) ;
    }



}
