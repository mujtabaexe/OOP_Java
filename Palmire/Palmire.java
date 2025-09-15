import java.io.*;
import java.util.*;

public class Palmire {

    private static final String LOGIN_FILE = "login.txt";
    private static final String PHYSICAL_PRODUCT_FILE = "physicalProducts.txt";
    private static final String DIGITAL_PRODUCT_FILE = "digitalProducts.txt";
    private static final String COUPON_FILE = "coupon.txt";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Welcome to Palmire E‑Commerce System ===");
        ensureFileExists(LOGIN_FILE);
        ensureFileExists(PHYSICAL_PRODUCT_FILE);
        ensureFileExists(DIGITAL_PRODUCT_FILE);
        ensureFileExists(COUPON_FILE);
        ensureFileExists("wishlist.txt");
        ensureFileExists("card.txt");

        boolean running = true;
        while (running) {
            System.out.println("\nMain Menu:");
            System.out.println("1. Signup");
            System.out.println("2. Login");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            int choice = safeIntInput(sc);
            sc.nextLine();

            switch (choice) {
                case 1 -> signupFlow(sc);
                case 2 -> login(sc);
                case 0 -> {
                    System.out.println("Exiting program. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Please enter 0–2.");
            }
        }

        sc.close();
    }

    private static void ensureFileExists(String fileName) {
        File f = new File(fileName);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                System.out.println("Error creating file '" + fileName + "': " + e.getMessage());
            }
        }
    }

    private static void signupFlow(Scanner sc) {
        System.out.println("\n=== Signup ===");
        System.out.print("Enter Gmail: ");
        String firstInput = sc.nextLine().trim();

        if (firstInput.equalsIgnoreCase("adminexe")) {
            System.out.println("✅ Admin signup selected.");
            signupAsAdmin(sc);
        } else {
            String email = firstInput;
            if (!email.toLowerCase().endsWith("@gmail.com")) {
                System.out.println("❌ Invalid email format. Must be a Gmail address.");
                return;
            }
            if (isEmailRegistered(email)) {
                System.out.println("❌ Email already registered. Please login or use another email.");
                return;
            }
            signupAsCustomerWithEmail(sc, email);
        }
    }

    private static void signupAsAdmin(Scanner sc) {
        System.out.println("\n=== Admin Signup ===");
        String email;
        while (true) {
            System.out.print("Enter Gmail address for admin account: ");
            email = sc.nextLine().trim();
            if (!email.toLowerCase().endsWith("@gmail.com")) {
                System.out.println("❌ Invalid email format. Must be a Gmail address.");
                continue;
            }
            if (isEmailRegistered(email)) {
                System.out.println("❌ Email already registered. Please login or use another email.");
                return;
            }
            break;
        }

        String username;
        while (true) {
            System.out.print("Enter username (cannot be empty): ");
            username = sc.nextLine().trim();
            if (username.isEmpty()) {
                System.out.println("❌ Username cannot be empty. Please enter a valid username.");
                continue;
            }
            break;
        }

        String password;
        while (true) {
            System.out.print("Enter password (cannot be empty): ");
            password = sc.nextLine();
            if (password.trim().isEmpty()) {
                System.out.println("❌ Password cannot be empty. Please enter a valid password.");
                continue;
            }
            break;
        }

        String role;
        while (true) {
            System.out.print("Enter role ('admin' or 'superadmin'): ");
            role = sc.nextLine().trim().toLowerCase();
            if (role.equals("admin") || role.equals("superadmin"))
                break;
            System.out.println("Invalid role. Must be 'admin' or 'superadmin'.");
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOGIN_FILE, true))) {
            String line = String.join(",", email, username, password, role);
            bw.write(line);
            bw.newLine();
            System.out.println("✅ Admin signup successful as '" + role + "'.");
        } catch (IOException e) {
            System.out.println("❌ Error writing to login file: " + e.getMessage());
        }
    }

    private static void signupAsCustomerWithEmail(Scanner sc, String email) {

        String username;
        while (true) {
            System.out.print("Enter username (cannot be empty): ");
            username = sc.nextLine().trim();
            if (username.isEmpty()) {
                System.out.println("❌ Username cannot be empty. Please enter a valid username.");
                continue;
            }
            break;
        }

        String password;
        while (true) {
            System.out.print("Enter password (cannot be empty): ");
            password = sc.nextLine();
            if (password.trim().isEmpty()) {
                System.out.println("❌ Password cannot be empty. Please enter a valid password.");
                continue;
            }
            break;
        }

        System.out.print("Enter address without commas: ");
        String address = sc.nextLine().trim();
        System.out.print("Enter phone number: ");
        String phone = sc.nextLine().trim();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOGIN_FILE, true))) {
            String line = String.join(",", email, username, password, address, phone);
            bw.write(line);
            bw.newLine();
            System.out.println("✅ Customer signup successful.");
        } catch (IOException e) {
            System.out.println("❌ Error writing to login file: " + e.getMessage());
        }
    }

    private static boolean isEmailRegistered(String email) {
        try (BufferedReader br = new BufferedReader(new FileReader(LOGIN_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.split(",", -1)[0].equalsIgnoreCase(email)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading login file: " + e.getMessage());
        }
        return false;
    }

    private static void login(Scanner sc) {
        System.out.println("\n=== Login ===");
        while (true) {
            System.out.print("Enter email (or '0' to go back): ");
            String email = sc.nextLine().trim();
            if (email.equals("0")) {
                System.out.println("Returning to main menu.");
                return;
            }
            System.out.print("Enter password: ");
            String password = sc.nextLine().trim();

            String role = verifyCredentials(email, password);
            if (role.equals("invalid")) {
                System.out.println("❌ Invalid credentials. Try again.");
                continue;
            }

            System.out.println("✅ Logged in as: " + role);
            switch (role) {
                case "superadmin" -> superAdminMenu(sc);
                case "admin" -> adminMenu(sc);
                default -> customerMenu(sc, email);
            }
            return;
        }
    }

    private static String verifyCredentials(String email, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(LOGIN_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts[0].equalsIgnoreCase(email) && parts[2].equals(password)) {
                    if (parts.length == 4)
                        return parts[3].toLowerCase(); // admin or superadmin
                    if (parts.length >= 5)
                        return "customer"; // customer
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading login file: " + e.getMessage());
        }
        return "invalid";
    }

    private static void superAdminMenu(Scanner sc) {
        Inventory inv = new Inventory();
        Coupon coupon = new Coupon();
        while (true) {
            System.out.println("\n--- SuperAdmin Menu ---");
            System.out.println("1. Add Product");
            System.out.println("2. Remove Product");
            System.out.println("3. Update Product");
            System.out.println("4. Display Products");
            System.out.println("5. Add Coupon");
            System.out.println("6. View Coupons");
            System.out.println("7. Remove Coupon");
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");
            int choice = safeIntInput(sc);
            sc.nextLine();

            switch (choice) {
                case 1 -> addProductFlow(inv, sc);
                case 2 -> inv.removeProductAsAdmin();
                case 3 -> inv.updateProductAsAdmin();
                case 4 -> {
                    inv.displayPhysicalProducts(PHYSICAL_PRODUCT_FILE);
                    inv.displayDigitalProducts(DIGITAL_PRODUCT_FILE);
                }
                case 5 -> {
                    System.out.print("Enter coupon code (or blank to auto‑gen): ");
                    String code = sc.nextLine().trim();
                    if (code.isEmpty()) {
                        code = coupon.generateCouponCode();
                        System.out.println("Generated coupon code: " + code);
                    }
                    System.out.print("Enter discount (e.g. 10%): ");
                    String disc = sc.nextLine().trim();
                    coupon.addCouponAsAdmin(new Coupon(code, disc));
                }
                case 6 -> coupon.displayCoupons();
                case 7 -> coupon.removeCouponAsAdmin();
                case 0 -> {
                    System.out.println("SuperAdmin logged out.");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void adminMenu(Scanner sc) {
        Inventory inv = new Inventory();
        Coupon couponMgr = new Coupon();
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Add Product");
            System.out.println("2. Update Product");
            System.out.println("3. Display Products");
            System.out.println("4. Add Coupon");
            System.out.println("5. View Coupons");
            System.out.println("6. Remove Coupon");
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");
            int choice = safeIntInput(sc);
            sc.nextLine();

            switch (choice) {
                case 1 -> addProductFlow(inv, sc);
                case 2 -> inv.updateProductAsAdmin();
                case 3 -> {
                    inv.displayPhysicalProducts(PHYSICAL_PRODUCT_FILE);
                    inv.displayDigitalProducts(DIGITAL_PRODUCT_FILE);
                }
                case 4 -> {
                    System.out.print("Enter coupon code (or blank to auto‑gen): ");
                    String code = sc.nextLine().trim();
                    if (code.isEmpty()) {
                        code = couponMgr.generateCouponCode();
                        System.out.println("Generated coupon code: " + code);
                    }
                    System.out.print("Enter discount (e.g. 15%): ");
                    String disc = sc.nextLine().trim();
                    couponMgr.addCouponAsAdmin(new Coupon(code, disc));
                }
                case 5 -> couponMgr.displayCoupons();
                case 6 -> couponMgr.removeCouponAsAdmin();
                case 0 -> {
                    System.out.println("Admin logged out.");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void customerMenu(Scanner sc, String email) {
        Inventory inv = new Inventory();
        Cart cart = new Cart();
        Wishlist wishlist = new Wishlist();
        cart.setUserEmail(email);
        String address = fetchCustomerAddress(email);
        String username = fetchCustomerUsername(email);

        while (true) {
            System.out.println("\n--- Customer Menu ---");
            System.out.println("1. View Products");
            System.out.println("2. Add to Cart");
            System.out.println("3. Remove from Cart");
            System.out.println("4. View Cart");
            System.out.println("5. Checkout");
            System.out.println("6. Wishlist");
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");
            int choice = safeIntInput(sc);
            sc.nextLine();

            switch (choice) {
                case 1 -> {
                    inv.displayPhysicalProducts(PHYSICAL_PRODUCT_FILE);
                    inv.displayDigitalProducts(DIGITAL_PRODUCT_FILE);
                }
                case 2 -> cart.addToCart(inv);
                case 3 -> cart.removeFromCart();
                case 4 -> cart.displayCart();
                case 5 -> {
                    cart.displayCart();
                    // if empty, go back immediately
                    if (cart.isEmpty()) {
                        System.out.println("Nothing to checkout.");
                        break;
                    }
                    System.out.println("Deliver to: " + (address.isEmpty() ? "(no address)" : address));
                    System.out.print("Confirm checkout? (1=Yes,0=No): ");
                    if (safeIntInput(sc) == 1) {
                        cart.checkout();
                        address = fetchCustomerAddress(email);
                    } else {
                        System.out.println("Checkout cancelled.");
                        sc.nextLine();
                    }
                }

                case 6 -> {
                    while (true) {
                        System.out.println("\n--- Wishlist Menu ---");
                        System.out.println("1. View Wishlist");
                        System.out.println("2. Add to Wishlist");
                        System.out.println("3. Remove from Wishlist");
                        System.out.println("0. Back");
                        System.out.print("Enter choice: ");
                        int wch = safeIntInput(sc);
                        sc.nextLine();
                        if (wch == 0)
                            break;
                        switch (wch) {
                            case 1 -> wishlist.viewWishlist(inv, username);
                            case 2 -> wishlist.addToWishlist(inv, username);
                            case 3 -> wishlist.removeFromWishlist(username);
                            default -> System.out.println("Invalid choice.");
                        }
                    }
                }
                case 0 -> {
                    // Restore any items in cart back to inventory since user is logging out without
                    // checkout
                    cart.restoreStockOnLogout();
                    System.out.println("Customer logged out.");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static String fetchCustomerAddress(String email) {
        try (BufferedReader br = new BufferedReader(new FileReader(LOGIN_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length >= 5 && p[0].equalsIgnoreCase(email)) {
                    return p[3];
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading login file for address: " + e.getMessage());
        }
        return "";
    }

    private static String fetchCustomerUsername(String email) {
        try (BufferedReader br = new BufferedReader(new FileReader(LOGIN_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length >= 5 && p[0].equalsIgnoreCase(email)) {
                    return p[1];
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading login file for username: " + e.getMessage());
        }
        return "";
    }

    private static void addProductFlow(Inventory inv, Scanner sc) {
        System.out.println("Add Product:");
        System.out.println("1. Physical Product");
        System.out.println("2. Digital Product");
        System.out.print("Select type: ");
        int t = safeIntInput(sc);
        sc.nextLine();

        switch (t) {
            case 1 -> inv.addProductAsAdmin(new PhysicalProduct(), 1);
            case 2 -> inv.addProductAsAdmin(new DigitalProduct(), 2);
            default -> System.out.println("Invalid selection.");
        }
    }

    private static int safeIntInput(Scanner sc) {
        while (!sc.hasNextInt()) {
            System.out.print("Invalid input. Enter an integer: ");
            sc.next();
        }
        return sc.nextInt();
    }
}