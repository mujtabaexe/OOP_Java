import java.io.*;
import java.util.*;

public class Customer extends User {
    private String address;
    private String phoneNumber;
    private List<String> wishlist = new ArrayList<>();
    private static final String LOGIN_FILE    = "login.txt";
    private static final String WISHLIST_FILE = "wishlist.txt";
    private final Scanner sc = new Scanner(System.in);

    public Customer(String email, String username, String password,
                    String address, String phoneNumber) {
        super(email, username, password);
        this.address     = address;
        this.phoneNumber = phoneNumber;
    }

    public String getAddress()    { return address;    }
    public void setAddress(String a)    { this.address = a; }
    public String getPhoneNumber() { return phoneNumber;}
    public void setPhoneNumber(String p) { this.phoneNumber = p; }

    @Override
    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOGIN_FILE, true))) {
            String line = String.join(",",
                getEmail(), getUsername(), getPassword(),
                address, phoneNumber
            );
            bw.write(line);
            bw.newLine();
            System.out.println("Customer saved to login.txt");
        } catch (IOException e) {
            System.out.println("Error writing login.txt: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteAccount() {
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

    /** Load wishlist into memory (if needed on startup). */
    public void loadWishlist() {
        wishlist.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(WISHLIST_FILE))) {
            String ln;
            while ((ln = br.readLine()) != null) {
                wishlist.add(ln);
            }
        } catch (IOException ignored) {}
    }

    /** Append a product to this customer’s wishlist file. */
    public void addProductToWishlist(Product p) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(WISHLIST_FILE, true))) {
            bw.write(p.getProductName());
            bw.newLine();
            wishlist.add(p.getProductName());
            System.out.println("Added to wishlist.");
        } catch (IOException e) {
            System.out.println("Error writing wishlist.txt: " + e.getMessage());
        }
    }

    /** Remove a product from wishlist (memory + file). */
    public void removeProductFromWishList() {
        System.out.print("Enter product name to remove: ");
        String name = sc.next();
        if (!wishlist.remove(name)) {
            System.out.println("Not in wishlist.");
            return;
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(WISHLIST_FILE))) {
            for (String item : wishlist) {
                bw.write(item);
                bw.newLine();
            }
            System.out.println("Removed from wishlist.");
        } catch (IOException e) {
            System.out.println("Error writing wishlist.txt: " + e.getMessage());
        }
    }

    /** Display in‑memory wishlist. */
    public void displayWishlist() {
        if (wishlist.isEmpty()) {
            System.out.println("Wishlist is empty.");
            return;
        }
        System.out.println("Your Wishlist:");
        for (int i = 0; i < wishlist.size(); i++) {
            System.out.printf("%d. %s%n", i+1, wishlist.get(i));
        }
    }
}


// import java.io.*;
// import java.util.*;

// /**
//  * Each customer has their own wishlist file named "wishlist_<username>.txt".
//  * Customer login data is stored/appended in "customer.txt".
//  */
// public class Customer extends User {

//     Scanner sc = new Scanner(System.in);
//     private String address;
//     private String phoneNumber;
//     private final List<String> wishlist = new ArrayList<>();

//     private static final String CUSTOMER_FILE = "customer.txt";
//     private final String wishlistFile;

//     public Customer(String email, String username, String password,
//             String address, String phoneNumber) {
//         super(email, username, password);
//         this.address = address;
//         this.phoneNumber = phoneNumber;
//         this.wishlistFile = "wishlist_" + getUsername() + ".txt";
//         loadWishlist();
//     }

//     // ─── Account Persistence ──────────────────────────────────────────────
//     /**
//      * Append this customer’s record to customer.txt
//      */
//     @Override
//     public void saveToFile() {

//         try (BufferedReader br = new BufferedReader(new FileReader(CUSTOMER_FILE))) {
//             String ln;
//             while ((ln = br.readLine()) != null) {
//                 if (ln.split(",", -1)[0].equalsIgnoreCase(getEmail())) {
//                     System.out.println("⚠️ Customer already exists; skip save.");
//                     return;
//                 }
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//         }

//         // ➋ original append logic
//         try (BufferedWriter bw = new BufferedWriter(new FileWriter(CUSTOMER_FILE, true))) {
//             String line = String.join(",",
//                     getEmail(), getUsername(), getPassword(), address, phoneNumber
//             );
//             bw.write(line);
//             bw.newLine();
//             System.out.println("✅ Customer saved to file.");
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     /**
//      * Delete this customer’s line (matched by email) from customer.txt
//      */
//     public boolean deleteAccount() {
//         List<String> all = new ArrayList<>();
//         boolean found = false;

