
import java.sql.*;
import java.util.InputMismatchException;
import java.util.PropertyResourceBundle;

public class Utilities {
    private static Statement searchStudyField;
    private static Statement searchStudentInstructor;
    private static Statement updateGrade;
    private static Statement updateSchol;
    private static int returnID=0;
    private static PreparedStatement insertSt;
    private static PreparedStatement insertIn;
    private static Statement deleteStud;
    private static Statement statement;
    private static ResultSet rs;
        public static void searchStudyField(Connection postGresConn, String in) throws SQLException {
            try {
                PreparedStatement searchStudyFieldd = postGresConn.prepareStatement("SELECT * FROM Student WHERE ID IN " +
                        "(SELECT StudID FROM Course_Student WHERE CourStudField = ?)");
                searchStudyFieldd.setString(1,in);
                rs = searchStudyFieldd.executeQuery();
                /*rs = searchStudyField.executeQuery("SELECT * FROM Student WHERE ID IN " +
                        "(SELECT StudID FROM Course_Student WHERE CourStudField = '" + in + "')");*/
                printStudent(rs);
            }catch(SQLException e) {
                System.out.println("SQL Error!");
             e.printStackTrace();
            }catch (InputMismatchException e){
                System.out.print("Bad input\n");
            } finally {
                try {
                    if (null != rs) rs.close();
                    if (null != searchStudyField) searchStudyField.close();
                } catch (SQLException exp) {
                System.out.println("Unexpected SQL Error!");
                exp.printStackTrace();
                }
            }
        }
    public static void searchStudentInstructor(Connection postGresConn, int in) throws SQLException {
            try {
                searchStudentInstructor = postGresConn.createStatement();
                rs = searchStudentInstructor.executeQuery("SELECT DISTINCT Student.ID, StFirstName, StLastName, StPhone, Student.Gender, Grade, Scholarship FROM Student " +
                        "JOIN Course_Student ON Student.ID = Course_Student.StudID " +
                        "JOIN Course ON Course_Student.CourID = Course.ID " +
                        "JOIN Course_Instructor ON Course.ID = Course_Instructor.CourID " +
                        "JOIN Instructor ON Course_Instructor.InstrID = Instructor.ID WHERE Instructor.ID =" + in);
                printStudent(rs);
            }catch(SQLException e) {
                System.out.println("SQL Error!");
                e.printStackTrace();
            }catch (InputMismatchException e){
                System.out.print("Bad input\n");
            } finally {
                try {
                    if (null != rs)
                        rs.close();
                    if (null != searchStudentInstructor)
                        searchStudentInstructor.close();
                } catch (SQLException exp) {
                    System.out.println("Unexpected SQL Error!");
                    exp.printStackTrace();
                }
            }
    }
    public static void insertStudent(Connection postGresConn, String F, String L, int P, String Gen, int Gra, int S) throws SQLException {
       try {
           insertSt = postGresConn.prepareStatement("INSERT INTO Student(StFirstName,StLastName,StPhone,Gender,Grade,Scholarship) VALUES (?, ?, ?, ?, ?, ?)");
           insertSt.setString(1, F);
           insertSt.setString(2, L);
           insertSt.setInt(3, P);
           insertSt.setString(4, Gen);
           insertSt.setInt(5, Gra);
           insertSt.setInt(6, S);
           insertSt.executeUpdate();
           statement = postGresConn.createStatement();
           rs = statement.executeQuery("SELECT ID, StFirstName, StLastName, StPhone, Gender, Grade, Scholarship FROM Student");
           printStudent(rs);
       }catch(SQLException e){
           System.out.print("Error");
       }finally {
           insertSt.close();
           statement.close();
           rs.close();
       }
    }
    public static void insertInstructor(Connection postGresConn, int input,String F, String L, int P, String G) throws SQLException {
    try {
        if (returnID != 1) {
            insertIn = postGresConn.prepareStatement("INSERT INTO Instructor VALUES (?, ?, ?, ?, ?)");
            insertIn.setInt(1, input);
            insertIn.setString(2, F);
            insertIn.setString(3, L);
            insertIn.setInt(4, P);
            insertIn.setString(5, G);

            insertIn.executeUpdate();
            statement = postGresConn.createStatement();
            rs = statement.executeQuery("SELECT * FROM Instructor");
            System.out.print("ID  First_Name  Last_Name  Phone  Gender\n");
            while (rs.next()) {
                int ID = rs.getInt("ID");
                String StFirst = rs.getString("InFirstName");
                String StLast = rs.getString("InLastName");
                int StPhone = rs.getInt("InPhone");
                String StGender = rs.getString("Gender");
                System.out.print(ID + "  " + StFirst + "  " + " " + StLast + "  " + StPhone + "  " + StGender + "  " + "\n");
            }

        } else
            System.out.print("Entered ID is already existing\n");
    }catch (SQLException e){
        System.out.print("Error");
    }finally {
        rs.close();
        insertIn.close();
        returnID = 0;
    }
    }
    public static void updateGrade(Connection postGresConn, int input1, int input2) throws SQLException {
            try {

                if (returnID == 1) {
                    updateGrade.executeUpdate("UPDATE Student SET Grade = " + input2 + " WHERE ID = " + input1);
                    rs = updateGrade.executeQuery("SELECT ID, StFirstName, StLastName, StPhone, Gender, Grade, Scholarship FROM Student");
                    printStudent(rs);
                } else {
                    System.out.print("Student does not exist\n");
                }
            }catch (SQLException e){
                System.out.print("Error");
            }finally {
                if(null!=rs)
                rs.close();
                if(updateGrade!= null)
                updateGrade.close();
                returnID = 0;
            }
    }
    public static void printTakenID(Connection postGresConn) throws SQLException {
        ResultSet resultSet = null;
            try {
                String string = "SELECT * FROM Instructor";
                statement = postGresConn.createStatement();
                resultSet = statement.executeQuery(string);
                while (resultSet.next()) {
                    int Id = resultSet.getInt("ID");
                    String First = resultSet.getString("InFirstName");
                    String Last = resultSet.getString("InLastName");
                    int Phone = resultSet.getInt("InPhone");
                    String Gender = resultSet.getString("Gender");
                    System.out.print(Id + " "+First+" "+Last+" "+Phone+ " "+ Gender+"\n");
                }
                System.out.print("\n");
            }catch (SQLException e){
                System.out.print("ERROR\n");
            }finally {
                if(null!=statement)
                statement.close();
                if(null!=resultSet)
                resultSet.close();
            }
    }
    public static void updateScholarship(Connection postGresConn, int stud1, int stud2) throws SQLException {
        PreparedStatement prep1=null;
        PreparedStatement prep2=null;
        Statement updateScho = postGresConn.createStatement();
        rs = updateScho.executeQuery("SELECT ID FROM Student");
        int yra=0;
        while (rs.next()) {
            int ID = rs.getInt("ID");
            if (check(ID, stud1) ) {
                yra++;
            }
            if(check(ID,stud2)){
                yra++;
            }
        }
        if(yra ==2) {
            try {
                postGresConn.setAutoCommit(false);

                prep1 = postGresConn.prepareStatement("UPDATE Student SET Scholarship = Scholarship + (SELECT Scholarship FROM Student WHERE ID = " + stud1 + ") WHERE ID = " + stud2);
                int affected = prep1.executeUpdate();
                if (affected > 0) {
                    prep2 = postGresConn.prepareStatement("UPDATE Student SET Scholarship = Scholarship - (SELECT Scholarship FROM Student WHERE ID = " + stud1 + ") WHERE ID = " + stud1);
                    prep2.executeUpdate();
                }
                postGresConn.commit();
            } catch (SQLException e) {
                postGresConn.rollback();
            } finally {
                rs = updateScho.executeQuery("SELECT ID, StFirstName, StLastName, StPhone, Gender, Grade, Scholarship FROM Student");
                printStudent(rs);
                //  postGresConn.setAutoCommit(true);
                assert prep1 != null;
                prep1.close();
                assert prep2 != null;
                prep2.close();
                updateScho.close();
            }
        }else{
            System.out.print("Both or one of the students does not exist\n");
            updateScho.close();
        }
    }
    public static void deleteStudent(Connection postGresConn, int input) throws SQLException {
        try{
            deleteStud = postGresConn.createStatement();
            deleteStud.executeUpdate("DELETE FROM Student WHERE ID = "+input);
            rs = deleteStud.executeQuery("SELECT ID, StFirstName, StLastName, StPhone, Gender, Grade, Scholarship FROM Student");
            printStudent(rs);
        }catch(SQLException e) {
            System.out.println("SQL Error!");
            e.printStackTrace();
        }catch (InputMismatchException e){
            System.out.print("Bad input\n");
        } finally {
            try {
                if (null != rs)
                    rs.close();
                if (null != statement)
                    statement.close();
                if(null!=deleteStud) deleteStud.close();
            } catch (SQLException exp) {
                System.out.println("Unexpected SQL Error!");
                exp.printStackTrace();
            }
        }
    }
    public  static int returnIDS(Connection postGresConn, int input1) throws SQLException {
        updateGrade = postGresConn.createStatement();
        rs = updateGrade.executeQuery("SELECT ID,Grade FROM Student");
        System.out.println("ID   Grade\n");
        while (rs.next()) {
            int ID = rs.getInt("ID");
            int Grade = rs.getInt("Grade");
            System.out.println(ID + "  " + Grade);
            if (check(ID, input1)) {
                returnID = 1;
                break;
            }
        }
        return returnID;
    }
    public static int returnID(Connection postGresConn, int input) throws SQLException {
                String string = "SELECT ID from Instructor";
                statement = postGresConn.createStatement();
                rs = statement.executeQuery(string);
                while (rs.next()) {
                    int Id = rs.getInt("ID");
                    if (check(Id, input)) {
                        returnID = 1;
                        break;
                    }
                }
                rs.close();
                statement.close();
                return returnID;

    }

