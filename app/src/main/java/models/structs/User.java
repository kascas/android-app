package models.structs;

public class User {
    private String user;
    private String passwd;

    public User(String user, String passwd) {
        this.user = user;
        this.passwd = passwd;
    }

    public String getUser() {
        return user;
    }

    public String getPasswd() {
        return passwd;
    }
}
