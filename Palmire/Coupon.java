import java.io.*;
import java.util.*;

public class Coupon {
    private String code;
    private String discount; // e.g. "10%"

    private static final String COUPON_FILE = "coupon.txt";

    public Coupon() {
        // default constructor
    }

    public Coupon(String code, String discount) {
        this.code = code;
        this.discount = discount;
    }

    /**
     * Generate a random coupon code (e.g., 8 uppercase letters/digits).
     */
    public String generateCouponCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * Add coupon as admin: append to coupon.txt if discount non-empty and valid
     * format.
     */
    public void addCouponAsAdmin(Coupon c) {
        Scanner sc = new Scanner(System.in);
        String code = c.code;
        String disc = c.discount == null ? "" : c.discount.trim();

        while (true) {
            // 1) Empty check
            if (disc.isEmpty()) {
                System.out.print("❌ Discount cannot be empty. Enter discount with '%' or '0' to cancel: ");
                disc = sc.nextLine().trim();
                if (disc.equals("0")) {
                    System.out.println("🛑 Coupon addition cancelled.");
                    return;
                }
                continue;
            }

            // 2) Must end with '%'
            if (!disc.endsWith("%")) {
                System.out.print("❌ Discount must end with '%'. Enter discount with '%' or '0' to cancel: ");
                disc = sc.nextLine().trim();
                if (disc.equals("0")) {
                    System.out.println("🛑 Coupon addition cancelled.");
                    return;
                }
                continue;
            }

            // 3) Numeric part positive integer
            String numPart = disc.substring(0, disc.length() - 1).trim();
            try {
                int pct = Integer.parseInt(numPart);
                if (pct <= 0) {
                    System.out.print(
                            "❌ Discount percentage must be positive. Enter discount with '%' or '0' to cancel: ");
                    disc = sc.nextLine().trim();
                    if (disc.equals("0")) {
                        System.out.println("🛑 Coupon addition cancelled.");
                        return;
                    }
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.print("❌ Invalid discount percentage format. Enter discount with '%' or '0' to cancel: ");
                disc = sc.nextLine().trim();
                if (disc.equals("0")) {
                    System.out.println("🛑 Coupon addition cancelled.");
                    return;
                }
                continue;
            }

            // If we reach here, disc is valid
            break;
        }

        // Finally append the valid coupon
        ensureFileExists();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(COUPON_FILE, true))) {
            bw.write(code + "," + disc);
            bw.newLine();
            System.out.println("✅ Coupon added: " + code + " => " + disc);
        } catch (IOException e) {
            System.out.println("❌ Error writing to coupon file: " + e.getMessage());
        }
    }

    /**
     * Display all coupons from coupon.txt.
     */
    public void displayCoupons() {
        ensureFileExists();
        System.out.println("\n--- Available Coupons ---");
        boolean any = false;
        try (BufferedReader br = new BufferedReader(new FileReader(COUPON_FILE))) {
            String ln;
            while ((ln = br.readLine()) != null) {
                String[] parts = ln.split(",", -1);
                if (parts.length >= 2) {
                    any = true;
                    System.out.printf("- Code: %s, Discount: %s%n", parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading coupon file: " + e.getMessage());
            return;
        }
        if (!any) {
            System.out.println("(no coupons available)");
        }
    }

    /**
     * Remove a coupon as admin: display all, prompt code, remove if exists.
     */
    public void removeCouponAsAdmin() {
        ensureFileExists();
        List<String> allLines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(COUPON_FILE))) {
            String ln;
            while ((ln = br.readLine()) != null) {
                allLines.add(ln);
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading coupon file: " + e.getMessage());
            return;
        }
        if (allLines.isEmpty()) {
            System.out.println("No coupons to remove.");
            return;
        }
        // Display coupons
        System.out.println("\n--- Coupons ---");
        int idx = 1;
        for (String ln : allLines) {
            String[] parts = ln.split(",", -1);
            if (parts.length >= 2) {
                System.out.printf("%d. Code: %s, Discount: %s%n", idx++, parts[0], parts[1]);
            }
        }
        System.out.print("Enter coupon code to remove (or blank to cancel): ");
        Scanner sc = new Scanner(System.in);
        String codeToRemove = sc.nextLine().trim();
        if (codeToRemove.isEmpty()) {
            System.out.println("Removal cancelled.");
            return;
        }
        boolean found = false;
        List<String> updated = new ArrayList<>();
        for (String ln : allLines) {
            String[] parts = ln.split(",", -1);
            if (parts.length >= 2 && parts[0].equalsIgnoreCase(codeToRemove)) {
                found = true;
                continue; // skip
            }
            updated.add(ln);
        }
        if (!found) {
            System.out.println("❌ Coupon code not found.");
            return;
        }
        // Write back updated list
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(COUPON_FILE))) {
            for (String ln : updated) {
                bw.write(ln);
                bw.newLine();
            }
            System.out.println("✅ Coupon removed: " + codeToRemove);
        } catch (IOException e) {
            System.out.println("❌ Error writing coupon file: " + e.getMessage());
        }
    }

    /**
     * Check if a coupon code exists; return discount string if found, otherwise
     * null.
     */
    public String getDiscountForCode(String code) {
        ensureFileExists();
        try (BufferedReader br = new BufferedReader(new FileReader(COUPON_FILE))) {
            String ln;
            while ((ln = br.readLine()) != null) {
                String[] parts = ln.split(",", -1);
                if (parts.length >= 2 && parts[0].equalsIgnoreCase(code.trim())) {
                    return parts[1]; // e.g. "10%"
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading coupon file: " + e.getMessage());
        }
        return null;
    }

    /**
     * Remove a coupon by code (used when customer applies it).
     * Returns true if removed, false if not found or error.
     */
    public boolean removeCouponCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        ensureFileExists();
        List<String> allLines = new ArrayList<>();
        boolean found = false;
        try (BufferedReader br = new BufferedReader(new FileReader(COUPON_FILE))) {
            String ln;
            while ((ln = br.readLine()) != null) {
                String[] parts = ln.split(",", -1);
                if (parts.length >= 2 && parts[0].equalsIgnoreCase(code.trim())) {
                    found = true;
                    continue; // skip this line
                }
                allLines.add(ln);
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading coupon file: " + e.getMessage());
            return false;
        }
        if (!found) {
            return false;
        }
        // Write back updated list
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(COUPON_FILE))) {
            for (String ln : allLines) {
                bw.write(ln);
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            System.out.println("❌ Error writing coupon file: " + e.getMessage());
            return false;
        }
    }

    private void ensureFileExists() {
        File f = new File(COUPON_FILE);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                System.out.println("❌ Error creating coupon file: " + e.getMessage());
            }
        }
    }
}