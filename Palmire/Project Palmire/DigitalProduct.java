// DigitalProduct.java

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class DigitalProduct extends Product {
    private String downloadLink;
    private final String digitalProductFile  = "digitalProducts.txt";

    public DigitalProduct(){

    }

    public DigitalProduct(String productName, String productType, double price, int quantity, String downloadLink) {
        super(productName, productType, price, quantity);
        this.downloadLink = downloadLink;
    }

    public String getDownloadLink() {
        return downloadLink;
    }
    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    @Override
    public String toString() {
        return String.format(
            "DigitalProduct [Name=%s, Type=%s, Price=%.2f, Qty=%d, Link=%s]",
            getProductName(), getProductType(), getPrice(), getQuantity(), downloadLink
        );
    }

    @Override
    public void displayProducts(String fileName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String generateDownloadLink() {
        String base  = "https://download.com/";
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rand  = new Random();
        String link;
        outer: while (true) {
            StringBuilder sb = new StringBuilder(base);
            for (int i=0; i<10; i++) {
                sb.append(chars.charAt(rand.nextInt(chars.length())));
            }
            link = sb.toString();
            try (BufferedReader br = new BufferedReader(new FileReader(digitalProductFile))) {
                String ln;
                while ((ln = br.readLine()) != null) {
                    String[] p = ln.split(",", -1);
                    if (p.length>=5 && p[4].equals(link)) continue outer;
                }
            } catch (IOException ignored) { }
            break;
        }
        return link;
    }

}
