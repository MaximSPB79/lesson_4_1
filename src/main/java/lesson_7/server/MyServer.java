package lesson_7.server;

import lesson_7.server.authentication.AuthenticationService;
import lesson_7.server.authentication.BaseAuthenticationService;
import lesson_7.server.handler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class MyServer {

    private final ServerSocket serverSocket;
    private final AuthenticationService authenticationService;
    private final List<ClientHandler> clients;

    public MyServer(int port) throws IOException {
        // создаем экземпляр сокета
        serverSocket = new ServerSocket(port);
        // создаем экземпляр аутентификации, который хранит логины и пароли пользователей с данными из класса
        // BaseAuthenticationService, в дальнейшем заменим на бд
        authenticationService = new BaseAuthenticationService();
        // создаем список клиентов
        clients = new ArrayList<>();
    }


    public void start() {
        authenticationService.startAuthentication();
        System.out.println("СЕРВЕР ЗАПУЩЕН!");
        System.out.println("----------------");
        try {
            // запускаем бесконечный цикл по ожиданию клиента
            while (true) {
                waitAndProcessNewClientConnection();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            authenticationService.endAuthentication();
        }
    }

    private void waitAndProcessNewClientConnection() throws IOException {
        System.out.println("Ожидание клиента...");
        // ловим сокет клиента
        Socket socket = serverSocket.accept();
        System.out.println("Клиент подключился!");

        processClientConnection(socket);
    }

    private void processClientConnection(Socket socket) throws IOException {
        // для пойманного сокета создается handler и передаем ему текущий экземпляр MyServer и сокет для двухстороннего
        // взаимодействия
        ClientHandler handler = new ClientHandler(this, socket);
        // запускаем обработку подключения handler
        handler.handle();
    }

    // добавляем клиента в список
    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    // удаляем клиента из списка
    public synchronized void unSubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    // проверяем подключен данный клиент или нет
    public synchronized boolean isUsernameBusy(String username) {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    // отправляем сообщение всем клиентам
    public synchronized void broadcastMessage(String message, ClientHandler sender, boolean isServerMessage) throws IOException {
        for (ClientHandler client : clients) {
            if (client == sender) {
                continue;
            }
            client.sendMessage(isServerMessage ? null : sender.getUsername(), message);
        }
    }

    public synchronized void broadcastMessage(String message, ClientHandler sender) throws IOException {
        broadcastMessage(message, sender, false);
    }

    public synchronized void sendPrivateMessage(ClientHandler sender, String recipient, String privateMessage) throws IOException {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(recipient)) {
                client.sendMessage(sender.getUsername(), privateMessage);
            }
        }
    }

    public synchronized void broadcastClients(ClientHandler sender) throws IOException {
        for (ClientHandler client : clients) {

            client.sendServerMessage(String.format("%s присоединился к чату", sender.getUsername()));
            client.sendClientsList(clients);
        }
    }

    public synchronized void broadcastClientDisconnected(ClientHandler sender) throws IOException {
        for (ClientHandler client : clients) {
            if (client == sender) {
                continue;
            }
            client.sendServerMessage(String.format("%s отключился", sender.getUsername()));
            client.sendClientsList(clients);

        }
    }
}
