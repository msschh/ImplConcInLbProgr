package dao;

import model.Message;
import model.User;
import server.ServerCommands;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
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

    public static List<Message> readMessages(Connection con, Integer id_from, Integer id_to) {
        if (con == null || id_from == null || id_to == null) {
            return null;
        }
        List<Message> messageList = new ArrayList<>();
        String sql = "SELECT * FROM MESSAGES WHERE (id_to = ? OR id_from = ?) OR (id_to = ? AND id_from = ?) ORDER BY date DESC";
        try (PreparedStatement prepareStatement = con.prepareStatement(sql)) {
            prepareStatement.setInt(1, id_from);
            prepareStatement.setInt(2, id_to);
            prepareStatement.setInt(3, id_to);
            prepareStatement.setInt(4, id_from);
            try (ResultSet res = prepareStatement.executeQuery()) {
                Message message;
                while (res.next()) {
                    message = new Message();
                    message.setId(res.getInt("id"));
                    message.setIdFrom(res.getInt("id_from"));
                    message.setIdTo(res.getInt("id_to"));
                    message.setMessage(res.getString("message"));
                    message.setDate(res.getDate("date"));
                    messageList.add(message);
                }
            }
        }
        catch (Exception ex) {
            Logger.getLogger(DaoUser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return messageList;
    }

    public static void writeMessage(Connection con, Message message) {
        if (message == null) {
            return;
        }
        String sql = "INSERT INTO messages('ID_FROM', 'ID_TO', 'MESSAGE', 'DATE') values (?, ?, ?, ?)";
        try (PreparedStatement prepareStatement = con.prepareStatement(sql)) {
            prepareStatement.setInt(1, message.getIdFrom());
            prepareStatement.setInt(2, message.getIdTo());
            prepareStatement.setString(3, message.getMessage());
            Calendar currenttime = Calendar.getInstance();
            prepareStatement.setDate(4, new Date((currenttime.getTime()).getTime()));

            prepareStatement.executeUpdate();
        } catch (Exception ex) {
            Logger.getLogger(DaoUser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
