import java.io.*;

public class Admin extends User {
    private String role;    // "admin" or "superadmin"
    private static final String LOGIN_FILE = "login.txt";

    public Admin(String email, String username, String password, String role) {
        super(email, username, password);
        this.role = role;
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @Override
    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOGIN_FILE, true))) {
            String line = String.join(",",
                getEmail(), getUsername(), getPassword(), role
            );
            bw.write(line);
            bw.newLine();
            System.out.println("Admin saved to login.txt");
        } catch (IOException e) {
            System.out.println("Error writing login.txt: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteAccount() {
        // remove any line whose first token matches this.email
        boolean deleted = false;
        try {
            File inFile = new File(LOGIN_FILE);
            File tempFile = new File("login_temp.txt");
            BufferedReader reader = new BufferedReader(new FileReader(inFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String ln;
            while ((ln = reader.readLine()) != null) {
                String[] parts = ln.split(",", -1);
                if (!parts[0].equalsIgnoreCase(getEmail())) {
                    writer.write(ln);
                    writer.newLine();
                } else {
                    deleted = true;
                }
            }
            reader.close();
            writer.close();

            if (!inFile.delete() || !tempFile.renameTo(inFile)) {
                System.out.println("Error finalizing deletion.");
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error deleting account: " + e.getMessage());
            return false;
        }
        return deleted;
    }
}
