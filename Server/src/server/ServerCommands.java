package server;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import dao.DaoUser;
import model.User;
import networking.Command;
import networking.Protocol;

public class ServerCommands {

    private static final String URL_PARAMS = "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/chat" + ServerCommands.URL_PARAMS;
    private final Random random = new Random();
    private Connection connection;

    public ServerCommands() {
        try {
            this.connection = DriverManager.getConnection(DB_URL, "root", "");
        } catch (SQLException ex) {
            Logger.getLogger(ServerCommands.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String createSalt() {
        byte[] bytes = new byte[16];
        this.random.nextBytes(bytes);
        String res = Base64.getEncoder().encodeToString(bytes);
        return res.substring(0, 20);
    }

    @Command
    private void loginUser(Socket socket, String username, String password, String lastIp) throws IOException {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

            String sql = "SELECT * FROM users WHERE username LIKE ?";
            try (PreparedStatement prepareStatement = this.connection.prepareStatement(sql)) {
                prepareStatement.setString(1, username);
                try (ResultSet res = prepareStatement.executeQuery()) {
                    if (!res.next()) {
                        return;
                    }
                    String salt = res.getString("salt");
                    password += salt;
                    byte[] passwordShClient = messageDigest.digest(password.getBytes());
                    byte[] passwordSh = res.getBytes("password");

                    boolean match = Arrays.equals(passwordSh, passwordShClient);

                    if (match) {
                        Integer result = res.getInt("id");
                        Protocol.sendResult(socket.getOutputStream(), result);
                        
                        sql = "UPDATE users SET last_ip = ? WHERE id = ?";
                        try (PreparedStatement updateStatement = this.connection.prepareStatement(sql)) {
                            updateStatement.setString(1, lastIp);
                            updateStatement.setInt(2, result);
                            
                            updateStatement.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException | NoSuchAlgorithmException ex) {
            Logger.getLogger(ServerCommands.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Command
    private void createUser(Socket socket, String username, String password) throws IOException {
        try {
            String sql = "SELECT * FROM users WHERE username LIKE ?";
            try (PreparedStatement prepareStatement = this.connection.prepareStatement(sql)) {
                prepareStatement.setString(1, username);
                try (ResultSet rs = prepareStatement.executeQuery()) {
                    if (rs.next()) {
                        socket.getOutputStream().write(0);
                    }
                }
            }

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            String salt = this.createSalt();
            password += salt;
            byte[] passwordSh = messageDigest.digest(password.getBytes());

            try (PreparedStatement prepareStatement = this.connection.prepareStatement("INSERT INTO users (username, password, salt) VALUES (?, ?, ?)")) {
                prepareStatement.setString(1, username);
                prepareStatement.setBlob(2, new ByteArrayInputStream(passwordSh));
                prepareStatement.setString(3, salt);
                boolean result = prepareStatement.execute();

                socket.getOutputStream().write(result ? 1 : 0);
            }
        } catch (SQLException | NoSuchAlgorithmException ex) {
            Logger.getLogger(ServerCommands.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Command
    private void searchUser(Socket socket, String query) throws IOException {
        String sql = "SELECT id, username, ip FROM users WHERE username LIKE ?";
        if (query == null) {
            query = "%%";
        } else {
            query = '%' + query + '%';
        }

        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setString(1, query);

            try (ResultSet res = statement.executeQuery()) {
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                while (res.next()) {
                    User user = new User();
                    user.setUsername(res.getString("username"));
                    user.setId(res.getInt("id"));
                    user.setLastIp(res.getString("ip"));
                    oos.writeUnshared(user);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ServerCommands.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Command
    private void getUsers(Socket socket, String username) throws IOException {
        Protocol.sendResult(socket.getOutputStream(), DaoUser.readUsers(connection, username));
    }

    @Command
    private void getMessages(Socket socket, Integer id_from, Integer id_to) throws IOException {
        Protocol.sendResult(socket.getOutputStream(), DaoUser.readMessages(connection, id_from, id_to));
    }

    public static void main(String[] args) throws IOException {
        Socket testSocket = new Socket() {
            @Override
            public InputStream getInputStream() throws IOException {
                return new InputStream() {
                    @Override
                    public int read() throws IOException {
                        return 0;
                    }
                };
            }

            @Override
            public OutputStream getOutputStream() throws IOException {
                return new OutputStream() {
                    @Override
                    public void write(int b) throws IOException {
                        System.out.println(b);
                    }
                };
            }
        };
        ServerCommands sc = new ServerCommands();
        sc.loginUser(testSocket, "test", "test", "localhost");
    }
}