//         try (BufferedReader br = new BufferedReader(
//                 new FileReader(CUSTOMER_FILE))) {
//             String ln;
//             while ((ln = br.readLine()) != null) {
//                 String[] parts = ln.split(",", -1);
//                 if (parts.length >= 1 && parts[0].equalsIgnoreCase(getEmail())) {
//                     found = true;  // skip this line
//                 } else {
//                     all.add(ln);
//                 }
//             }
//         } catch (IOException e) {
//             System.out.println("❌ Error reading customer file: " + e.getMessage());
//             return false;
//         }

//         if (!found) {
//             System.out.println("⚠️ Customer not found for deletion.");
//             return false;
//         }

//         try (BufferedWriter bw = new BufferedWriter(
//                 new FileWriter(CUSTOMER_FILE))) {
//             for (String l : all) {
//                 bw.write(l);
//                 bw.newLine();
//             }
//             System.out.println("✅ Customer account deleted.");
//             return true;
//         } catch (IOException e) {
//             System.out.println("❌ Error writing customer file: " + e.getMessage());
//             return false;
//         }
//     }

//     // ─── Wishlist Management ─────────────────────────────────────────────
//     /**
//      * Load wishlist from file into memory at startup
//      */
//     private void loadWishlist() {
//         wishlist.clear();
//         File f = new File(wishlistFile);
//         if (!f.exists()) {
//             return;  // no wishlist yet
//         }
//         try (BufferedReader br = new BufferedReader(new FileReader(f))) {
//             String ln;
//             while ((ln = br.readLine()) != null) {
//                 if (!ln.trim().isEmpty()) {
//                     wishlist.add(ln.trim());
//                 }
//             }
//         } catch (IOException e) {
//             System.out.println("❌ Error loading wishlist: " + e.getMessage());
//         }
//     }

//     /**
//      * Persist current in‑memory wishlist back to its file
//      */
//     private void saveWishlist() {
//         try (BufferedWriter bw = new BufferedWriter(new FileWriter(wishlistFile))) {
//             for (String prod : wishlist) {
//                 bw.write(prod);
//                 bw.newLine();
//             }
//         } catch (IOException e) {
//             System.out.println("❌ Error saving wishlist: " + e.getMessage());
//         }
//     }

//     /**
//      * Add a product’s name to wishlist (memory + file)
//      */
//     public void addProductToWishlist(Product p) {
//         if (wishlist.contains(p.getProductName())) {
//             System.out.println("⚠️ Already in wishlist.");
//             return;
//         }
//         wishlist.add(p.getProductName());
//         saveWishlist();
//         System.out.println("✅ Added to wishlist: " + p.getProductName());
//     }

//     /**
//      * Remove by name from wishlist (memory + file)
//      */
//     public void removeProductFromWishList() {
//         if (wishlist.isEmpty()) {
//             System.out.println("Wishlist is empty.");
//             return;
//         }
//         displayWishlist();
//         System.out.print("Enter product name to remove: ");
//         String name = sc.nextLine().trim();
//         if (wishlist.removeIf(item -> item.equalsIgnoreCase(name))) {
//             saveWishlist();
//             System.out.println("✅ Removed from wishlist.");
//         } else {
//             System.out.println("⚠️ Product not in wishlist.");
//         }
//     }

//     /**
//      * Display in‑memory wishlist
//      */
//     public void displayWishlist() {
//         System.out.println("\n--- Your Wishlist ---");
//         if (wishlist.isEmpty()) {
//             System.out.println("(empty)");
//             return;
//         }
//         for (int i = 0; i < wishlist.size(); i++) {
//             System.out.printf("%d. %s%n", i + 1, wishlist.get(i));
//         }
//     }

//     // ─── Getters/Setters ─────────────────────────────────────────────────
//     public String getAddress() {
//         return address;
//     }

//     public void setAddress(String a) {
//         address = a;
//     }

//     public String getPhoneNumber() {
//         return phoneNumber;
//     }

//     public void setPhoneNumber(String p) {
//         phoneNumber = p;
//     }
// }

