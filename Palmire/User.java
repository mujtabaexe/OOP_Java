public abstract class User {
    private String email;
    private String username;
    private String password;

    public User(String email, String username, String password) {
        this.email    = email;
        this.username = username;
        this.password = password;
    }
    public String getEmail()    { return email;    }
    public void setEmail(String e)    { this.email = e; }
    public String getUsername() { return username; }
    public void setUsername(String u) { this.username = u; }
    public String getPassword() { return password; }
    public void setPassword(String p) { this.password = p; }

    /** Save this user’s login (and extra fields) to login.txt. */
    public abstract void saveToFile();
    /** Delete this user’s account from login.txt. */
    public abstract boolean deleteAccount();
}
