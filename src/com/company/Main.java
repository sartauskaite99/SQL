

import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;



import java.sql.*;
        import java.util.InputMismatchException;
        import java.util.Scanner;

class Main{

    /********************************************************/
    public static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Couldn't find driver class!");
            cnfe.printStackTrace();
            System.exit(1);
        }
    }

    /********************************************************/
    public static Connection getConnection() {
        Connection postGresConn = null;
        try {
            postGresConn = DriverManager.getConnection("jdbc:postgresql://pgsql3.mif/studentu", "******", "*******");
        } catch (SQLException sqle) {
            System.out.println("Couldn't connect to database!");
            sqle.printStackTrace();
            return null;
        }
        System.out.println("Successfully connected to Postgres Database");

        return postGresConn;
    }
    public static void Search(Connection postGresConn, Utilities utilities) throws SQLException {
        Scanner scan = new Scanner(System.in);
            System.out.println("Enter your option:\n" +
                    "1 - Search Students with a specific study field\n" +
                    "2 - Search Students who has the same instructor\n");
            int input = scan.nextInt();
            if(input == 1) {
                System.out.print("Enter Study Field and see what Students have it \n ");
                String in = scan.next();
                Utilities.searchStudyField(postGresConn,in);
            }else if(input==2){

                System.out.print("Enter instructors ID \n ");
                int in = scan.nextInt();
                Utilities.searchStudentInstructor(postGresConn,in);
            }

    }
    /********************************************************/
    public static void Insert(Connection postGresConn, Scanner scan) throws SQLException {
        String First, Last,  Gender;
        int Phone, Grade, Scholarship;
        System.out.println("Creating statement...");
        System.out.println("Enter your option:\n" +
                "1 - Register New Student\n" +
                "2 - Register New Instructor\n");
        int input = scan.nextInt();
        if(input == 1){
            System.out.print("Enter values separated by space:First Name, Last Name, Phone, Gender, Grade, Scholarship\n");

            //  stmt.executeUpdate("INSERT INTO Student " + "VALUES (, 'Simpson', 'Mr.', 'Springfield', 2001)");
            assert postGresConn != null;
            First = scan.next();
            Last = scan.next();
            if(!isAlpha(First) || !isAlpha(Last)){
                throw new InputMismatchException();
            }
            Phone = scan.nextInt();
            Gender = scan.next();
            Grade = scan.nextInt();
            Scholarship = scan.nextInt();
            Utilities.insertStudent(postGresConn,First,Last,Phone,Gender,Grade,Scholarship);
        }else if(input == 2) {
            System.out.print("Enter Instructors' ID, First_Name, Last_Name, Phone, Gender:\n");
            System.out.print("Taken ID's:\n");
            Utilities.printTakenID(postGresConn);
            input = scan.nextInt();
            if(Utilities.returnID(postGresConn,input)!=1) {
                First = scan.next();
                Last = scan.next();
                if (!isAlpha(First) && !isAlpha(Last)) {
                    throw new InputMismatchException();
                }
                Phone = scan.nextInt();
                Gender = scan.next();
                Utilities.insertInstructor(postGresConn, input, First, Last, Phone, Gender);
            }else{
                System.out.print("Entered ID is already existing\n");
            }
        }
    }

    public static void Update(Connection postGresConn, Scanner scan) throws SQLException {
        Statement statement = null;
        ResultSet rs = null;
        Statement stmt = null;
        Scanner scanner = new Scanner(System.in);

            System.out.println("Enter your option:\n" +
                    "1 - Change students' grade\n" +
                    "2 - Assign one student's scholarship to another\n");
            int input1 = scan.nextInt();
            if(input1 == 1) {
                System.out.println("Enter that students ID, whose grade you would like to change");

                input1 = scan.nextInt();
                if(Utilities.returnIDS(postGresConn,input1)==1) {
                  //  Utilities.print(postGresConn);
                    System.out.println("Enter grade\n");
                    int input2 = scan.nextInt();
                    Utilities.updateGrade(postGresConn, input1, input2);
                }else{
                    System.out.print("Student does not exist\n");
                }
            }else if(input1 == 2){
                int stud1, stud2;
                System.out.println("Creating statement...");
                System.out.println("Enter Student's ID who will give his scholarship and ID who will accept");
                Utilities.print(postGresConn);
                stud1 = scanner.nextInt();
                stud2 = scanner.nextInt();
                Utilities.updateScholarship(postGresConn,stud1,stud2);
            }
    }
    public static boolean isAlpha(String name) {
        return name.matches("[a-zA-Z]+");
    }
    public static void Delete(Connection postGresConn, Scanner scan) throws SQLException {
            System.out.print("Enter ID of Student that you want to remove \n ");
        Utilities.print(postGresConn);
            int input = scan.nextInt();
            Utilities.deleteStudent(postGresConn,input);

    }
    /********************************************************/
    public static void main(String[] args) {
        loadDriver();
        Utilities utilities = new Utilities();
        Connection con = getConnection();
        Scanner in = new Scanner(System.in);
        if (null != con) {
            try{
                int input = 1;
                while(input != 0) {
                    System.out.println("Enter your option:  \n" +
                            "1 - Search\n" +
                            "2 - Insert\n" +
                            "3 - Update\n" +
                            "4 - Delete\n" +
                            "0 - Exit\n");
                    input = in.nextInt();
                    if (input == 1) {
                        Search(con,utilities);
                    } else if (input == 2) {
                        Insert(con, in);
                    } else if (input == 3) {
                        Update(con, in);
                    } else if (input == 4) {
                        Delete(con, in);
                    }
                }
            }catch (InputMismatchException | SQLException e){
                System.out.print("Bad input! \n");
            }
        }
        if (null != con) {
            try {
                con.close();
            } catch (SQLException exp) {
                System.out.println("Can not close connection!");
                exp.printStackTrace();
            }
        }
    }
}
/********************************************************/
