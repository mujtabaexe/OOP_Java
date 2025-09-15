import java.io.*;
import java.util.*;

public class Wishlist {
    private static final String WISHLIST_FILE = "wishlist.txt";
    private final Scanner sc = new Scanner(System.in);

    /**
     * Add a product to wishlist for the given username.
     * Prompts type and product name, verifies it exists in inventory, then appends to wishlist file if not already present.
     */
    public void addToWishlist(Inventory inventory, String username) {
        System.out.println("\n--- Add to Wishlist ---");
        System.out.println("1. Physical Product");
        System.out.println("2. Digital Product");
        System.out.print("Select type to add to wishlist: ");
        int choice = safeIntInput();
        String fileToCheck;
        switch (choice) {
            case 1 -> {
                inventory.displayPhysicalProducts("physicalProducts.txt");
                fileToCheck = "physicalProducts.txt";
            }
            case 2 -> {
                inventory.displayDigitalProducts("digitalProducts.txt");
                fileToCheck = "digitalProducts.txt";
            }
            default -> {
                System.out.println("❌ Invalid selection.");
                return;
            }
        }

        System.out.print("Enter product name to add to wishlist: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("❌ Name cannot be empty.");
            return;
        }
        // Check existence in inventory file
        if (!productExistsInFile(fileToCheck, name)) {
            System.out.println("❌ Product not found in inventory.");
            return;
        }
        // Check if already in wishlist for this user
        if (isInWishlist(username, choice, name)) {
            System.out.println("❌ Already in your wishlist.");
            return;
        }
        // Append to wishlist file: format: username,TYPE,productName
        // TYPE: "P" or "D"
        String typeTag = (choice == 1) ? "P" : "D";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(WISHLIST_FILE, true))) {
            String line = String.join(",", username, typeTag, name);
            bw.write(line);
            bw.newLine();
            System.out.println("✅ Added to wishlist.");
        } catch (IOException e) {
            System.out.println("❌ Error writing to wishlist file: " + e.getMessage());
        }
    }

    /**
     * Remove a product from wishlist for this username.
     */
    public void removeFromWishlist(String username) {
        System.out.println("\n--- Remove from Wishlist ---");
        List<String> allLines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(WISHLIST_FILE))) {
            String ln;
            while ((ln = br.readLine()) != null) {
                allLines.add(ln);
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading wishlist file: " + e.getMessage());
            return;
        }
        List<String[]> userItems = new ArrayList<>();
        for (String ln : allLines) {
            String[] parts = ln.split(",", -1);
            if (parts.length >= 3 && parts[0].equals(username)) {
                userItems.add(parts);
            }
        }
        if (userItems.isEmpty()) {
            System.out.println("Your wishlist is empty.");
            return;
        }
        System.out.println("Your wishlist items:");
        int idx = 1;
        for (String[] parts : userItems) {
            String typeStr = parts[1].equals("P") ? "Physical" : "Digital";
            System.out.printf("%d. [%s] %s%n", idx++, typeStr, parts[2]);
        }
        System.out.print("Enter number to remove (or 0 to cancel): ");
        int sel = safeIntInput();
        if (sel == 0) {
            System.out.println("Cancelled removal.");
            return;
        }
        if (sel < 1 || sel > userItems.size()) {
            System.out.println("❌ Invalid selection.");
            return;
        }
        String[] toRemove = userItems.get(sel - 1);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(WISHLIST_FILE))) {
            for (String ln : allLines) {
                String[] parts = ln.split(",", -1);
                if (parts.length >= 3
                        && parts[0].equals(username)
                        && parts[1].equals(toRemove[1])
                        && parts[2].equalsIgnoreCase(toRemove[2])) {
                    continue;
                }
                bw.write(ln);
                bw.newLine();
            }
            System.out.println("✅ Removed from wishlist.");
        } catch (IOException e) {
            System.out.println("❌ Error updating wishlist file: " + e.getMessage());
        }
    }

    /**
     * View wishlist items for this username.
     */
    public void viewWishlist(Inventory inventory, String username) {
        System.out.println("\n--- Your Wishlist ---");
        boolean any = false;
        try (BufferedReader br = new BufferedReader(new FileReader(WISHLIST_FILE))) {
            String ln;
            while ((ln = br.readLine()) != null) {
                String[] parts = ln.split(",", -1);
                if (parts.length >= 3 && parts[0].equals(username)) {
                    any = true;
                    String typeStr = parts[1].equals("P") ? "Physical" : "Digital";
                    String name = parts[2];
                    System.out.printf("- [%s] %s%n", typeStr, name);
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading wishlist file: " + e.getMessage());
            return;
        }
        if (!any) {
            System.out.println("(empty)");
        } else {
            System.out.println("You can add/remove items when needed.");
        }
    }

    /**
     * Check if a product exists in the given inventory file.
     */
    private boolean productExistsInFile(String file, String name) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String ln;
            while ((ln = br.readLine()) != null) {
                String[] data = ln.split(",", -1);
                if (data.length >= 1 && data[0].equalsIgnoreCase(name)) {
                    return true;
                }
            }
        } catch (IOException e) {
            // ignore
        }
        return false;
    }

    /**
     * Check if already in wishlist for this user.
     */
    private boolean isInWishlist(String username, int typeChoice, String name) {
        String typeTag = (typeChoice == 1) ? "P" : "D";
        try (BufferedReader br = new BufferedReader(new FileReader(WISHLIST_FILE))) {
            String ln;
            while ((ln = br.readLine()) != null) {
                String[] parts = ln.split(",", -1);
                if (parts.length >= 3
                        && parts[0].equals(username)
                        && parts[1].equals(typeTag)
                        && parts[2].equalsIgnoreCase(name)) {
                    return true;
                }
            }
        } catch (IOException e) {
            // ignore
        }
        return false;
    }

    private int safeIntInput() {
        while (!sc.hasNextInt()) {
            System.out.print("Invalid input. Enter a number: ");
            sc.next();
        }
        int val = sc.nextInt();
        sc.nextLine(); // consume newline
        return val;
    }
}
