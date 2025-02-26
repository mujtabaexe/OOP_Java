class Student{
    String name;
    int age;

    // Non Parametarized Constructor
    Student(){
        // this.name= "Unknown";
        // this.age = 0;
        System.out.println("Constructor is called.");   
        printInfo();
    }
    
    public void printInfo(){
        System.out.println("Student name is: "+name+" and age is: "+age);
    } 
    // Parametarized Constructor
    Student(String name, int age){
        this.name = name;
        this.age= age;
        printInfo();
    }
}

public class Constructor {
    public static void main(String[] args) {
        Student s1 = new Student();
        s1.name = "Ahmad";
        s1.age = 22;
        s1.printInfo();

        Student s2 = new Student("Mujtaba",19);
        s2.printInfo();
    }
}
