
class Singleton{

    private Singleton(){

    }

    private static Singleton instance;

    public static Singleton getInstance(){

        if(instance == null){
            instance = new Singleton();

        }
        return instance;
    }

}

public class SingletonClassConcept {
    public static void main(String[] args) {

        // All three references (obj1, obj2, obj3) point to the same Singleton instance.
        // This ensures that only one instance of the class is created throughout the application.
        // Singleton pattern is useful in scenarios where a single shared resource is needed,
        // such as a configuration manager, logger, or a centralized AI agent like Jarvis.
        Singleton obj1 = Singleton.getInstance();
        Singleton obj2 = Singleton.getInstance();
        Singleton obj3 = Singleton.getInstance();
        
    }
}