    private static boolean check(int SQLint, int Inputint){
        return SQLint == Inputint;
    }
    private static void printStudent(ResultSet rs) throws SQLException {

        System.out.print("ID  First_Name  Last_Name  Phone  Gender  Grade  Scholarship\n");
        while(rs.next()){
            int ID = rs.getInt("ID");
            String StFirst = rs.getString("StFirstName");
            String StLast = rs.getString("StLastName");
            int StPhone = rs.getInt("StPhone");
            String StGender = rs.getString("Gender");
            int StGrade = rs.getInt("Grade");
            int StSchol = rs.getInt("Scholarship");
            System.out.print(ID + "  " + StFirst + "  " + " " +StLast+ "  " +StPhone+"  "+StGender+"  "+StGrade+"  "+StSchol+"\n");
        }
    }
    public static void print(Connection connection) throws SQLException {
        System.out.print("ID  First_Name  Last_Name  Phone  Gender  Grade  Scholarship\n");
        Statement st ;
        st = connection.createStatement();
        ResultSet rs = st.executeQuery("SELECT ID, StFirstName, StLastName, StPhone, Gender, Grade, Scholarship FROM Student");
        while(rs.next()){
            int ID = rs.getInt("ID");
            String StFirst = rs.getString("StFirstName");
            String StLast = rs.getString("StLastName");
            int StPhone = rs.getInt("StPhone");
            String StGender = rs.getString("Gender");
            int StGrade = rs.getInt("Grade");
            int StSchol = rs.getInt("Scholarship");
            System.out.print(ID + "  " + StFirst + "  " + " " +StLast+ "  " +StPhone+"  "+StGender+"  "+StGrade+"  "+StSchol+"\n");
        }
        rs.close();
        st.close();
    }
}
