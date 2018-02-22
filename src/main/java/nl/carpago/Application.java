package nl.carpago;

import java.sql.SQLException;

import nl.carpago.util.MySQLAccess;

public class Application {

   public static void main(String[] args) throws ClassNotFoundException, SQLException {
      MySQLAccess dao = new MySQLAccess();
      dao.readDataBase();
   }
}
