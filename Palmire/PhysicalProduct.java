// PhysicalProduct.java

public class PhysicalProduct extends Product {
    private double weight;  // kg

    public PhysicalProduct(){

    }
    
    public PhysicalProduct(String productName, String productType, double price, int quantity, double weight) {
        super(productName, productType, price, quantity);
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }
    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return String.format(
            "PhysicalProduct [Name=%s, Type=%s, Price=%.2f, Qty=%d, Weight=%.2fkg]",
            getProductName(), getProductType(), getPrice(), getQuantity(), weight
        );
    }

    @Override
    public void displayProducts(String fileName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

