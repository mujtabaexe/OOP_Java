
import java.util.ArrayList;

class AnimalX {
    void sound() {
        System.out.println("Animal makes a sound");
    }
}

class Dog extends AnimalX {
    void bark() {
        System.out.println("Dog barks");
    }
}

class Cat extends AnimalX {
    void meow() {
        System.out.println("Meow");
    }
}

public class AnimalRunner {
    public static void main(String[] args) {
        AnimalX a = new Dog(); 
        // a.sound();
        Dog d = (Dog) a;
        // d.bark();
        // a.bark();

        // Cat c = new Cat();
        // AnimalX catAnimal = (AnimalX) c;

        AnimalX myCat = new Cat();

        AnimalX[] arr = new AnimalX[3];
        arr[0] = a;
        arr[1] = myCat;
        arr[2] = d;

        for (AnimalX myAnimal : arr) {
            if (myAnimal.getClass().toString().contains("Cat")) {
                Cat downCastToCat = (Cat) myAnimal;
                downCastToCat.meow();
            } else if (myAnimal.getClass().toString().contains("Dog")) {
                Dog downCastToDog = (Dog) myAnimal;
                downCastToDog.bark();
            }
        }
    }
}
