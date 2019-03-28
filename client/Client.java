package com.javarush.task.task30.task3008.client;

import com.javarush.task.task30.task3008.Connection;
import com.javarush.task.task30.task3008.ConsoleHelper;
import com.javarush.task.task30.task3008.Message;
import com.javarush.task.task30.task3008.MessageType;

import java.io.IOException;
import java.net.Socket;

public class Client extends Thread {

    protected Connection connection;
    private volatile boolean clientConnected = false;

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }

    protected String getServerAddress() {
        System.out.println("Введите адрес сервера:");
        return ConsoleHelper.readString();

    }

    protected int getServerPort() {
        System.out.println("Введите порт:");
        return ConsoleHelper.readInt();
    }

    protected String getUserName() {
        System.out.println("Введите имя пользователя: ");
        return ConsoleHelper.readString();
    }

    protected boolean shouldSendTextFromConsole() {
        return true;
    }

    protected SocketThread getSocketThread() {
        return new SocketThread();
    }

    protected void sendTextMessage(String text) {
        try {
            connection.send(new Message(MessageType.TEXT, text));
        } catch (IOException e) {
            ConsoleHelper.writeMessage(e.getMessage());
            clientConnected = false;

        }
    }

    @Override
    public void run() {
        SocketThread socketThread;
        synchronized (this) {
            socketThread = getSocketThread();
            socketThread.setDaemon(true);
            socketThread.start();
            try {
                this.wait();
                if (clientConnected)
                    ConsoleHelper.writeMessage("Соединение установлено. Для выхода наберите команду 'exit'.");
                else ConsoleHelper.writeMessage("Произошла ошибка во время работы клиента.");
                while (clientConnected) {
                    String ff = ConsoleHelper.readString();
                    if (ff.equals("exit")) break;
                    if (shouldSendTextFromConsole()) {
                        sendTextMessage(ff);
                    }
                }
            } catch (InterruptedException e) {
                ConsoleHelper.writeMessage(e.getMessage());
            }
        }
    }

    public class SocketThread extends Thread {
        protected void clientHandshake() throws IOException, ClassNotFoundException {
            while (true) {
                Message s = connection.receive();
                if (s.getType() == MessageType.NAME_REQUEST) {
                    String nameUser = getUserName();
                    connection.send(new Message(MessageType.USER_NAME, nameUser));
                    continue;
                }
                if (s.getType() == MessageType.NAME_ACCEPTED) {
                    notifyConnectionStatusChanged(true);
                    break;
                } else throw new IOException("Unexpected MessageType");
            }
        }

        protected void clientMainLoop() throws IOException, ClassNotFoundException {

            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT) {
                    processIncomingMessage(message.getData());
                } else if (message.getType() == MessageType.USER_ADDED) {
                    informAboutAddingNewUser(message.getData());
                } else if (message.getType() == MessageType.USER_REMOVED) {
                    informAboutDeletingNewUser(message.getData());
                } else {
                    throw new IOException("Unexpected MessageType");
                }

            }
        }

        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
        }

        protected void informAboutAddingNewUser(String userName) {
            ConsoleHelper.writeMessage(userName + "вошел в чат!");

        }

        protected void informAboutDeletingNewUser(String userName) {
            ConsoleHelper.writeMessage(userName + "покинул чат.");
        }

        protected void notifyConnectionStatusChanged(boolean clientConnected) {
            Client.this.clientConnected = clientConnected;
            synchronized (Client.this) {
                Client.this.notify();
            }
        }

        @Override
        public void run() {
            String serverAdress = getServerAddress();
            int serverPort = getServerPort();
            try {
                Socket socket = new Socket(serverAdress, serverPort);
                 connection = new Connection(socket);
                clientMainLoop();
                clientHandshake();
            } catch (IOException | ClassNotFoundException e) {
                notifyConnectionStatusChanged(false);
            }
        }
    }

}
