package edu.virginia.sde.reviews;

public class User {
    private String username;
    private String password;

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    public User(){}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User temp = (User) o;

        return username.equals(temp.getUsername()) && password.equals(temp.getPassword());
    }

    @Override
    public String toString() {
        return "User" +
                "username=" + username +
                ", password=" + password;
    }
}
