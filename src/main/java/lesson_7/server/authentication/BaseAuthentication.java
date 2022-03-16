package lesson_7.server.authentication;

import lesson_7.server.models.User;

import java.util.List;

public class BaseAuthentication implements AuthenticationService {

    private static final List<User> clients = List.of(
            new User("martin", "1111", "Martin_Cat"),
            new User("batmen", "2222", "Брюс_Уэйн"),
            new User("gena", "3333", "Гендельф_Серый")
    );

    @Override
    public String getUserNameByLoginAndPassword(String login, String password) {
        for (User client : clients) {
            if (client.getLogin().equals(login) && client.getPassword().equals(password)) {
                return client.getUsername();
            }
        }
        return null;
    }


    @Override
    public void startAuthentication() {
        System.out.println("Старт аутентификации");
    }

    @Override
    public void endAuthentication() {
        System.out.println("Конец аутентификации");
    }
}
