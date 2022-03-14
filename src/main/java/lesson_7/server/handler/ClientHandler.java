package lesson_7.server.handler;

import lesson_7.server.MyServer;
import lesson_7.server.authentication.AuthenticationService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class ClientHandler {

    private final static String AUTH_CMD_PREFIX = "/auth";
    private final static String AUTHOK_CMD_PREFIX = "/authok";
    private final static String AUTHERR_CMD_PREFIX = "/autherr";
    private final static String CLIENT_MSG_CMD_PREFIX = "/cMsg";
    private final static String SERVER_MSG_CMD_PREFIX = "/sMsg";
    private final static String PRIVAT_MSG_CMD_PREFIX = "/pMsg";
    private final static String STOP_SERVER_CMD_PREFIX = "/stop";
    private final static String END_CLIENT_CMD_PREFIX = "/end";


    private MyServer myServer;
    private Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream in;
    private String username;

    public ClientHandler(MyServer myServer, Socket socket) {

        this.myServer = myServer;
        clientSocket = socket;
    }

    public void handle() throws IOException {
        out = new DataOutputStream(clientSocket.getOutputStream());
        in = new DataInputStream(clientSocket.getInputStream());

        new Thread(() -> {
            try {
                authentication();
                readMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void authentication() throws IOException {
        while (true) {
            String message = in.readUTF();
            if (message.startsWith(AUTH_CMD_PREFIX)) {
                boolean isSuccessAuth = processAuthentication(message);
                if (isSuccessAuth) {
                    break;
                }
            } else {
                out.writeUTF(AUTHERR_CMD_PREFIX + " Ошибка аутентификации");
                System.out.println("Неудачная попытка аутентификации");
            }
        }
    }

    private boolean processAuthentication(String message) throws IOException {
        String[] parts = message.split("\\s+");
        if (parts.length != 3) {
            out.writeUTF(AUTHERR_CMD_PREFIX + " Ошибка аутентификации");
        }
        String login = parts[1];
        String password = parts[2];

        AuthenticationService auth = myServer.getAuthenticationService();

        username = auth.getUserNameByLoginAndPassword(login, password);

        if (username != null) {
            if (myServer.isUsernameBusy(username)) { // проверяем есть клиент в чате или нет
                out.writeUTF(AUTHERR_CMD_PREFIX + " Логин уже используется");
                return false;
            }
            out.writeUTF(AUTHOK_CMD_PREFIX + " " + username);
            myServer.subscribe(this);
            System.out.println("Пользователь " + username + " подключился к чату");
            return true;
        } else {
            out.writeUTF(AUTHERR_CMD_PREFIX + " Пароль и логин не соответствуют действительности");
            return false;
        }
    }

    private void readMessage() throws IOException {
        while (true) {
            String message = in.readUTF();
            System.out.println("message | " + username + ": " + message);
            out.writeUTF(message);
            if (message.startsWith(STOP_SERVER_CMD_PREFIX)) {
                System.exit(0);
            } else if (message.startsWith(END_CLIENT_CMD_PREFIX)) {
                return;
            } else if (message.startsWith(PRIVAT_MSG_CMD_PREFIX)) {
                 messagePrivateProcessing(message);
            } else {
                myServer.broadcastMessage(message, this);
            }

        }
    }

    private void  messagePrivateProcessing(String message) throws IOException {
        String[] part = message.split("\\s");
        String nick = part[1];
        for (ClientHandler o : myServer.getClients()) {
            if (o.getUsername().equals(nick)) {
                o.sendPrivatMessage(getUsername(), message);
            }
        }
    }


    public void sendMessage(String sender, String message) throws IOException {

        out.writeUTF(String.format("%s %s %s", CLIENT_MSG_CMD_PREFIX, sender, message));
    }

    public void sendPrivatMessage(String sender, String message) throws IOException {

        out.writeUTF(String.format("%s %s", sender, message));
    }
    
    public String getUsername() {
        return username;
    }
}
