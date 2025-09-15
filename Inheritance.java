import bank.Account;


class Shape{
    public float length;
    private float wid3th;
    private float radius;
    private float area;
    private float circumference;



}

// The shape class is the Base Class or Parent Class and the Triangle class is the Subclass or Child Class.

class Triangle extends Shape{
    
    public void area(float length, float width) {
        System.out.println(0.5*length*width);
    }

}

// It is is the Subclass of the Triangle class . It is called Multi Level Inheritance.

class EquilateralTriangle extends Triangle{

    public void area(float length, float width) {
        System.out.println(1/2*length*width);

    }
}

// So it is also the Subclass class of Shape class and the Triangle class and this Circle class makes the Hierarchial Inheritance.  
// Circle + Triangle + Equilateral classes makes the Hybrid Inheritance 

class Circle extends Shape{

    public void area(float radius) {
        System.out.println((3.14)*radius*radius);

    }
}


public class Inheritance {
    public static void main(String[] args) {
        Account acc1 = new Account();
        acc1.name = "Mujtaba";
        System.out.println(acc1.name);
    }
}
