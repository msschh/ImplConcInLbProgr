package dao;

import model.Message;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DaoMessage {

    DBOperations dbOperations;
    Connection con;

    public DaoMessage(Connection con, DBOperations dbOperations) {
        this.con = con;
        this.dbOperations = dbOperations;
    }

    public List<Message> readMessages(Integer id_from, Integer id_to) {
        if (con == null || id_from == null || id_to == null) {
            return null;
        }
        List<Message> messageList = new ArrayList<>();
        String sql = "SELECT * FROM MESSAGES WHERE (id_to = ? AND id_from = ?) OR (id_to = ? AND id_from = ?) ORDER BY date DESC";
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

    public void writeMessage(Message message) {
        if (message == null) {
            return;
        }
        dbOperations.writeDB(msg -> {
            String sql = "INSERT INTO messages(`id_from`, `id_to`, `message`, `date`) values (?, ?, ?, ?)";
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
        }, message);

    }
}
