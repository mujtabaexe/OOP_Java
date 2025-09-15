interface A {

    static void hello(){
        System.out.println("Hello");
    }

    void clap();
    default void fun() {
        System.out.println("I am in A");
    }
}

interface B extends A {
    void greet();
}

public class InterfaceExample implements A, B {

    @Override
    public void greet() {
        System.out.println("Hello from greet");
    }
    @Override
    public void clap() {
        System.out.println("Clapping.");
    }

    public static void main(String[] args) {
        InterfaceExample obj = new InterfaceExample();
        obj.fun();   // From A
        obj.greet(); // Implemented in InterfaceExample
        
        A.hello(); // You can call only via interface's name
    }

}

