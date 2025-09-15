import java.util.*;

class User {

    private String name;
    private int id;

    public User() {

        System.out.println("User Created");
        
    }
    
    public User(String name ,int id) {
        
        this.name = name;
        this.id = id;
        
    }

    public void getName() {

    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}

class Admin extends User {
    
    private String company;
    private int salary;
    
    public Admin(){
        
        System.out.println("Admin Created");


    }        
    public Admin(String name , int id , String company, int salary){

        super(name , id);
        this.company = company;
        this.salary = salary;

    }
    public String getCompany() {
        return company;
    }
    public void setCompany(String company) {
        this.company = company;
    }
    public int getSalary() {
        return salary;
    }
    public void setSalary(int salary) {
        this.salary = salary;
    }
    

}

class Customer extends User{

    private int age;
    private String phone;

    public Customer() {
    }

    public Customer(String name, int id, int age, String phone) {

        super(name,id);
        this.age = age;
        this.phone = phone;
    }

    
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    

}

class Cart{

    Item[] items;

    public Cart() {

        items = new Item[10];

    }

    public void addItem(Item item){

        for (int i = 0; i < items.length; i++) {
            
            if(items[i] == null){
                items[i] = item;
                break;
            }

        }

    }

    public void display(){

        for (int i = 0; i < items.length; i++) {
            
            if(items[i] == null){
                continue;
            }
            System.out.print(items[i].getProductName() + " " + items[i].getQuantity());

        }

    }
    

}


class Item{

    private String productName;
    private int quantity;

    Item(String productName, int quantity){

        this.productName = productName;
        this.quantity = quantity;

    }

    Item(Item item){
        this.productName = item.productName;
        this.quantity = item.quantity;

    }

    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    

}

public class Ecommerce {

    static Item[] systemArray = new Item[10];
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        String admin = "a";
        String adminEmail = "a";
        String customer = "c";
        String customerEmail = "c";


        while(true){}




    }


    public void addItem(Item item){

        for (int i = 0; i < systemArray.length; i++) {
            if(systemArray[i] == null){
                systemArray[i] = item;
                break;
            }
        }

    }

    public void removeItem(Item item){

        for (int i = 0; i < systemArray.length; i++) {
            if(systemArray[i].getProductName().equals(item.getProductName())){
                systemArray[i].setQuantity(systemArray[i].getQuantity() - item.getQuantity());
                break;
            }
        }

    }

    public void display(){

        for (int i = 0; i < systemArray.length; i++) {
            if(systemArray[i] == null){
                continue;
            }
            System.out.println(systemArray[i].getProductName() + " " + systemArray[i].getQuantity());
        }

    }

}
