package edu.hitsz.server.dao;

public class UserInfo {
    public String name;
    public String password;
    public int score;
    public UserInfo(String name, String password, int score){
        this.name = name;
        this.password = password;
        this.score = score;
    }

    @Override
    public String toString() {
        return "name: "+name + "; password: " + password + "; score: "+score;
    }

}
