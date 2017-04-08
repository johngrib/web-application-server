package model;

public class User {
    final private String userId;
    final private String password;
    final private String name;
    final private String email;

    public User(final String userId, final String password, final String name, final String email) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public User(final String userId, final String password) {
        this.userId = userId;
        this.password = password;
        this.name = null;
        this.email = null;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", password=" + password + ", name=" + name + ", email=" + email + "]";
    }
}
