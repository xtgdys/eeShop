package main.service.impls;

import main.dao.UserDao;
import main.domain.User;

public class UserService {
    private final UserDao dao = new UserDao();
    public boolean regist(User user) {
        int row = dao.regist(user);
        return row > 0;
    }

    public void active(String activeCode) {
        dao.active(activeCode);
    }

    public boolean checkUsername(String username) {
       return dao.checkUsername(username);
    }

    public User login(String username, String password) {
        return dao.login(username, password);
    }
}
