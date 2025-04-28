package src;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;

public class MessageServer {
  public static final String DB_LOCATION = "jdbc:mysql://db.engr.ship.edu:3306/cmsc471_10?useTimezone=true&serverTimezone=UTC";
  public static final String LOGIN_NAME = "cmsc471_10";
  public static final String PASSWORD = "Password_10";

  public static void main(String[] args) {
    try {
      DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
    } catch (SQLException sqle) {
      sqle.printStackTrace();
    }

    try (ServerSocket serverSocket = new ServerSocket(4446)) {
      System.out.println("[Server] Listening on port 4446...");

      while (true) {
        Socket clientSocket = serverSocket.accept();
        System.out.println("[Server] Client connected.");

//        thread allows for multiple client connections
        new Thread(() -> handleClient(clientSocket)).start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

//  this may need updated... the message gets recieved from the client,
//  but the server doesn't seem to connect to the database itself.
  private static void handleClient(Socket clientSocket) {
    try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
         BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
         Connection conn = DriverManager.getConnection(DB_LOCATION, LOGIN_NAME, PASSWORD);
         Statement stmt = conn.createStatement()) {

      String sql = in.readLine();
      System.out.println("[Server] Received query: " + sql);

      if (sql.trim().toLowerCase().startsWith("select")) {
        ResultSet rs = stmt.executeQuery(sql);
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        // Send column header first
        StringBuilder header = new StringBuilder();
        for (int i = 1; i <= columnCount; i++) {
          header.append(meta.getColumnName(i));
          if (i < columnCount) header.append(", ");
        }
        out.write(header.toString());
        out.newLine();

        // Send each row
        while (rs.next()) {
          StringBuilder row = new StringBuilder();
          for (int i = 1; i <= columnCount; i++) {
            row.append(rs.getString(i));
            if (i < columnCount) row.append(", ");
          }
          out.write(row.toString());
          out.newLine();
        }
        out.flush();
      } else {
        //for UPDATE, SEARCH, and DELETE
        stmt.executeUpdate(sql);
        out.write("OK");
        out.newLine();
        out.flush();
      }

    } catch (Exception e) {
      System.out.println("[Server] Error: " + e.getMessage());
      e.printStackTrace();
    }
  }


  private static String executeSQL(Connection conn, String message) {
//    get the first word of the message to determine the command type
//    tbh there might be a better/safer way to do this
    String commandWord = message.trim().split("\\s+")[0].toUpperCase();

    try {
      return switch (commandWord) {
        case "SELECT" -> handleSelect(conn, message);
        case "INSERT" -> handleInsert(conn, message);
        case "UPDATE" -> handleUpdate(conn, message);
        case "DELETE" -> handleDelete(conn, message);
        case "CALL" -> storedProcedure(conn, message);
        default -> "Invalid command type: " + commandWord;
      };
    } catch (SQLException e) {
      return "SQL Error: " + e.getMessage();
    } catch (Exception e) {
      return "Server Processing Error: " + e.getMessage();
    }
  }

  //handles the stored procedure calls
  private static String storedProcedure(Connection conn, String message) {
    try (CallableStatement stmt = conn.prepareCall(message)) {
      boolean hasResults = stmt.execute();
      StringBuilder result = new StringBuilder();

      if (hasResults) {
        try (ResultSet rs = stmt.getResultSet()) {
          ResultSetMetaData meta = rs.getMetaData();
          int colCount = meta.getColumnCount();

          for (int i = 1; i <= colCount; i++) {
            result.append(meta.getColumnName(i)).append("\t");
          }
          result.append("\n");

          while (rs.next()) {
            for (int i = 1; i < colCount; i++) {
              result.append(rs.getString(i)).append("\t");
            }
            result.append("\n");
          }
        }
      }
      return result.toString();
    } catch (SQLException e) {
      return "SQL Error: " + e.getMessage();
    }
  }

  private static String handleSelect(Connection conn, String query) throws SQLException {
    try (Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

      ResultSetMetaData meta = rs.getMetaData();
      int colCount = meta.getColumnCount();
      StringBuilder result = new StringBuilder();


      for (int i = 1; i <= colCount; i++) {
        result.append(meta.getColumnName(i)).append("\t");
      }
      result.append("\n");


      while (rs.next()) {
        for (int i = 1; i < colCount; i++) {
          result.append(rs.getString(i)).append("\t");
        }
        result.append("\n");
      }
      return result.toString();
    }
  }

  private static String handleInsert(Connection conn, String query) throws SQLException {
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
      int rowsAffected = stmt.executeUpdate();
      return rowsAffected + (" row(s) inserted.");
    }
  }

  private static String handleUpdate(Connection conn, String query) throws SQLException {
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
      int rowsAffected = stmt.executeUpdate();
      return rowsAffected + (" row(s) updated.");
    }
  }

  private static String handleDelete(Connection conn, String query) throws SQLException {
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
      int rowsAffected = stmt.executeUpdate();
      return rowsAffected + (" row(s) deleted.");
    }
  }
}