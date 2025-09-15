
// The getters and setters we use in OOP is called Encapsulation.

class Student {

    private int age;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}

public class Encapsulation {

    public static void main(String[] args) {
        Student s1 = new Student();
        s1.setName("Ahmad");
        s1.setAge(19);
        System.out.println(s1.getName());
        System.out.println(s1.getAge());
    }
}
