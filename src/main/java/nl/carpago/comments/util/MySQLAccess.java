package nl.carpago.comments.util;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class MySQLAccess {

   private Connection connect = null;
   private Statement statement = null;
   private PreparedStatement preparedStatement = null;
   
   public void readDataBase() throws SQLException, ClassNotFoundException {
      try {
         // This will load the MySQL driver, each DB has its own driver
         Class.forName("com.mysql.cj.jdbc.Driver");
         // Setup the connection with the DB
         connect = DriverManager
                           .getConnection("jdbc:mysql://localhost/feedback?"
                                             + "user=username&password=password");

         // Statements allow to issue SQL queries to the database
         statement = connect.createStatement();
         // Result set get the result of the SQL query
         // autoclose also applies for the connection and resultset but that will mingle the code too much for now ... 
         try (ResultSet resultSet = statement
                           .executeQuery("select * from feedback.comments");) {

            writeResultSet(resultSet);

            // PreparedStatements can use variables and are more efficient
            preparedStatement = connect
                              .prepareStatement("insert into  feedback.comments values (default, ?, ?, ?, ? , ?, ?)");
            // "myuser, webpage, datum, summary, COMMENTS from feedback.comments");
            // Parameters start with 1
            preparedStatement.setString(1, "Test");
            preparedStatement.setString(2, "TestEmail");
            preparedStatement.setString(3, "TestWebpage");

            LocalDate birthDay = LocalDate.of(1968, 8, 9);
            Date birthDayAsSqlDate = Date.valueOf(birthDay);
            preparedStatement.setDate(4, birthDayAsSqlDate);

            preparedStatement.setString(5, "TestSummary");
            preparedStatement.setString(6, "TestComment");
            preparedStatement.executeUpdate();

            preparedStatement = connect
                              .prepareStatement("SELECT myuser, webpage, datum, summary, COMMENTS from feedback.comments");
         }

         try (ResultSet resultSet = preparedStatement.executeQuery()) {

            writeResultSet(resultSet);

            // Remove again the insert comment
            preparedStatement = connect
                              .prepareStatement("delete from feedback.comments where myuser= ? ; ");
            preparedStatement.setString(1, "Test");
            preparedStatement.executeUpdate();
         }

         try (ResultSet resultSet = statement
                           .executeQuery("select * from feedback.comments");) {

            writeMetaData(resultSet);
         }

      }
      catch (SQLException | ClassNotFoundException e) {
         throw e;
      }
      close();
   }

   private void writeMetaData(ResultSet resultSet) throws SQLException {
      // Now get some metadata from the database
      // Result set get the result of the SQL query

      System.out.println("The columns in the table are: ");

      System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
      for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
         System.out.println("Column " + i + " " + resultSet.getMetaData().getColumnName(i));
      }
   }

   private void writeResultSet(ResultSet resultSet) throws SQLException {
      // ResultSet is initially before the first data set
      while (resultSet.next()) {
         // It is possible to get the columns via name
         // also possible to get the columns via the column number
         // which starts at 1
         // e.g. resultSet.getSTring(2);
         String user = resultSet.getString("myuser");
         String website = resultSet.getString("webpage");
         String summary = resultSet.getString("summary");
         Date date = resultSet.getDate("datum");
         String comment = resultSet.getString("comments");
         System.out.println("User: " + user);
         System.out.println("Website: " + website);
         System.out.println("summary: " + summary);
         System.out.println("Date: " + date);
         System.out.println("Comment: " + comment);
      }
   }
// You need to close the resultSet
   private void close() {
       try {
           if (statement != null) {
               statement.close();
           }
           if (connect != null) {
               connect.close();
           }
       } catch (Exception e) {
          // log some
          // for now
          System.err.println("That closing went pretty wrong!");
       }
   }
}