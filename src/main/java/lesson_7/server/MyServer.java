package lesson_7.server;

import lesson_7.server.authentication.AuthenticationService;
import lesson_7.server.authentication.BaseAuthentication;
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
        serverSocket = new ServerSocket(port);
        authenticationService = new BaseAuthentication();
        clients = new ArrayList<>();
    }

    public void start() {
        System.out.println("СЕРВЕР ЗАПУЩЕН!");
        System.out.println("---------------");
// цикл ожидания новых пользователей
        try {
            while (true) {
                waitAndProcessNewClientConnection();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void waitAndProcessNewClientConnection() throws IOException {
        System.out.println("Ожидание клиента ...");
        Socket socket = serverSocket.accept(); // ловим клиента
        System.out.println("Клиент подключился!");

        processClientConnection(socket);
    }

    private void processClientConnection(Socket socket) throws IOException {
        ClientHandler handler = new ClientHandler(this, socket);
        handler.handle();
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public synchronized void anSubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public synchronized boolean isUsernameBusy(String username) {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void broadcastMessage(String message, ClientHandler sender) throws IOException {
        for (ClientHandler client : clients) {
            if (client == sender) {
                continue;
            }
            client.sendMessage(sender.getUsername(),message);
        }
    }
}
