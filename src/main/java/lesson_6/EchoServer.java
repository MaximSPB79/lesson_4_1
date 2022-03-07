package lesson_6;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class EchoServer {
    private static final int SERVER_PORT = 8186;
    private static DataInputStream in;
    private static DataOutputStream out;

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {

            while (true) {
                System.out.println("Ожидание подключения ...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Подключение установленно!");

                in = new DataInputStream(clientSocket.getInputStream());
                out = new DataOutputStream(clientSocket.getOutputStream());

                try {
                    while (true) {
                        String message = in.readUTF();
                        if (message.equals("/server-stop")) {
                            System.out.println("Сервер остановлен");
                            System.exit(0);
                        }
                        System.out.println("Клиент: " + message);
                        out.writeUTF(message.toUpperCase());
                    }
                } catch (SocketException e) {
                    clientSocket.close();
                    System.out.println("Клиент отключился");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
