package dao;

import model.Message;
import model.User;
import server.ServerCommands;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DaoUser {

    DBOperations dbOperations;
    Connection con;

    public DaoUser(Connection con, DBOperations dbOperations) {
        this.con = con;
        this.dbOperations = dbOperations;
    }

    public List<User> readUsers(String username) {
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
                    user.setLastIp(res.getString("last_ip"));
                    userList.add(user);
                }
            }
        }
        catch (Exception ex) {
            Logger.getLogger(DaoUser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userList;
    }

    public User searchUser(String query) {
        String sql = "SELECT id, username, ip FROM users WHERE username LIKE ?";
        if (query == null) {
            query = "%%";
        } else {
            query = '%' + query + '%';
        }

        try (PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, query);

            try (ResultSet res = statement.executeQuery()) {
                while (res.next()) {
                    User user = new User();
                    user.setUsername(res.getString("username"));
                    user.setId(res.getInt("id"));
                    user.setLastIp(res.getString("ip"));
                    return user;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DaoUser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
