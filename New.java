class Student{
    String name;

    public Student(String name) {
        this.name = name;
    }
}
public class New {
    public static void main(String[] args) {
        int a = 10;
        Student s = new Student("Ahmad");

        changeA(a);
        changeStudent(s);

        System.out.println(a);
        System.out.println(s.name);

        
    }

    public static void changeA(int a) {
        a = 5;
    }

    public static void changeStudent(Student s) {
        s.name = "Mujtaba";
    }

}
