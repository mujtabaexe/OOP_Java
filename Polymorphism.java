class Student{

private String name;
private int age;

public void setName(String name){
    this.name = name;
}
public void setAge(int age){
    this.age = age;
}

public void display(){
    System.out.println("Hello My name is "+ name +" and my age is "+age);
}
}


public class Polymorphism {
    public static void main(String[] args) {
        
        // Mainly the concept of Polymorphism is the overloading and overriding of fucntions in the class

        Student s1 = new Student();
        s1.setName("Mujtaba");
        s1.setAge(20);

        s1.display();

    }
}
