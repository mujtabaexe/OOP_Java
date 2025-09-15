public class Generics {
    public static void main(String[] args) {
        // Box<Double> stringBox = new Box<>(1.5);
        // stringBox.print();

        // Box<Integer> intBox = new Box<>(1);
        // intBox.print();

        Box<Toyota> carbox = new Box<>(new Toyota("toyota", "v8"));
        carbox.sound();

        Box<Honda> hondaBox = new Box<>(new Honda("Honda", "v8"));
        hondaBox.sound();
    }
}

class Box<T extends Car> {
    T data;

    public Box(T data) {
        this.data = data;
    }

    public void sound() {
        data.makeSound();
    }
}

abstract class Car {
    String carName;
    String engineName;

    public Car(String carName, String engineName) {
        this.carName = carName;
        this.engineName = engineName;
    }
    abstract void makeSound();
}

class Toyota extends Car {

    public Toyota(String carName, String engineName) {
        super(carName, engineName);
    }

    @Override
    void makeSound() {
        System.out.println("Toyota SOund");
    }
}

class Honda extends Car {

    public Honda(String carName, String engineName) {
        super(carName, engineName);
    }

    @Override
    void makeSound() {
        System.out.println("Honda Sound");
    }

}