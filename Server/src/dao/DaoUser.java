package dao;

import model.User;
import server.ServerCommands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DaoUser {

    public static List<User> readUsers(Connection con, String username) {
        if (con == null) {
            return null;
        }
        List<User> userList = new ArrayList<>();
        String sql;
        if (username == null) {
            sql = "SELECT * FROM users";
        } else {
            sql = "SELECT * FROM users WHERE username LIKE ?";
        }
        try (PreparedStatement prepareStatement = con.prepareStatement(sql)) {
            if (username != null) {
                prepareStatement.setString(1, username + "%");
            }
            try (ResultSet res = prepareStatement.executeQuery()) {
                User user;
                while (res.next()) {
                    user = new User();
                    user.setId(Integer.parseInt(res.getString("id")));
                    user.setUsername(res.getString("username"));
                    userList.add(user);
                }
            }
        }
        catch (Exception ex) {
            Logger.getLogger(DaoUser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userList;
    }
}
