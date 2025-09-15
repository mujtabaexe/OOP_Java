interface Engine {
    int PRICE = 9500000;

    void start();
    void stop();
    void accelerate();
}

interface Brake {
    void brake();
}

interface Media {
    void start();
    void stop();
}


class CDPlayer implements Media{

    @Override
    public void start(){
        System.out.println("Music Starts");
    }
    
    @Override
    public void stop(){
        System.out.println("Music Stops");
    
    }
        
}

class PowerEngine implements Engine{

    @Override
    public void start() {
        System.out.println("Power Engine Starts");
    }
    
    @Override
    public void stop() {
        System.out.println("Power Engine Stops");
    }
    
    @Override
    public void accelerate() {
        System.out.println("Power Engine Accelarate");
    }
    
}

class ElectricEngine implements Engine{

    @Override
    public void start() {
        System.out.println("Electric Engine Starts");
    }
    
    @Override
    public void stop() {
        System.out.println("Electric Engine Stops");
    }
    
    @Override
    public void accelerate() {
        System.out.println("Electric Engine Accelarate");
    }
    
}

class Car implements Engine, Brake{

    int a = 30;

    @Override
    public void start() {
        System.out.println("Engine starts");
    }

    @Override
    public void stop() {
        System.out.println("Engine stops");
    }

    @Override
    public void accelerate() {
        System.out.println("Accelerating...");
    }

    @Override
    public void brake() {
        System.out.println("Brakes applied");
    }
}

class NiceCar {

    private Engine engine;
    private Media player = new CDPlayer();

    public NiceCar() {
        engine = new PowerEngine();
    }

    public NiceCar(Engine engine) {
        this.engine = engine;
    }

    public void start(){
        engine.start();
    }

    public void stop(){
        engine.stop();
    }

    public void startMusic(){
        player.start();
    }

    public void stopMusic(){
        player.stop();
    }

    public void upgradeEngine(){

        this.engine = new ElectricEngine();
        
    }
    

}

public class CarSystem{
    public static void main(String[] args) {
        Car car = new Car();
        Engine car2 = new Car();

        car.start();
        System.out.println(car.a); 
        car.accelerate();
        car.stop();
        car.brake();

        System.out.println(((Car)car2).a); // If reference is of the parent class you can access parameters and methods of the parent class

        System.out.println("Car Price: " + Engine.PRICE);

        NiceCar car3 = new NiceCar();
        car3.start();
        car3.stop();
        car3.startMusic();
        car3.stopMusic();
        car3.upgradeEngine();
        car3.start();
    }
}