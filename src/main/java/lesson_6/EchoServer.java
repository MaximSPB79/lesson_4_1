package lesson_6;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class EchoServer {
    private static final int SERVER_PORT = 8186;
    private static DataInputStream in;
    private static DataOutputStream out;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {

            while (true) {
                System.out.println("Ожидание подключения ...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Подключение установленно!");

                in = new DataInputStream(clientSocket.getInputStream());
                out = new DataOutputStream(clientSocket.getOutputStream());
                Thread server = new Thread(() -> {
                    try {
                        receivingMessageFromTheServer(scanner, clientSocket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                Thread client = new Thread(() -> {
                    try {
                        receivingMessageFromTheClient(clientSocket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                client.start();
                server.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void receivingMessageFromTheServer(Scanner scanner, Socket clientSocket) throws IOException {
        try {
            while (true) {
                String message = scanner.nextLine();
                if (message.equals("/server-stop")) {
                    System.out.println("Сервер остановлен");
                    System.exit(0);
                }
                System.out.println("Сервер: " + message);
                out.writeUTF("Сервер: " + message.toUpperCase());
            }
        } catch (SocketException e) {
            clientSocket.close();
            System.out.println("Клиент отключился");
        }
    }

    private static void receivingMessageFromTheClient(Socket clientSocket) throws IOException {
        try {
            while (true) {
                String message = in.readUTF();
                ;
                if (message.equals("/server-stop")) {
                    System.out.println("Сервер остановлен");
                    System.exit(0);
                }
                System.out.println("Клиент: " + message);
                out.writeUTF("Клиент: " + message.toUpperCase());
            }
        } catch (SocketException e) {
            clientSocket.close();
            System.out.println("Клиент отключился");
        }
    }
}