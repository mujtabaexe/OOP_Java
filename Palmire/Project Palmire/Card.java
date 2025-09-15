import java.io.*;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Card {
    private String cardHolder;
    private String cardNumber;      // 16 digits
    private YearMonth expiry;       // MM/YY
    private String cvv;             // 3 digits

    private static final String CARD_FILE = "card.txt";
    private final Scanner sc = new Scanner(System.in);

    /** Ensure the card file exists on disk */
    private void ensureFileExists() {
        File f = new File(CARD_FILE);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                System.out.println("❌ Could not create " + CARD_FILE + ": " + e.getMessage());
            }
        }
    }

    /** 
     * Collect card details with validation loops and cancel option.
     * Returns true if valid (and saved or existing), false on cancel or error.
     */
    public boolean collectAndValidate() {
        // Make sure the file is there before we read or write
        ensureFileExists();

        // 1) Cardholder name
        while (true) {
            System.out.print("Cardholder name (or 0 to cancel): ");
            String input = sc.nextLine().trim();
            if (input.equals("0")) {
                System.out.println("🛑 Card entry cancelled.");
                return false;
            }
            if (input.isEmpty()) {
                System.out.println("❌ Name cannot be empty.");
                continue;
            }
            cardHolder = input;
            break;
        }

        // 2) Card number
        while (true) {
            System.out.print("Card number (16 digits) (or 0 to cancel): ");
            String input = sc.nextLine().trim();
            if (input.equals("0")) {
                System.out.println("🛑 Card entry cancelled.");
                return false;
            }
            if (!input.matches("\\d{16}")) {
                System.out.println("❌ Card number must be exactly 16 digits.");
                continue;
            }
            cardNumber = input;
            break;
        }

        // 3) Expiry
        String expStr;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/yy");
        while (true) {
            System.out.print("Expiry (MM/YY) (or 0 to cancel): ");
            expStr = sc.nextLine().trim();
            if (expStr.equals("0")) {
                System.out.println("🛑 Card entry cancelled.");
                return false;
            }
            if (!expStr.matches("(0[1-9]|1[0-2])/\\d{2}")) {
                System.out.println("❌ Expiry must be in MM/YY format.");
                continue;
            }
            try {
                expiry = YearMonth.parse(expStr, fmt);
            } catch (DateTimeParseException e) {
                System.out.println("❌ Invalid expiry format.");
                continue;
            }
            if (expiry.isBefore(YearMonth.now())) {
                System.out.println("❌ Card expired.");
                continue;
            }
            break;
        }

        // 4) CVV
        while (true) {
            System.out.print("CVV (3 digits) (or 0 to cancel): ");
            String input = sc.nextLine().trim();
            if (input.equals("0")) {
                System.out.println("🛑 Card entry cancelled.");
                return false;
            }
            if (!input.matches("\\d{3}")) {
                System.out.println("❌ CVV must be exactly 3 digits.");
                continue;
            }
            cvv = input;
            break;
        }

        // 5) Check for existing card number
        boolean exists = false;
        try (BufferedReader br = new BufferedReader(new FileReader(CARD_FILE))) {
            String ln;
            while ((ln = br.readLine()) != null) {
                String[] parts = ln.split(",", -1);
                if (parts.length >= 2 && parts[1].trim().equals(cardNumber)) {
                    exists = true;
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading " + CARD_FILE + ": " + e.getMessage());
            return false;
        }

        if (exists) {
            System.out.println("Using existing card on file.");
            return true;
        }

        // 6) Append new card details
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CARD_FILE, true))) {
            bw.write(String.join(",", cardHolder, cardNumber, expStr, cvv));
            bw.newLine();
            bw.flush();
            System.out.println("✅ Card details saved for future use.");
        } catch (IOException e) {
            System.out.println("❌ Error writing to " + CARD_FILE + ": " + e.getMessage());
            return false;
        }

        return true;
    }
}

