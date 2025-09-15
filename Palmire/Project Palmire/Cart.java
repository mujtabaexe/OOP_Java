import java.io.*;
import java.util.*;

public class Cart {
    private final Scanner sc = new Scanner(System.in);

    // File names for inventory
    private final String physicalProductFile = "physicalProducts.txt";
    private final String digitalProductFile  = "digitalProducts.txt";

    // Delivery constants
    private final double deliveryBaseFee = 300;  // base fee
    private final double perKgRate       = 50;   // per kg

    // In-cart items: pairs ["P-ProductName"/"D-ProductName", quantityString]
    private final ArrayList<String> buyingList = new ArrayList<>();
    private double totalPrice  = 0;
    private double totalWeight = 0;

    // User email for address updates in checkout
    private String userEmail;

    public Cart() {
        // default constructor
    }

    /** Setter so ECommerceSystem can set current user email before checkout/delivery updates */
    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    /**
     * Adds a product to cart:
     * - Prompts type, shows inventory via Inventory methods.
     * - Also displays user's wishlist items of that type (if any).
     * - Checks availability.
     * - Deducts stock immediately from the respective product file.
     * - Updates in-memory totals and buyingList.
     * Called from ECommerceSystem.customerMenu.
     */
    public void addToCart(Inventory inventory) {
        System.out.println("1. Physical Product");
        System.out.println("2. Digital Product");
        System.out.print("Select type to add: ");
        int choice = safeIntInput();
        sc.nextLine(); // consume newline

        Wishlist wishlist = new Wishlist();
        String username = fetchUsername(); // from login.txt

        if (choice == 1) {
            inventory.displayPhysicalProducts(physicalProductFile);
            if (username != null && !username.isEmpty()) {
                System.out.println("\nYour Wishlist (Physical):");
                wishlist.viewWishlist(inventory, username);
            }
            handlePhysicalAdd();
        }
        else if (choice == 2) {
            inventory.displayDigitalProducts(digitalProductFile);
            if (username != null && !username.isEmpty()) {
                System.out.println("\nYour Wishlist (Digital):");
                wishlist.viewWishlist(inventory, username);
            }
            handleDigitalAdd();
        }
        else {
            System.out.println("❌ Invalid selection.");
        }
    }

    // Internal: handle adding a physical product, deducting stock immediately
    private void handlePhysicalAdd() {
        System.out.print("Enter product name: ");
        String name = sc.nextLine().trim();      // read full line
        if (name.isEmpty()) {
            System.out.println("❌ Name cannot be empty.");
            return;
        }
        System.out.print("Enter quantity: ");
        int qty = safeIntInput();
        sc.nextLine(); // consume newline

        int stock = getStock(physicalProductFile, name);
        if (stock < 0) {
            System.out.println("❌ Product not found.");
            return;
        }
        while (qty > stock) {
            System.out.printf("Only %d in stock. Retry? (1=Yes, 0=Cancel): ", stock);
            int opt = safeIntInput();
            sc.nextLine();
            if (opt == 0) {
                return;
            }
            System.out.print("Enter new quantity: ");
            qty = safeIntInput();
            sc.nextLine();
        }
        updateStock(physicalProductFile, name, stock - qty);

        double price = getUnitPrice(physicalProductFile, name);
        double weight = getUnitWeight(physicalProductFile, name);
        totalPrice += price * qty;
        totalWeight += weight * qty;

        boolean found = false;
        for (int i = 0; i < buyingList.size(); i += 2) {
            String tag = buyingList.get(i);
            if (tag.equalsIgnoreCase("P-" + name)) {
                int prevQty = Integer.parseInt(buyingList.get(i + 1));
                buyingList.set(i + 1, String.valueOf(prevQty + qty));
                found = true;
                break;
            }
        }
        if (!found) {
            buyingList.add("P-" + name);
            buyingList.add(String.valueOf(qty));
        }

        System.out.println("✅ Added to cart (stock deducted).");
    }

