
import java.util.*;

// import java.io.BufferedReader;
// import java.io.BufferedWriter;
// import java.io.FileReader;
// import java.io.FileWriter;
// import java.io.IOException;
// import java.io.Reader;
class User {

    private int id;
    private String name;
    private String email;
    private String phone;
    private String address;

    public User(int id, String name, String email, String phone, String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public String register() {

        return "Successfully Registered.";

    }

    public boolean verifyLogin() {

        return true;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}

class Customer extends User {

    private String creditCardInfo;
    private String coupon;

    public Cart cart;



    Customer(int id, String name, String email, String phone, String address, String creditCardInfo, String coupon) {

        super(id, name, email, phone, address);
        this.creditCardInfo = creditCardInfo;
        this.coupon = coupon;

        this.cart = new Cart();  
    }


    public String updateProfile() {

        return "Profile successfully updated";

    }

    public String getCreditCardInfo() {
        return creditCardInfo;
    }

    public void setCreditCardInfo(String creditCardInfo) {
        this.creditCardInfo = creditCardInfo;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

}

class Administrator extends User {

    private int salary;
    private String coupon;

    Administrator(int id, String name, String email, String phone, String address) {

        super(id, name, email, phone, address);

    }

    public String updateProfile() {

        return "Profile successfully updated.";

    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }
}

class Cart {

    public Item[] items;

    Cart() {

        this.items = new Item[10];

    }

    public void addItem(Item item) {

        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                items[i] = item;
                break;
            }

        }

    }

    public void display(){

        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                continue;
            }
            System.out.print(items[i].getName() + " " + items[i].getQuantity());

        }

    }

}

class Item {

    private String name;
    private int quantity;

    Item(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    Item(Item item) {
        this.name = item.name;
        this.quantity = item.quantity;
    }

    public String update() {

        return "Successfully updated.";

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

public class ECommerceSystem {

    static Item[] systemArray = new Item[10];

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        String adminName = "admin";
        String adminEmail = "admin@gmail.com";
        String customerName = "customer";
        String customerEmail = "customer@gmail.com";

        while (true) {

            System.out.print("Enter 1 to Login or 0 to exit: ");
            int opt = sc.nextInt();

            if (opt == 0) {
                return;
            }

            System.out.print("Enter Name: ");
            sc.nextLine();
            String name = sc.nextLine();
            System.out.print("Enter Email: ");
            String email = sc.nextLine();
            

            System.out.println(email + name);
            if (email.equalsIgnoreCase(adminEmail) && name.equalsIgnoreCase(adminName)) {

                Administrator admin = new Administrator(10, name, email, "03355599991", "H411");

                System.out.print("Enter 1 to Add new item 2 to remove item or 0 to return to main menu: ");

                opt = sc.nextInt();

                if (opt == 0) {

                }
                else if (opt == 1) {

                    System.out.print("Enter item name: ");
                    sc.nextLine();
                    String itemName = sc.nextLine();
                    System.out.print("Enter item name: ");
                    int itemQuantity = sc.nextInt();

                    Item item = new Item(itemName, itemQuantity);

                    addProduct(item);

                }

                else if(opt == 2){
                    
                    printItems();
                    System.out.print("Enter item no: ");
                    sc.nextLine();
                    opt = sc.nextInt();
                    

                    removeProduct(systemArray[opt]);

                }

            } else if (email.equalsIgnoreCase(customerEmail) && name.equalsIgnoreCase(customerName)) {

                Customer c = new Customer(1, name, email, "0351513581651", "H213", "10923847", "coup");

                printItems();
                System.out.print("Enter the item to add to cart: ");
    
                opt = sc.nextInt();
                System.out.print("Enter item quantity: ");
                int itemquantity = sc.nextInt();

                Item userPurchasedItem = new Item(systemArray[opt]);
                userPurchasedItem.setQuantity(itemquantity);

                removeProduct(userPurchasedItem);

                c.cart.addItem(userPurchasedItem);

                System.out.println("Your Cart: ");
                c.cart.display();

            }
        }
    }

    public static void addProduct(Item item) {

        for (int i = 0; i < systemArray.length; i++) {

            if (systemArray[i] == null) {

                systemArray[i] = item;
                break;

            }

        }

    }

    public static void removeProduct(Item item) {

        for (int i = 0; i < systemArray.length; i++) {

            if (systemArray[i].getName().equals(item.getName())) {

                systemArray[i].setQuantity(systemArray[i].getQuantity() - item.getQuantity());
                break;

            }

        }

    }

    public static void printItems(){

        for (int i = 0 ; i < systemArray.length ; i++) {
            
            if(systemArray[i] == null){
                continue;
            }
            System.out.println(i + ". " + systemArray[i].getName() + " " + systemArray[i].getQuantity() );

        }

    }

}
