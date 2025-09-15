class A1{

    public interface NestedInterfaceExample{
        boolean isOdd(int num);
    }

}

class B1 implements A1.NestedInterfaceExample{

    @Override
    public boolean isOdd(int num){
        return (num & 1) == 1;
    }

}

public class NestedInterface {

    public static void main(String[] args) {
        
        B1 objB1 = new B1();
        
        System.out.println(objB1.isOdd(5));
    
    }
}