    // Internal: handle adding a digital product, deducting stock immediately
    private void handleDigitalAdd() {
        System.out.print("Enter product name: ");
        String name = sc.nextLine().trim();      // read full line
        if (name.isEmpty()) {
            System.out.println("❌ Name cannot be empty.");
            return;
        }
        System.out.print("Enter quantity: ");
        int qty = safeIntInput();
        sc.nextLine(); // consume newline

        int stock = getStock(digitalProductFile, name);
        if (stock < 0) {
            System.out.println("❌ Product not found.");
            return;
        }
        while (qty > stock) {
            System.out.printf("Only %d in stock. Retry? (1=Yes, 0=Cancel): ", stock);
            int opt = safeIntInput();
            sc.nextLine();
            if (opt == 0) {
                return;
            }
            System.out.print("Enter new quantity: ");
            qty = safeIntInput();
            sc.nextLine();
        }
        updateStock(digitalProductFile, name, stock - qty);

        double price = getUnitPrice(digitalProductFile, name);
        totalPrice += price * qty;

        boolean found = false;
        for (int i = 0; i < buyingList.size(); i += 2) {
            String tag = buyingList.get(i);
            if (tag.equalsIgnoreCase("D-" + name)) {
                int prevQty = Integer.parseInt(buyingList.get(i + 1));
                buyingList.set(i + 1, String.valueOf(prevQty + qty));
                found = true;
                break;
            }
        }
        if (!found) {
            buyingList.add("D-" + name);
            buyingList.add(String.valueOf(qty));
        }

        System.out.println("✅ Added to cart (stock deducted).");
    }

    /**
     * Display cart contents: type, product name, quantity.
     * Also shows product total, total weight, delivery fee, grand total.
     * Called from ECommerceSystem.customerMenu.
     */
    public void displayCart() {
        if (buyingList.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }
        System.out.println("\n--- Your Cart ---");
        System.out.printf("%-9s %-15s %s%n", "Type", "Name", "Qty");
        for (int i = 0; i < buyingList.size(); i += 2) {
            String tag = buyingList.get(i);
            String qty = buyingList.get(i + 1);
            String type = tag.startsWith("P-") ? "Physical" : "Digital";
            String name = tag.substring(2);
            System.out.printf("%-9s %-15s %s%n", type, name, qty);
        }
        double deliveryFee = deliveryBaseFee + (perKgRate * totalWeight);
        System.out.printf("Products Total: Rs %.2f%n", totalPrice);
        System.out.printf("Total Weight: %.2f kg%n", totalWeight);
        System.out.printf("Delivery Fee: Rs %.2f%n", deliveryFee);
        System.out.printf("Grand Total: Rs %.2f%n", (totalPrice + deliveryFee));
    }

    /**
     * Remove items from cart: restores stock immediately, adjusts buyingList and totals.
     * Called from ECommerceSystem.customerMenu.
     */
    public void removeFromCart() {
        if (buyingList.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }
        displayCart();
        System.out.print("Enter product name to remove: ");
        String name = sc.nextLine().trim();  // use nextLine
        if (name.isEmpty()) {
            System.out.println("❌ Name cannot be empty.");
            return;
        }
        for (int i = 0; i < buyingList.size(); i += 2) {
            String tag = buyingList.get(i);
            String prodName = tag.substring(2);
            if (prodName.equalsIgnoreCase(name)) {
                int inCart = Integer.parseInt(buyingList.get(i + 1));
                System.out.print("Enter quantity to remove (0=cancel): ");
                int rm = safeIntInput();
                sc.nextLine();
                if (rm == 0) {
                    System.out.println("Cancelled removal.");
                    return;
                }
                if (rm < 0 || rm > inCart) {
                    System.out.println("❌ Invalid quantity to remove.");
                    return;
                }
                boolean isPhysical = tag.startsWith("P-");
                String file = isPhysical ? physicalProductFile : digitalProductFile;

                int currentStock = getStock(file, prodName);
                updateStock(file, prodName, currentStock + rm);

                if (isPhysical) {
                    double unitPrice  = getUnitPrice(physicalProductFile, prodName);
                    double unitWeight = getUnitWeight(physicalProductFile, prodName);
                    totalPrice  -= unitPrice  * rm;
                    totalWeight -= unitWeight * rm;
                } else {
                    double unitPrice = getUnitPrice(digitalProductFile, prodName);
                    totalPrice -= unitPrice * rm;
                }
                if (rm == inCart) {
                    buyingList.remove(i + 1);
                    buyingList.remove(i);
                } else {
                    buyingList.set(i + 1, String.valueOf(inCart - rm));
                }
                System.out.println("✅ Removed from cart (stock restored).");
                return;
            }
        }
        System.out.println("❌ Item not found in cart.");
    }

