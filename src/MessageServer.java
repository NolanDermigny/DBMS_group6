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
    System.out.println("[Server] Handling client: " +
          clientSocket.getInetAddress() + ":" + clientSocket.getPort());
    BufferedReader in;
    PrintWriter out;
    try {
      in  = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      out = new PrintWriter(clientSocket.getOutputStream(), true);

      // read the client’s SQL
      String message = in.readLine();
      if (message == null) {
        System.out.println("[Server] client closed connection immediately");
        return;
      }
      System.out.println("[Server] Received message: " + message);

//      this is where things seem to fail/break/not work
      // try to open the DB
      DriverManager.setLoginTimeout(30);    // fail in 30s if DB is unreachable
      try (Connection conn = DriverManager.getConnection(DB_LOCATION, LOGIN_NAME, PASSWORD)) {
        System.out.println("[Server] Connected to database.");
        String response = executeSQL(conn, message);
        out.println(response);
      } catch (SQLException e) {
        System.err.println("[Server] DB error:");
        e.printStackTrace();
        out.println("SQL Error: " + e.getMessage());
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        clientSocket.close();
        System.out.println("[Server] Client disconnected.");
      } catch (IOException ignored) {}
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
        default -> "Invalid command type: " + commandWord;
      };
    } catch (SQLException e) {
      return "SQL Error: " + e.getMessage();
    } catch (Exception e) {
      return "Server Processing Error: " + e.getMessage();
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
        for (int i = 1; i <= colCount; i++) {
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