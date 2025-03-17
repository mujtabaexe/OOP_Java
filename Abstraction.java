

// We do not want the user to access the properties of the Animal class directly.  
// For example, we do not want the user to make Animal walk, but  
// we want to give access to specific animals like Horse or Chicken to walk.  
// To achieve this, we use an Abstract class, which ensures only the important  
// functionality of the subclasses is accessible while hiding unnecessary details.  


// class Animal{

//     public void walk(){

//     }

// }

// So we make Abstract class Instead

abstract class Animal{

    // Abstract methods does not have a code body
    abstract void sound();
    
    public void walk(){
        System.out.println("Animals Walk");
    }
}


class Horse extends Animal{
    public void walk(){
        System.out.println("Walks on 4 legs");
    }
}

class Chicken extends Animal{
    public void walk(){
        System.out.println("Walks on 2 legs");
    }
}

public class Abstraction {
    public static void main(String[] args) {
        
    }
}
