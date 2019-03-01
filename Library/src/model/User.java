package model;

import java.io.Serializable;

public class User implements Serializable {

    private int id;
    private String username;
    private String ip;

    public User() {
    }

    public User(int id) {
        this.id = id;
    }

    public User(String username, String ip) {
        this.username = username;
        this.ip = ip;
    }

    public User(int id, String username, String ip) {
        this.id = id;
        this.username = username;
        this.ip = ip;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return this.username;
    }

}
