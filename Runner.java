public interface RegisterForExams {
public void register();
}
public class EmplayeeTask implements RegisterForExams{

private String name; private String date; private int salary;

public EmplayeeTask()
{
name = null; date = null; salary = 0;
}

public EmplayeeTask(String name,String date,int salary)
{
this.name = name; this.date = date; this.salary = salary;
}

@Override
public void register() {
System.out.println("Employee is registered " + "Name " + name + " salary " + salary + " date " + date);
}
}
public class StudentTask implements RegisterForExams{
private String name; private int age; private double gpa;

public StudentTask()
{

name = null; age = 0;
gpa = 0;
}
public StudentTask(String name,int age,double gpa)
{this.name = name;
this.age = age;
 
this.gpa = gpa;

}
@Override
public void register() {
System.out.println("Student is registered  " + "Student name " + name + " gpa " + gpa);
}}
public class Runner {
public static void main(String[] args) {
       
EmplayeeTask e = new EmplayeeTask("Ahmed","11,02,2001",20000);
StudentTask s = new StudentTask("Ali",22,3.5);
e.register();
s.register();
}  }
 


