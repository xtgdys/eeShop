package main.dao;

import main.domain.User;
import org.apache.commons.dbutils.QueryRunner;
import main.utils.DataSourceUtils;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;

public class UserDao {
    private final QueryRunner qr =
            new QueryRunner(DataSourceUtils.getDataSource());

    public int regist(User user) {
        if (user.getName() == null) return 0;
        String sql = "INSERT INTO user VALUES(?,?,?,?,?,?,?,?,?,?)";
        Object[] params = {
                user.getUid(), user.getUsername(),
                user.getPassword(), user.getName(),
                user.getEmail(), user.getTelephone(),
                user.getBirthday(), user.getSex(),
                user.getState(), user.getCode()
        };
        int row = 0;
        try {
            row = qr.update(sql, params);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("User registration failed.");
        }
        return row;
    }

    public void active(String activeCode) {
        String sql = "UPDATE user SET state=1 WHERE code=?";
        try {
            qr.update(sql, activeCode);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkUsername(String username) {
        String sql = "SELECT COUNT(*) FROM user WHERE username=?";
        Long isExist = 0L;
        try {
            isExist = (Long) qr.query(sql, new ScalarHandler(), username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isExist == 0;

    }

    //用户登录的方法
    public User login(String username, String password){
        String sql = "select * from user where username=? and password=?";
        try {
            return qr.query(sql, new BeanHandler<>(User.class), username,password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
