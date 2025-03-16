class Student{
    String name;
    int age;
    float marks;

    
    Student(){
        this.name = "Ahmad";
        this.age=20;
        
    
    }

    public void greeting(){ 
        System.out.println("Hello My name is: "+ name+" age is: "+age+"marks is: "+marks);
    }

    public void chName(String name){
        this.name = name;
    }
    public void changeName(String name){
        this.name = name;
        // name = newName;
    }

    Student(String name, int age, float marks){
        this.name = name;
        this.age = age;
        this.marks = marks;
    }
    
    // This constructor calls another constructor and give its values 
    // to the new student(object) made through this constructor
    Student(Student other){
        this.name = other.name;
        this.age = other.age;
        this.marks = other.marks;


    
}
}

public class This {
    public static void main(String[] args) {
        Student s1 = new Student();
        s1.greeting();
        s1.changeName("Mujtaba");
        s1.greeting();
        s1.changeName("oanoav");
        s1.greeting();


        Student s2 = new Student();
        s2.greeting();
        s2.chName("Mujtaba");
        s2.greeting();

        Student s3 = new Student("Hasanat", 22,23.0f);
        s3.greeting();
        
        Student s4 = new Student(s3);
        s4.greeting();
        
        
        
    }
}