    /**
     * Checkout: first select payment, then coupon, then finalize.
     * - If Card: collect & validate immediately.
     * - Then prompt coupon and, if valid, apply discount & remove coupon file entry.
     * - Finally show totals, delivery fee, download links, clear cart.
     */
    public void checkout() {
        if (buyingList.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }

        // 1) Payment method selection
        System.out.print("Pay by Cash (1) or Card (2)? ");
        int m = safeIntInput();
        sc.nextLine();
        if (m == 2) {
            Card card = new Card();
            boolean ok = card.collectAndValidate();
            if (!ok) {
                System.out.println("❌ Card validation failed. Aborting checkout.");
                return;
            }
        } else if (m != 1) {
            System.out.println("❌ Invalid payment option. Aborting checkout.");
            return;
        }

        // 2) Coupon application
        double subTotal       = totalPrice;
        double discountAmount = 0;
        String appliedCode    = null;

        System.out.print("Enter coupon code to apply (or blank to skip): ");
        String code = sc.nextLine().trim();
        if (!code.isEmpty()) {
            Coupon couponMgr = new Coupon();
            String discStr = couponMgr.getDiscountForCode(code);
            if (discStr != null) {
                int pct = Integer.parseInt(discStr.replace("%", ""));
                discountAmount = subTotal * pct / 100.0;
                appliedCode    = code.toUpperCase();
                System.out.printf("✅ Coupon '%s' applied: %s off (%.2f)%n",
                                  appliedCode, discStr, discountAmount);
                if (couponMgr.removeCouponCode(code)) {
                    System.out.println("✅ Coupon removed from system after use.");
                } else {
                    System.out.println("⚠️ Failed to remove coupon file entry.");
                }
            } else {
                System.out.println("❌ Invalid coupon code. No discount applied.");
            }
        } else {
            System.out.println("No coupon entered. Proceeding without discount.");
        }

        // 3) Final summary
        double discountedTotal = Math.max(0, subTotal - discountAmount);
        double deliveryFee     = deliveryBaseFee + (perKgRate * totalWeight);
        double grandTotal      = discountedTotal + deliveryFee;

        System.out.println("\n--- Order Details ---");
        displayCart();
        System.out.printf("Subtotal: Rs %.2f%n", subTotal);
        if (appliedCode != null) {
            System.out.printf("Discount (%s): -Rs %.2f%n", appliedCode, discountAmount);
            System.out.printf("After discount: Rs %.2f%n", discountedTotal);
        }
        System.out.printf("Delivery Fee: Rs %.2f%n", deliveryFee);
        System.out.printf("Grand Total: Rs %.2f%n", grandTotal);

        // 4) Digital download links
        for (int i = 0; i < buyingList.size(); i += 2) {
            String tag = buyingList.get(i);
            if (tag.startsWith("D-")) {
                String prodName = tag.substring(2);
                int qty = Integer.parseInt(buyingList.get(i + 1));
                System.out.println("Download links for " + prodName + ":");
                for (int j = 0; j < qty; j++) {
                    System.out.println("  " + generateDownloadLink());
                }
            }
        }

        // 5) Clear cart memory
        buyingList.clear();
        totalPrice  = 0;
        totalWeight = 0;
        System.out.println("✅ Thank you for your purchase!");
    }

