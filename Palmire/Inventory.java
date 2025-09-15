// Inventory.java

import java.io.*;
import java.util.*;

public class Inventory {
    private final Scanner sc = new Scanner(System.in);
    private final String physicalProductFile = "physicalProducts.txt";
    private final String digitalProductFile = "digitalProducts.txt";

    /** Delivery constants used by Cart */
    public static final double DELIVERY_BASE = 300;
    public static final double PRICE_PER_KG = 50;

    /**
     * Add a new product (physical or digital) as Admin/SuperAdmin.
     * Prevents duplicates by name, appends CSV line to appropriate file.
     */
    public void addProductAsAdmin(Product p, int choice) {
        // System.out.println("\n--- Add Product ---");
        // System.out.println("1. Physical Product");
        // System.out.println("2. Digital Product");
        // System.out.print("Select type to add: ");
        // int choice = safeIntInput();
        // sc.nextLine(); // consume leftover
        switch (choice) {
            case 1 ->                 {
                    // Physical
                    System.out.print("Enter product name: ");
                    String name = sc.nextLine().trim();
                    if (name.isEmpty()) {
                        System.out.println("❌ Name cannot be empty.");
                        return;
                    }       if (existsInFile(physicalProductFile, name)) {
                        System.out.println("⚠️ Physical product already exists.");
                        return;
                    }       System.out.print("Enter product category/type: ");
                    String type = sc.nextLine().trim();
                    System.out.print("Enter price: ");
                    double price = safeDoubleInput();
                    System.out.print("Enter quantity: ");
                    int qty = safeIntInput();
                    System.out.print("Enter weight (In grams): ");
                    double weight = safeDoubleInput();
                    String line = String.join(",",
                            name,
                            type,
                            String.valueOf(price),
                            String.valueOf(qty),
                            String.valueOf(weight));
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(physicalProductFile, true))) {
                        bw.write(line);
                        bw.newLine();
                        System.out.println("✅ Physical product added.");
                    } catch (IOException e) {
                        System.out.println("❌ Error writing physical file: " + e.getMessage());
                    }                      }
            case 2 ->                 {
                    // Digital
                    System.out.print("Enter product name: ");
                    String name = sc.nextLine().trim();
                    if (name.isEmpty()) {
                        System.out.println("❌ Name cannot be empty.");
                        return;
                    }       if (existsInFile(digitalProductFile, name)) {
                        System.out.println("⚠️ Digital product already exists.");
                        return;
                    }       System.out.print("Enter product category/type: ");
                    String type = sc.nextLine().trim();
                    System.out.print("Enter price: ");
                    double price = safeDoubleInput();
                    System.out.print("Enter quantity: ");
                    int qty = safeIntInput();
                    sc.nextLine(); // consume leftover
                    System.out.print("Enter download link (leave blank to auto-generate): ");
                    String link = sc.nextLine().trim();
                    if (link.isEmpty()) {
                        link = generateUniqueDownloadLink();
                        System.out.println("Generated link: " + link);
                    }       String line = String.join(",",
                            name,
                            type,
                            String.valueOf(price),
                            String.valueOf(qty),
                            link);
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(digitalProductFile, true))) {
                        bw.write(line);
                        bw.newLine();
                        System.out.println("✅ Digital product added.");
                    } catch (IOException e) {
                        System.out.println("❌ Error writing digital file: " + e.getMessage());
                    }                      }
            default -> System.out.println("❌ Invalid selection.");
        }
    }

    /**
     * Remove product by name (physical or digital).
     * Reads entire file, filters out matching name, rewrites file.
     */
    public void removeProductAsAdmin() {
        System.out.println("\n--- Remove Product ---");
        System.out.println("1. Physical Product");
        System.out.println("2. Digital Product");
        System.out.print("Select type: ");
        int choice = safeIntInput();
        sc.nextLine();

        String file = choice == 1 ? physicalProductFile
                : choice == 2 ? digitalProductFile
                        : null;
        if (file == null) {
            System.out.println("❌ Invalid selection.");
            return;
        }

        // Display existing
        if (choice == 1)
            displayPhysicalProducts(physicalProductFile);
        else
            displayDigitalProducts(digitalProductFile);

        System.out.print("Enter product name to remove: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("❌ Name cannot be empty.");
            return;
        }

        List<String> kept = new ArrayList<>();
        boolean found = false;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String ln;
            while ((ln = br.readLine()) != null) {
                String[] parts = ln.split(",", -1);
                if (parts.length >= 1 && parts[0].equalsIgnoreCase(name)) {
                    found = true;
                } else {
                    kept.add(ln);
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading file: " + e.getMessage());
            return;
        }

        if (!found) {
            System.out.println("⚠️ Product not found.");
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String out : kept) {
                bw.write(out);
                bw.newLine();
            }
            System.out.println("✅ Product removed.");
        } catch (IOException e) {
            System.out.println("❌ Error writing file: " + e.getMessage());
        }
    }

    /**
     * Update product by name: lets user choose which field to change.
     * Reads file, prompts for field, rewrites updated lines.
     */
    public void updateProductAsAdmin() {
        System.out.println("\n--- Update Product ---");
        System.out.println("1. Physical Product");
        System.out.println("2. Digital Product");
        System.out.print("Select type: ");
        int choice = safeIntInput();
        sc.nextLine();

        String file = choice == 1 ? physicalProductFile
                : choice == 2 ? digitalProductFile
                        : null;
        if (file == null) {
            System.out.println("❌ Invalid selection.");
            return;
        }

        // Display
        if (choice == 1)
            displayPhysicalProducts(physicalProductFile);
        else
            displayDigitalProducts(digitalProductFile);

        System.out.print("Enter product name to update: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("❌ Name cannot be empty.");
            return;
        }

        List<String> updated = new ArrayList<>();
        boolean found = false;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String ln;
            while ((ln = br.readLine()) != null) {
                String[] parts = ln.split(",", -1);
                if (parts.length < 5 || !parts[0].equalsIgnoreCase(name)) {
                    updated.add(ln);
                } else {
                    found = true;
                    System.out.printf("Updating '%s':%n", parts[0]);
                    for (int i = 0; i < parts.length; i++) {
                        String label = switch (i) {
                            case 0 -> "Name";
                            case 1 -> "Category";
                            case 2 -> "Price";
                            case 3 -> "Quantity";
                            case 4 -> choice == 1 ? "Weight" : "DownloadLink";
                            default -> "Field" + i;
                        };
                        System.out.printf("%d. %s = %s%n", i + 1, label, parts[i]);
                    }
                    System.out.print("Choose field number to update (1–5): ");
                    int fld = safeIntInput();
                    sc.nextLine();
                    if (fld >= 1 && fld <= 5) {
                        System.out.print("Enter new value: ");
                        String nv = sc.nextLine().trim();
                        if (!nv.isEmpty()) {
                            parts[fld - 1] = nv;
                        }
                    }
                    updated.add(String.join(",", parts));
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading file: " + e.getMessage());
            return;
        }

        if (!found) {
            System.out.println("⚠️ Product not found.");
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String out : updated) {
                bw.write(out);
                bw.newLine();
            }
            System.out.println("✅ Product updated.");
        } catch (IOException e) {
            System.out.println("❌ Error writing file: " + e.getMessage());
        }
    }

    /** Display all physical products. */
    public void displayPhysicalProducts(String physicalProductFile) {
        System.out.println("\n--- Physical Products ---");
        System.out.printf("%-15s %-12s %-8s %-9s %s%n",
                "Name", "Category", "Price", "Quantity", "Weight(kg)");
        try (BufferedReader br = new BufferedReader(new FileReader(physicalProductFile))) {
            String ln;
            while ((ln = br.readLine()) != null) {
                String[] p = ln.split(",", -1);
                if (p.length >= 5) {
                    System.out.printf("%-15s %-12s %-8s %-9s %s%n",
                            p[0], p[1], p[2], p[3], p[4]);
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error displaying physical file: " + e.getMessage());
        }
    }

    /** Display all digital products. */
    public void displayDigitalProducts(String file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            System.out.println("\n--- Digital Products ---");
            System.out.printf("%-15s %-12s %-8s %-8s%n",
                    "Name", "Category", "Price", "Quantity");
            String ln;
            while ((ln = br.readLine()) != null) {
                String[] d = ln.split(",", -1);
                if (d.length >= 4) {
                    System.out.printf("%-15s %-12s %-8s %-8s%n",
                            d[0], d[1], d[2], d[3]);
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading " + file + ": " + e.getMessage());
        }
    }

    /**
     * Generate a 10‑char download link, ensuring no collision in digital file.
     */
    private String generateUniqueDownloadLink() {
        String base = "https://download.com/";
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rand = new Random();
        String link;
        outer: while (true) {
            StringBuilder sb = new StringBuilder(base);
            for (int i = 0; i < 10; i++) {
                sb.append(chars.charAt(rand.nextInt(chars.length())));
            }
            link = sb.toString();
            try (BufferedReader br = new BufferedReader(new FileReader(digitalProductFile))) {
                String ln;
                while ((ln = br.readLine()) != null) {
                    String[] p = ln.split(",", -1);
                    if (p.length >= 5 && p[4].equals(link))
                        continue outer;
                }
            } catch (IOException ignored) {
            }
            break;
        }
        return link;
    }

    /** Check whether name exists (first CSV field) in given file. */
    private boolean existsInFile(String file, String name) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String ln;
            while ((ln = br.readLine()) != null) {
                if (ln.split(",", -1)[0].equalsIgnoreCase(name))
                    return true;
            }
        } catch (IOException ignored) {
        }
        return false;
    }

    /** Safely read an int, retry until valid. */
    private int safeIntInput() {
        while (!sc.hasNextInt()) {
            System.out.print("Invalid — enter a number: ");
            sc.next();
        }
        return sc.nextInt();
    }

    /** Safely read a double, retry until valid. */
    private double safeDoubleInput() {
        while (!sc.hasNextDouble()) {
            System.out.print("Invalid — enter a decimal: ");
            sc.next();
        }
        return sc.nextDouble();
    }
}
