package model;

import java.io.Serializable;

public class User implements Serializable {

    private int id;
    private String username;
    private String lastIp;

    public User() {
    }

    public User(int id) {
        this.id = id;
    }

    public User(int id, String username, String lastIp) {
        this.id = id;
        this.username = username;
        this.lastIp = lastIp;
    }

    public User(String username, String lastIp) {
        this.username = username;
        this.lastIp = lastIp;
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

    public String getLastIp() {
        return lastIp;
    }

    public void setLastIp(String lastIp) {
        this.lastIp = lastIp;
    }

    @Override
    public String toString() {
        return this.username;
    }

}
