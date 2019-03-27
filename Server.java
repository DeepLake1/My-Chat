package com.javarush.task.task30.task3008;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();


    private static class Handler extends Thread {
        Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            while(true){
                connection.send(new Message(MessageType.NAME_REQUEST));
                Message answer = connection.receive();
                if(answer==null ) continue;
                if(answer.getType()!=MessageType.USER_NAME) continue;
                String answerData = answer.getData();
                if(answerData==null||answerData.isEmpty()) continue;
                if(connectionMap.containsKey(answerData)) continue;
                connectionMap.put(answerData,connection);
                connection.send(new Message(MessageType.NAME_ACCEPTED));
                return answerData;
            }

            }




        public static void sendBroadcastMessage(Message message) {
            for (Map.Entry<String, Connection> s : connectionMap.entrySet()) {
                try {
                    s.getValue().send(message);
                } catch (IOException e) {
                    System.out.println("Sorry, we couldn't send your message.. ");
                }
            }

        }

        public static void main(String[] args) throws IOException {
            ServerSocket serverSocket = new ServerSocket(ConsoleHelper.readInt());
            System.out.println("Сервер запущен!");
            try {
                while (true) {
                    new Handler(serverSocket.accept()).start();
                }
            } catch (Exception x) {
                System.out.println("Ошибка!");
            } finally {
                serverSocket.close();
            }
        }
    }}
