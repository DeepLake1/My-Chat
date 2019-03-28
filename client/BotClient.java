package com.javarush.task.task30.task3008.client;

import com.javarush.task.task30.task3008.ConsoleHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BotClient extends Client {
        BotSocketThread sct = new BotSocketThread();
    public static void main(String[] args) {
        new BotClient().run();
    }

    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    @Override
    protected String getUserName() {
        return "date_bot_" + (int) (Math.random() * 100);
    }

    public class BotSocketThread extends SocketThread {
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();

        }

        @Override
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
            if (message != null && message.contains(": ")) {
                ConsoleHelper.writeMessage(message);
                String name = message.split(": ")[0];
                String text = message.split(": ")[1];
                String date;
                if(text.equals("дата")) {
                    SimpleDateFormat format = new SimpleDateFormat("d.MM.YYYY");
                    date = format.format(Calendar.getInstance().getTime());
                }
                else if(text.equals("день")) {
                    SimpleDateFormat format = new SimpleDateFormat("d");
                    date = format.format(Calendar.getInstance().getTime());
                }
                else if(text.equals("месяц")) {
                    SimpleDateFormat format = new SimpleDateFormat("MMMM");
                    date = format.format(Calendar.getInstance().getTime());
                }
                else if(text.equals("год")) {
                    SimpleDateFormat format = new SimpleDateFormat("YYYY");
                    date = format.format(Calendar.getInstance().getTime());
                }
                else if(text.equals("время")) {
                    SimpleDateFormat format = new SimpleDateFormat("H:mm:ss");
                    date = format.format(Calendar.getInstance().getTime());
                }
                else if(text.equals("час")) {
                    SimpleDateFormat format = new SimpleDateFormat("H");
                    date = format.format(Calendar.getInstance().getTime());
                }
                else if(text.equals("минуты")) {
                    SimpleDateFormat format = new SimpleDateFormat("m");
                    date = format.format(Calendar.getInstance().getTime());
                }
                else if(text.equals("секунды")) {
                    SimpleDateFormat format = new SimpleDateFormat("s");
                    date = format.format(Calendar.getInstance().getTime());
                }
                else return;
                sendTextMessage(String.format("Информация для %s: %s", name, date));
            }


            }
        }

    }