// // // Customer.java
// // import java.io.*;
// // import java.util.*;
// // public class Customer extends User {
// //     private String address;
// //     private String phoneNumber;
// //     private ArrayList<String> wishlist = new ArrayList<>();
// //     private static final String WISHLIST_FILE = "wishlist.txt";
// //     private static final String CUSTOMER_FILE = "customer.txt";
// //     private final Scanner sc = new Scanner(System.in);
// //     public Customer(String email, String username, String password, String address, String phoneNumber) {
// //         super(email, username, password);
// //         this.address = address;
// //         this.phoneNumber = phoneNumber;
// //     }
// //     public String getAddress() {
// //         return address;
// //     }
// //     public void setAddress(String address) {
// //         this.address = address;
// //     }
// //     public String getPhoneNumber() {
// //         return phoneNumber;
// //     }
// //     public void setPhoneNumber(String phoneNumber) {
// //         this.phoneNumber = phoneNumber;
// //     }
// //     public ArrayList<String> getWishlist() {
// //         return wishlist;
// //     }
// //     public void setWishlist(ArrayList<String> wishlist) {
// //         this.wishlist = wishlist;
// //     }
// //     /**
// //      * Append this customer's data to customer.txt in format:
// //      * email,username,password,address,phoneNumber
// //      */
// //     @Override
// //     public void saveToFile() {
// //         try (BufferedWriter bw = new BufferedWriter(new FileWriter(CUSTOMER_FILE, true))) {
// //             String line = String.join(",",
// //                     getEmail(), getUsername(), getPassword(),
// //                     address != null ? address : "",
// //                     phoneNumber != null ? phoneNumber : ""
// //             );
// //             bw.write(line);
// //             bw.newLine();
// //             System.out.println("✅ Customer saved to file.");
// //         } catch (IOException e) {
// //             System.out.println("❌ Error writing customer file: " + e.getMessage());
// //         }
// //     }
// //     /**
// //      * Remove this customer's line (matching email) from customer.txt. Returns
// //      * true if deleted, false otherwise.
// //      */
// //     @Override
// //     public boolean deleteAccount() {
// //         List<String> updated = new ArrayList<>();
// //         boolean found = false;
// //         try (BufferedReader br = new BufferedReader(new FileReader(CUSTOMER_FILE))) {
// //             String line;
// //             while ((line = br.readLine()) != null) {
// //                 String[] parts = line.split(",", -1);
// //                 if (parts.length >= 1 && parts[0].equalsIgnoreCase(getEmail())) {
// //                     found = true; // skip
// //                 } else {
// //                     updated.add(line);
// //                 }
// //             }
// //         } catch (IOException e) {
// //             System.out.println("❌ Error reading customer file: " + e.getMessage());
// //             return false;
// //         }
// //         if (!found) {
// //             System.out.println("⚠️ Customer email not found for deletion.");
// //             return false;
// //         }
// //         try (BufferedWriter bw = new BufferedWriter(new FileWriter(CUSTOMER_FILE))) {
// //             for (String l : updated) {
// //                 bw.write(l);
// //                 bw.newLine();
// //             }
// //             System.out.println("✅ Customer account deleted.");
// //             return true;
// //         } catch (IOException e) {
// //             System.out.println("❌ Error writing customer file: " + e.getMessage());
// //             return false;
// //         }
// //     }
// //     /**
// //      * Add a product name to wishlist.txt (one per line).
// //      */
// //     public void addProductToWishlist(Product p) {
// //         displayWishlist();
// //         try (BufferedWriter bw = new BufferedWriter(new FileWriter(WISHLIST_FILE, true))) {
// //             String line = p.getProductName();
// //             bw.write(line);
// //             bw.newLine();
// //             System.out.println("✅ Product added to wishlist.");
// //         } catch (IOException e) {
// //             System.out.println("❌ Error writing wishlist: " + e.getMessage());
// //         }
// //     }
// //     /**
// //      * Remove a product by name from wishlist.txt.
// //      */
// //     public void removeProductFromWishList() {
// //         displayWishlist();
// //         System.out.print("Enter product name to remove from wishlist: ");
// //         String productName = sc.next();
// //         List<String> updated = new ArrayList<>();
// //         boolean found = false;
// //         try (BufferedReader br = new BufferedReader(new FileReader(WISHLIST_FILE))) {
// //             String line;
// //             while ((line = br.readLine()) != null) {
// //                 if (line.equalsIgnoreCase(productName)) {
// //                     found = true;
// //                 } else {
// //                     updated.add(line);
// //                 }
// //             }
// //         } catch (IOException e) {
// //             System.out.println("❌ Error reading wishlist: " + e.getMessage());
// //             return;
// //         }
// //         if (!found) {
// //             System.out.println("⚠️ Product not found in wishlist.");
// //             return;
// //         }
// //         try (BufferedWriter bw = new BufferedWriter(new FileWriter(WISHLIST_FILE))) {
// //             for (String l : updated) {
// //                 bw.write(l);
// //                 bw.newLine();
// //             }
// //             System.out.println("✅ Product removed from wishlist.");
// //         } catch (IOException e) {
// //             System.out.println("❌ Error writing wishlist: " + e.getMessage());
// //         }
// //     }
// //     /**
// //      * Display wishlist entries from wishlist.txt.
// //      */
// //     public void displayWishlist() {
// //         System.out.println("\nYour Wishlist:");
// //         int row = 1;
// //         try (BufferedReader br = new BufferedReader(new FileReader(WISHLIST_FILE))) {
// //             String line;
// //             while ((line = br.readLine()) != null) {
// //                 System.out.println((row++) + ". " + line);
// //             }
// //             if (row == 1) {
// //                 System.out.println("(empty)");
// //             }
// //         } catch (IOException e) {
// //             System.out.println("❌ Error reading wishlist: " + e.getMessage());
// //         }
// //     }
// //     public void loadWishlist() {
// //         wishlist.clear(); // clear any existing data
// //         try (BufferedReader br = new BufferedReader(new FileReader(wishlistFile))) {
// //             String line;
// //             while ((line = br.readLine()) != null) {
// //                 if (!line.trim().isEmpty()) {
// //                     wishlist.add(line.trim());
// //                 }
// //             }
// //         } catch (IOException e) {
// //             // It's okay if the file doesn't exist (new user)
// //         }
// //     }
// // }
// // import java.io.*;
// // import java.util.ArrayList;
// // import java.util.Scanner;
// // public class Customer extends User {
// //     Scanner sc = new Scanner(System.in);
// //     private String address;
// //     private String phoneNumber;
// //     private ArrayList<String> wishlist = new ArrayList<>();
// //     String wishlistFile = "wishlist.txt";
// //     public Customer(String email, String username, String password, String address, String phoneNumber) {
// //         super(email, username, password);
// //         this.address = address;
// //         this.phoneNumber = phoneNumber;
// //     }
// //     public String getAddress() {
// //         return address;
// //     }
// //     public void setAddress(String address) {
// //         this.address = address;
// //     }
// //     public String getPhoneNumber() {
// //         return phoneNumber;
// //     }
// //     public void setPhoneNumber(String phoneNumber) {
// //         this.phoneNumber = phoneNumber;
// //     }
// //     public ArrayList<String> getWishlist() {
// //         return wishlist;
// //     }
// //     public void setWishlist(ArrayList<String> wishlist) {
// //         this.wishlist = wishlist;
// //     }
// //     public void addProductToWishlist(Product p) {
// //         displayWishlist();
// //         try (BufferedWriter bw = new BufferedWriter(new FileWriter(wishlistFile, true))) {
// //             String line = p.getProductName();
// //             bw.write(line);
// //             bw.newLine();
// //             System.out.print("Product added to wishlist successfully");
// //         } catch (IOException e) {
// //             System.out.println("Error writing product to wishlist: " + e.getMessage());
// //         }
// //     }
// //     public void removeProductFromWishList() {
// //         displayWishlist();
// //         System.out.print("Enter Product Name you want to remove: ");
// //         String productName = sc.next();
// //         ArrayList<String> updatedWishlist = new ArrayList<>();
// //         try (BufferedReader br = new BufferedReader(new FileReader(wishlistFile))) {
// //             String currentLine;
// //             while ((currentLine = br.readLine()) != null) {
// //                 if (!currentLine.equalsIgnoreCase(productName)) {
// //                     updatedWishlist.add(currentLine);
// //                 }
// //             }
// //         } catch (IOException e) {
// //             System.out.println("Error reading file: " + e.getMessage());
// //             return;
// //         }
// //         try (BufferedWriter bw = new BufferedWriter(new FileWriter(wishlistFile))) {
// //             for (String line : updatedWishlist) {
// //                 bw.write(line);
// //                 bw.newLine();
// //             }
// //             System.out.println("Product removed successfully.");
// //         } catch (IOException e) {
// //             System.out.println("Error writing file: " + e.getMessage());
// //         }
// //     }
// //     public void displayWishlist() {
// //         int row = 1;
// //         System.out.println("Your Wishlist:");
// //         try (BufferedReader br = new BufferedReader(new FileReader(wishlistFile))) {
// //             String line;
// //             while ((line = br.readLine()) != null) {
// //                 System.out.println((row++) + ". " + line);
// //             }
// //         } catch (IOException e) {
// //             System.out.println("Error reading wishlist: " + e.getMessage());
// //         }
// //     }

// // }
