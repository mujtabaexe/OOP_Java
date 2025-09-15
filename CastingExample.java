// Base class
class Animal {
    void sound() {
        System.out.println("Animal makes a sound");
    }
}

// Subclass
class Dog extends Animal {
    void bark() {
        System.out.println("Dog barks");
    }

    @Override
    void sound() {
        System.out.println("Dog makes a sound");
    }
}

// Another Subclass
class Cat extends Animal {
    void meow() {
        System.out.println("Cat meows");
    }

    @Override
    void sound() {
        System.out.println("Cat makes a sound");
    }
}

// Interface
interface Eatable {
    void eat();
}

class Human implements Eatable {
    public void eat() {
        System.out.println("Human eats food");
    }

    void speak() {
        System.out.println("Human speaks");
    }
}

public class CastingExample {

    public static void main(String[] args) {

        // --------------------------
        // 1. Primitive Casting
        // --------------------------

        // Widening (Implicit)
        int x = 100;
        double y = x;
        System.out.println("Widening int to double: " + y);

        // Narrowing (Explicit)
        double d = 99.99;
        int i = (int) d;  // fractional part lost
        System.out.println("Narrowing double to int: " + i);

        // --------------------------
        // 2. Upcasting (Subclass to Superclass)
        // --------------------------
        Dog dog = new Dog();
        Animal animalRef = dog; // upcasting
        animalRef.sound(); // dynamic dispatch

        // --------------------------
        // 3. Downcasting (Superclass to Subclass)
        // --------------------------
        if (animalRef instanceof Dog) {
            Dog dogRef = (Dog) animalRef; // downcasting
            dogRef.bark();
        }

        // Unsafe downcast - causes ClassCastException at runtime
        Animal catAnimal = new Cat();
        // Dog wrongDog = (Dog) catAnimal; // ❌ runtime error

        if (catAnimal instanceof Cat) {
            Cat catRef = (Cat) catAnimal;
            catRef.meow();
        }

        // --------------------------
        // 4. Casting with Interfaces
        // --------------------------
        Eatable eatable = new Human(); // upcast to interface
        eatable.eat();

        if (eatable instanceof Human) {
            Human human = (Human) eatable; // downcast to actual object
            human.speak();
        }

        // --------------------------
        // 5. Mixing all together
        // --------------------------
        Object obj = new Dog(); // Object reference to Dog

        if (obj instanceof Animal) {
            Animal a = (Animal) obj;
            a.sound();
        }

        if (obj instanceof Dog) {
            Dog realDog = (Dog) obj;
            realDog.bark();
        }
    }
}