    /**
     * Restore any remaining items in cart back to inventory.
     * Called when user logs out without checkout.
     * After restoring, clear the in-memory cart.
     */
    public void restoreStockOnLogout() {
        if (buyingList.isEmpty()) return;

        System.out.println("Restoring items from cart back to stock...");
        for (int i = 0; i < buyingList.size(); i += 2) {
            String tag = buyingList.get(i);
            String name = tag.substring(2);
            int qty     = Integer.parseInt(buyingList.get(i + 1));
            String file = tag.startsWith("P-") ? physicalProductFile : digitalProductFile;
            int stock   = getStock(file, name);
            updateStock(file, name, stock + qty);
        }
        buyingList.clear();
        totalPrice  = 0;
        totalWeight = 0;
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────────

    /** Safely read int from console, reprompting on invalid input. */
    private int safeIntInput() {
        while (!sc.hasNextInt()) {
            System.out.print("Invalid input. Enter a number: ");
            sc.next();
        }
        return sc.nextInt();
    }

    /** Read current stock; returns -1 if not found/error. */
    private int getStock(String file, String productName) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String ln;
            while ((ln = br.readLine()) != null) {
                String[] d = ln.split(",", -1);
                if (d.length >= 4 && d[0].equalsIgnoreCase(productName)) {
                    return Integer.parseInt(d[3]);
                }
            }
        } catch (IOException | NumberFormatException e) {
            // ignore
        }
        return -1;
    }

    /** Read unit price; returns 0 if not found/error. */
    private double getUnitPrice(String file, String productName) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String ln;
            while ((ln = br.readLine()) != null) {
                String[] d = ln.split(",", -1);
                if (d.length >= 3 && d[0].equalsIgnoreCase(productName)) {
                    return Double.parseDouble(d[2]);
                }
            }
        } catch (IOException | NumberFormatException e) {
            // ignore
        }
        return 0;
    }

    /** Read unit weight for physical products; returns 0 if not found/error. */
    private double getUnitWeight(String file, String productName) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String ln;
            while ((ln = br.readLine()) != null) {
                String[] d = ln.split(",", -1);
                if (d.length >= 5 && d[0].equalsIgnoreCase(productName)) {
                    return Double.parseDouble(d[4]);
                }
            }
        } catch (IOException | NumberFormatException e) {
            // ignore
        }
        return 0;
    }

    /** Update stock in file for productName to newStock. */
    private void updateStock(String file, String productName, int newStock) {
        List<String> all = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String ln;
            while ((ln = br.readLine()) != null) {
                String[] d = ln.split(",", -1);
                if (d.length >= 4 && d[0].equalsIgnoreCase(productName)) {
                    d[3] = String.valueOf(newStock);
                }
                all.add(String.join(",", d));
            }
        } catch (IOException e) {
            System.out.println("❌ Error updating stock in " + file + ": " + e.getMessage());
            return;
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String l : all) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("❌ Error writing stock to " + file + ": " + e.getMessage());
        }
    }

    /** Generate a random 10-character download link. */
    private String generateDownloadLink() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder("https://download.com/");
        Random rand = new Random();
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * Update address and phone in login.txt for the given userEmail.
     * Returns true if successful.
     */
    private boolean updateLoginDetails(String userEmail, String newAddress, String newPhone) {
        File file = new File("login.txt");
        List<String> all = new ArrayList<>();
        boolean updated = false;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String ln;
            while ((ln = br.readLine()) != null) {
                String[] parts = ln.split(",", -1);
                if (parts.length >= 5 && parts[0].equalsIgnoreCase(userEmail)) {
                    parts[3] = newAddress;
                    parts[4] = newPhone;
                    updated = true;
                }
                all.add(String.join(",", parts));
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading login file: " + e.getMessage());
            return false;
        }
        if (!updated) return false;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String line : all) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("❌ Error writing login file: " + e.getMessage());
            return false;
        }
        return true;
    }

    /** Fetch username from login.txt given userEmail. */
    private String fetchUsername() {
        if (userEmail == null) return "";
        try (BufferedReader br = new BufferedReader(new FileReader("login.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length >= 5 && p[0].equalsIgnoreCase(userEmail)) {
                    return p[1];
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading login file for username: " + e.getMessage());
        }
        return "";
    }
        
    public boolean isEmpty() {
        return buyingList.isEmpty();
    }

}
