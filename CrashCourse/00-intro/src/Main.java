public class Main {
    // This is my first java program

    /*
    This is a multiline
    comment.
     */

    public static void main(String[] args){
        System.out.println("I like Tilapia!");
        System.out.println("It's really good!");
        System.out.println("Cook me Tilapia.");
        System.out.println("Lovely!");

        // Variables
        // Integer data type
        int age = 21;
        int year = 2025;
        int quantity = 1;

        System.out.println(age);
        System.out.println(year);
        System.out.println("The year is " + year);

        // Double data types
        double price = 19.99;
        double gpa = 3.8;
        double temperature = -12.5;

        System.out.println("My gpa this semester is " + gpa);

        // Char data types
        char grade = 'A';
        char symbol = '!';

        System.out.println(grade);

        // Boolean
        boolean isStudent = false;
        boolean forSale = false;
        boolean isOnline = true;

        if (isStudent){
            System.out.println("You are a student");
        }
        else{
            System.out.println("You are not a student!!");
        }

        //Reference data types
        String name = "Kanele Bokosi1";
        String food = "Tilapia";
        String email = "fake123@gmail.com";
        String car = "Mustang";
        String color = "Red";

        System.out.println("Hello "+ name);
        System.out.println("You are " + age + "Years old!");
        System.out.println("Your gpa is "+ gpa);
        System.out.println("Your letter grade is " + grade);

        System.out.println("Your choice is a " + color + " "+ year + " " + car);
    }
}
